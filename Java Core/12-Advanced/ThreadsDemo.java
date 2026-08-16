// Problem  : Run work concurrently with threads, and coordinate access to shared state safely.
// Approach : Create threads (Runnable), start/join them, and use 'synchronized' to prevent race
//            conditions on a shared counter.
// Intuition: Each thread runs independently, so two threads updating the same variable can interleave
//            and lose updates (a "race condition"). A synchronized block enforces one-at-a-time access.
// Time     : concurrency can speed up I/O-bound / parallelizable work   Space: each thread has its own stack
// Trade-off: Concurrency boosts throughput but adds complexity - races, deadlocks, and visibility
//            bugs. Prefer high-level tools (ExecutorService, concurrent collections, atomics) over
//            raw threads and manual locks in real code.

public class ThreadsDemo {

    // Shared, mutable state accessed by multiple threads.
    static int counter = 0;

    // 'synchronized' makes increments atomic: only one thread executes this at a time.
    static synchronized void increment() {
        counter++; // read-modify-write; without synchronization two threads can clobber each other
    }

    public static void main(String[] args) throws InterruptedException {
        // ---- Basic thread via a lambda (Runnable) ----
        Thread t = new Thread(() -> System.out.println("Hello from " + Thread.currentThread().getName()));
        t.start();      // runs the Runnable on a NEW thread (do not call run() - that stays on this thread)
        t.join();       // wait for t to finish before continuing

        // ---- Race condition demo: two threads each increment 100000 times ----
        Runnable job = () -> { for (int i = 0; i < 100_000; i++) increment(); };
        Thread a = new Thread(job);
        Thread b = new Thread(job);
        a.start(); b.start();
        a.join();  b.join();  // wait for BOTH to finish

        // With 'synchronized' the result is deterministic: exactly 200000.
        // Remove 'synchronized' and this often prints LESS due to lost updates.
        System.out.println("counter = " + counter); // 200000
    }
}
