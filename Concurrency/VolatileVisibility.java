// Topic    : Visibility - the SECOND concurrency problem, distinct from race conditions.
// Approach : Reproduce a real visibility failure (a thread that never sees a stop flag), fix it with
//            volatile, then show what volatile does NOT fix.
// Intuition: A race condition is about ORDERING - two threads interleaving badly. Visibility is
//            about whether a write is ever SEEN at all. Each core has its own cache, and the JIT may
//            hoist a field read out of a loop into a register. Without a memory barrier, one
//            thread's write can remain invisible to another indefinitely - the loop spins forever on
//            a stale value.
// Time     : a volatile read is nearly free; a volatile WRITE costs a memory barrier
// Space    : O(1)
// Trade-off: volatile guarantees VISIBILITY and ORDERING but NOT ATOMICITY. `volatile counter++` is
//            still a race, because it is still read-modify-write. Use volatile for flags and for
//            publishing immutable objects; use atomics or locks when you must also update safely.
// NOTE     : The broken case below is timing- and JIT-dependent. A watchdog bounds it so this file
//            always terminates, and it reports honestly whether the bug reproduced on this run.

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class VolatileVisibility {

    // NOT volatile - a write from one thread may never become visible to another.
    static boolean plainFlag = false;

    // volatile - every read goes to main memory, every write is published there.
    static volatile boolean volatileFlag = false;

    // Also not volatile, and NOT atomic even if it were - see section 3.
    static volatile int volatileCounter = 0;

    public static void main(String[] args) throws InterruptedException {

        // ---------------------------------------------------------------
        System.out.println("1. THE BUG - a worker that may never see a plain flag change");

        Thread worker = new Thread(() -> {
            long spins = 0;
            // The JIT is entitled to hoist `plainFlag` into a register, because nothing in this loop
            // could change it as far as single-threaded analysis can tell. The loop then reads a
            // register forever and never notices main memory.
            while (!plainFlag) {
                spins++;
                if (spins == Long.MAX_VALUE) break;   // unreachable; just stops the loop being empty
            }
            System.out.println("      worker exited after " + spins + " spins");
        }, "plain-worker");
        worker.setDaemon(true);                        // so a stuck worker cannot block JVM exit
        worker.start();

        Thread.sleep(300);                             // let the JIT compile and optimize the loop
        plainFlag = true;                              // the write that may never be seen
        System.out.println("      main set plainFlag = true");

        worker.join(1500);                             // WATCHDOG - never wait forever
        if (worker.isAlive()) {
            System.out.println("      [!] worker is STILL SPINNING after 1.5s");
            System.out.println("      -> visibility failure reproduced: the write is invisible to it");
        } else {
            System.out.println("      (the worker did see it this run - this bug is JIT- and");
            System.out.println("       timing-dependent, which is exactly what makes it dangerous:");
            System.out.println("       it can pass every test and hang in production)");
        }

        // ---------------------------------------------------------------
        System.out.println();
        System.out.println("2. THE FIX - volatile");

        Thread fixedWorker = new Thread(() -> {
            long spins = 0;
            // A volatile read cannot be cached in a register. Each iteration genuinely re-reads it.
            while (!volatileFlag) {
                spins++;
            }
            System.out.println("      volatile worker exited after " + spins + " spins");
        }, "volatile-worker");
        fixedWorker.setDaemon(true);
        fixedWorker.start();

        Thread.sleep(300);
        volatileFlag = true;
        System.out.println("      main set volatileFlag = true");

        fixedWorker.join(1500);
        System.out.println("      worker still alive? " + fixedWorker.isAlive()
                + "   <- false means it saw the change immediately");

        // ---------------------------------------------------------------
        System.out.println();
        System.out.println("3. WHAT volatile DOES NOT FIX - it is not atomic");

        int threads = 4, perThread = 50_000;
        Thread[] pool = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            pool[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) {
                    // Still READ-MODIFY-WRITE. volatile makes each read and each write visible, but
                    // the three steps are not one operation, so updates are still lost.
                    volatileCounter++;
                }
            });
        }
        for (Thread t : pool) t.start();
        for (Thread t : pool) t.join();

        int expected = threads * perThread;
        System.out.println("      expected " + expected + ", got " + volatileCounter
                + (volatileCounter == expected ? "" : "  <-- LOST " + (expected - volatileCounter)));
        System.out.println("      volatile fixed VISIBILITY but not ATOMICITY.");

        AtomicInteger atomic = new AtomicInteger();
        Thread[] pool2 = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            pool2[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) atomic.incrementAndGet();
            });
        }
        for (Thread t : pool2) t.start();
        for (Thread t : pool2) t.join();
        System.out.println("      AtomicInteger gave " + atomic.get() + "  <- correct, because CAS");
        System.out.println("      makes read-modify-write a single atomic operation.");

        // ---------------------------------------------------------------
        System.out.println();
        System.out.println("4. SAFE PUBLICATION - the other job volatile does");
        System.out.println("      A volatile write is a RELEASE; a volatile read is an ACQUIRE.");
        System.out.println("      Everything written BEFORE the volatile write is guaranteed visible");
        System.out.println("      to any thread that reads it afterwards. That is why publishing an");
        System.out.println("      immutable object through a volatile field is safe:");
        System.out.println("        config = new Config(host, port);   // fully built, then published");
        System.out.println("      Without volatile, another thread could see the reference before the");
        System.out.println("      object's fields - a partially constructed object.");
    }
}

