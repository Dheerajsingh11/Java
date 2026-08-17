// Topic    : The concurrent collections - thread-safe containers that do not need external locking.
// Approach : Show why a plain HashMap corrupts under concurrency, what ConcurrentHashMap does
//            instead, its atomic compound operations, CopyOnWriteArrayList, and the concurrent queues.
// Intuition: Wrapping a HashMap in synchronized makes it CORRECT but not SCALABLE - every thread
//            queues on one lock, so more cores add nothing. ConcurrentHashMap locks only the bucket
//            being touched, so unrelated keys proceed in parallel. That difference between "safe"
//            and "safe and concurrent" is the whole point of the package.
// Time     : ConcurrentHashMap get/put O(1) average with far less contention than a synchronized map
// Space    : O(n); CopyOnWriteArrayList is O(n) PER WRITE, since it copies the whole array
// Trade-off: Each collection targets a specific read/write mix. Choosing wrongly is expensive:
//            CopyOnWriteArrayList is superb for read-mostly data and catastrophic for write-heavy
//            data, because every single write allocates a full copy.
// Real use  : caches and registries (ConcurrentHashMap), listener lists (CopyOnWriteArrayList),
//            work queues (BlockingQueue), rate limiters and metrics.

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentCollectionsDemo {

    public static void main(String[] args) throws Exception {

        // ---------------------------------------------------------------
        System.out.println("1. A plain HashMap under concurrent writes");
        // WATCHDOG REQUIRED. Corrupting a HashMap concurrently does not merely produce a wrong size -
        // a concurrent resize can leave a CYCLE in a bucket, after which put/get spins forever at
        // 100% CPU. That is not a hypothetical: it hung this very file during development.
        // The work therefore runs on DAEMON threads behind a timeout, so the demo always terminates.
        Map<Integer, Integer> unsafe = new HashMap<>();
        Thread corrupter = new Thread(() -> {
            Thread[] writers = new Thread[4];
            for (int t = 0; t < 4; t++) {
                final int base = t * 25_000;
                writers[t] = new Thread(() -> {
                    for (int i = 0; i < 25_000; i++) unsafe.put(base + i, i);
                });
                writers[t].setDaemon(true);
                writers[t].start();
            }
            for (Thread w : writers) {
                try { w.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
            }
        });
        corrupter.setDaemon(true);
        corrupter.start();
        corrupter.join(3000);            // bounded wait - never hang

        if (corrupter.isAlive()) {
            System.out.println("      [watchdog] writers are STILL RUNNING after 3s");
            System.out.println("      -> the HashMap has corrupted itself into an infinite loop.");
            System.out.println("         This is the real failure mode, not just a wrong count.");
        } else {
            System.out.println("      expected 100000 entries, got " + unsafe.size()
                    + (unsafe.size() == 100_000 ? "  (no corruption this run)" : "  <-- CORRUPTED"));
        }
        System.out.println("      Either way the map is unusable: lost entries at best, an infinite");
        System.out.println("      loop at worst. A HashMap must never be written from many threads.");

        // ---------------------------------------------------------------
        System.out.println("2. ConcurrentHashMap - correct AND concurrent");
        Map<Integer, Integer> safe = new ConcurrentHashMap<>();
        try (ExecutorService pool = Executors.newFixedThreadPool(4)) {
            for (int t = 0; t < 4; t++) {
                final int base = t * 25_000;
                pool.submit(() -> { for (int i = 0; i < 25_000; i++) safe.put(base + i, i); });
            }
        }
        System.out.println("      entries = " + safe.size() + "  (always correct)");

        // ---------------------------------------------------------------
        System.out.println("3. Why the ATOMIC compound methods matter");
        // BROKEN: two separate operations. Another thread can act between the check and the put -
        // the classic check-then-act race. Thread safety per-call does NOT make a sequence safe.
        Map<String, Integer> counts = new ConcurrentHashMap<>();
        AtomicInteger lost = new AtomicInteger();
        try (ExecutorService pool = Executors.newFixedThreadPool(8)) {
            for (int t = 0; t < 8; t++) {
                pool.submit(() -> {
                    for (int i = 0; i < 10_000; i++) {
                        Integer current = counts.get("key");            // read
                        counts.put("key", current == null ? 1 : current + 1);   // write - RACE
                    }
                });
            }
        }
        System.out.println("      get-then-put   : expected 80000, got " + counts.get("key")
                + "  <-- lost updates despite a thread-safe map");

        // CORRECT: one atomic operation, performed under the bucket's lock.
        Map<String, Integer> atomicCounts = new ConcurrentHashMap<>();
        try (ExecutorService pool = Executors.newFixedThreadPool(8)) {
            for (int t = 0; t < 8; t++) {
                pool.submit(() -> {
                    for (int i = 0; i < 10_000; i++) atomicCounts.merge("key", 1, Integer::sum);
                });
            }
        }
        System.out.println("      merge()        : expected 80000, got " + atomicCounts.get("key")
                + "  <- correct");
        System.out.println("      Also atomic: computeIfAbsent, computeIfPresent, compute, putIfAbsent.");

        // ---------------------------------------------------------------
        System.out.println("4. computeIfAbsent - the standard lazy-cache idiom");
        Map<String, String> cache = new ConcurrentHashMap<>();
        String v1 = cache.computeIfAbsent("user:1", k -> { System.out.println("      loading " + k); return "Asha"; });
        String v2 = cache.computeIfAbsent("user:1", k -> { System.out.println("      NOT loaded again"); return "?"; });
        System.out.println("      " + v1 + " / " + v2 + "  - the loader runs once per key");

        // ---------------------------------------------------------------
        System.out.println("5. CopyOnWriteArrayList - for read-mostly data");
        List<String> listeners = new CopyOnWriteArrayList<>(List.of("email", "sms"));
        // Iteration works on a SNAPSHOT, so no ConcurrentModificationException even while modifying.
        for (String listener : listeners) {
            if (listener.equals("email")) listeners.add("push");   // legal here; would throw on ArrayList
        }
        System.out.println("      modified during iteration without exception: " + listeners);
        System.out.println("      Each write copies the WHOLE array - O(n) per write.");
        System.out.println("      Perfect for listener lists (rare writes, frequent iteration);");
        System.out.println("      terrible for anything write-heavy.");

        // ---------------------------------------------------------------
        System.out.println("6. The concurrent queues");
        BlockingQueue<String> blocking = new ArrayBlockingQueue<>(2);
        blocking.put("a"); blocking.put("b");
        System.out.println("      ArrayBlockingQueue full, offer with timeout: "
                + blocking.offer("c", 50, TimeUnit.MILLISECONDS) + "  <- back-pressure");

        ConcurrentLinkedQueue<String> nonBlocking = new ConcurrentLinkedQueue<>();
        nonBlocking.add("x");
        System.out.println("      ConcurrentLinkedQueue poll: " + nonBlocking.poll()
                + ", empty poll returns " + nonBlocking.poll() + " (never blocks)");

        // ---------------------------------------------------------------
        System.out.println("7. Sorted and skip-list based");
        ConcurrentSkipListMap<Integer, String> sorted = new ConcurrentSkipListMap<>();
        sorted.put(30, "c"); sorted.put(10, "a"); sorted.put(20, "b");
        System.out.println("      ConcurrentSkipListMap keeps order: " + sorted);
        System.out.println("      firstKey=" + sorted.firstKey() + " ceilingKey(15)=" + sorted.ceilingKey(15));
        System.out.println("      (ConcurrentHashMap cannot do range or nearest queries at all)");

        // ---------------------------------------------------------------
        System.out.println("8. Weakly consistent iterators");
        Map<String, Integer> live = new ConcurrentHashMap<>(Map.of("a", 1, "b", 2));
        Iterator<String> it = live.keySet().iterator();
        live.put("c", 3);                       // modified DURING iteration
        int seen = 0;
        while (it.hasNext()) { it.next(); seen++; }
        System.out.println("      iterated " + seen + " keys while the map grew - no exception.");
        System.out.println("      Concurrent iterators are WEAKLY CONSISTENT: they never throw, but");
        System.out.println("      may or may not reflect changes made after they were created.");
        System.out.println("      Plain collections are FAIL-FAST and throw ConcurrentModificationException.");
    }
}

/* ------------------------------ CHOOSING A COLLECTION ------------------------------
 * | Need | Use | Why |
 * | Thread-safe map | ConcurrentHashMap | per-bucket locking; scales with cores |
 * | Sorted thread-safe map | ConcurrentSkipListMap | ordering + range queries |
 * | Read-mostly list (listeners) | CopyOnWriteArrayList | snapshot iteration; writes are O(n) |
 * | Producer-consumer handoff | ArrayBlockingQueue / LinkedBlockingQueue | blocking + back-pressure |
 * | Non-blocking queue | ConcurrentLinkedQueue | lock-free; poll returns null when empty |
 * | Priority ordering | PriorityBlockingQueue | unbounded, ordered by priority |
 * | Thread-safe set | ConcurrentHashMap.newKeySet() | a set backed by CHM |
 *
 * ---------------------- WHY NOT Collections.synchronizedMap? ------------------------
 * It wraps EVERY method in one lock on the whole map. Correct, but every thread serializes on that
 * single lock, so extra cores buy nothing. ConcurrentHashMap locks only the bucket being written and
 * lets reads proceed without locking at all. The old Hashtable and Vector have the same flaw and
 * should be considered legacy.
 *
 * ------------------------- THE MISTAKE THAT SURVIVES EVERYTHING ----------------------
 * A thread-safe collection makes each INDIVIDUAL call atomic. It does NOT make a SEQUENCE of calls
 * atomic:
 *     if (!map.containsKey(k)) map.put(k, v);      // still a race between the two calls
 * Use the atomic compound methods instead - putIfAbsent, computeIfAbsent, merge, compute, replace -
 * as section 3 demonstrates with 80,000 expected updates and thousands lost.
 *
 * ------------------------------------ WHEN NOT TO USE ---------------------------------
 * - Single-threaded code: HashMap and ArrayList are faster; concurrency machinery is pure overhead.
 * - The whole collection is confined to one thread, or is immutable (List.of, Map.of) - nothing to
 *   protect.
 * - Write-heavy lists: CopyOnWriteArrayList degrades badly. Use a synchronized list or restructure.
 * ---------------------------------------------------------------------------------------- */
