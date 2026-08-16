# Bit Manipulation

Working directly with the binary representation of integers enables very fast, memory-light tricks.
Java integers are 32-bit (`int`) / 64-bit (`long`), signed, in two's-complement.

## Operators (see also `Java Core/02-Operators/`)

| Op | Name | Effect |
|----|------|--------|
| `&` | AND | 1 only where both bits are 1 |
| `\|` | OR | 1 where either bit is 1 |
| `^` | XOR | 1 where bits differ |
| `~` | NOT | flips every bit (`~x == -x-1`) |
| `<<` | left shift | ×2ⁿ |
| `>>` | arithmetic right shift | ÷2ⁿ, keeps sign |
| `>>>` | logical right shift | fills 0 (use for unsigned-style work) |

## Essential idioms

| Goal | Trick |
|------|-------|
| test bit i | `(n >> i) & 1` |
| set bit i | `n \| (1 << i)` |
| clear bit i | `n & ~(1 << i)` |
| toggle bit i | `n ^ (1 << i)` |
| lowest set bit | `n & (-n)` |
| clear lowest set bit | `n & (n - 1)` |
| is power of two | `n > 0 && (n & (n-1)) == 0` |

## Files

| File | Idea |
|------|------|
| `countSetBits.java` | popcount: naive vs Brian Kernighan (`n & (n-1)`) |
| `powerOfTwo.java` | single-set-bit test |
| `singleNumberXor.java` | XOR cancels pairs → find the lone element |

## Why XOR is special

`x ^ x = 0` and `x ^ 0 = x`. XOR is commutative and associative, so XOR-ing a collection cancels
every value that appears an even number of times — the basis of many O(1)-space tricks.

## Watch out

- Use `>>>` (not `>>`) when looping over the bits of a possibly-negative number, or the sign bit
  causes an infinite loop.
- `1 << 31` overflows a positive `int`; use `1L << 31` for large shifts.
- Prefer `Integer.bitCount`, `Integer.numberOfTrailingZeros`, `Long.highestOneBit` in real code —
  they compile to single instructions.

## Applications

- Bitmask DP and subset enumeration, flags/permissions, hashing, low-level graphics, compression,
  fast arithmetic (×/÷ by powers of two), and cryptography.
