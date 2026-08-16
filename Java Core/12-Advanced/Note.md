# Advanced Java

## What this covers

Concurrency, file I/O, and the JVM concepts underneath everything else. Each is a deep field; this
folder is an entry point with the reasoning that matters most.

---

# Concurrency

## Why threads exist

Two genuinely different motivations, often confused:

| Goal | Why threads help | Typical workload |
|---|---|---|
| **Throughput** | use multiple CPU cores simultaneously | CPU-bound computation |
| **Responsiveness** | don't block while waiting | I/O-bound: network, disk, database |

The second is usually the bigger win. A web server waiting on a database is *idle*, not busy — another
thread can serve a different request meanwhile. This is why servers use thread pools even on one core.

## The core problem: shared mutable state

```java
counter++;    // looks atomic. It is NOT.
```

That single line is **read → modify → write**. Two threads can both read `5`, both compute `6`, and
both store `6` — one increment vanishes. That is a **race condition**, and `ThreadsDemo.java`
demonstrates it: without synchronization, two threads each incrementing 100,000 times reliably
produce *less* than 200,000.

Races are the worst class of bug: **non-deterministic**, timing-dependent, often invisible in testing
and only appearing under production load.

## Three tools, in order of preference

**1. Don't share.** Immutable objects and thread-local data cannot race. This is the most reliable
strategy — an immutable `String` or `record` needs no synchronization at all.

**2. `synchronized` / locks.** Only one thread at a time in the guarded region.
Costs: contention (threads queue up) and **deadlock** risk if two threads acquire two locks in
opposite orders. Keep critical sections short and always acquire locks in a consistent order.

**3. Higher-level utilities — prefer these.**

| Instead of | Use | Why |
|---|---|---|
| `new Thread(...)` per task | `ExecutorService` / thread pool | thread creation is expensive; pools bound and reuse |
| `synchronized` counter | `AtomicInteger` | lock-free CAS — faster, no deadlock |
| `synchronized` map | `ConcurrentHashMap` | fine-grained locking, far better scaling |
| manual wait/notify | `BlockingQueue`, `CountDownLatch` | correct by construction |
| chained callbacks | `CompletableFuture` | composable async without callback nesting |

> **Rule: write concurrent code with `java.util.concurrent`, not raw threads and locks.** Raw
> primitives are for understanding what the utilities do.

## When NOT to use threads

Concurrency is the single easiest way to make a program incorrect. Do not reach for it by default:

- **The work is small.** Thread creation and coordination cost more than the work itself. A parallel
  sum of 100 numbers is slower than a loop.
- **The task is inherently sequential.** Each step needing the previous one's result cannot be
  parallelized; you will add complexity for zero speedup (Amdahl's law).
- **Heavy shared mutable state.** If threads must constantly lock the same data, they serialize
  anyway — you pay the locking cost and keep the sequential speed.
- **Correctness matters more than speed and the code is subtle.** Race conditions are
  non-deterministic and can pass every test, then fail in production.
- **A simpler model fits.** Async I/O, batching, or a single background thread often solves the real
  problem with far less risk than a multi-threaded design.

> Measure first. "It should be faster with threads" is a hypothesis, not a fact — and the cost of
> being wrong is a class of bug that is extremely hard to reproduce.

## Visibility — the subtle half of the problem

Beyond races, there is **visibility**: without synchronization, one thread's write may never become
visible to another, because threads may cache values in registers or per-core caches. A loop reading a
non-`volatile` flag can spin forever even after another thread has set it.

`synchronized` and `volatile` both establish *happens-before* ordering, guaranteeing visibility.
`volatile` gives visibility **but not atomicity** — `volatile counter++` is still a race.

---

# File I/O

## Why NIO replaced the old API

`java.nio.file` (Java 7+) is the modern API; `java.io.File` is legacy.

| | `java.io.File` | `java.nio.file` |
|---|---|---|
| Error reporting | returns `false` — no reason given | throws with a **cause** |
| Symlinks, permissions, metadata | poor | full support |
| Bulk operations | manual | `Files.readAllLines`, `Files.copy`, `Files.walk` |

The error-reporting difference is the decisive one: `file.delete()` returning `false` tells you
nothing about *why*, making failures nearly undiagnosable.

## Whole-file vs streaming — the memory decision

```java
List<String> lines = Files.readAllLines(path);      // simple - loads EVERYTHING into memory
try (BufferedReader r = Files.newBufferedReader(path)) { ... }   // streams line by line
```

**Read the whole file only when you know it is small.** `readAllLines` on a multi-gigabyte log will
exhaust the heap. Streaming keeps memory constant regardless of file size.

## Always use try-with-resources

Every stream and reader **must** be closed. Leaked file handles are a finite OS resource; exhaust
them and unrelated parts of the program start failing mysteriously. try-with-resources closes
automatically, even when an exception is thrown — see `08-Exceptions`.

---

# JVM concepts worth knowing

## JDK ⊃ JRE ⊃ JVM

- **JVM** — executes bytecode
- **JRE** — JVM + standard libraries (run programs)
- **JDK** — JRE + compiler and tools (develop programs)

`.java` → `javac` → `.class` bytecode → JVM. Bytecode is **platform-independent**; the JVM is not.
That is "write once, run anywhere". The **JIT** compiler then translates hot bytecode into native
code at run time, which is why long-running Java approaches C-like speed after warm-up.

## Stack vs heap

| | Stack | Heap |
|---|---|---|
| Holds | locals, call frames, primitive values, **references** | **objects**, arrays |
| Lifetime | until the method returns | until unreachable |
| Managed by | automatic push/pop | **garbage collector** |
| Overflow | `StackOverflowError` (deep recursion) | `OutOfMemoryError` |

This directly explains recursion's O(depth) space cost, and why an object survives after the method
that created it returns — the reference was copied out, so it is still reachable.

## Garbage collection

The JVM automatically frees objects that are no longer **reachable** from any live reference. You
never call `free()`, which eliminates entire bug categories: dangling pointers, double frees, most
memory leaks.

It is not magic, though. **You can still leak** by keeping references alive unintentionally — a static
collection that only ever grows, an unremoved listener, or an inner class holding its outer instance
(see `07-OOP`). "Reachable but useless" is the Java form of a memory leak.

`System.gc()` is only a *suggestion*; do not rely on it.

## Files in this folder

| File | Covers |
|---|---|
| `ThreadsDemo` | creating threads, `start` vs `run`, `join`, a demonstrable race condition, `synchronized` |
| `FileIODemo` | NIO reads/writes, whole-file vs buffered streaming, try-with-resources |

## Further topics (not yet covered here)

`CompletableFuture` for composable async pipelines; **virtual threads** (Java 21) for very large
numbers of blocking tasks; NIO channels and selectors for high-performance networking; the module
system; and the Java Memory Model in detail.

## Pitfalls

- Calling `thread.run()` instead of `thread.start()` — runs on the *current* thread, no concurrency.
- Assuming `counter++` or any read-modify-write is atomic.
- Using `volatile` where you need atomicity.
- Acquiring locks in inconsistent orders → deadlock.
- Loading huge files entirely into memory.
- Forgetting to close resources.
- Believing GC makes leaks impossible.

## Where this leads

Concurrency underpins every server and UI framework; NIO underpins all file and network handling; and
the stack/heap model explains recursion limits and object lifetimes throughout this repository.

## Also in this folder

`DateTimeDemo` (java.time; Duration vs Period) · `ExecutorServiceDemo` (thread pools, Future, AtomicInteger) · `AnnotationsReflection` (a miniature test runner) · `SealedAndPatternMatching` (compile-time exhaustive switches).
