# Concurrency

## What it is

Running multiple paths of execution at once. Each thread has its **own call stack** but shares the
**heap** with every other thread — and that one asymmetry explains almost everything that follows:
local variables are automatically safe, shared objects are not.

## Why threads exist — two different reasons

| Goal | Why threads help | Workload |
|---|---|---|
| **Throughput** | use multiple CPU cores | CPU-bound computation |
| **Responsiveness** | don't sit idle while waiting | I/O-bound: network, disk, DB |

The second is usually the bigger win and the one people underestimate. A server waiting on a
database is *idle*, not busy — which is exactly what virtual threads exploit.

## The two problems, not one

Concurrency bugs come in two flavours, and fixing one does not fix the other:

**1. Race conditions (atomicity).** `counter++` is read-modify-write. Two threads can both read 5,
both compute 6, and both store 6 — one increment vanishes. `RaceConditionDemo.java` loses ~290,000
of 400,000 increments, reliably.

**2. Visibility.** Even without interleaving, one thread's write may *never become visible* to
another, because values can live in registers or per-core caches. A loop reading a non-`volatile`
flag can spin forever after another thread has set it.

`synchronized`, `volatile` and the atomics all establish **happens-before** ordering and fix
visibility. `volatile` fixes visibility **but not atomicity** — `volatile counter++` is still a race.

## The toolkit, in order of preference

> **Don't share → immutable → atomic → concurrent collection → lock.**
> Each step down adds contention and one more way to get it wrong.

| Tool | Use for | File |
|---|---|---|
| **Not sharing** | partition the work; combine at the end | `RaceConditionDemo` |
| **Immutability** | objects that cannot change cannot race | `Java Core/07-OOP/ImmutableClass` |
| **Atomics / CAS** | one counter or one reference | `AtomicVariablesDemo` |
| **Concurrent collections** | shared maps, queues, lists | `ProducerConsumer` |
| **`synchronized` / locks** | several fields must change together | `RaceConditionDemo`, `DeadlockDemo` |
| **Coordination** | timing and capacity, not exclusion | `CoordinationPrimitives` |
| **`CompletableFuture`** | composing async stages without blocking | `CompletableFutureBasics` |
| **Virtual threads** | tens of thousands of *blocking* tasks | `VirtualThreadScaling` |

## Virtual threads — the measured case

`VirtualThreadScaling.java`, 10,000 tasks each blocking 100ms:

| | Time |
|---|---|
| Platform threads, pool of 200 | **5,383 ms** |
| Virtual threads, one per task | **215 ms** |

~25× faster, **with no change to the task code** — still plain `Thread.sleep()`. The JVM unmounts a
virtual thread when it blocks, so a handful of carrier threads serve them all.

**But:** virtual threads help only with *blocking* work. The same file shows CPU-bound work running
at the same speed on both — you still have the same number of cores. And a virtual thread **cannot
unmount inside `synchronized`** (it gets *pinned*), which defeats the purpose; use `ReentrantLock`
there instead.

## Choosing a coordination primitive

The confusions worth memorizing:

- **Latch vs Barrier** — "wait until N *things happen*" → `CountDownLatch` (one-shot, cannot reset).
  "wait until N *threads arrive*" → `CyclicBarrier` (reusable each round). Needing to repeat is
  decisive.
- **Semaphore vs Lock** — a lock is *owned* and *reentrant* and grants exclusive access. A semaphore
  has N permits, is **not owned** (any thread may release), and is **not reentrant** — a binary
  semaphore acquired twice on one thread deadlocks against itself. Lock = exclusion, semaphore =
  capacity.
- **`thenApply` vs `thenCompose`** — if your lambda returns a plain value use `thenApply`; if it
  returns another `CompletableFuture` use `thenCompose`, or you get a nested future.

## Deadlock

Needs all four **Coffman conditions** at once — break any one and it becomes impossible:

1. Mutual exclusion 2. Hold and wait 3. No pre-emption 4. **Circular wait**

The fourth is the practical one: **acquire locks in a consistent global order** and no cycle can
form. When locks are chosen dynamically (transferring between two arbitrary accounts), order them
by a stable key, or use `tryLock` with a timeout so a thread backs off instead of blocking forever.

Diagnose with `jstack <pid>` — the JVM prints "Found one Java-level deadlock". Threads stuck in
`BLOCKED` that never change are the signature, as `DeadlockDemo.java` shows.

## When NOT to use threads

Concurrency is the easiest way to make a program incorrect. Do not reach for it by default:

- **Small work** — coordination costs more than the work itself.
- **Inherently sequential** — each step needs the previous result (Amdahl's law).
- **Heavy shared state** — if threads constantly lock the same data they serialize anyway; you pay
  the locking cost and keep the sequential speed.
- **Correctness matters and the code is subtle** — races are non-deterministic, pass every test, and
  fail in production.

> Measure first. "It should be faster with threads" is a hypothesis, not a fact.

## Files

**Foundations** — `ThreadBasics` (start vs run, join, daemon, interruption) ·
`RaceConditionDemo` (a reproducible lost update, then four fixes) ·
`DeadlockDemo` (a real deadlock behind a watchdog, plus two fixes)

**Modern async** — `CompletableFutureBasics` (the three `then` families, `allOf`/`anyOf`, timeouts) ·
`VirtualThreadScaling` (10,000 blocking tasks, measured)

**Thread safety** — `AtomicVariablesDemo` (CAS, the retry loop, LongAdder, the ABA problem) ·
`ProducerConsumer` (hand-written `wait`/`notify`, then `BlockingQueue`, plus the four classic
mistakes) · `CoordinationPrimitives` (latch, barrier, semaphore, phaser)

> Related: `Java Core/12-Advanced/ThreadsDemo.java` and `ExecutorServiceDemo.java` cover basic
> threads and thread pools and remain there.

## Pitfalls checklist

- `thread.run()` instead of `start()` — no concurrency, no error, no warning.
- Assuming `counter++` is atomic.
- `volatile` where you need atomicity.
- `if (condition) wait();` instead of `while` — spurious wakeups are permitted by the spec.
- `notify()` instead of `notifyAll()` with mixed waiters.
- Forgetting `countDown()` / `release()` / `unlock()` in a **`finally`** — a task that throws first
  leaves waiters blocked forever, and it looks like a deadlock with no locks involved.
- Blocking on the **common ForkJoinPool** (default for `supplyAsync` and parallel streams) — pass
  your own executor.
- Not shutting down an `ExecutorService` — non-daemon pool threads keep the JVM alive.
- Blocking inside `synchronized` on a virtual thread — it pins the carrier.
