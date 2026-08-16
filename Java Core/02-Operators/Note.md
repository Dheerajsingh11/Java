# Operators

## What this covers

Operators are the verbs of expressions. Most are obvious; a handful have behaviour that causes real
bugs, and those are what this note focuses on.

## The categories

| Category | Operators | Produces |
|---|---|---|
| Arithmetic | `+ - * / %` | a number |
| Unary inc/dec | `a++ ++a a-- --a` | a number (with a side effect) |
| Assignment | `= += -= *= /= %= &= \|= ^= <<= >>= >>>=` | the assigned value |
| Relational | `> < >= <= == !=` | `boolean` |
| Logical | `&& \|\| !` | `boolean` (short-circuiting) |
| Ternary | `cond ? a : b` | either branch's value |
| Bitwise | `& \| ^ ~` | a number (bit-by-bit) |
| Shift | `<< >> >>>` | a number |

## The five things that actually cause bugs

### 1. Integer division truncates
`7 / 2 == 3`. There is no rounding and no warning. `(a + b) / 2` on ints is a floor, not an average.
Make one operand a `double` when you want a real quotient.

### 2. Pre vs post increment
```java
int a = 5;
System.out.println(a++);  // prints 5, THEN a becomes 6
System.out.println(++a);  // a becomes 7, THEN prints 7
```
**Post** returns the old value; **pre** returns the new one. The side effect happens either way — only
the *returned* value differs. Never combine two increments of the same variable in one expression;
the result is confusing even when well-defined.

### 3. Short-circuit evaluation — a feature, not an optimization
`&&` stops at the first `false`; `||` stops at the first `true`. The right-hand side **may never run**.

This is what makes guard conditions possible:
```java
if (s != null && s.length() > 0)     // safe: length() only runs if s isn't null
if (i < arr.length && arr[i] == x)   // safe: no out-of-bounds access
```
Reverse the order and you get a `NullPointerException` or `ArrayIndexOutOfBoundsException`. The
ordering is load-bearing.

`&` and `|` also work on booleans but do **not** short-circuit — both sides always evaluate. Use them
only when you genuinely want the side effects.

### 4. `>>` vs `>>>`
- `>>` **arithmetic** — preserves the sign bit (`-8 >> 1 == -4`)
- `>>>` **logical** — always fills with zeros (`-8 >>> 1` is a huge positive number)

For non-negative values they behave identically, which is why the difference is easy to miss until a
negative number appears. **When looping over the bits of a possibly-negative int, `>>` never
terminates** — the sign bit keeps refilling. Use `>>>`.

### 5. Precedence surprises
Bitwise `&`, `|`, `^` bind **more loosely than `==`**:
```java
if (flags & MASK == 0)     // parses as: flags & (MASK == 0)  -> compile error or wrong
if ((flags & MASK) == 0)   // correct
```
**Always parenthesize bitwise expressions.** When in doubt, add parentheses — they cost nothing and
document intent.

## `~x == -x - 1`

Two's complement means bitwise NOT is not negation. `~5` is `-6`, not `-5`. Worth memorizing because
the intuition ("flip the bits, get the negative") is wrong.

## Ternary: when it helps and when it hurts

`max = (a > b) ? a : b` is clearer than a four-line `if`. But ternaries **nest badly** — the
three-way max in `operators.java` is already at the edge of readability. If you need parentheses to
understand your own ternary, use `if/else`.

Unlike `if`, a ternary is an **expression**, so it can be assigned, passed as an argument, or returned
directly.

## Compound assignment has a hidden cast

`x += y` is *not* exactly `x = x + y` — it silently inserts a cast:
```java
byte b = 10;
b += 300;      // compiles! silently truncates
b = b + 300;   // compile error: possible lossy conversion
```
The compound form hides the narrowing. Occasionally convenient, occasionally a lurking bug.

## When to use bitwise operators

- **Flags/permissions** packed into one int: `perms | WRITE`, `perms & READ`.
- Genuine bit-level work: masks, hashing, protocol parsing, graphics.
- Algorithms where bits *are* the problem — see `Data Structures with Java/BitManipulation/`.

## When NOT to

- **As a speed trick in ordinary arithmetic.** `x << 1` instead of `x * 2` is a micro-optimization the
  JIT already performs, and it makes code harder to read. Optimize where you have measured, not on
  reflex.
- Where a library method is clearer: `Integer.bitCount`, `Math.floorDiv`, `Math.abs`.

## File in this folder

`operators.java` — every category with worked examples, including pre/post increment ordering,
short-circuit demonstration, the three shifts on negative numbers, and a nested ternary.

## Where this connects

Short-circuiting appears in every null/bounds guard in this repo. Integer division and overflow are
the root cause of the binary-search midpoint bug (`Searching/`) and comparator overflow
(`Collections/`). Bitwise operators are the whole subject of `BitManipulation/` and power the
Fenwick tree's `i & (-i)` (`Trees/`).
