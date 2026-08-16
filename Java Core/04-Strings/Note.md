# Strings

A Java `String` is an **immutable** sequence of characters. Immutability makes strings safe to share,
cache, and use as `HashMap` keys — but means every "edit" creates a new object.

## Files

| File | Covers |
|------|--------|
| `StringBasics.java` | identity vs equality, immutability, the string pool, common methods |
| `StringBuilderDemo.java` | mutable string building (append/insert/reverse/delete/replace) |

## Core facts

- **Immutable**: methods like `toUpperCase`, `substring`, `replace` return a NEW string; the original
  is unchanged.
- **String pool**: identical string *literals* share one object, so `"hi" == "hi"` is `true`. But
  `new String("hi")` makes a distinct object, so `== ` is `false`. **Always compare with `.equals()`.**
- **`substring(a, b)`**: `b` is exclusive.
- Concatenating in a loop with `+` is **O(n²)** — use `StringBuilder` (amortized O(1) appends).

## String vs StringBuilder vs StringBuffer

| | String | StringBuilder | StringBuffer |
|--|--------|---------------|--------------|
| mutable? | no | yes | yes |
| thread-safe? | n/a (immutable) | no | yes (synchronized) |
| use for | fixed text, keys | building strings (single thread) | building across threads |

## Handy methods

`length`, `charAt`, `indexOf`, `substring`, `replace`, `toUpperCase`/`toLowerCase`, `trim`/`strip`,
`split`, `String.join`, `contains`, `startsWith`/`endsWith`, `String.format`.

## Applications

- Parsing/tokenizing input, building output/reports, template rendering, and as keys in maps/sets.
  For heavy text search, see `Data Structures with Java/Strings/` (KMP, Rabin-Karp) and `Trees/Trie`.
