// Topic    : Atomic variables - lock-free thread safety built on compare-and-swap (CAS).
// Approach : Show AtomicInteger/Long/Reference, the CAS loop underneath, accumulators for high
//            contention, and the ABA problem with its fix.
// Intuition: A lock makes other threads WAIT. CAS instead says "change X from the value I expect to
//            this new value, but only if nobody beat me to it" - a single CPU instruction. If
//            someone did beat you, you retry with the fresh value. No thread ever blocks, so there
//            is no contention queue, no context switch, and no deadlock.
// Time     : O(1) uncontended; retries grow with contention   Space: O(1)
// Trade-off: Faster than locks for a SINGLE variable, but atomics cannot make several fields change
//            together - that still needs a lock. Under very high contention the retry loop can waste
//            more CPU than simply blocking, which is why LongAdder exists.
// Real use  : counters and statistics, non-blocking algorithms, ConcurrentHashMap internals,
//            AtomicReference for lock-free state machines, ID generators.

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.atomic.LongAdder;

public class AtomicVariablesDemo {

    record Config(String host, int port) { }

    public static void main(String[] args) throws Exception {

        // ---------------------------------------------------------------
        System.out.println("1. AtomicInteger - the basics");
        AtomicInteger counter = new AtomicInteger(0);
        System.out.println("      incrementAndGet : " + counter.incrementAndGet());   // 1
        System.out.println("      getAndIncrement : " + counter.getAndIncrement());   // returns 1, now 2
        System.out.println("      addAndGet(10)   : " + counter.addAndGet(10));       // 12
        System.out.println("      get             : " + counter.get());

        // ---------------------------------------------------------------
        System.out.println("2. compareAndSet - the primitive everything else is built on");
        AtomicInteger value = new AtomicInteger(100);
        // "If it is still 100, make it 200." Returns false if another thread changed it first.
        System.out.println("      CAS(100 -> 200) : " + value.compareAndSet(100, 200) + "  now " + value.get());
        System.out.println("      CAS(100 -> 300) : " + value.compareAndSet(100, 300)
                + "  still " + value.get() + "  (expected value no longer matches)");

        // ---------------------------------------------------------------
        System.out.println("3. what incrementAndGet does underneath - a CAS retry loop");
        AtomicInteger manual = new AtomicInteger(0);
        // This is essentially the JDK implementation. Note there is no lock anywhere: a thread that
        // loses the race simply reads the new value and tries again.
        int prev, next;
        do {
            prev = manual.get();
            next = prev + 1;
        } while (!manual.compareAndSet(prev, next));
        System.out.println("      hand-written CAS loop produced " + manual.get());
        System.out.println("      updateAndGet does the same thing for you: "
                + manual.updateAndGet(x -> x * 10));

        // ---------------------------------------------------------------
        System.out.println("4. AtomicReference - CAS on an OBJECT, for lock-free state swaps");
        AtomicReference<Config> config = new AtomicReference<>(new Config("localhost", 8080));
        Config old = config.get();
        boolean swapped = config.compareAndSet(old, new Config("prod.example.com", 443));
        System.out.println("      swapped config: " + swapped + " -> " + config.get());
        // Because Config is IMMUTABLE, readers always see a complete, consistent object - never a
        // half-updated one. Atomics plus immutability is a very effective combination.

        // ---------------------------------------------------------------
        System.out.println("5. contention: AtomicLong vs LongAdder");
        int threads = 8, perThread = 200_000;

        AtomicInteger atomic = new AtomicInteger();
        long t0 = System.currentTimeMillis();
        runConcurrently(threads, perThread, atomic::incrementAndGet);
        long atomicMs = System.currentTimeMillis() - t0;

        LongAdder adder = new LongAdder();
        t0 = System.currentTimeMillis();
        runConcurrently(threads, perThread, adder::increment);
        long adderMs = System.currentTimeMillis() - t0;

        System.out.printf("      AtomicInteger : %7d in %4d ms%n", atomic.get(), atomicMs);
        System.out.printf("      LongAdder     : %7d in %4d ms%n", adder.sum(), adderMs);
        System.out.println("      LongAdder keeps PER-THREAD cells and sums them only when read, so");
        System.out.println("      threads stop fighting over one cache line.");
        System.out.println();
        System.out.println("      HONEST CAVEAT: at this scale the two are usually within noise of");
        System.out.println("      each other, and LongAdder may even look slower - as it may above.");
        System.out.println("      Its advantage appears under SUSTAINED high contention (many cores,");
        System.out.println("      many more threads than cores, updates in a tight loop). A short");
        System.out.println("      microbenchmark like this one is exactly the kind of measurement that");
        System.out.println("      misleads - which is why JMH exists. Treat the guidance below as the");
        System.out.println("      rule, not this timing:");
        System.out.println("        write-heavy counter, read rarely  -> LongAdder");
        System.out.println("        read often, or you need CAS       -> AtomicInteger");

        // ---------------------------------------------------------------
        System.out.println("6. the ABA problem");
        AtomicReference<String> aba = new AtomicReference<>("A");
        String observed = aba.get();                       // thread 1 reads "A"
        aba.set("B");                                      // thread 2 changes it to "B"...
        aba.set("A");                                      // ...and back to "A"
        // Thread 1's CAS SUCCEEDS, because the value matches - but the world changed in between.
        System.out.println("      CAS(A -> C) succeeded: " + aba.compareAndSet(observed, "C"));
        System.out.println("      It could not tell that the value had been B in the meantime.");
        System.out.println("      Harmless for a counter; dangerous for a lock-free stack, where a");
        System.out.println("      popped-and-reused node makes the CAS reattach a stale pointer.");

        System.out.println("      FIX - AtomicStampedReference adds a version stamp:");
        AtomicStampedReference<String> stamped = new AtomicStampedReference<>("A", 0);
        int[] stampHolder = new int[1];
        String seen = stamped.get(stampHolder);
        int seenStamp = stampHolder[0];

        stamped.compareAndSet("A", "B", 0, 1);             // other thread: A -> B, stamp 0 -> 1
        stamped.compareAndSet("B", "A", 1, 2);             // other thread: B -> A, stamp 1 -> 2

        boolean cas = stamped.compareAndSet(seen, "C", seenStamp, seenStamp + 1);
        System.out.println("      stamped CAS succeeded: " + cas + "   <- correctly REJECTED");
        System.out.println("      the value is 'A' again but the stamp moved, so the change is detected");
    }

