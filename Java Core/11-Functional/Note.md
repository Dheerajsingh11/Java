# Functional Java (Java 8+)

## What it is

Java 8 added the ability to treat **behaviour as data** (lambdas) and to process collections
**declaratively** (streams). It complements object-oriented code rather than replacing it.

## Why it was added — the problem it solves

Before Java 8, passing behaviour required an entire anonymous class:

```java
Collections.sort(people, new Comparator<Person>() {
    public int compare(Person a, Person b) { return a.age - b.age; }
});
```

Five lines of ceremony around one line of logic. The **intent** — "compare by age" — is buried.

```java
people.sort(Comparator.comparingInt(Person::getAge));   // Java 8
```

The bigger shift is from **imperative** ("how") to **declarative** ("what"):

```java
// imperative: the reader must simulate the loop to work out the intent
List<Integer> out = new ArrayList<>();
for (int n : nums) if (n % 2 == 0) out.add(n * n);

// declarative: the intent is the code
nums.stream().filter(n -> n % 2 == 0).map(n -> n * n).toList();
```

## Lambdas and functional interfaces

A **lambda** `(a, b) -> a + b` is an anonymous function. Its *type* is a **functional interface** — an
interface with exactly one abstract method.

That design choice matters: lambdas required **no new type system**. A lambda is simply a compact way
to implement a single-method interface, so every existing single-method interface (`Runnable`,
`Comparator`, `Callable`) instantly became lambda-compatible.

The built-ins in `java.util.function`:

| Interface | Shape | Use |
|---|---|---|
| `Predicate<T>` | `T -> boolean` | filtering, tests |
| `Function<T,R>` | `T -> R` | transformation |
| `BiFunction<T,U,R>` | `(T,U) -> R` | two-argument transformation |
| `Supplier<T>` | `() -> T` | lazy production |
| `Consumer<T>` | `T -> void` | side effects |

**Method references** are shorthand for a lambda that only calls one method: `Integer::parseInt`,
`String::toUpperCase`, `person::getName`. Use them when the lambda would just forward its argument.

## Streams: a pipeline, not a collection

A stream is **not** a data structure — it holds no elements. It is a *pipeline description*:

```
source  →  intermediate ops (lazy)  →  terminal op (triggers everything)
```

| Stage | Examples | Runs when? |
|---|---|---|
| Source | `list.stream()`, `Arrays.stream(a)` | — |
| Intermediate | `filter`, `map`, `sorted`, `distinct`, `limit` | **lazily** — builds the pipeline |
| Terminal | `collect`, `reduce`, `count`, `forEach`, `anyMatch` | **triggers execution** |

**Laziness is the key mechanism and has real consequences.** Intermediate operations do nothing until
a terminal operation runs. Then elements flow through the *whole* pipeline one at a time, not stage by
stage. So `stream().filter(...).findFirst()` can stop after the first match instead of filtering the
entire list — the pipeline short-circuits.

A stream is also **single-use**: consume it once, and it is spent.

## `Optional` — making absence explicit

`Optional<T>` is a container that either holds a value or is empty.

**The point is not to eliminate null** — it is to move "might be absent" **into the type signature**.
A method returning `Optional<User>` tells every caller, at compile time, that absence is possible.
A method returning `User` gives no such warning, and the `NullPointerException` arrives later.

```java
opt.orElse(defaultValue)         // supply a fallback
opt.ifPresent(v -> ...)          // act only if present
opt.map(User::getName)           // transform if present
opt.orElseThrow(() -> new ...)   // fail loudly
opt.get()                        // AVOID - throws when empty, defeating the purpose
```

**Use `Optional` as a return type.** Do not use it for fields or method parameters — it is not
serializable and adds a wrapper allocation for no benefit there.

## When to use the functional style

- **Data transformation pipelines** — filter/map/reduce chains that read like a description.
- **Grouping and aggregation** — `Collectors.groupingBy`, `partitioningBy`, `summingInt`.
- **Passing behaviour** — comparators, callbacks, strategies, event handlers.
- **Expressing "may be absent"** — return `Optional`.
- **Genuinely parallelizable bulk work** — `parallelStream()` over large, CPU-bound, stateless
  operations.

## When NOT to use it

- **Simple iteration** — a plain `for` loop is clearer and faster than `forEach` for basic work.
- **Hot loops / performance-critical paths** — streams add allocation and virtual-call overhead. Do
  not rewrite a tight numeric loop as a stream.
- **When you need the index** — streams have no natural index; use an indexed loop.
- **Complex control flow** — `break`, `continue`, and multiple exits do not translate; forcing them
  into a stream produces something worse than the loop.
- **`parallelStream()` by default** — it is not free. Thread coordination often costs more than it
  saves on small collections, and it is **wrong** for stateful or order-dependent operations.
- **Debugging-heavy code** — stack traces through stream internals are notoriously hard to read.

> Rule: use streams when they make the intent *clearer*. Clarity, not fashion, is the criterion.

## Side effects: the one real discipline

Lambdas in streams should be **pure** — no mutation of external state:

```java
List<Integer> out = new ArrayList<>();
nums.stream().filter(...).forEach(out::add);   // works, but misuses the model
nums.stream().filter(...).toList();            // the intended way
```

The first breaks outright under `parallelStream()` (unsynchronized concurrent mutation). Prefer
`collect`/`toList` over `forEach` + mutation. Lambdas also require captured local variables to be
**effectively final**, which enforces part of this discipline.

## Files in this folder

| File | Covers |
|---|---|
| `LambdasAndInterfaces` | custom functional interface, built-ins, method references, `andThen` composition |
| `StreamsAndOptional` | filter/map/collect, `reduce`, `count`, `anyMatch`, `groupingBy`, and `Optional` |

## Pitfalls

- Reusing a consumed stream → `IllegalStateException`.
- Forgetting the terminal operation — nothing happens at all.
- `Optional.get()` without checking — reintroduces exactly the failure `Optional` exists to prevent.
- Mutating shared state inside a lambda, especially in parallel.
- Assuming `parallelStream()` is faster. Measure it.

## Where this leads

Streams pair naturally with the Collections Framework (`09-Collections`) and with `Comparator`
chaining. `Optional` is the modern answer to the null-return problem discussed in `06-Methods`.
