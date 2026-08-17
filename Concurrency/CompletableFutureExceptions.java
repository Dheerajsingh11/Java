// Topic    : Error handling in asynchronous pipelines - exceptionally, handle, whenComplete.
// Approach : Show how a failure propagates through a chain, the three recovery methods and how they
//            differ, timeouts, and the mistake that makes async failures vanish silently.
// Intuition: In synchronous code an uncaught exception unwinds the stack and someone notices. In an
//            async pipeline there IS no shared stack - the exception is stored INSIDE the future.
//            If nobody ever inspects that future, the failure is never reported: no log, no crash,
//            nothing. Silent loss is the defining hazard of async error handling.
// Time     : O(1) per stage   Space: O(1)
// Trade-off: The three recovery methods look similar and are easy to confuse. The table at the
//            bottom is the thing worth remembering: `exceptionally` recovers, `handle` transforms
//            both paths, `whenComplete` observes without changing anything.
// Real use  : fallback values when a service is down, retry policies, logging failures, converting
//            technical exceptions into domain ones at a service boundary.

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CompletableFutureExceptions {

    static String callService(String name, boolean fail) {
        sleep(50);
        if (fail) throw new IllegalStateException(name + " is unavailable");
        return name + ":ok";
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(4);

        // ---------------------------------------------------------------
        System.out.println("1. A failure SKIPS every downstream stage");
        CompletableFuture<String> failed = CompletableFuture
                .supplyAsync(() -> callService("inventory", true), pool)
                .thenApply(s -> { System.out.println("      never runs"); return s.toUpperCase(); })
                .thenApply(s -> { System.out.println("      never runs either"); return s + "!"; });

        try {
            failed.join();
        } catch (CompletionException e) {
            // join() wraps the original in CompletionException, so the real cause is one level down.
            System.out.println("      join threw " + e.getClass().getSimpleName()
                    + ", cause = " + e.getCause().getMessage());
        }

        // ---------------------------------------------------------------
        System.out.println("2. exceptionally - supply a FALLBACK value");
        // Runs ONLY on failure. The type is unchanged, so it is the natural choice for a default.
        String withFallback = CompletableFuture
                .supplyAsync(() -> callService("pricing", true), pool)
                .exceptionally(ex -> {
                    System.out.println("      recovering from: " + ex.getCause().getMessage());
                    return "pricing:CACHED";              // degrade gracefully instead of failing
                })
                .join();
        System.out.println("      result = " + withFallback);

        // ---------------------------------------------------------------
        System.out.println("3. handle - runs on BOTH paths, and may change the type");
        // Receives (value, throwable) - exactly one of which is null. Useful for turning a
        // success/failure pair into a single result object.
        String handled = CompletableFuture
                .supplyAsync(() -> callService("shipping", true), pool)
                .handle((value, ex) -> ex == null
                        ? "OK: " + value
                        : "FAILED: " + ex.getCause().getMessage())
                .join();
        System.out.println("      " + handled);

        String handledOk = CompletableFuture
                .supplyAsync(() -> callService("shipping", false), pool)
                .handle((value, ex) -> ex == null ? "OK: " + value : "FAILED")
                .join();
        System.out.println("      " + handledOk + "   (same code, success path)");

        // ---------------------------------------------------------------
        System.out.println("4. whenComplete - OBSERVE without altering the outcome");
        // Ideal for logging or metrics: it sees the result but cannot change it, and the exception
        // still propagates afterwards.
        try {
            CompletableFuture
                    .supplyAsync(() -> callService("audit", true), pool)
                    .whenComplete((value, ex) ->
                            System.out.println("      [log] completed " + (ex == null ? "ok" : "with error")))
                    .join();
        } catch (CompletionException e) {
            System.out.println("      exception STILL propagated: " + e.getCause().getMessage());
        }

        // ---------------------------------------------------------------
        System.out.println("5. recovering mid-pipeline, then continuing");
        String recovered = CompletableFuture
                .supplyAsync(() -> callService("catalog", true), pool)
                .exceptionally(ex -> "catalog:DEFAULT")      // recovery happens here...
                .thenApply(String::toUpperCase)              // ...so downstream stages DO run
                .thenApply(s -> "[" + s + "]")
                .join();
        System.out.println("      " + recovered + "   (placement matters: recover early to continue)");

        // ---------------------------------------------------------------
        System.out.println("6. a partial failure among several parallel calls");
        CompletableFuture<String> a = CompletableFuture.supplyAsync(() -> callService("a", false), pool)
                .exceptionally(ex -> "a:UNAVAILABLE");
        CompletableFuture<String> b = CompletableFuture.supplyAsync(() -> callService("b", true), pool)
                .exceptionally(ex -> "b:UNAVAILABLE");
        CompletableFuture<String> c = CompletableFuture.supplyAsync(() -> callService("c", false), pool)
                .exceptionally(ex -> "c:UNAVAILABLE");

        // Each future recovers INDIVIDUALLY, so one failure does not destroy the whole aggregate.
        // Without the per-future exceptionally, allOf().join() would throw and lose a and c too.
        CompletableFuture.allOf(a, b, c).join();
        System.out.println("      " + a.join() + ", " + b.join() + ", " + c.join());
        System.out.println("      one service down, the response is still useful");

        // ---------------------------------------------------------------
        System.out.println("7. timeouts");
        String timedOut = CompletableFuture
                .supplyAsync(() -> { sleep(1000); return "slow"; }, pool)
                .completeOnTimeout("DEFAULT-ON-TIMEOUT", 150, TimeUnit.MILLISECONDS)
                .join();
        System.out.println("      completeOnTimeout gave: " + timedOut);
        System.out.println("      (orTimeout would instead FAIL with TimeoutException)");

        // ---------------------------------------------------------------
        System.out.println("8. THE TRAP - an unobserved failure disappears");
        CompletableFuture.supplyAsync(() -> callService("forgotten", true), pool);
        // No join, no exceptionally, no whenComplete. The exception is stored in a future nobody
        // reads, so it is NEVER reported - no stack trace, no log line, nothing.
        Thread.sleep(150);
        System.out.println("      a future failed just now and printed nothing at all.");
        System.out.println("      ALWAYS terminate a pipeline with join/handle/whenComplete,");
        System.out.println("      or attach exceptionally - otherwise failures are silently lost.");

        pool.shutdown();
        pool.awaitTermination(2, TimeUnit.SECONDS);
    }
}

