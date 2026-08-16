# Operators

Operators combine values into expressions. Java groups them into a handful of categories.

## Files

| File | Covers |
|------|--------|
| `operators.java` | Every operator category with worked examples |

## Categories

| Category | Operators | Notes |
|----------|-----------|-------|
| Arithmetic | `+ - * / %` | `/` truncates on ints; `%` is remainder |
| Unary inc/dec | `a++ ++a a-- --a` | **post** uses value then changes; **pre** changes then uses |
| Assignment | `= += -= *= /= %= &= \|= ^= <<= >>= >>>=` | `x += y` ≡ `x = x + y` |
| Relational | `> < >= <= == !=` | produce a `boolean` |
| Logical | `&& \|\| !` | `&&`/`\|\|` **short-circuit** |
| Ternary | `cond ? a : b` | compact if/else expression |
| Bitwise | `& \| ^ ~` | operate bit-by-bit |
| Shift | `<< >> >>>` | `<<` ×2ⁿ, `>>` ÷2ⁿ (sign-keeping), `>>>` zero-fill |

## Things that trip people up

- **Post vs pre increment**: `x++` returns the old value; `++x` returns the new one.
- **Short-circuit**: in `a && b`, if `a` is false, `b` is never evaluated — used to guard, e.g.
  `s != null && s.length() > 0`.
- **`>>` vs `>>>`**: for negative numbers, `>>` keeps the sign bit, `>>>` fills zeros. For
  non-negative numbers they behave the same.
- **`~x == -x - 1`** (two's-complement bit flip).

## Applications

- Bitwise/shift power flags, bitmasks, and fast ×/÷ by powers of two (heavily used in
  BitManipulation and Hashing). Short-circuit logic guards null/empty checks everywhere.
