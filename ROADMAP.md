# Roadmap

Plan for extending this repository, and what is deliberately deferred to a separate repo.

> **Status: for review.** Nothing below is built yet.

---

## Split of the five proposed areas

| # | Area | Where | Why |
|---|---|---|---|
| 1 | **Advanced Concurrency & Multithreading** | **This repo** | Pure Java, no framework, no infrastructure. JDK 21 is already installed, so virtual threads work today |
| 2 | **Design Patterns & SOLID** | **This repo** | Pure Java. Needs no dependencies at all |
| 3 | Microservices, Spring Boot, JPA, Kafka | **New repo** | Needs a framework, a database and a broker |
| 4 | **Testing** | **Split** | JUnit 5 + Mockito + **JMH** here; **Testcontainers** in the new repo (it requires Docker) |
| 5 | **Tooling & CI/CD** | **Split** | **Maven + Gradle** here; **Dockerfile + CI/CD pipeline** in the new repo |

**This repo becomes:** the complete *language and computer-science* reference — core Java, DSA,
concurrency, design patterns, and the tests/benchmarks that prove all of it correct.

**The new repo becomes:** the *applied engineering* path — Spring Boot, persistence, Kafka,
containers, and deployment.

---

## A constraint that shapes the plan

The existing 253 files **cannot be compiled as a single Maven/Gradle source tree**. Three reasons,
all verified:

1. **17 duplicate top-level class names** — `Node` ×6, `myHash` ×3, `basicSearch` ×2, plus `Point`,
   `Circle`, `Rectangle` ×2. A single-unit build fails with `duplicate class` errors.
2. **237 of 253 files declare no package**, so everything lands in the default package where those
   collisions are unavoidable.
3. **Every path contains spaces** (`Java Core/`, `Data Structures with Java/`, `Linked List/`),
   which many build and CI tools handle badly.

Fixing this would mean touching all 253 files (add packages, rename folders, rename classes) — a
large, disruptive refactor with no learning value.

### The approach instead

**Leave the existing 253 files exactly as they are.** They stay simple: open one file, run it
directly, no build tool needed — which is genuinely the right model for a language/DSA reference.

**Add new content as properly packaged, buildable modules** alongside it. New code gets packages, no
spaces in paths, and a Maven build. The two styles coexist:

```
Java Core/                    unchanged - single-file, run directly
Data Structures with Java/    unchanged - single-file, run directly

concurrency/                  NEW - Maven module, packaged
design-patterns/              NEW - Maven module, packaged
benchmarks/                   NEW - Maven module, JMH benchmarks of the existing algorithms
tests/                        NEW - Maven module, JUnit tests for the existing algorithms
```

---

## Phase 1 — Advanced Concurrency & Multithreading

New top-level `concurrency/`, ~22 files. No build tool required for most of it; every file runs
standalone as the repo does today.

**1.1 Foundations (recap + depth)**
- `ThreadBasics` — creating, starting, joining; `start()` vs `run()`
- `ThreadLifecycle` — the six states, with a demo that prints transitions
- `RaceConditionDemo` — a reproducible lost-update, then the fix

**1.2 Modern threading**
- `ExecutorServiceDemo` *(already exists in 12-Advanced — will be moved/expanded here)*
- `CompletableFutureBasics` — `supplyAsync`, `thenApply`, `thenCompose`, `thenCombine`
- `CompletableFutureComposition` — parallel calls, `allOf`/`anyOf`, timeouts
- `CompletableFutureExceptions` — `exceptionally`, `handle`, `whenComplete`
- `ForkJoinDemo` — `RecursiveTask`, work-stealing, parallel merge sort
- `ParallelStreamsDemo` — when they help, when they hurt, and why

**1.3 Project Loom — virtual threads (Java 21)**
- `VirtualThreadBasics` — creating them; how they differ from platform threads
- `VirtualThreadScaling` — **10,000 concurrent blocking tasks**, platform vs virtual, measured
- `StructuredConcurrency` — `StructuredTaskScope`, scoped lifetimes

