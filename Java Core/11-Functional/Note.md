# Functional Java (Java 8+)

Java 8 added a functional style: treat **behavior as data** (lambdas), and process collections
**declaratively** (streams). This complements — not replaces — object-oriented code.

## Files

| File | Covers |
|------|--------|
| `LambdasAndInterfaces.java` | lambdas, functional interfaces, method references, composition |
| `StreamsAndOptional.java` | filter/map/reduce/collect/groupingBy; `Optional` |

## Lambdas & functional interfaces

- A **lambda** `(a, b) -> a + b` is an anonymous function.
- Its type is a **functional interface**: an interface with exactly one abstract method
  (`@FunctionalInterface`). Common built-ins in `java.util.function`:

| Interface | Shape | Use |
|-----------|-------|-----|
| `Predicate<T>` | `T -> boolean` | filtering/tests |
| `Function<T,R>` | `T -> R` | transform |
| `BiFunction<T,U,R>` | `(T,U) -> R` | two-arg transform |
| `Supplier<T>` | `() -> T` | lazy provide |
| `Consumer<T>` | `T -> void` | side effect |

- **Method references** (`Integer::parseInt`, `String::toUpperCase`) are shorthand for lambdas that
  just call one method.

## Streams

A stream pipeline = **source** → lazy **intermediate ops** (`filter`, `map`, `sorted`, `distinct`)
→ one **terminal op** (`collect`, `reduce`, `count`, `forEach`, `anyMatch`). Intermediate ops don't
run until a terminal op is called.

## Optional

`Optional<T>` is a container that is either present or empty — it makes "might be absent" explicit in
the type. Access safely with `orElse`, `ifPresent`, `map`, `orElseThrow`; avoid `get()` (throws when
empty). It reduces `NullPointerException`.

## When to use (and not)

- Great for clear data transformations and pipelines; enables easy parallelism (`parallelStream`).
- For trivial iterations or performance-critical hot loops, a plain `for` loop can be clearer/faster.

## Applications

- Data processing/ETL, collection transformations, event/callback handling, and building readable
  query-like code over in-memory data.
