# Strings

## What it is

A Java `String` is an **immutable** sequence of characters. Every method that appears to modify a
string actually returns a **new** one.

## Why immutability was chosen — and what it costs you

Immutability is the single design decision that explains all String behaviour.

**What it buys:**
- **Thread safety for free** — no shared string can be changed underneath you.
- **Safe as `HashMap` keys** — the hash can be cached and never goes stale. (A mutable key that
  changes after insertion becomes unreachable — see `09-Collections`.)
- **Interning** — identical literals can share one object, saving memory.
- **Security** — a file path or connection string passed to a method cannot be altered by it after
  validation.

**What it costs:**
- Every "edit" allocates. Building a string in a loop with `+` is **O(n²)** — each concatenation
  copies everything accumulated so far.

```java
String s = "";
for (int i = 0; i < n; i++) s += i;        // O(n^2) - allocates and copies every iteration
StringBuilder sb = new StringBuilder();
for (int i = 0; i < n; i++) sb.append(i);  // O(n) - one growable buffer
```

For n = 100,000 this is the difference between milliseconds and minutes. **This is the single most
common Java performance mistake.**

## The string pool and why `==` betrays you

String **literals** are interned in a shared pool, so identical literals are the *same object*:

```java
String a = "hello", b = "hello";
a == b                  // true  - same pooled object
new String("hello") == a  // false - deliberately a distinct object
a.equals(...)           // true  - compares CONTENT
```

`==` therefore *appears* to work in simple tests and then fails on strings built at runtime (from
input, concatenation, or a file). That inconsistency is exactly why it is so dangerous.

> **Rule: always compare strings with `.equals()`.** Use `.equalsIgnoreCase()` for case-insensitive
> comparison. Reserve `==` for checking `null`.

## String vs StringBuilder vs StringBuffer

| | `String` | `StringBuilder` | `StringBuffer` |
|---|---|---|---|
| Mutable | no | **yes** | yes |
| Thread-safe | n/a (immutable) | no | yes (synchronized) |
| Speed | — | **fastest** | slower (locking overhead) |
| Use for | fixed text, keys, APIs | **building strings** | the rare cross-thread case |

**Default to `StringBuilder`.** `StringBuffer` pays a synchronization cost that is almost never
needed, since a builder is normally a local variable used by one thread.

> Note: the compiler already converts simple `a + b + c` into `StringBuilder` calls. It is the
> **loop** case it cannot fix, because each iteration is a separate expression.

## The methods worth knowing

| Purpose | Methods |
|---|---|
| Inspect | `length()`, `charAt(i)`, `isEmpty()`, `isBlank()` |
| Search | `indexOf`, `lastIndexOf`, `contains`, `startsWith`, `endsWith` |
| Extract | `substring(a)`, `substring(a, b)` — **`b` is exclusive** |
| Transform | `toUpperCase`, `toLowerCase`, `replace`, `trim` / `strip`, `repeat` |
| Split & join | `split(regex)`, `String.join(sep, parts)` |
| Build | `String.format`, `formatted`, text blocks (`"""`) |

Two details: `split` takes a **regex**, so `split(".")` splits on *every character* — escape it as
`split("\\.")`. And `strip()` (Java 11+) is Unicode-aware, unlike the older `trim()`.

## The `charAt(i) - 'a'` idiom

```java
int index = s.charAt(i) - 'a';   // maps 'a'..'z' to 0..25
```

Characters are numeric, so subtracting `'a'` yields a 0-based index — the standard way to build a
26-slot frequency table. Used in `Strings/anagramCheck.java` and throughout string algorithms. It
assumes lowercase ASCII; use a `HashMap` for arbitrary Unicode.

## When to use what

| Need | Use |
|---|---|
| Fixed text, map keys, method parameters | `String` |
| Building a string in a loop | **`StringBuilder`** |
| Compare content | `.equals()` / `.equalsIgnoreCase()` |
| Compare ignoring order/whitespace | normalize first (strip, lowercase, sort) |
| Complex patterns | regex (`Pattern`/`Matcher`) |
| Substring search in large text | `indexOf`, or KMP/Rabin-Karp (`Strings/`) |
| Prefix/autocomplete queries | a **Trie** (`Trees/Trie.java`) |

## When NOT to use String

- **As a mutable buffer** — that is what `StringBuilder` is for.
- **For money or precise decimals** — use `BigDecimal`.
- **For huge binary data** — use `byte[]`; String assumes character encoding.
- **As a poor man's enum** — magic strings have no compile-time checking. Use an `enum`.
- **For passwords** — an immutable String lingers in memory until GC. Use `char[]` so it can be
  zeroed.

## Files in this folder

| File | Covers |
|---|---|
| `StringBasics` | identity vs equality, the pool, immutability in action, the common methods |
| `StringBuilderDemo` | mutable building: `append`, `insert`, `reverse`, `delete`, `replace`; the O(n²) trap |

## Pitfalls

- `==` instead of `.equals()` — works in tests, fails in production.
- `+` concatenation inside a loop → quadratic.
- `substring`'s end index is **exclusive**.
- `split` takes a regex, not a literal.
- Ignoring the return value: `s.toUpperCase();` alone does nothing — you must assign it.
- `charAt` / `substring` throw `StringIndexOutOfBoundsException` — check `length()` first.

## Where this leads

Strings feed directly into `Data Structures with Java/Strings/` (KMP, Rabin-Karp, anagrams),
`Trees/Trie.java` (prefix search), and `DynamicProgramming/editDistance.java` (fuzzy matching) —
each of which builds on the immutability and indexing facts above.