**1.4 Thread safety toolkit**
- `SynchronizedVsLock` — intrinsic locks vs `ReentrantLock`
- `ReentrantLockDemo` — `tryLock`, fairness, interruptible acquisition
- `ReadWriteLockDemo` — many readers, one writer
- `CountDownLatchDemo` — wait for N tasks to finish
- `CyclicBarrierDemo` — N threads rendezvous repeatedly (and how it differs from a latch)
- `SemaphoreDemo` — bounding concurrent access to a resource
- `AtomicVariablesDemo` — CAS, `AtomicInteger`/`AtomicReference`, ABA problem
- `ConcurrentCollectionsDemo` — `ConcurrentHashMap`, `CopyOnWriteArrayList`, `BlockingQueue`
- `ProducerConsumer` — the classic, via `BlockingQueue`
- `DeadlockDemo` — cause it deliberately, then fix by lock ordering
- `VolatileVisibility` — a visibility bug you can actually observe

Each with the existing conventions: header block, why-not-what comments, runnable `main`,
expected output, plus a `Note.md` covering when to use each primitive and when not to.

---

## Phase 2 — Design Patterns & SOLID

New top-level `design-patterns/`, ~28 files. **Real domains, not `Foo`/`Bar`** — as requested.

**2.1 Creational**
- `Singleton` — enum, holder idiom, double-checked locking; thread-safety and why most uses are wrong
- `Factory` — payment-method factory (Card / UPI / NetBanking)
- `AbstractFactory` — cross-platform UI widget families
- `Builder` — an HTTP request builder with optional fields
- `Prototype` — cloning document templates

**2.2 Structural**
- `Adapter` — adapting a legacy payment gateway to a new interface
- `Decorator` — coffee pricing, and notification channels stacked
- `Proxy` — lazy image loading, plus an access-control proxy
- `Facade` — a simple ordering facade over inventory + payment + shipping
- `Composite` — a filesystem tree (files and folders treated uniformly)
- `Bridge` — message types × delivery channels
- `Flyweight` — sharing glyph objects in a text editor

**2.3 Behavioural**
- `Strategy` — pluggable pricing/discount strategies
- `Observer` — an order-event listener system
- `Command` — undoable text-editor operations
- `TemplateMethod` — a report generator with fixed steps
- `State` — an order lifecycle (Placed → Paid → Shipped → Delivered)
- `ChainOfResponsibility` — an approval chain by amount
- `Iterator` — a custom collection
- `Mediator` — chat-room coordination

**2.4 SOLID — each as a `Before` / `After` pair**
- `S_SingleResponsibility` — a class doing persistence + email + validation, split apart
- `O_OpenClosed` — a `switch` over shape types replaced by polymorphism
- `L_LiskovSubstitution` — the classic `Square extends Rectangle` violation, and the fix
- `I_InterfaceSegregation` — a fat `Worker` interface forcing empty methods, split
- `D_DependencyInversion` — a service hard-wired to MySQL, inverted behind an interface

**2.5 Supporting**
- `Note.md` — pattern selection guide, and **when a pattern is over-engineering**
- Anti-patterns worth naming: God object, singleton abuse, premature abstraction

---

## Phase 3 — Testing & Benchmarking

This is where a build tool becomes necessary, and it delivers real value: **automated tests would
have caught the three broken sorts found manually in the last audit.**

**3.1 Build tooling** (`pom.xml` + `build.gradle`, ~4 files)
- Maven: POM structure, dependencies, scopes, lifecycle, Surefire
- Gradle: the same project in Groovy DSL, side by side for comparison
- A `Note.md` comparing them honestly

**3.2 JUnit 5** (~8 files)
- Assertions, lifecycle (`@BeforeEach`/`@AfterEach`), `@DisplayName`
- `@ParameterizedTest` with `@ValueSource`, `@CsvSource`, `@MethodSource`
- `assertThrows`, `assertTimeout`, `assertAll`
- Nested tests, tagging, conditional execution
- **Property-style testing of the existing algorithms** — e.g. every sort checked against
  `Arrays.sort` on thousands of random arrays, the exact technique that exposed the earlier bugs

