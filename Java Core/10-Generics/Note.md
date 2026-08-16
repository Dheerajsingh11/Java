# Generics

**Generics** parameterize classes and methods by type (e.g. `List<String>`, `Box<T>`), giving
compile-time type safety and eliminating casts. "Write once, use with many types."

## Files

| File | Covers |
|------|--------|
| `GenericsDemo.java` | generic class, generic method, bounded types, wildcards |

## Forms

| Form | Syntax | Meaning |
|------|--------|---------|
| Generic class | `class Box<T> { T value; }` | T is a type placeholder |
| Generic method | `<T> T firstOf(List<T> l)` | T inferred from arguments |
| Bounded type | `<T extends Comparable<T>>` | T must be (a subtype of) the bound |
| Wildcard | `List<?>` | list of some unknown type (read-only-ish) |
| Bounded wildcard | `List<? extends Number>` / `List<? super Integer>` | producer / consumer |

## PECS (a rule of thumb for wildcards)

**Producer `extends`, Consumer `super`**: if a structure PRODUCES values you read, use
`? extends T`; if it CONSUMES values you add, use `? super T`.

## Type erasure (the big caveat)

Generic type information is **removed at runtime** ("erasure"). Consequences:
- You cannot do `new T()`, `new T[n]`, or `x instanceof List<String>`.
- All `List<...>` are the same class at runtime (`List`).
- Generics are a **compile-time** guarantee, not a runtime one.

## Why use them

- Catch type errors at compile time instead of via `ClassCastException` at runtime.
- No manual casting when reading from collections.
- Self-documenting APIs (`Map<String, List<Order>>` says exactly what it holds).

## Applications

- The entire Collections Framework (`List<T>`, `Map<K,V>`), `Comparable<T>`/`Comparator<T>`,
  `Optional<T>`, and any reusable container or algorithm that should be type-agnostic.