/* ------------------------------- WHY THIS HAPPENS -------------------------------
 * Two independent mechanisms can hide a write:
 *   1. CPU CACHES - each core has its own L1/L2. A write may sit in one core's cache and not reach
 *      main memory (or the other core's cache) for an unbounded time.
 *   2. COMPILER / JIT OPTIMIZATION - this is usually the real culprit. Seeing that nothing in the
 *      loop modifies the field, the JIT hoists the read OUT of the loop:
 *          while (!flag) { ... }     becomes     if (!flag) { while (true) { ... } }
 *      That transformation is perfectly legal for single-threaded code, and it turns the loop into
 *      an infinite one.
 *
 * The Java Memory Model does not promise that a write is EVER visible without synchronization. Not
 * "slowly visible" - potentially never.
 *
 * ----------------------------- WHAT volatile GUARANTEES ---------------------------
 * VISIBILITY  - reads go to main memory; writes are published there.
 * ORDERING    - a volatile write is a release barrier, a volatile read an acquire barrier. Everything
 *               written before the write is visible after the read (happens-before).
 * ATOMICITY   - only for a SINGLE read or a SINGLE write. Also makes long/double reads and writes
 *               atomic, which they are not otherwise on 32-bit JVMs.
 *
 * It does NOT give you atomic compound operations: ++, +=, check-then-act are all still races.
 *
 * ------------------------------- WHEN TO USE volatile -----------------------------
 * YES - a stop/shutdown flag written by one thread and read by others.
 *     - safely publishing an immutable object (the double-checked-locking idiom needs it; see
 *       DesignPatterns/Creational/SingletonPattern.java).
 *     - a value written by ONE thread and read by many, where lost updates cannot occur.
 * NO  - counters or accumulators -> AtomicInteger / LongAdder.
 *     - anything needing several fields consistent together -> a lock.
 *
 * ---------------------------- OTHER WAYS TO GET VISIBILITY ------------------------
 * volatile is not the only barrier. All of these establish happens-before:
 *   - entering/exiting a `synchronized` block
 *   - the atomics (AtomicInteger and friends)
 *   - final fields, once the constructor completes
 *   - Thread.start() and Thread.join()
 *   - the concurrent collections
 * So a field only accessed inside synchronized blocks does not also need to be volatile.
 * ------------------------------------------------------------------------------------ */