**3.3 Mockito** (~5 files)
- Mocks, stubs, spies; `when`/`thenReturn`, `verify`
- `ArgumentCaptor`, argument matchers
- Mocking exceptions; `@InjectMocks`
- **When mocking is the wrong answer** (mocking value objects, over-mocking)

**3.4 JMH benchmarking** (~6 files)
- Setup, warm-up, why naive `System.nanoTime()` benchmarks lie
- **Benchmark the algorithms already in this repo**:
  - `ArrayList` vs `LinkedList` traversal (proves the cache-locality claim in the notes)
  - Sorting algorithms head to head at varying n
  - `String` `+` vs `StringBuilder` in a loop (proves the O(n²) claim)
  - HashMap vs TreeMap lookup
  - Boxed `List<Integer>` vs `int[]`
  - Virtual vs platform threads under blocking I/O

This closes a real gap: the notes make performance claims that are currently asserted rather than
measured. JMH turns them into evidence.

---

## Deferred to the new repository

| Topic | Why it needs its own repo |
|---|---|
| Spring Core, Boot, MVC, Data JPA, Security | Needs a full project skeleton, a database, and a running server |
| Mock payments API (transactions, security, external calls) | An application, not a reference snippet |
| Kafka producers/consumers, event-driven architecture | Needs a broker running |
| Testcontainers | Needs Docker |
| Multi-stage `Dockerfile` (Alpine / Distroless) | There is no deployable application in this repo |
| `.github/workflows` CI/CD pipeline | Belongs with the deployable application |

> **One small exception worth considering:** a *minimal* GitHub Actions workflow for **this** repo
> that compiles all 253 files and runs the Phase 3 test suite on every push. That is CI for
> correctness, not deployment, and it would prevent a regression like the broken sorts from ever
> being merged again. Cheap to add once Phase 3 exists — your call.

---

## Suggested order

| Phase | Scope | Size | Depends on |
|---|---|---|---|
| **1** | Concurrency | ~22 files | nothing — can start immediately |
| **2** | Design patterns + SOLID | ~28 files | nothing |
| **3** | Build tools + JUnit + Mockito + JMH | ~23 files | ideally after 1 and 2, so there is more to test |

Phases 1 and 2 are independent — either can go first. Phase 3 benefits from going last, since it can
then test and benchmark everything.

**Total: ~73 new files**, all pure Java, all runnable, all consistent with the current conventions.

---

## Open questions

1. **Order** — Concurrency first, or Design Patterns first?
2. **Placement** — new top-level folders (`concurrency/`, `design-patterns/`), or as
   `Java Core/13-Concurrency` and `Java Core/14-DesignPatterns`?
3. **Existing `12-Advanced/ThreadsDemo` and `ExecutorServiceDemo`** — move them into the new
   concurrency module, or leave them and cross-reference?
4. **Build tool** — Maven only, or Maven **and** Gradle side by side for comparison?
5. **The minimal CI workflow** for this repo — yes or no?

---

## Decisions (agreed)

| Question | Decision |
|---|---|
| **Order** | **Design Patterns → Concurrency → Testing**, easiest to hardest (rationale below) |
| **Placement** | New top-level folders: `DesignPatterns/`, `Concurrency/` |
| `12-Advanced/ThreadsDemo`, `ExecutorServiceDemo` | **Stay where they are.** The new module cross-references them rather than moving them |
| Folder naming | No spaces or hyphens in new folders, so they stay package- and tool-compatible later |
| Build tool / CI | Deferred to Phase 3 |

### Why this order is easy → hard

**Design Patterns (easiest)** — deterministic output, so documented expected results are reliable;
builds directly on `07-OOP`; no JDK-version or timing concerns; each file is self-contained OOP.

**Concurrency (harder)** — non-deterministic by nature, so output varies between runs and "expected
output" must be written carefully; timing-based demos can be flaky; virtual threads require Java 21;
and a deadlock demo must carry its own watchdog or it will hang any automated run.

**Testing & JMH (hardest)** — the only phase that requires build tooling and external dependencies,
and benchmarks are the most environment-sensitive thing in the repo.
