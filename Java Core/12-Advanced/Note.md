# Advanced Java

Higher-level topics that build on the fundamentals: concurrency, file I/O, and JVM concepts. These
are gateways — each is a deep area on its own.

## Files

| File | Covers |
|------|--------|
| `ThreadsDemo.java` | threads, `start`/`join`, race conditions, `synchronized` |
| `FileIODemo.java` | reading/writing files with NIO (`java.nio.file.Files`) + try-with-resources |

## Concurrency (threads)

- A **thread** is an independent path of execution; each has its own stack, all share the heap.
- Start with `thread.start()` (NOT `run()`, which stays on the current thread); wait with `join()`.
- **Race condition**: unsynchronized read-modify-write on shared state loses updates.
- **`synchronized`** / locks enforce one-at-a-time access; still watch for **deadlock**.
- Prefer high-level tools: `ExecutorService`, `java.util.concurrent` collections, `AtomicInteger`,
  `CompletableFuture` — over raw threads and manual locking.

## File I/O (NIO)

- `java.nio.file.Files` + `Path` is the modern API (cleaner than legacy `java.io.File`).
- Whole-file: `Files.write`, `Files.readAllLines`. Streaming: `Files.newBufferedReader/Writer`.
- Always use **try-with-resources** so streams close (no file-handle leaks), even on error.

## JVM concepts (good to know)

- **JDK** (develop) ⊃ **JRE** (run) ⊃ **JVM** (execute bytecode).
- **Stack vs heap**: locals/call frames on the stack; objects on the heap.
- **Garbage collection**: the JVM automatically frees unreachable objects — no manual `free`.
- Source `.java` → `javac` → bytecode `.class` → JVM runs it (JIT-compiles hot paths to native).

## Other advanced areas (pointers for further study)

Annotations & reflection, `java.time` (dates), sealed classes, pattern matching for `switch`,
the module system, and NIO channels/selectors for high-performance networking.

## Applications

- Servers and web apps (concurrency), data pipelines and logging (I/O), frameworks (reflection/
  annotations), and performance tuning (understanding the JVM and GC).
