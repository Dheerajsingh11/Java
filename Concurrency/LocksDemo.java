// Topic    : Explicit locks - ReentrantLock, ReadWriteLock and StampedLock, versus synchronized.
// Approach : Show what each explicit lock does that `synchronized` cannot: tryLock, timeouts,
//            interruptible acquisition, fairness, multiple conditions, and reader/writer separation.
// Intuition: `synchronized` is a good default - it cannot leak, because the JVM releases it on exit.
//            Its limitation is that it is ALL-OR-NOTHING: you wait indefinitely, uninterruptibly,
//            with no way to ask "is it free?". Explicit locks trade the safety of automatic release
//            for control over how you wait.
// Time     : uncontended, both are comparable; ReadWriteLock wins when reads vastly outnumber writes
// Space    : O(1)
// Trade-off: An explicit lock MUST be released in a `finally`, or an exception leaks it and every
//            other thread blocks forever. That single risk is why `synchronized` remains the right
//            default - reach for a Lock only when you need one of the capabilities below.
// Real use  : cache implementations (ReadWriteLock), bounded buffers (Condition), transfer
//            operations (tryLock to avoid deadlock), any lock acquisition that must time out.

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

public class LocksDemo {

    // ================================================================
    // 1. What ReentrantLock can do that synchronized cannot
    // ================================================================
    static void reentrantLockCapabilities() throws InterruptedException {
        System.out.println("1. ReentrantLock - the four capabilities synchronized lacks");
        ReentrantLock lock = new ReentrantLock();

        // (a) tryLock - test without committing to wait. Impossible with synchronized.
        lock.lock();
        try {
            Thread other = new Thread(() -> {
                System.out.println("      (a) tryLock while held  : " + lock.tryLock());
                System.out.println("          -> can take an alternative path instead of blocking");
            });
            other.start();
            other.join();
        } finally {
            lock.unlock();
        }

        // (b) tryLock with a TIMEOUT - wait, but only so long.
        lock.lock();
        try {
            Thread other = new Thread(() -> {
                try {
                    boolean got = lock.tryLock(100, TimeUnit.MILLISECONDS);
                    System.out.println("      (b) tryLock(100ms)      : " + got + " (gave up rather than hang)");
                    if (got) lock.unlock();
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
            other.start();
            other.join();
        } finally {
            lock.unlock();
        }

        // (c) INTERRUPTIBLE acquisition. A thread blocked on synchronized cannot be interrupted at
        //     all - it is stuck until the lock frees, which makes clean shutdown impossible.
        lock.lock();
        try {
            Thread waiter = new Thread(() -> {
                try {
                    lock.lockInterruptibly();
                    lock.unlock();
                } catch (InterruptedException e) {
                    System.out.println("      (c) lockInterruptibly   : interrupted while waiting - can abort");
                }
            });
            waiter.start();
            Thread.sleep(50);
            waiter.interrupt();                  // would be IGNORED if it were blocked on synchronized
            waiter.join();
        } finally {
            lock.unlock();
        }

        // (d) FAIRNESS - longest-waiting thread goes first. Prevents starvation, costs throughput.
        ReentrantLock fair = new ReentrantLock(true);
        System.out.println("      (d) new ReentrantLock(true) : fair = " + fair.isFair()
                + " (FIFO order; slower, but no thread starves)");

        // Bonus: introspection that synchronized simply does not offer.
        System.out.println("      also: isLocked=" + lock.isLocked()
                + ", holdCount=" + lock.getHoldCount() + ", queued=" + lock.getQueueLength());
    }

    // ================================================================
    // 2. Condition - multiple wait-sets on one lock
    // ================================================================
    static class BoundedBuffer<T> {
        private final Object[] items;
        private int count, putIndex, takeIndex;
        private final ReentrantLock lock = new ReentrantLock();

        // TWO separate conditions on ONE lock. With synchronized there is only one wait-set, so
        // notifyAll() wakes producers and consumers alike and most of them go straight back to sleep.
        private final Condition notFull  = lock.newCondition();
        private final Condition notEmpty = lock.newCondition();

        BoundedBuffer(int capacity) { items = new Object[capacity]; }

        void put(T item) throws InterruptedException {
            lock.lock();
            try {
                while (count == items.length) notFull.await();     // still a while loop, not if
                items[putIndex] = item;
                putIndex = (putIndex + 1) % items.length;
                count++;
                notEmpty.signal();          // wake exactly ONE consumer - not every waiter
            } finally { lock.unlock(); }
        }

        @SuppressWarnings("unchecked")
        T take() throws InterruptedException {
            lock.lock();
            try {
                while (count == 0) notEmpty.await();
                T item = (T) items[takeIndex];
                takeIndex = (takeIndex + 1) % items.length;
                count--;
                notFull.signal();           // wake exactly ONE producer
                return item;
            } finally { lock.unlock(); }
        }
    }

    static void conditionDemo() throws Exception {
        System.out.println("2. Condition - targeted signalling instead of notifyAll()");
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(3);
        try (ExecutorService pool = Executors.newFixedThreadPool(2)) {
            pool.submit(() -> {
                try { for (int i = 1; i <= 6; i++) { buffer.put(i); System.out.println("      put " + i); } }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
            pool.submit(() -> {
                try {
                    for (int i = 1; i <= 6; i++) {
                        Thread.sleep(20);
                        System.out.println("        took " + buffer.take());
                    }
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
        }
        System.out.println("      producers waited on notFull, consumers on notEmpty - no wasted wakeups");
    }

    // ================================================================
    // 3. ReadWriteLock - many readers OR one writer
    // ================================================================
    static class SharedCache {
        private final Map<String, String> map = new HashMap<>();
        private final ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
        final AtomicInteger concurrentReaders = new AtomicInteger();
        final AtomicInteger peakReaders = new AtomicInteger();

        String get(String key) {
            rw.readLock().lock();                    // MANY threads may hold the read lock at once
            try {
                int now = concurrentReaders.incrementAndGet();
                peakReaders.updateAndGet(p -> Math.max(p, now));
                try { Thread.sleep(20); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                return map.get(key);
            } finally {
                concurrentReaders.decrementAndGet();
                rw.readLock().unlock();
            }
        }

        void put(String key, String value) {
            rw.writeLock().lock();                   // EXCLUSIVE - blocks all readers and writers
            try { map.put(key, value); }
            finally { rw.writeLock().unlock(); }
        }
    }

    static void readWriteLockDemo() throws Exception {
        System.out.println("3. ReadWriteLock - concurrent reads, exclusive writes");
        SharedCache cache = new SharedCache();
        cache.put("k", "v");

        try (ExecutorService pool = Executors.newFixedThreadPool(6)) {
            for (int i = 0; i < 6; i++) pool.submit(() -> cache.get("k"));
        }
        System.out.println("      peak concurrent readers: " + cache.peakReaders.get()
                + " (with synchronized this would be 1)");
        System.out.println("      Worth it only when reads GREATLY outnumber writes - the bookkeeping");
        System.out.println("      costs more than a plain lock when writes are frequent.");
    }

    // ================================================================
    // 4. StampedLock - optimistic reading (Java 8+)
    // ================================================================
    static void stampedLockDemo() {
        System.out.println("4. StampedLock - optimistic reads that take no lock at all");
        StampedLock sl = new StampedLock();
        final double[] point = { 3.0, 4.0 };

        // Read WITHOUT locking, then verify nothing changed. If a writer intervened, fall back.
        long stamp = sl.tryOptimisticRead();
        double x = point[0], y = point[1];
        if (!sl.validate(stamp)) {                   // a write happened - the values may be torn
            stamp = sl.readLock();
            try { x = point[0]; y = point[1]; }
            finally { sl.unlockRead(stamp); }
            System.out.println("      optimistic read failed, fell back to a real read lock");
        } else {
            System.out.println("      optimistic read succeeded with NO locking: distance = "
                    + Math.hypot(x, y));
        }
        System.out.println("      Fastest for read-dominated data, but StampedLock is NOT reentrant");
        System.out.println("      and misusing the stamps is easy - use it only when measured.");
    }

    public static void main(String[] args) throws Exception {
        reentrantLockCapabilities();
        System.out.println();
        conditionDemo();
        System.out.println();
        readWriteLockDemo();
        System.out.println();
        stampedLockDemo();
    }
}

/* ------------------------------ synchronized vs Lock ------------------------------
 * | | synchronized | ReentrantLock |
 * | Release | AUTOMATIC on block exit, even on exception | MANUAL - you must use finally |
 * | tryLock | no | yes |
 * | Timeout | no | yes |
 * | Interruptible while waiting | NO | yes |
 * | Fairness | no | optional |
 * | Multiple wait-sets | one, via wait/notify | many, via Condition |
 * | Introspection | none | isLocked, getHoldCount, getQueueLength |
 * | Performance | equal since Java 6 (biased/thin locks) | equal |
 *
 * PREFER synchronized. It cannot leak a lock, and modern JVMs optimize it as well as any Lock. Reach
 * for ReentrantLock when you specifically need tryLock, a timeout, interruptibility, fairness, or
 * more than one condition.
 *
 * ------------------------------- THE ONE BIG RISK ----------------------------------
 *     lock.lock();
 *     doWork();          // if this throws...
 *     lock.unlock();     // ...this never runs, and every other thread blocks forever
 *
 * ALWAYS:
 *     lock.lock();
 *     try { doWork(); } finally { lock.unlock(); }
 *
 * And note lock() goes OUTSIDE the try - if lock() itself throws, there is nothing to unlock.
 *
 * ---------------------------------- REENTRANCY --------------------------------------
 * Both synchronized and ReentrantLock are REENTRANT: a thread already holding the lock may acquire
 * it again (a synchronized method calling another synchronized method on the same object). The hold
 * count must reach zero before another thread gets in, so a lock acquired twice must be released
 * twice. Semaphores and StampedLock are NOT reentrant - acquiring twice deadlocks against yourself.
 *
 * ------------------------------ CHOOSING A LOCK TYPE --------------------------------
 * synchronized         the default; simple mutual exclusion
 * ReentrantLock        need tryLock, timeout, interruptibility, fairness, or Conditions
 * ReadWriteLock        reads GREATLY outnumber writes (a cache); otherwise the overhead loses
 * StampedLock          read-dominated and performance-critical; not reentrant, easy to misuse
 * No lock at all       best of all - see AtomicVariablesDemo and the concurrent collections
 * -------------------------------------------------------------------------------------- */
