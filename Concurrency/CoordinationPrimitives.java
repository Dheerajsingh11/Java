// Topic    : The coordination toolkit - CountDownLatch, CyclicBarrier, Semaphore and Phaser.
// Approach : One runnable demo of each, focused on the distinction people actually get wrong:
//            latch vs barrier, and semaphore vs lock.
// Intuition: These are not about MUTUAL EXCLUSION (that is a lock's job) - they are about TIMING
//            and CAPACITY. "Wait until N things finish", "wait until N threads arrive", "let at most
//            N through at a time". Building these by hand with wait/notify is possible and almost
//            always wrong; these are correct, tested and clearer.
// Time     : all O(1) per operation   Space: O(waiting threads)
// Trade-off: Each is single-purpose, so the skill is picking the right one. The classic mistake is
//            reaching for a latch when the coordination needs to REPEAT - latches are one-shot and
//            cannot be reset.
// Real use  : service start-up gating (latch), parallel simulation rounds (barrier), connection and
//            rate limiting (semaphore), multi-phase pipelines (phaser).

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CoordinationPrimitives {

    // ================================================================
    // 1. CountDownLatch - ONE-SHOT: wait until N events have happened
    // ================================================================
    static void countDownLatch() throws InterruptedException {
        System.out.println("1. CountDownLatch - wait for N tasks, then proceed (one-shot)");

        int services = 3;
        CountDownLatch ready = new CountDownLatch(services);

        try (ExecutorService pool = Executors.newFixedThreadPool(services)) {
            for (int i = 1; i <= services; i++) {
                final int id = i;
                pool.submit(() -> {
                    try {
                        Thread.sleep(id * 60L);            // each takes a different time
                        System.out.println("      service-" + id + " ready");
                    } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    finally {
                        // ALWAYS count down in finally. If a task throws before countDown(), the
                        // waiting thread blocks forever - the classic latch bug.
                        ready.countDown();
                    }
                });
            }

            System.out.println("      main is waiting for all services...");
            // await with a TIMEOUT rather than the no-arg version: a hung task then fails visibly
            // instead of hanging the application.
            boolean allReady = ready.await(5, TimeUnit.SECONDS);
            System.out.println("      all ready = " + allReady + " -> application can start");
        }

        System.out.println("      the latch is now at " + ready.getCount()
                + " and CANNOT be reset - that is what 'one-shot' means");
    }

    // ================================================================
    // 2. CyclicBarrier - REUSABLE: N threads wait for EACH OTHER, repeatedly
    // ================================================================
    static void cyclicBarrier() throws InterruptedException {
        System.out.println("2. CyclicBarrier - threads rendezvous each round (reusable)");

        int workers = 3, rounds = 3;
        // The optional barrier ACTION runs once, on the last thread to arrive, before any are
        // released. Ideal for merging results between rounds.
        CyclicBarrier barrier = new CyclicBarrier(workers,
                () -> System.out.println("        --- all arrived, next round ---"));

        try (ExecutorService pool = Executors.newFixedThreadPool(workers)) {
            for (int w = 1; w <= workers; w++) {
                final int id = w;
                pool.submit(() -> {
                    try {
                        for (int round = 1; round <= rounds; round++) {
                            Thread.sleep(id * 30L);        // workers finish at different times
                            System.out.println("      worker-" + id + " finished round " + round);
                            barrier.await();               // block until ALL have finished this round
                        }
                    } catch (InterruptedException | BrokenBarrierException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
        System.out.println("      the SAME barrier served all " + rounds + " rounds");
    }

    // ================================================================
    // 3. Semaphore - CAPACITY: at most N threads inside at once
    // ================================================================
    static void semaphore() throws InterruptedException {
        System.out.println("3. Semaphore - limit concurrent access to a scarce resource");

        int permits = 2, users = 6;
        Semaphore connections = new Semaphore(permits);      // a pool of 2 "connections"
        AtomicInteger concurrent = new AtomicInteger();
        AtomicInteger peak = new AtomicInteger();

        try (ExecutorService pool = Executors.newFixedThreadPool(users)) {
            for (int u = 1; u <= users; u++) {
                final int id = u;
                pool.submit(() -> {
                    try {
                        connections.acquire();               // BLOCKS if no permit is free
                        try {
                            int now = concurrent.incrementAndGet();
                            peak.updateAndGet(p -> Math.max(p, now));
                            System.out.println("      user-" + id + " acquired (in use: " + now + ")");
                            Thread.sleep(80);
                        } finally {
                            concurrent.decrementAndGet();
                            // Release in finally, or a failing task permanently leaks a permit and
                            // the pool shrinks until nothing can proceed.
                            connections.release();
                        }
                    } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                });
            }
        }
        System.out.println("      " + users + " users, but never more than " + peak.get()
                + " at once (limit was " + permits + ")");

        // tryAcquire: do not wait at all - useful for shedding load rather than queueing it.
        Semaphore tight = new Semaphore(1);
        tight.acquire();
        System.out.println("      tryAcquire when exhausted: " + tight.tryAcquire()
                + "  -> reject the request instead of blocking");
        tight.release();
    }

    // ================================================================
    // 4. Phaser - like a barrier, but the party count can CHANGE
    // ================================================================
    static void phaser() throws InterruptedException {
        System.out.println("4. Phaser - a barrier where participants can join and leave");

        Phaser phaser = new Phaser(1);                        // "1" registers the main thread
        try (ExecutorService pool = Executors.newFixedThreadPool(3)) {
            for (int i = 1; i <= 3; i++) {
                final int id = i;
                phaser.register();                            // parties can be added dynamically
                pool.submit(() -> {
                    for (int phase = 1; phase <= 2; phase++) {
                        System.out.println("      task-" + id + " working in phase " + phase);
                        phaser.arriveAndAwaitAdvance();       // wait for everyone in this phase
                    }
                    phaser.arriveAndDeregister();             // leave; others continue without it
                });
            }
            phaser.arriveAndAwaitAdvance();                   // main joins phase 1
            System.out.println("        --- phase 1 complete ---");
            phaser.arriveAndAwaitAdvance();                   // main joins phase 2
            System.out.println("        --- phase 2 complete ---");
            phaser.arriveAndDeregister();
        }
        System.out.println("      a CyclicBarrier cannot do this - its party count is fixed");
    }

    public static void main(String[] args) throws InterruptedException {
        countDownLatch();
        System.out.println();
        cyclicBarrier();
        System.out.println();
        semaphore();
        System.out.println();
        phaser();
    }
}

/* ---------------------------- LATCH vs BARRIER - the usual confusion ----------------------------
 * | | CountDownLatch | CyclicBarrier |
 * | Who waits | usually ONE thread waits for others | the participants wait for EACH OTHER |
 * | Reusable | NO - one-shot, cannot reset | YES - resets automatically each cycle |
 * | Counting | any thread may count down, any number of times | each thread arrives exactly once per cycle |
 * | Action on release | none | optional barrier action runs before release |
 *
 * Rule of thumb: "wait until N THINGS HAPPEN" -> latch. "wait until N THREADS ARRIVE" -> barrier.
 * Needing to repeat the coordination is decisive: a latch cannot be reset, so reaching for one in a
 * loop is the mistake to watch for.
 *
 * ------------------------------- SEMAPHORE vs LOCK ----------------------------------------------
 * A LOCK grants exclusive access to ONE thread and is REENTRANT and OWNED - the holder must release it.
 * A SEMAPHORE has N permits, is NOT owned, and one thread may release a permit acquired by another.
 * A binary semaphore (1 permit) resembles a lock but is NOT reentrant: acquiring twice in the same
 * thread deadlocks it against itself. Use a lock for mutual exclusion, a semaphore for CAPACITY.
 *
 * --------------------------------- CHOOSING ONE -------------------------------------------------
 * CountDownLatch  wait for start-up/shutdown, or for N results. One-shot.
 * CyclicBarrier   repeated rounds - simulations, iterative algorithms, batch phases.
 * Semaphore       bound concurrency - connection pools, rate limiting, throttling an API.
 * Phaser          like a barrier but participants join/leave; multi-stage pipelines.
 * Exchanger       two threads swap objects at a rendezvous point.
 *
 * ---------------------------------- THE TWO TRAPS -----------------------------------------------
 * 1. Not counting down / releasing in a `finally`. A task that throws first leaves waiters blocked
 *    forever - and it will look like a deadlock with no locks involved.
 * 2. Using the no-argument await(). Always prefer await(timeout, unit) so a stuck participant fails
 *    loudly rather than hanging the system silently.
 * ------------------------------------------------------------------------------------------------- */
