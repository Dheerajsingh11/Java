# Generics

## What it is

**Generics** parameterize a class or method by type — `List<String>`, `Box<T>` — so the compiler
knows exactly what a container holds.

## Why generics exist — what Java was like without them

Before Java 5, collections held `Object`:

```java
List names = new ArrayList();
names.add("Alice");
names.add(42);                        // compiles fine - nobody stops you
String s = (String) names.get(1);     // ClassCastException at RUNTIME
```

Two problems: every read needed a **manual cast**, and type errors surfaced **at run time**, often far
from the code that caused them.

Generics fix both:

```java
List<String> names = new ArrayList<>();
names.add(42);              // COMPILE ERROR - caught immediately
String s = names.get(0);    // no cast needed
```

**The value is moving failures from run time to compile time.** A `ClassCastException` in production
becomes a red squiggle in your editor.

There is a third, underrated benefit: **self-documenting APIs.** `Map<String, List<Order>>` tells you
exactly what it holds; a bare `Map` tells you nothing.

## The forms

| Form | Syntax | Meaning |
|---|---|---|
| Generic class | `class Box<T> { T value; }` | `T` is a placeholder for a type |
| Generic method | `<T> T firstOf(List<T> l)` | `T` inferred from the arguments |
| Bounded type | `<T extends Comparable<T>>` | `T` must be a subtype of the bound |
| Wildcard | `List<?>` | a list of *some* unknown type |
| Bounded wildcard | `List<? extends Number>` / `List<? super Integer>` | producer / consumer |

**Bounded types are what make generics useful rather than just safe.** Inside `<T>` alone, you can
only call `Object` methods. Declaring `<T extends Comparable<T>>` tells the compiler that `T` has
`compareTo`, so a generic `max` method becomes possible.

## Wildcards and PECS

The rule: **Producer `extends`, Consumer `super`.**

- If a structure **produces** values you read out → `? extends T`
- If it **consumes** values you put in → `? super T`

```java
void printAll(List<? extends Number> src)  // read Numbers out; cannot add
void addInts(List<? super Integer> dst)    // add Integers in; reads come out as Object
```

**Why the asymmetry?** `List<? extends Number>` might really be a `List<Integer>`, so adding a
`Double` would corrupt it — the compiler forbids adds. Conversely `List<? super Integer>` might be a
`List<Number>`, so adding an `Integer` is always safe, but reading gives only `Object` because the
actual element type is unknown.

This also answers a common confusion: **generics are invariant.** A `List<String>` is *not* a
`List<Object>`, even though `String` is an `Object` — because if it were, you could insert an
`Integer` into it. Wildcards are the escape hatch that restores controlled flexibility.

## Type erasure — the big caveat

Generic type information is **removed at compile time**. At run time, `List<String>` and
`List<Integer>` are both just `List`.

Why: Java 5 needed backward compatibility with pre-generics bytecode, so generics were implemented as
a compile-time-only feature.

What that forbids:

```java
new T()                    // no - T's type is unknown at run time
new T[10]                  // no
x instanceof List<String>  // no - only `instanceof List` is possible
catch (MyException<T> e)   // no
```

Workarounds: pass a `Class<T>` token, or use `List<T>` instead of arrays. Note that generic arrays are
also blocked because arrays are **covariant** at run time while generics are not — mixing them would
break the type system.

**Generics are a compile-time guarantee, not a run-time one.**

## When to use generics

- Writing a **container or data structure** meant for any type (`Box<T>`, a custom `Stack<T>`).
- Writing an **algorithm** independent of element type (`max`, `swap`, `reverse`).
- Any API where the element type should be visible to callers.
- Whenever you would otherwise cast from `Object` — that cast is the signal.

## When NOT to use them

- **Only one concrete type will ever be used** — `<T>` adds noise for no gain.
- **You need run-time type information** — erasure removes it; use a `Class<T>` parameter or
  reflection.
- **Primitive-heavy numeric code** — `List<Integer>` boxes every value. Use `int[]` for bulk numbers.
- Deeply nested wildcards that nobody can read — at some point an interface or a concrete type is
  clearer.

## File in this folder

`GenericsDemo.java` — a generic `Box<T>`, a generic method with inference, a bounded
`<T extends Comparable<T>>` max, and a `List<?>` wildcard, with a note on what would not compile.

## Pitfalls

- Expecting `List<String>` to be usable as `List<Object>` — invariance forbids it.
- **Raw types** (`List` with no type argument) — legal for backward compatibility, but they disable
  all checking and reintroduce the pre-generics problems. Never use them in new code.
- Assuming type info exists at run time.
- Trying `new T()` or `new T[n]`.
- Ignoring "unchecked" warnings — they mark exactly the places the compiler could not verify.

## Where generics are used

The entire Collections Framework (`List<E>`, `Map<K,V>`, `Set<E>`), `Comparable<T>`/`Comparator<T>`,
`Optional<T>`, the Streams API (`Stream<T>`), `CompletableFuture<T>`, and any reusable library — they
are the reason those APIs are both type-safe and cast-free.
