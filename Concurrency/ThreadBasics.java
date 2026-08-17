// Topic    : Creating and controlling threads - the foundation everything else builds on.
// Approach : Show the three ways to create a thread, the start() vs run() trap, join(), daemon
//            threads, interruption, and why Thread.stop() no longer exists.
// Intuition: A thread is an independent path of execution with its OWN call stack, sharing the heap
//            with every other thread. That single sentence explains most of concurrency: separate
//            stacks mean local variables are safe, and a shared heap means OBJECT state is not.
// Time     : creating a platform thread costs ~1MB of stack and a system call - it is expensive
// Space    : O(1MB) per platform thread (virtual threads change this - see VirtualThreadBasics)
// Trade-off: Raw threads are the wrong tool for real work: unbounded creation exhausts memory, and
//            there is no built-in way to get a result back or handle failures. Use an
//            ExecutorService (Java Core/12-Advanced/ExecutorServiceDemo.java). These primitives are
//            for understanding what the higher-level tools do.
// NOTE     : This file is deliberately deterministic - every thread is joined, so output order is
//            stable. Real concurrent output is not.

public class ThreadBasics {

    // Way 1: extend Thread. Uses up your single inheritance slot, so it is rarely the right choice.
    static class CounterThread extends Thread {
        private final int limit;
        CounterThread(String name, int limit) { super(name); this.limit = limit; }

        @Override
        public void run() {
            for (int i = 1; i <= limit; i++) {
                System.out.println("      " + getName() + " counting " + i);
            }
        }
    }

    // Way 2: implement Runnable. Preferred - the task is separate from the thread that runs it,
    // so the same Runnable can go to a thread, an executor, or a virtual thread unchanged.
    static class GreetingTask implements Runnable {
        private final String who;
        GreetingTask(String who) { this.who = who; }
        public void run() { System.out.println("      hello from " + who
                + " on " + Thread.currentThread().getName()); }
    }

    public static void main(String[] args) throws InterruptedException {

        System.out.println("1. start() vs run() - the classic mistake");
        Thread t = new Thread(() ->
                System.out.println("      running on: " + Thread.currentThread().getName()));

        t.run();     // NOT a new thread. This is an ordinary method call on the CURRENT thread.
        t.start();   // THIS creates the thread and calls run() on it.
        t.join();
        System.out.println("      run() printed 'main'; start() printed 'Thread-N'.");
        System.out.println("      run() compiles, runs, and silently gives you no concurrency at all.");

        System.out.println("2. three ways to create one");
        Thread a = new CounterThread("counter", 2);           // extends Thread
        Thread b = new Thread(new GreetingTask("task"));      // implements Runnable
        Thread c = new Thread(() -> System.out.println("      hello from a lambda")); // lambda
        a.start(); b.start(); c.start();
        // join() blocks until that thread finishes. Joining all three makes the output deterministic;
        // without it, main could print the next section before these threads even run.
        a.join(); b.join(); c.join();

        System.out.println("3. join() - waiting for a result the crude way");
        final int[] result = new int[1];                       // a one-element array as a mailbox
        Thread worker = new Thread(() -> {
            int sum = 0;
            for (int i = 1; i <= 100; i++) sum += i;
            result[0] = sum;
        });
        worker.start();
        worker.join();                                          // without this, result[0] may still be 0
        System.out.println("      sum 1..100 = " + result[0]);
        System.out.println("      note the awkwardness: a thread cannot RETURN a value.");
        System.out.println("      That is exactly what Callable and CompletableFuture exist to fix.");

        System.out.println("4. thread properties");
        Thread named = new Thread(() -> { }, "worker-1");
        System.out.println("      name     : " + named.getName());
        System.out.println("      priority : " + named.getPriority() + " (1-10; the OS may ignore it)");
        System.out.println("      daemon   : " + named.isDaemon());
        System.out.println("      state    : " + named.getState() + " (not started yet)");

        System.out.println("5. daemon threads");
        Thread daemon = new Thread(() -> {
            while (true) {                                      // an infinite loop, deliberately
                try { Thread.sleep(50); } catch (InterruptedException e) { return; }
            }
        });
        daemon.setDaemon(true);   // MUST be set before start(). A daemon does NOT keep the JVM alive.
        daemon.start();
        System.out.println("      started an infinite daemon thread - the JVM will still exit.");
        System.out.println("      A non-daemon thread doing this would hang the program forever.");

        System.out.println("6. interruption - the cooperative way to stop a thread");
        Thread interruptible = new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    // Checking the flag is what makes it cooperative. A thread that never checks
                    // cannot be stopped this way.
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("      worker noticed the interrupt flag and stopped");
                        return;
                    }
                    Thread.sleep(20);
                }
            } catch (InterruptedException e) {
                // sleep/wait/join throw InterruptedException AND CLEAR the flag. Restoring it is the
                // correct thing to do so callers further up can also see the interruption.
                Thread.currentThread().interrupt();
                System.out.println("      worker was interrupted while sleeping");
            }
        });
        interruptible.start();
        Thread.sleep(60);
        interruptible.interrupt();     // a REQUEST to stop, not a command
        interruptible.join();

        System.out.println("7. why Thread.stop() was removed");
        System.out.println("      stop() killed a thread instantly, wherever it was - possibly");
        System.out.println("      halfway through updating a shared object, leaving it corrupted");
        System.out.println("      with its locks released. It was deprecated for being unfixable.");
        System.out.println("      Interruption is cooperative: the thread chooses a safe point to stop.");

        System.out.println("done - main exits, and the daemon thread dies with it");
    }
}

/* ----------------------------- WHAT A THREAD ACTUALLY IS -----------------------------
 * Each thread gets its OWN call stack (local variables, method frames) but SHARES the heap with
 * every other thread. That asymmetry is the root of everything:
 *   - Local variables are automatically thread-safe - nobody else can see your stack.
 *   - Objects on the heap are shared, so unsynchronized access races (see RaceConditionDemo).
 *
 * ------------------------------- THE COST OF A THREAD ---------------------------------
 * A platform thread maps 1:1 to an OS thread: roughly 1MB of reserved stack plus kernel scheduling
 * structures. Creating thousands is not viable, which is why thread POOLS exist - and why virtual
 * threads (Java 21) were added for workloads that need tens of thousands of concurrent blocking
 * tasks. See VirtualThreadScaling.java for the measured difference.
 *
 * ---------------------------------- THREE TRAPS ---------------------------------------
 * 1. run() instead of start() - no concurrency, no error, no warning.
 * 2. setDaemon() after start() - throws IllegalThreadStateException; it must be set first.
 * 3. Swallowing InterruptedException - catching it and doing nothing DISCARDS the cancellation
 *    request, and code above you never learns it was asked to stop. Either rethrow it or restore
 *    the flag with Thread.currentThread().interrupt().
 * ---------------------------------------------------------------------------------------- */
