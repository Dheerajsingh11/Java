// Topic    : The producer-consumer problem - the canonical coordination pattern.
// Approach : Three implementations of the same thing: raw wait/notify, a BlockingQueue, and
//            wait/notify done WRONG so the classic bugs are visible.
// Intuition: Producers and consumers run at different speeds. A shared bounded buffer decouples
//            them: producers block when it is full, consumers block when it is empty. That
//            back-pressure is the real value - without it a fast producer exhausts memory.
// Time     : O(1) per item   Space: O(buffer capacity) - bounded, which is the point
// Trade-off: Hand-written wait/notify is instructive but easy to get wrong in four specific ways
//            (listed below). BlockingQueue does all of it correctly in one line, and is what you
//            should actually use.
// Real use  : thread pools (a work queue feeding worker threads), Kafka consumers, logging
//            frameworks (an async appender), request pipelines, ETL stages.

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ProducerConsumer {

    private static final int CAPACITY = 5;
    private static final int ITEMS = 12;
    private static final String POISON = "__DONE__";   // sentinel to shut consumers down cleanly

    // ================================================================
    // 1. HAND-WRITTEN with wait/notify - what BlockingQueue does for you
    // ================================================================
    static class BoundedBuffer {
        private final Queue<Integer> queue = new LinkedList<>();
        private final int capacity;

        BoundedBuffer(int capacity) { this.capacity = capacity; }

        synchronized void put(int item) throws InterruptedException {
            // WHILE, not IF. A woken thread must RE-CHECK the condition, because between being
            // notified and reacquiring the lock another thread may have filled the buffer again
            // (and spurious wakeups are permitted by the spec). `if` here is the classic bug.
            while (queue.size() == capacity) {
                wait();                       // releases the lock and sleeps until notified
            }
            queue.add(item);
            // notifyAll, not notify: with mixed producers and consumers waiting on the SAME monitor,
            // notify() might wake another producer instead of a consumer, and everyone stalls.
            notifyAll();
        }

        synchronized int take() throws InterruptedException {
            while (queue.isEmpty()) {
                wait();
            }
            int item = queue.poll();
            notifyAll();
            return item;
        }

        synchronized int size() { return queue.size(); }
    }

    static void handWritten() throws InterruptedException {
        System.out.println("1. Hand-written with wait/notify");
        BoundedBuffer buffer = new BoundedBuffer(CAPACITY);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= ITEMS; i++) {
                    buffer.put(i);
                    System.out.println("      produced " + i + " (buffer=" + buffer.size() + ")");
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "producer");

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= ITEMS; i++) {
                    int item = buffer.take();
                    System.out.println("        consumed " + item);
                    Thread.sleep(35);          // deliberately SLOWER, so the buffer fills and blocks
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "consumer");

        producer.start(); consumer.start();
        producer.join(); consumer.join();
        System.out.println("      the producer BLOCKED whenever the buffer hit "
                + CAPACITY + " - that is back-pressure");
    }

    // ================================================================
    // 2. BlockingQueue - the same thing, correct, in a fraction of the code
    // ================================================================
    static void withBlockingQueue() throws Exception {
        System.out.println("2. BlockingQueue - what you should actually use");
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(CAPACITY);

        try (ExecutorService pool = Executors.newFixedThreadPool(3)) {

            // One producer.
            pool.submit(() -> {
                try {
                    for (int i = 1; i <= ITEMS; i++) {
                        queue.put("item-" + i);      // BLOCKS when full - no wait/notify needed
                        System.out.println("      produced item-" + i + " (queue=" + queue.size() + ")");
                        Thread.sleep(10);
                    }
                    // POISON PILL: one per consumer, so each shuts down after draining the queue.
                    // Simply interrupting them could discard items still in flight.
                    queue.put(POISON);
                    queue.put(POISON);
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });

            // Two consumers sharing the queue.
            for (int c = 1; c <= 2; c++) {
                final int id = c;
                pool.submit(() -> {
                    try {
                        while (true) {
                            String item = queue.take();          // BLOCKS when empty
                            if (POISON.equals(item)) {
                                System.out.println("        consumer-" + id + " stopping");
                                return;
                            }
                            System.out.println("        consumer-" + id + " got " + item);
                            Thread.sleep(30);
                        }
                    } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                });
            }
        }   // close() waits for all three to finish
        System.out.println("      two consumers shared the work; both stopped on the poison pill");
    }

    // ================================================================
    // 3. THE BUGS - what goes wrong with careless wait/notify
    // ================================================================
    static void theBugs() {
        System.out.println("3. The four classic wait/notify mistakes");
        System.out.println("      a) `if (empty) wait();` instead of `while` - a woken thread does not");
        System.out.println("         re-check, so it proceeds on a condition that is no longer true.");
        System.out.println("         Spurious wakeups are explicitly allowed, so this WILL bite.");
        System.out.println("      b) notify() instead of notifyAll() - may wake a thread that cannot");
        System.out.println("         proceed (a producer when the buffer is still full) and everyone stalls.");
        System.out.println("      c) calling wait()/notify() without holding the monitor ->");
        System.out.println("         IllegalMonitorStateException at run time.");
        System.out.println("      d) forgetting that wait() RELEASES the lock but sleep() does NOT -");
        System.out.println("         sleeping inside a synchronized block blocks everyone else.");

        // (c) demonstrated safely - it throws immediately and is caught.
        try {
            Object monitor = new Object();
            monitor.wait(1);                       // not inside synchronized(monitor)
        } catch (IllegalMonitorStateException e) {
            System.out.println("      -> (c) reproduced: " + e.getClass().getSimpleName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws Exception {
        handWritten();
        System.out.println();
        withBlockingQueue();
        System.out.println();
        theBugs();
    }
}

/* ------------------------------- WHY BOUNDED MATTERS -------------------------------
 * An UNBOUNDED queue looks simpler until the producer outruns the consumer - then the queue grows
 * until the heap is gone. A bounded buffer applies BACK-PRESSURE: the producer blocks, which slows
 * it to the consumer's pace automatically. Choosing the capacity is a real design decision:
 *   too small -> producers block constantly, throughput suffers
 *   too large -> more memory, and failures surface later and harder
 *
 * ------------------------------ CHOOSING A QUEUE -------------------------------------
 * ArrayBlockingQueue     bounded, array-backed, optional fairness - the usual default
 * LinkedBlockingQueue    optionally bounded; higher throughput, more allocation
 * SynchronousQueue       capacity ZERO - a handoff; the producer waits for a consumer.
 *                        This is what Executors.newCachedThreadPool uses.
 * PriorityBlockingQueue  unbounded, ordered by priority rather than arrival
 * DelayQueue             items become available only after a delay - scheduling
 * LinkedTransferQueue    the most flexible/fastest general choice in modern JDKs
 *
 * -------------------------------- SHUTTING DOWN --------------------------------------
 * Consumers blocked in take() never notice a "finished" flag. Two correct options:
 *   POISON PILL (used here) - one sentinel per consumer; each drains the queue first, so nothing
 *                             in flight is lost. Preferred when remaining work must be processed.
 *   INTERRUPT               - take() throws InterruptedException. Faster, but may abandon queued items.
 *
 * ------------------------------- WHEN NOT TO USE --------------------------------------
 * - Producer and consumer run at the same rate and in the same thread - just call the method.
 * - You are hand-writing wait/notify in new code: use BlockingQueue. It is correct, tested, and one
 *   line. Write the manual version to understand it, then delete it.
 * - Work is CPU-bound and needs splitting rather than queueing -> ForkJoinPool.
 * --------------------------------------------------------------------------------------- */