    static void runConcurrently(int threads, int iterations, Runnable op) throws Exception {
        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                pool.submit(() -> { for (int j = 0; j < iterations; j++) op.run(); });
            }
        }   // close() shuts down and waits for completion
    }
}

/* --------------------------------- WHAT CAS IS ---------------------------------
 * compareAndSet(expected, newValue) maps to a single CPU instruction (CMPXCHG on x86):
 *     "atomically: if this memory location still holds `expected`, store `newValue` and report true;
 *      otherwise change nothing and report false."
 *
 * That one instruction is enough to build every atomic operation. incrementAndGet is a loop:
 * read, compute, try to swap, retry if someone beat you. Because a losing thread retries rather than
 * blocks, the algorithm is LOCK-FREE - at least one thread always makes progress, and there is no
 * deadlock and no priority inversion.
 *
 * ------------------------------ ATOMIC vs synchronized ---------------------------
 * | | Atomic | synchronized |
 * | Blocking | never - retries | yes, threads queue |
 * | Deadlock | impossible | possible |
 * | Scope | ONE variable | any block, several fields |
 * | Best at | low-to-moderate contention | complex invariants |
 *
 * The decisive limitation: atomics protect a SINGLE variable. If two fields must change together
 * (transfer money between accounts), no atomic can express that - you need a lock, or an
 * AtomicReference to an immutable object holding both.
 *
 * -------------------------------- CHOOSING ONE -----------------------------------
 * AtomicInteger/Long   a single counter or flag, read often
 * LongAdder/DoubleAdder  write-heavy counters under contention (metrics) - much faster to update,
 *                      slightly slower to read, and sum() is not an atomic snapshot
 * AtomicReference      swapping an immutable object as a whole
 * AtomicStampedReference  when ABA matters (lock-free stacks, queues)
 * Atomic*FieldUpdater  atomic access to a volatile field of many objects, without per-object overhead
 *
 * -------------------------------- WHEN NOT TO USE --------------------------------
 * - Several variables must change together -> use a lock.
 * - Extremely high contention on one value -> the retry loop burns CPU; use LongAdder or shard.
 * - The state is not really shared -> do not share it at all; that is always the fastest fix.
 * ------------------------------------------------------------------------------------ */
