// Topic    : CompletableFuture - running work asynchronously and CHAINING what happens next.
// Approach : supplyAsync to start, then thenApply / thenCompose / thenCombine / thenAccept to build
//            a pipeline that never blocks until the very end.
// Intuition: A plain Future can only be asked "are you done?" or blocked on with get(). That forces
//            the calling thread to WAIT, which defeats the point of going asynchronous.
//            CompletableFuture inverts it: instead of asking for the result, you REGISTER what
//            should happen when it arrives. The work and the follow-up are both scheduled, and no
//            thread sits idle.
// Time     : total = the LONGEST path through the pipeline, not the sum of its stages
// Space    : O(number of stages)
// Trade-off: Far more composable than Future, but the API is large and the three "then" families
//            (thenApply / thenCompose / thenCombine) are easy to confuse - the distinction is
//            explained below. Exception handling also needs care; see CompletableFutureExceptions.
// Real use  : calling several microservices in parallel, async I/O, any workflow where stages depend
//            on each other but you do not want to block a thread between them.

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CompletableFutureBasics {

    // Stand-ins for slow I/O - a database call, an HTTP request.
    static String fetchUser(String id)      { sleep(120); return "user:" + id; }
    static String fetchOrders(String user)  { sleep(120); return user + "/orders(3)"; }
    static int    fetchScore(String user)   { sleep(120); return user.length() * 7; }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public static void main(String[] args) throws Exception {

        // A dedicated pool. Without one, these run on the common ForkJoinPool, which is shared with
        // parallel streams - fine for CPU work, a poor idea for BLOCKING I/O, because a blocked
        // common-pool thread starves everything else using it.
        ExecutorService pool = Executors.newFixedThreadPool(4);

        // ---------------------------------------------------------------
        System.out.println("1. supplyAsync - start work off the calling thread");
        CompletableFuture<String> user = CompletableFuture.supplyAsync(() -> fetchUser("42"), pool);
        System.out.println("      main is NOT blocked; the future is " +
                (user.isDone() ? "done" : "still running"));
        System.out.println("      result when joined: " + user.join());

        // ---------------------------------------------------------------
        System.out.println("2. thenApply - transform the result (a plain function)");
        // Use thenApply when your function returns a NORMAL VALUE.
        String upper = CompletableFuture
                .supplyAsync(() -> fetchUser("7"), pool)
                .thenApply(String::toUpperCase)          // String -> String
                .thenApply(s -> "[" + s + "]")           // chains freely
                .join();
        System.out.println("      " + upper);

        // ---------------------------------------------------------------
        System.out.println("3. thenCompose - chain a DEPENDENT async call (flatMap)");
        // Use thenCompose when your function itself returns a CompletableFuture. thenApply would
        // give you CompletableFuture<CompletableFuture<String>> - a nested future nobody wants.
        String orders = CompletableFuture
                .supplyAsync(() -> fetchUser("9"), pool)
                .thenCompose(u -> CompletableFuture.supplyAsync(() -> fetchOrders(u), pool))
                .join();
        System.out.println("      " + orders + "   (two SEQUENTIAL calls - the second needs the first)");

        // ---------------------------------------------------------------
        System.out.println("4. thenCombine - join two INDEPENDENT futures");
        long start = System.currentTimeMillis();
        CompletableFuture<String> ordersF = CompletableFuture
                .supplyAsync(() -> fetchUser("11"), pool)
                .thenCompose(u -> CompletableFuture.supplyAsync(() -> fetchOrders(u), pool));
        CompletableFuture<Integer> scoreF = CompletableFuture
                .supplyAsync(() -> fetchUser("11"), pool)
                .thenCompose(u -> CompletableFuture.supplyAsync(() -> fetchScore(u), pool));

        // Both pipelines are ALREADY RUNNING in parallel. thenCombine merges them when both finish.
        String combined = ordersF.thenCombine(scoreF, (o, s) -> o + " score=" + s).join();
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("      " + combined);
        System.out.println("      took ~" + elapsed + "ms - about 240ms of work each, run CONCURRENTLY");
        System.out.println("      (sequentially this would be ~480ms)");

        // ---------------------------------------------------------------
        System.out.println("5. thenAccept / thenRun - terminal stages");
        CompletableFuture.supplyAsync(() -> fetchUser("99"), pool)
                .thenAccept(u -> System.out.println("      consumed: " + u))   // takes a value, returns nothing
                .thenRun(() -> System.out.println("      pipeline finished"))  // takes nothing
                .join();

        // ---------------------------------------------------------------
        System.out.println("6. allOf - wait for MANY futures");
        CompletableFuture<String> a = CompletableFuture.supplyAsync(() -> fetchUser("a"), pool);
        CompletableFuture<String> b = CompletableFuture.supplyAsync(() -> fetchUser("b"), pool);
        CompletableFuture<String> c = CompletableFuture.supplyAsync(() -> fetchUser("c"), pool);

        // allOf returns CompletableFuture<Void> - it signals COMPLETION, not results. Collect the
        // values afterwards with join(), which is instant because they are already done.
        CompletableFuture.allOf(a, b, c).join();
        System.out.println("      all three done: " + a.join() + ", " + b.join() + ", " + c.join());

        // ---------------------------------------------------------------
        System.out.println("7. anyOf - take whichever finishes FIRST");
        CompletableFuture<String> fast = CompletableFuture.supplyAsync(() -> { sleep(50);  return "fast"; }, pool);
        CompletableFuture<String> slow = CompletableFuture.supplyAsync(() -> { sleep(300); return "slow"; }, pool);
        System.out.println("      winner: " + CompletableFuture.anyOf(fast, slow).join());
        System.out.println("      (useful for hedged requests to redundant services)");

        // ---------------------------------------------------------------
        System.out.println("8. orTimeout - do not wait forever");
        try {
            CompletableFuture.supplyAsync(() -> { sleep(1000); return "too slow"; }, pool)
                    .orTimeout(200, TimeUnit.MILLISECONDS)
                    .join();
        } catch (Exception e) {
            System.out.println("      timed out as expected: " + e.getCause().getClass().getSimpleName());
        }

        pool.shutdown();
        pool.awaitTermination(2, TimeUnit.SECONDS);
        System.out.println("pool shut down cleanly");
    }
}

