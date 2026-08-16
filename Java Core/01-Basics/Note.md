# Java Basics

## What this covers

How a Java program is shaped, the primitive types, and how to read input — the foundation every
later topic builds on.

## Why Java looks the way it does

Three design decisions explain most of the "why do I have to write all this?" friction:

**1. Everything lives in a class.** Java has no free-floating functions. This enforces the OOP model
and gives every piece of code an owner — verbose for a hello-world, valuable in a million-line system.

**2. `public static void main(String[] args)` is a fixed contract.** The JVM looks for exactly this
signature. `public` so the JVM can call it, `static` because **no object exists yet** when the program
starts, `void` because there is nothing to return to, `String[]` for command-line arguments.

**3. Static typing.** Every variable's type is fixed at compile time, so type errors are caught before
the program runs rather than in production.

## Primitives vs references — the distinction that explains everything else

| | Primitive | Reference |
|---|---|---|
| Holds | the **value** itself | an **address** pointing to a heap object |
| Lives | on the stack (as a local) | variable on stack, object on heap |
| Examples | `int`, `double`, `char`, `boolean` | `String`, arrays, all objects |
| `==` compares | the values | **the addresses** |
| Can be null | no | yes |

This one distinction explains:
- Why `==` works for `int` but is wrong for `String` (use `.equals()`).
- Why passing an array to a method lets the method modify it, while passing an `int` does not — Java
  copies the *reference*, so both point at the same object (see `06-Methods`).
- Why `NullPointerException` exists at all.

## The eight primitives

| Type | Bits | Range / notes |
|---|---|---|
| `byte` | 8 | −128 … 127 |
| `short` | 16 | −32,768 … 32,767 |
| `int` | 32 | ±2.1 billion — **the default** for whole numbers |
| `long` | 64 | very large; literal needs `L` |
| `float` | 32 | decimal; literal needs `f` |
| `double` | 64 | **the default** for decimals |
| `char` | 16 | one Unicode character, **unsigned** |
| `boolean` | — | `true` / `false` only |

**Choosing:** use `int` and `double` unless you have a reason. `byte`/`short` save memory only in
huge arrays and are promoted to `int` in arithmetic anyway. Use `long` when values may exceed
2 billion. Use `BigDecimal` — never `double` — for **money**, because binary floating point cannot
represent 0.1 exactly (`0.1 + 0.2 != 0.3`).

## The three gotchas that catch everyone

**1. Integer division truncates.** `7 / 2` is `3`, not `3.5`. Make one operand a `double`
(`7 / 2.0`) to get a real quotient. Silent and extremely common.

**2. Overflow wraps silently.** `Integer.MAX_VALUE + 1` becomes the most negative int. No exception,
no warning. This is why `(low + high) / 2` in binary search is a real bug, and why numeric code
should default to `long`.

**3. Defaults are guaranteed.** Fields and array elements are auto-initialized (`0`, `false`,
`null`) — unlike C, there is no uninitialized garbage. **Local variables are the exception**: they
must be assigned before use, and the compiler enforces it.

## Reading input: Scanner vs BufferedReader

| | `Scanner` | `BufferedReader` |
|---|---|---|
| Parses types for you | **yes** (`nextInt`, `nextDouble`) | no — you call `Integer.parseInt` |
| Speed | slower (regex/parsing per token) | **faster** (buffered lines) |
| Thread-safe | no | yes (synchronized) |
| Best for | everyday input, readability | large input, competitive programming |

**Use `Scanner` by default; switch to `BufferedReader` when input volume is large.** The difference is
measurable — often several times faster on tens of thousands of lines.

**The classic `Scanner` trap:** `nextInt()` consumes the number but leaves the newline in the buffer,
so the next `nextLine()` returns an empty string. Fix with an extra `nextLine()`, or read everything
with `nextLine()` and parse manually.

Both should be wrapped in **try-with-resources** so they close automatically.

## Swapping — and what it teaches

`swapNum.java` shows three ways:
- **Temp variable** — use this. Clear, works for any type, no edge cases.
- **Arithmetic** (`a=a+b; b=a-b; a=a-b`) — no temp, but can **overflow** and breaks if both
  references are the same variable.
- **XOR** — no overflow, but unreadable and **zeroes the value** if the two are aliased.

The lesson is broader than swapping: *clever tricks that save a variable but introduce edge cases are
usually a bad trade.* Reach for them only when a measured constraint demands it.

## When NOT to use each type

- **`float`/`double` for money or exact decimals** → use `BigDecimal`. Binary floating point cannot
  represent 0.1, so `0.1 + 0.2 != 0.3` and cents drift over time. This is a real financial bug class.
- **`int` for anything that might exceed ~2.1 billion** (timestamps in ms, byte counts, running
  totals, products) → use `long`. Overflow is silent.
- **`byte`/`short` to "save memory" on a few variables** → they are promoted to `int` in arithmetic
  anyway; the saving only exists in large arrays.
- **`char` for text** → use `String`. A `char` is a single UTF-16 unit and cannot hold an emoji or
  many non-Latin characters, which need two units.
- **`==` on anything non-primitive** → use `.equals()`.
- **`Scanner` for very large input** → use `BufferedReader`; the parsing overhead is significant.

## Files in this folder

| File | Covers |
|---|---|
| `HelloWorld` | program structure, why `main` has that exact signature |
| `dataType` | the 8 primitives, ranges, overflow and integer-division traps |
| `swapNum` | three swap techniques and their trade-offs |
| `scanRead` | `Scanner` — convenient, type-aware input |
| `buffRead` | `BufferedReader` — fast, manual parsing |
| `nthTerm` | a formula program (AP n-th term): O(1) closed form vs an O(n) loop |

## Pitfalls recap

- `arr.length` (field) vs `str.length()` (method) vs `list.size()` — three spellings, one concept.
- Class name **must** match the file name for a `public` class.
- `==` on objects compares references. Use `.equals()`.
- Comparing `double`s with `==` is unreliable — compare within a small epsilon.

## Where this leads

These primitives and I/O basics underpin everything: arrays and collections store them, algorithms
manipulate them, and the overflow/division traps here are the root cause of real bugs in the
algorithm folders (binary search midpoint overflow, sieve `p*p` overflow, comparator subtraction).
