// Topic    : Fork/Join and parallel streams - divide-and-conquer parallelism for CPU-bound work.
// Approach : A RecursiveTask that splits an array until a threshold, then parallel streams (which
//            are built on the same pool), then the cases where parallelism LOSES.
// Intuition: Fork/Join splits a task into subtasks, runs them on a pool, and joins the results. Its
//            distinguishing feature is WORK STEALING: an idle worker takes tasks from the back of a
//            busy worker's deque, so uneven splits still keep every core busy. Parallel streams are
//            a declarative front-end to exactly this machinery.
// Time     : ideal speedup approaches the core count; real speedup is bounded by Amdahl's law
// Space    : O(depth) for the task tree
// Trade-off: Parallelism is NOT free. Splitting, scheduling and merging all cost, so small or cheap
//            workloads run SLOWER in parallel. This file measures a case where that happens, rather
//            than only showing the happy path.
// Real use  : Arrays.parallelSort, aggregations over large in-memory collections, image and matrix
//            processing, any genuinely CPU-bound divide-and-conquer.

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class ForkJoinAndParallelStreams {

    // ================================================================
    // 1. Fork/Join - a RecursiveTask that sums an array
    // ================================================================
    static class SumTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 10_000;    // below this, do it directly
        private final long[] data;
        private final int from, to;

        SumTask(long[] data, int from, int to) { this.data = data; this.from = from; this.to = to; }

        @Override
        protected Long compute() {
            int length = to - from;

            // BASE CASE. The threshold matters: too small and the splitting overhead dominates; too
            // large and cores sit idle. It is a tuning decision, not a constant to copy blindly.
            if (length <= THRESHOLD) {
                long sum = 0;
                for (int i = from; i < to; i++) sum += data[i];
                return sum;
            }

            int mid = from + length / 2;
            SumTask left = new SumTask(data, from, mid);
            SumTask right = new SumTask(data, mid, to);

            left.fork();                      // schedule the left half for another worker...
            long rightResult = right.compute();   // ...and compute the right half on THIS thread
            long leftResult = left.join();        // then wait for the left

            // Why not fork() both? Forking one and computing the other keeps the current thread
            // busy instead of parking it, which is the recommended Fork/Join idiom.
            return leftResult + rightResult;
        }
    }

    // Deliberately CPU-heavy per element: no memory traffic, just arithmetic. This is the shape of
    // work parallelism actually accelerates.
    static long expensive(int seed) {
        long acc = seed;
        for (int i = 0; i < 500; i++) {
            acc = (acc * 31 + i) % 1_000_003;
        }
        return acc;
    }

    public static void main(String[] args) {

        long[] data = new long[4_000_000];
        for (int i = 0; i < data.length; i++) data[i] = i % 100;

        System.out.println("Available cores: " + Runtime.getRuntime().availableProcessors());
        System.out.println();
        System.out.println("PART 1 - summing " + data.length + " longs (memory-bound work)");

        // ---- sequential baseline ----
        long t0 = System.currentTimeMillis();
        long sequential = 0;
        for (long v : data) sequential += v;
        long seqMs = System.currentTimeMillis() - t0;
        System.out.printf("  %-28s %,15d  %5d ms%n", "sequential loop", sequential, seqMs);

        // ---- Fork/Join ----
        t0 = System.currentTimeMillis();
        long forkJoin;
        try (ForkJoinPool pool = new ForkJoinPool()) {
            forkJoin = pool.invoke(new SumTask(data, 0, data.length));
        }
        long fjMs = System.currentTimeMillis() - t0;
        System.out.printf("  %-28s %,15d  %5d ms%n", "ForkJoin RecursiveTask", forkJoin, fjMs);

        // ---- parallel stream: the same machinery, far less code ----
        t0 = System.currentTimeMillis();
        long parallel = Arrays.stream(data).parallel().sum();
        long parMs = System.currentTimeMillis() - t0;
        System.out.printf("  %-28s %,15d  %5d ms%n", "parallel stream", parallel, parMs);

        System.out.println();
        System.out.println("  all three agree: " + (sequential == forkJoin && forkJoin == parallel));
        System.out.printf("  parallel speedup: %.1fx%n", (double) seqMs / Math.max(1, parMs));
        System.out.println();
        System.out.println("  EXPECT THIS TO BE ~1x OR WORSE, and that is the lesson: adding one");
        System.out.println("  long per element is MEMORY-BANDWIDTH-bound, not CPU-bound. Every core");
        System.out.println("  competes for the same memory bus, so extra threads cannot help - they");
        System.out.println("  only add splitting and merging cost. Parallelism speeds up COMPUTATION,");
        System.out.println("  not memory traffic.");

        // ================================================================
        // PART 2 - genuinely CPU-BOUND work, where parallelism does pay
        // ================================================================
        System.out.println();
        System.out.println("PART 2 - the same element count, but real computation per element");

        t0 = System.currentTimeMillis();
        long seqHeavy = IntStream.range(0, 200_000).mapToLong(ForkJoinAndParallelStreams::expensive).sum();
        long seqHeavyMs = System.currentTimeMillis() - t0;

        t0 = System.currentTimeMillis();
        long parHeavy = IntStream.range(0, 200_000).parallel()
                .mapToLong(ForkJoinAndParallelStreams::expensive).sum();
        long parHeavyMs = System.currentTimeMillis() - t0;

        System.out.printf("  %-28s %,15d  %5d ms%n", "sequential (CPU-bound)", seqHeavy, seqHeavyMs);
        System.out.printf("  %-28s %,15d  %5d ms%n", "parallel   (CPU-bound)", parHeavy, parHeavyMs);
        System.out.println("  results agree: " + (seqHeavy == parHeavy));
        System.out.printf("  speedup: %.1fx  <- THIS is where parallelism earns its keep%n",
                (double) seqHeavyMs / Math.max(1, parHeavyMs));

        // ================================================================
        System.out.println();
        System.out.println("WHERE PARALLELISM LOSES");

        // (a) Too little work - overhead dominates.
        int[] small = IntStream.range(0, 1_000).toArray();
        t0 = System.nanoTime();
        int smallSeq = Arrays.stream(small).sum();
        long smallSeqNs = System.nanoTime() - t0;
        t0 = System.nanoTime();
        int smallPar = Arrays.stream(small).parallel().sum();
        long smallParNs = System.nanoTime() - t0;
        System.out.printf("  (a) 1,000 elements  sequential %,8d ns | parallel %,8d ns  %s%n",
                smallSeqNs, smallParNs, smallParNs > smallSeqNs ? "<- parallel LOST" : "<- noisy");
        System.out.println("      At this size the numbers are dominated by JIT warm-up and timer");
        System.out.println("      noise, so either can 'win' - which is itself the point: a single");
        System.out.println("      untimed run proves nothing. Use JMH for claims this small.");

        // (b) A source that splits badly. LinkedList has no random access, so the framework cannot
        //     divide it evenly - it must walk it. ArrayList and arrays split in O(1).
        List<Integer> linked = new java.util.LinkedList<>();
        for (int i = 0; i < 200_000; i++) linked.add(i);
        List<Integer> arrayList = new java.util.ArrayList<>(linked);

        t0 = System.currentTimeMillis();
        long linkedSum = linked.parallelStream().mapToLong(Integer::longValue).sum();
        long linkedMs = System.currentTimeMillis() - t0;
        t0 = System.currentTimeMillis();
        long arraySum = arrayList.parallelStream().mapToLong(Integer::longValue).sum();
        long arrayMs = System.currentTimeMillis() - t0;
        System.out.printf("  (b) LinkedList %,3d ms | ArrayList %,3d ms   (sums equal: %s)%n",
                linkedMs, arrayMs, linkedSum == arraySum);
        System.out.println("      a source that cannot be split cheaply cannot parallelize well");

        // (c) Ordered operations force extra coordination.
        t0 = System.currentTimeMillis();
        LongStream.range(0, 5_000_000).parallel().boxed().limit(100).count();
        long orderedMs = System.currentTimeMillis() - t0;
        System.out.printf("  (c) parallel + boxing + limit: %,d ms%n", orderedMs);
        System.out.println("      limit() and other ORDERED operations force coordination between");
        System.out.println("      threads, and boxing allocates - both erode the parallel gain");

        // (d) The shared common pool.
        System.out.println();
        System.out.println("  (d) parallel streams use the SHARED common ForkJoinPool:");
        System.out.println("      parallelism = " + ForkJoinPool.commonPool().getParallelism()
                + " (cores - 1), shared by the WHOLE application.");
        System.out.println("      One long or BLOCKING task there starves every other parallel");
        System.out.println("      stream in the JVM. Never do blocking I/O in a parallel stream -");
        System.out.println("      use a dedicated executor, or virtual threads.");
    }
}

