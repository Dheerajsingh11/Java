// Problem  : Run many tasks concurrently without creating and managing threads by hand.
// Approach : Use an ExecutorService thread POOL - submit tasks, collect results via Future, and shut
//            the pool down cleanly.
// Intuition: A thread is an expensive OS resource (~1MB of stack, plus scheduling cost). Creating one
//            per task wastes most of the time on setup and can exhaust memory under load. A pool
//            creates a FIXED number of threads once and feeds them a queue of tasks, so the cost is
//            paid once and concurrency stays bounded.
// Time     : ~ (total work) / (pool size) for parallelizable work   Space: O(pool size + queued tasks)
// Trade-off: Pools add lifecycle management (you MUST shut them down) but remove the two biggest raw
//            -thread hazards: unbounded thread creation and manual result plumbing. This is the API
//            to reach for in real code - raw `new Thread(...)` is for learning.

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorServiceDemo {
    public static void main(String[] args) throws Exception {

        // A fixed pool of 4 threads. Submitting 8 tasks does NOT create 8 threads - the extra tasks
        // wait in the pool's queue until a thread frees up. That bounding is the whole point.
        ExecutorService pool = Executors.newFixedThreadPool(4);

        // ---- submit() returns a Future: a handle to a result that is not ready yet ----
        List<Future<Integer>> futures = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            final int n = i;
            futures.add(pool.submit(() -> {          // Callable<Integer> - it can RETURN a value
                Thread.sleep(100);                    // stand-in for real work (I/O, computation)
                return n * n;
            }));
        }

        int total = 0;
        for (Future<Integer> f : futures) {
            total += f.get();      // BLOCKS until this task finishes, then yields its result
        }
        System.out.println("sum of squares 1..8 = " + total);   // expected: 204

        // ---- AtomicInteger: lock-free shared counter ----
        // Unlike counter++ (a read-modify-write race), incrementAndGet is a single atomic operation,
        // so no synchronization is needed and there is no deadlock risk.
        AtomicInteger counter = new AtomicInteger();
        List<Future<?>> tasks = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            tasks.add(pool.submit(() -> { for (int j = 0; j < 25_000; j++) counter.incrementAndGet(); }));
        }
        for (Future<?> f : tasks) f.get();
        System.out.println("atomic counter = " + counter.get());  // expected: 100000, always

        // ---- SHUTDOWN is mandatory ----
        // Pool threads are non-daemon by default, so without this the JVM never exits.
        pool.shutdown();                                   // stop accepting new tasks, finish queued
        if (!pool.awaitTermination(5, TimeUnit.SECONDS)) { // wait, with a bound
            pool.shutdownNow();                            // interrupt whatever is still running
        }
        System.out.println("pool terminated: " + pool.isTerminated());
    }
}

/* ------------------------------- CHOOSING A POOL -------------------------------
 * Executors.newFixedThreadPool(n)   bounded parallelism. The safe default.
 *                                   For CPU-bound work, n ~ availableProcessors().
 *                                   For I/O-bound work, n can be much larger (threads mostly wait).
 * Executors.newSingleThreadExecutor()  tasks run one at a time, in order - a simple way to confine
 *                                   mutable state to one thread instead of locking it.
 * Executors.newCachedThreadPool()   grows without bound - convenient, but a burst of tasks can
 *                                   create thousands of threads. Avoid under untrusted load.
 * Executors.newScheduledThreadPool(n)  delayed and periodic execution.
 * Executors.newVirtualThreadPerTaskExecutor()  (Java 21) virtual threads - extremely cheap, ideal
 *                                   for large numbers of blocking I/O tasks.
 *
 * -------------------------------- Runnable vs Callable --------------------------
 *   Runnable  run()  returns nothing, cannot throw a checked exception
 *   Callable  call() RETURNS a value and may throw - use it whenever you need a result
 *
 * ------------------------------------ PITFALLS ----------------------------------
 *   - Forgetting shutdown() - the JVM hangs on exit.
 *   - Calling future.get() immediately after each submit inside the loop: that BLOCKS on every task
 *     in turn and serializes everything. Submit them all first, then collect (as above).
 *   - Swallowing exceptions: a task's exception surfaces only when you call get(), wrapped in an
 *     ExecutionException. If you never call get(), the failure disappears silently.
 *   - Sizing a CPU-bound pool far above the core count - the threads just contend.
 *
 * ------------------------------- WHEN NOT TO USE --------------------------------
 * Small or inherently sequential work: coordination overhead exceeds the gain. Measure before
 * parallelizing - see the "When NOT to use threads" section in this folder's Note.md.
 * -------------------------------------------------------------------------------- */