/* ------------------------- THE THREE RECOVERY METHODS -------------------------
 * | Method        | Runs when        | Receives         | Can change result | Typical use |
 * | exceptionally | FAILURE only     | throwable        | yes - a fallback  | default value |
 * | handle        | ALWAYS           | (value, throwable) | yes, and the TYPE | map both paths |
 * | whenComplete  | ALWAYS           | (value, throwable) | NO - passes through | logging, metrics |
 *
 * Choosing:
 *   - just need a fallback value        -> exceptionally
 *   - need to convert both outcomes     -> handle
 *   - only want to observe/log          -> whenComplete (the exception continues to propagate)
 *
 * ------------------------------ EXCEPTION WRAPPING -----------------------------
 * Exceptions thrown inside a stage are wrapped:
 *   join()  throws CompletionException  -> use getCause() for the original
 *   get()   throws ExecutionException   -> use getCause() likewise
 * So `catch (IllegalStateException e)` around join() will NOT match. Always unwrap with getCause(),
 * as every example above does.
 *
 * ------------------------------- PLACEMENT MATTERS ------------------------------
 * A recovery method only covers the stages BEFORE it:
 *   supplyAsync(...).thenApply(f).exceptionally(fallback)   // covers supplyAsync AND thenApply
 *   supplyAsync(...).exceptionally(fallback).thenApply(f)   // recovers first, so thenApply runs
 * Put it early to continue the pipeline, late to catch everything.
 *
 * ------------------------------- THE SILENT-FAILURE RULE -------------------------
 * The single most important habit: every pipeline must END in something that observes the result.
 * A CompletableFuture that fails and is never inspected reports nothing anywhere. In a service this
 * shows up as requests that simply never complete, with no error in the logs.
 * ---------------------------------------------------------------------------------- */