/* -------------------------------- WORK STEALING ---------------------------------
 * Each worker keeps a double-ended queue of tasks. It pushes and pops its OWN work from the head
 * (cache-friendly, no contention). When it runs out, it STEALS from the TAIL of another worker's
 * deque - taking the oldest, and usually largest, task.
 *
 * Two consequences: contention is rare (the thief and the owner touch opposite ends), and uneven
 * splits self-correct, because idle threads find work rather than waiting. That is why Fork/Join
 * handles irregular problems - tree traversal, sparse matrices - better than a fixed pool with a
 * single shared queue.
 *
 * -------------------------- WHEN A PARALLEL STREAM PAYS ---------------------------
 * All of these should hold. If any fails, measure before trusting it:
 *   1. LARGE N - typically 10,000+ elements.
 *   2. EXPENSIVE per element, or a very large N of cheap ones.
 *   3. A SPLITTABLE source - arrays, ArrayList, IntStream.range. Not LinkedList, not Iterator.
 *   4. CPU-BOUND work - no I/O, no blocking, no locks.
 *   5. STATELESS, side-effect-free lambdas - no shared mutable state.
 *   6. Independent, associative operations - reduce and sum qualify; order-dependent folds do not.
 *
 * -------------------------------- AMDAHL'S LAW -----------------------------------
 * Speedup is capped by the SEQUENTIAL fraction. If 10% of a program cannot be parallelized, the
 * maximum possible speedup is 10x no matter how many cores you add. This is why "just add threads"
 * so often disappoints.
 *
 * ------------------------------- FORK/JOIN vs STREAMS ------------------------------
 * Parallel streams are simply a declarative API over Fork/Join. Prefer them for readability. Drop to
 * a RecursiveTask when you need control over the split threshold or the recursion shape, or when
 * the problem is not naturally a stream (tree recursion, matrix blocks).
 * ------------------------------------------------------------------------------------ */