/* ------------------- THE THREE "then" METHODS - the usual confusion -------------------
 * | Method        | Your function returns | Result                | Analogy  |
 * | thenApply     | a plain value    T -> U     | CompletableFuture<U> | map      |
 * | thenCompose   | another future   T -> CF<U> | CompletableFuture<U> | flatMap  |
 * | thenCombine   | combines TWO futures        | CompletableFuture<V> | zip      |
 *
 * Using thenApply where you needed thenCompose gives you CompletableFuture<CompletableFuture<U>>,
 * which compiles and then behaves confusingly. If your lambda returns a future, use thenCompose.
 *
 * ------------------------------- ASYNC VARIANTS ----------------------------------------
 * Every method has an ...Async twin:
 *   thenApply(fn)              may run on the thread that COMPLETED the previous stage
 *   thenApplyAsync(fn)         always submits to a pool
 *   thenApplyAsync(fn, pool)   submits to YOUR pool
 * If a stage does anything slow, use the Async form with an explicit executor - otherwise you can
 * accidentally run heavy work on a thread that should be free.
 *
 * ------------------------------ join() vs get() ----------------------------------------
 * join()  throws unchecked CompletionException - convenient inside streams and lambdas.
 * get()   throws checked ExecutionException and InterruptedException - noisier but explicit.
 * Both BLOCK, so call them once, at the very end. Blocking in the middle of a pipeline throws away
 * the whole benefit.
 *
 * -------------------------------- WHEN NOT TO USE ---------------------------------------
 * - The work is fast and sequential - the machinery costs more than it saves.
 * - You would immediately join() each stage; that is synchronous code with extra steps.
 * - Blocking I/O on the COMMON pool - always pass your own executor, or use virtual threads
 *   (VirtualThreadScaling.java), which make plain blocking code scale without this complexity.
 * ---------------------------------------------------------------------------------------- */
