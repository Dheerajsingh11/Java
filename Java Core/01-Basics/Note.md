# Java Basics

The starting point: how a Java program is shaped, the primitive types, and reading console input.

## Files

| File | Covers |
|------|--------|
| `HelloWorld.java` | Program structure, the `main` entry point, `System.out.println` |
| `dataType.java` | The 8 primitive types, their sizes/ranges, overflow & integer-division gotchas |
| `swapNum.java` | Swapping variables (temp, arithmetic, XOR) |
| `scanRead.java` | Console input with `Scanner` (type-aware, convenient) |
| `buffRead.java` | Console input with `BufferedReader` (fast, manual parsing) |
| `nthTerm.java` | A tiny formula program (n-th term of an AP) reading input |

## The 8 primitive types

| Type | Bits | Range / notes |
|------|------|---------------|
| `byte` | 8 | -128 .. 127 |
| `short` | 16 | -32,768 .. 32,767 |
| `int` | 32 | ~ -2.1e9 .. 2.1e9 — **default** for whole numbers |
| `long` | 64 | very large; literal needs `L` suffix |
| `float` | 32 | decimal; literal needs `f` suffix |
| `double` | 64 | decimal — **default** for fractions |
| `char` | 16 | a single Unicode character (unsigned) |
| `boolean` | — | `true` / `false` only |

Everything else (String, arrays, objects) is a **reference type** — the variable holds a pointer to
data on the heap, not the data itself.

## Key gotchas

- **Integer division truncates**: `7 / 2 == 3`. Make one operand a `double` (`7 / 2.0`) for `3.5`.
- **Overflow wraps silently**: `Integer.MAX_VALUE + 1` becomes the most negative int — no error.
- **Class name must equal the file name** for a `public` class.

## Scanner vs BufferedReader

| | Scanner | BufferedReader |
|--|---------|----------------|
| Ease | parses types for you | you call `Integer.parseInt` yourself |
| Speed | slower (per-token parsing) | faster (buffered lines) |
| Use when | everyday input, readability | large input, performance |

## Applications

- Every program starts here: entry point + variables + input. These are the primitives every later
  topic (arrays, OOP, DSA) builds on.
