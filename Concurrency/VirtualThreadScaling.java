// Topic    : Virtual threads (Java 21) - the measured difference against platform threads.
// Approach : Run 10,000 concurrent BLOCKING tasks on both, and time them. Then show why the result
//            is what it is.
// Intuition: A platform thread is a 1:1 wrapper around an OS thread: ~1MB of stack, a system call to
//            create, and the OS scheduler decides when it runs. Blocking one wastes all of that.
//            A virtual thread is managed by the JVM: when it blocks on I/O, the JVM UNMOUNTS it from
//            its carrier thread and runs something else. The stack lives on the heap and grows on
//            demand. Blocking becomes cheap, so you can have millions of them.
// Time     : for N blocking tasks of duration D, virtual threads approach D total; platform threads
//            take roughly D * N / poolSize
// Space    : platform ~1MB each; virtual a few hundred bytes, growing as needed
// Trade-off: Virtual threads help ONLY with blocking (I/O-bound) work. For CPU-bound work they are
//            no faster - you still have a fixed number of cores - and a fixed pool is the right tool.
//            They also do not help if the blocking happens inside `synchronized`, which PINS the
//            virtual thread to its carrier. See the note at the bottom.
// REQUIRES : Java 21+.

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualThreadScaling {

    private static final int TASKS = 10_000;
    private static final int BLOCK_MS = 100;      // stands in for a network or database call

    // Each task blocks - exactly the workload virtual threads were designed for.
    static Runnable blockingTask(AtomicInteger counter) {
        return () -> {
            try {
                Thread.sleep(BLOCK_MS);            // the thread is doing NOTHING but waiting
                counter.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
    }

    static Duration run(String label, ExecutorService executor) throws InterruptedException {
        AtomicInteger done = new AtomicInteger();
        Instant start = Instant.now();

        try (executor) {                                   // try-with-resources shuts down and waits
            for (int i = 0; i < TASKS; i++) {
                executor.submit(blockingTask(done));
            }
        }   // close() calls shutdown() then awaitTermination - all tasks are finished here

        Duration elapsed = Duration.between(start, Instant.now());
        System.out.printf("  %-34s %5d tasks in %5d ms%n", label, done.get(), elapsed.toMillis());
        return elapsed;
    }

    public static void main(String[] args) throws InterruptedException {

        System.out.println(TASKS + " tasks, each blocking for " + BLOCK_MS + "ms");
        System.out.println("Perfect concurrency would finish in ~" + BLOCK_MS + "ms.");
        System.out.println();

        // ---- Platform threads, fixed pool of 200. A common production sizing. ----
        // 10,000 tasks / 200 threads = 50 sequential rounds x 100ms = ~5 seconds.
        // The pool is the bottleneck: 199 threads sit blocked while work waits in the queue.
        Duration fixed = run("platform threads (pool of 200)",
                Executors.newFixedThreadPool(200));

        // ---- Virtual threads: one per task, 10,000 of them. ----
        // When each blocks, the JVM unmounts it, so a handful of carrier threads serve them all.
        Duration virtual = run("virtual threads (one per task)",
                Executors.newVirtualThreadPerTaskExecutor());

        System.out.println();
        System.out.printf("  virtual threads were ~%.1fx faster here%n",
                (double) fixed.toMillis() / Math.max(1, virtual.toMillis()));
        System.out.println("  and they did it WITHOUT any change to the task code - still plain");
        System.out.println("  blocking Thread.sleep(). That is the point: simple blocking code scales.");

        // ---- Why creating 10,000 PLATFORM threads is not an option ----
        System.out.println();
        System.out.println("Could we just create 10,000 platform threads instead?");
        System.out.println("  Each reserves ~1MB of stack -> ~10GB of virtual memory.");
        System.out.println("  Most machines will throw OutOfMemoryError: unable to create native thread.");
        System.out.println("  A virtual thread starts at a few hundred bytes on the HEAP and grows,");
        System.out.println("  which is why millions are feasible.");

        // ---- Creating a few directly, to show the API ----
        System.out.println();
        System.out.println("Creating virtual threads directly:");
        Thread vt = Thread.ofVirtual().name("vt-1").start(() ->
                System.out.println("      running on " + Thread.currentThread()));
        vt.join();
        System.out.println("      isVirtual() = " + vt.isVirtual());

        Thread pt = Thread.ofPlatform().name("pt-1").start(() -> { });
        pt.join();
        System.out.println("      platform thread isVirtual() = " + pt.isVirtual());

        // ---- CPU-bound work: virtual threads bring NO benefit ----
        System.out.println();
        System.out.println("CPU-bound work - virtual threads do NOT help:");
        int cpuTasks = 8;
        Instant s1 = Instant.now();
        try (ExecutorService e = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors())) {
            for (int i = 0; i < cpuTasks; i++) e.submit(VirtualThreadScaling::burnCpu);
        }
        long platformCpu = Duration.between(s1, Instant.now()).toMillis();

        Instant s2 = Instant.now();
        try (ExecutorService e = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < cpuTasks; i++) e.submit(VirtualThreadScaling::burnCpu);
        }
        long virtualCpu = Duration.between(s2, Instant.now()).toMillis();

        System.out.printf("      platform pool: %d ms, virtual: %d ms%n", platformCpu, virtualCpu);
        System.out.println("      Roughly the same - you still only have "
                + Runtime.getRuntime().availableProcessors() + " cores.");
        System.out.println("      Virtual threads remove WAITING, not computation.");
    }

    static void burnCpu() {
        long acc = 0;
        for (int i = 0; i < 8_000_000; i++) acc += i % 7;
        if (acc == Long.MIN_VALUE) System.out.print("");   // stop the JIT eliminating the loop
    }
}

/* --------------------------- HOW VIRTUAL THREADS WORK ---------------------------
 * A virtual thread runs on a CARRIER (a platform thread from a small ForkJoinPool). When it hits a
 * blocking operation the JVM saves its stack to the heap and UNMOUNTS it, freeing the carrier for
 * another virtual thread. When the I/O completes it is remounted, possibly on a different carrier.
 *
 * The result: the number of concurrent tasks is limited by MEMORY, not by OS threads. Blocking is
 * no longer expensive, so "one thread per request" becomes viable again - the programming model
 * that reactive/async frameworks were invented to avoid.
 *
 * ------------------------------- WHEN THEY HELP ----------------------------------
 * YES - I/O-bound work: HTTP calls, database queries, file I/O, message consumers. Anything where
 *       threads spend most of their time waiting.
 * NO  - CPU-bound work: parallelism is capped by cores. Use a fixed pool sized to availableProcessors().
 *
 * -------------------------------- THE PINNING TRAP -------------------------------
 * A virtual thread CANNOT unmount while it is inside a `synchronized` block or a native call. It
 * stays PINNED to its carrier, so a blocking call there consumes a real OS thread - exactly the
 * problem virtual threads were meant to solve, and it can starve the carrier pool.
 *
 * Fix: replace `synchronized` with `ReentrantLock`, which unmounts correctly. Diagnose with
 * -Djdk.tracePinnedThreads=full.
 *
 * ------------------------------- OTHER GUIDANCE ----------------------------------
 * - DO NOT POOL virtual threads. They are cheap to create - pooling them adds contention for no
 *   benefit. Use newVirtualThreadPerTaskExecutor().
 * - Do not use them as a semaphore for limiting concurrency; use an actual Semaphore.
 * - ThreadLocal still works but is more costly at these numbers - prefer scoped values.
 * ------------------------------------------------------------------------------------ */
