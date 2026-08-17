// Topic    : Race conditions - what they are, why they are so hard to catch, and four ways to fix one.
// Approach : Run the same unsynchronized counter with many threads and SHOW the lost updates, then
//            fix it with synchronized, AtomicInteger, a lock, and by not sharing at all.
// Intuition: `counter++` looks atomic but is three operations - READ, MODIFY, WRITE. Two threads can
//            both read 5, both compute 6, and both store 6. One increment vanishes. Nothing throws;
//            the number is simply wrong, and only sometimes.
// Time     : the fixes cost between ~0 (no sharing) and a lock acquisition per operation
// Space    : O(1)
// Trade-off: Races are the worst class of bug because they are NON-DETERMINISTIC: they pass tests,
//            pass code review, and appear under production load on a machine with more cores. This
//            file makes one reproducible by using enough iterations that the interleaving is
//            near-certain.
// NOTE     : The broken run below prints a number that VARIES between runs. That variation is the
//            entire point - it is what makes these bugs so hard to pin down.

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class RaceConditionDemo {

    private static final int THREADS = 4;
    private static final int INCREMENTS_PER_THREAD = 100_000;
    private static final int EXPECTED = THREADS * INCREMENTS_PER_THREAD;

    // ---- 1. BROKEN: a plain field with no synchronization ----
    static class UnsafeCounter {
        private int count = 0;
        void increment() {
            // Three bytecode operations, not one:
            //     getfield count   -> read
            //     iadd             -> modify
            //     putfield count   -> write
            // A thread can be pre-empted between ANY of them, and another thread then works from a
            // stale value. The write it makes is later overwritten - a "lost update".
            count++;
        }
        int get() { return count; }
    }

    // ---- 2. FIX A: synchronized - mutual exclusion via the object's intrinsic lock ----
    static class SynchronizedCounter {
        private int count = 0;
        // Only one thread may be inside this method at a time. Also gives VISIBILITY: changes made
        // under the lock are guaranteed visible to the next thread that acquires it.
        synchronized void increment() { count++; }
        synchronized int get() { return count; }
    }

    // ---- 3. FIX B: AtomicInteger - lock-free, using a CPU compare-and-swap instruction ----
    static class AtomicCounter {
        private final AtomicInteger count = new AtomicInteger();
        // incrementAndGet is a SINGLE atomic hardware operation. No lock, so no contention queue and
        // no deadlock risk. Usually the fastest option for a simple counter.
        void increment() { count.incrementAndGet(); }
        int get() { return count.get(); }
    }

    // ---- 4. FIX C: explicit lock - same guarantee as synchronized, more control ----
    static class LockCounter {
        private int count = 0;
        private final ReentrantLock lock = new ReentrantLock();
        void increment() {
            lock.lock();
            try { count++; }
            finally { lock.unlock(); }   // ALWAYS in finally - an exception must not leak the lock
        }
        int get() { lock.lock(); try { return count; } finally { lock.unlock(); } }
    }

    // ---- 5. FIX D (best): DO NOT SHARE. Each thread counts privately; combine at the end. ----
    //      No synchronization means no contention, no deadlock, and nothing to get wrong.

    static void runWith(String label, Runnable increment, java.util.function.IntSupplier read)
            throws InterruptedException {
        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) increment.run();
            });
        }
        long start = System.currentTimeMillis();
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();          // wait for all before reading
        long elapsed = System.currentTimeMillis() - start;

        int actual = read.getAsInt();
        System.out.printf("  %-22s expected %d, got %-9d %s  (%d ms)%n",
                label, EXPECTED, actual,
                actual == EXPECTED ? "OK" : "<-- LOST " + (EXPECTED - actual) + " UPDATES",
                elapsed);
    }

    public static void main(String[] args) throws InterruptedException {

        System.out.println(THREADS + " threads x " + INCREMENTS_PER_THREAD
                + " increments = " + EXPECTED + " expected");
        System.out.println();

        UnsafeCounter unsafe = new UnsafeCounter();
        runWith("unsynchronized", unsafe::increment, unsafe::get);

        SynchronizedCounter sync = new SynchronizedCounter();
        runWith("synchronized", sync::increment, sync::get);

        AtomicCounter atomic = new AtomicCounter();
        runWith("AtomicInteger", atomic::increment, atomic::get);

        LockCounter locked = new LockCounter();
        runWith("ReentrantLock", locked::increment, locked::get);

        // FIX D: no sharing at all - each thread accumulates locally.
        long start = System.currentTimeMillis();
        int[] partials = new int[THREADS];
        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            final int index = i;
            // partials[index] is written by exactly ONE thread, so there is no race even though the
            // array itself is shared. Distinct indices are independent.
            threads[i] = new Thread(() -> {
                int local = 0;
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) local++;
                partials[index] = local;
            });
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        int total = 0;
        for (int p : partials) total += p;
        System.out.printf("  %-22s expected %d, got %-9d %s  (%d ms)%n",
                "no sharing", EXPECTED, total, total == EXPECTED ? "OK" : "<-- WRONG",
                System.currentTimeMillis() - start);

        System.out.println();
        System.out.println("Run this again - the unsynchronized number will DIFFER each time.");
        System.out.println("That non-determinism is why race conditions survive code review and");
        System.out.println("test suites, then fail in production on a machine with more cores.");
    }
}

/* -------------------------- WHY count++ IS NOT ATOMIC --------------------------
 * Thread A: read count (5)
 * Thread B: read count (5)          <- B read before A wrote
 * Thread A: compute 6, write 6
 * Thread B: compute 6, write 6      <- overwrites A's work; one increment lost
 *
 * With 400,000 increments across 4 threads there are enormous numbers of opportunities for this
 * interleaving, which is why the demo reliably loses updates. With 100 increments it might well
 * produce the right answer - and that is exactly how these bugs escape testing.
 *
 * ------------------------- CHOOSING BETWEEN THE FIXES ---------------------------
 * | Approach        | Cost              | Use when                                        |
 * | no sharing      | none              | ALWAYS prefer this - partition the work         |
 * | AtomicInteger   | one CAS           | a single counter or reference; lock-free        |
 * | synchronized    | lock acquisition  | several fields must change together             |
 * | ReentrantLock   | lock acquisition  | you need tryLock, timeout, fairness, or interrupt|
 *
 * Preference order: don't share -> immutable -> atomic -> synchronized -> explicit lock.
 * Each step down adds contention and a way to get it wrong.
 *
 * --------------------------- THE OTHER HALF: VISIBILITY -------------------------
 * Mutual exclusion is only half the problem. Even with no interleaving, one thread's write may never
 * become VISIBLE to another, because values can sit in registers or per-core caches.
 * `synchronized`, `volatile` and the atomics all establish happens-before ordering and fix this.
 * See VolatileVisibility.java for a bug you can actually observe.
 * ---------------------------------------------------------------------------------- */
