// Topic    : Deadlock - causing one deliberately, detecting it, and the two standard fixes.
// Approach : Two threads acquire two locks in OPPOSITE orders and hang. A watchdog thread detects
//            the hang and reports it, so this program always terminates. Then two fixes are shown:
//            consistent lock ordering, and tryLock with a timeout.
// Intuition: Deadlock needs four conditions to hold at once (Coffman's conditions - listed at the
//            bottom). Break ANY one and it becomes impossible. The easiest to break in practice is
//            "circular wait": if every thread acquires locks in the same global order, a cycle
//            cannot form.
// Time     : n/a - a deadlocked thread makes no progress, forever
// Space    : O(1)
// Trade-off: Consistent ordering is free at run time but requires discipline across the whole
//            codebase. tryLock costs retries and can livelock, but works when ordering is impossible
//            (locks chosen dynamically, e.g. two arbitrary bank accounts).
// IMPORTANT: The deadlocked threads below are DAEMON threads with a watchdog. They can never prevent
//            the JVM from exiting - which is what makes this file safe to run automatically.

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class DeadlockDemo {

    // Locks used ONLY by the deadlock demonstration. They are never released, because the threads
    // holding them stay blocked forever - which is precisely the point.
    static final Object LOCK_A = new Object();
    static final Object LOCK_B = new Object();

    // The fix demonstrations use SEPARATE locks. Reusing LOCK_A/LOCK_B here would hang: the
    // deadlocked daemon threads above still hold them and will never let go, so even correctly
    // ordered code could not acquire them. A real and easy mistake to make when writing this demo.
    static final Object SAFE_A = new Object();
    static final Object SAFE_B = new Object();

    // ------------------------------------------------------------------
    // 1. THE DEADLOCK - two threads, two locks, opposite acquisition order
    // ------------------------------------------------------------------
    static void demonstrateDeadlock() throws InterruptedException {
        System.out.println("1. Causing a real deadlock (with a watchdog so we can still exit)");

        // A latch-free signal so both threads are inside their FIRST lock before either tries the
        // second. Without this the timing might let one thread finish before the other starts, and
        // no deadlock would occur - the demo would silently "work".
        final Object gate = new Object();
        final boolean[] bothReady = { false };

        Thread one = new Thread(() -> {
            synchronized (LOCK_A) {
                System.out.println("      thread-1 holds A, wants B");
                waitForPartner(gate, bothReady);
                synchronized (LOCK_B) {                 // BLOCKS - thread-2 holds B
                    System.out.println("      thread-1 got both (never printed)");
                }
            }
        }, "thread-1");

        Thread two = new Thread(() -> {
            synchronized (LOCK_B) {                     // <-- OPPOSITE ORDER: B first, then A
                System.out.println("      thread-2 holds B, wants A");
                waitForPartner(gate, bothReady);
                synchronized (LOCK_A) {                 // BLOCKS - thread-1 holds A
                    System.out.println("      thread-2 got both (never printed)");
                }
            }
        }, "thread-2");

        // DAEMON so these permanently-stuck threads cannot keep the JVM alive.
        one.setDaemon(true);
        two.setDaemon(true);
        one.start();
        two.start();

        // The WATCHDOG: wait a bounded time, then declare the deadlock rather than hanging.
        one.join(1500);
        two.join(1500);

        if (one.isAlive() && two.isAlive()) {
            System.out.println("      [watchdog] both threads still alive after 1.5s");
            System.out.println("      [watchdog] thread-1 state = " + one.getState());
            System.out.println("      [watchdog] thread-2 state = " + two.getState());
            System.out.println("      -> DEADLOCK confirmed. Each holds what the other needs.");
            System.out.println("      (BLOCKED means waiting for a monitor - the classic signature)");
        } else {
            // Deadlock is timing-dependent; on rare scheduling it may not trigger.
            System.out.println("      (no deadlock this run - it is timing-dependent, try again)");
        }
    }

    private static void waitForPartner(Object gate, boolean[] bothReady) {
        synchronized (gate) {
            if (!bothReady[0]) {
                bothReady[0] = true;
                try { gate.wait(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            } else {
                gate.notifyAll();
            }
        }
    }

    // ------------------------------------------------------------------
    // 2. FIX A - CONSISTENT LOCK ORDERING (breaks "circular wait")
    // ------------------------------------------------------------------
    static void demonstrateOrderingFix() throws InterruptedException {
        System.out.println("2. Fix A - both threads acquire in the SAME order (A then B)");

        Runnable sameOrder = () -> {
            synchronized (SAFE_A) {                     // EVERYONE takes A first...
                sleep(50);
                synchronized (SAFE_B) {                 // ...then B. No cycle can form.
                    System.out.println("      " + Thread.currentThread().getName() + " got both locks");
                }
            }
        };

        Thread one = new Thread(sameOrder, "ordered-1");
        Thread two = new Thread(sameOrder, "ordered-2");
        one.start(); two.start();
        one.join(); two.join();                          // completes - no watchdog needed
        System.out.println("      both finished. Consistent ordering makes deadlock IMPOSSIBLE.");
    }

    // ------------------------------------------------------------------
    // 3. FIX B - tryLock WITH TIMEOUT (breaks "hold and wait")
    //    Use when ordering is impractical, e.g. transferring between two arbitrary accounts.
    // ------------------------------------------------------------------
    static class Account {
        final String name;
        final ReentrantLock lock = new ReentrantLock();
        int balance;
        Account(String name, int balance) { this.name = name; this.balance = balance; }
    }

    static boolean transfer(Account from, Account to, int amount) {
        // Acquire BOTH or NEITHER. If the second lock is unavailable, release the first and retry -
        // so no thread ever holds one lock while blocking indefinitely for another.
        try {
            if (from.lock.tryLock(100, TimeUnit.MILLISECONDS)) {
                try {
                    if (to.lock.tryLock(100, TimeUnit.MILLISECONDS)) {
                        try {
                            if (from.balance < amount) return false;
                            from.balance -= amount;
                            to.balance += amount;
                            return true;
                        } finally { to.lock.unlock(); }
                    }
                } finally { from.lock.unlock(); }        // released even if the second lock failed
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;   // could not get both - the caller may retry
    }

    static void demonstrateTryLockFix() throws InterruptedException {
        System.out.println("3. Fix B - tryLock with timeout (for dynamically chosen locks)");
        Account alice = new Account("alice", 1000);
        Account bob = new Account("bob", 1000);

        // Deliberately opposite directions - the setup that would deadlock with plain locking.
        Thread t1 = new Thread(() -> {
            int done = 0;
            for (int i = 0; i < 100; i++) if (transfer(alice, bob, 10)) done++;
            System.out.println("      alice->bob completed " + done + "/100");
        });
        Thread t2 = new Thread(() -> {
            int done = 0;
            for (int i = 0; i < 100; i++) if (transfer(bob, alice, 10)) done++;
            System.out.println("      bob->alice completed " + done + "/100");
        });

        t1.start(); t2.start();
        t1.join(); t2.join();
        System.out.println("      final: alice=" + alice.balance + " bob=" + bob.balance
                + " total=" + (alice.balance + bob.balance) + " (conserved)");
        System.out.println("      No deadlock: a thread that cannot get both locks backs off.");
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public static void main(String[] args) throws InterruptedException {
        demonstrateDeadlock();
        System.out.println();
        demonstrateOrderingFix();
        System.out.println();
        demonstrateTryLockFix();
        System.out.println();
        System.out.println("Program exits cleanly - the deadlocked threads were DAEMONS.");
    }
}

/* ------------------------- COFFMAN'S FOUR CONDITIONS -------------------------
 * A deadlock requires ALL FOUR simultaneously. Break any one and it cannot happen:
 *
 * 1. MUTUAL EXCLUSION  - a resource is held exclusively.
 *                        Break it: use immutable data or lock-free structures.
 * 2. HOLD AND WAIT     - a thread holds one resource while waiting for another.
 *                        Break it: acquire everything at once, or use tryLock (Fix B).
 * 3. NO PRE-EMPTION    - a resource cannot be forcibly taken back.
 *                        Break it: timeouts, so a thread gives up voluntarily.
 * 4. CIRCULAR WAIT     - a cycle of threads each waiting on the next.
 *                        Break it: a global lock ORDER (Fix A) - the usual choice.
 *
 * ------------------------------ HOW TO DIAGNOSE -------------------------------
 * In production a deadlock shows as threads stuck with no CPU use. To confirm:
 *   jstack <pid>            - the JVM detects and prints "Found one Java-level deadlock"
 *   jcmd <pid> Thread.print - same information
 *   ThreadMXBean.findDeadlockedThreads() - programmatic detection, for a health check
 * Threads in state BLOCKED that never change are the signature - as the watchdog above shows.
 *
 * ------------------------------ PRACTICAL ADVICE -------------------------------
 * - Hold as FEW locks as possible, for as SHORT a time as possible. Most deadlocks disappear when
 *   the second lock turns out to be unnecessary.
 * - Never call unknown code (a listener, a callback, an override) while holding a lock - it may
 *   acquire locks you know nothing about.
 * - Prefer higher-level tools: ConcurrentHashMap, BlockingQueue and the atomics have no user-visible
 *   locks to order, so the problem cannot arise.
 * - When locks are chosen dynamically, impose an order using a stable key, e.g. System.identityHashCode
 *   or an account id, and always lock the lower one first.
 *
 * ------------------------------- RELATED FAILURES ------------------------------
 * LIVELOCK  - threads keep responding to each other and make no progress. Two people stepping aside
 *             in a corridor, repeatedly. tryLock retry loops can cause this; add randomized backoff.
 * STARVATION- a thread never gets the lock because others keep taking it. Fair locks
 *             (new ReentrantLock(true)) prevent it, at a throughput cost.
 * -------------------------------------------------------------------------------- */
