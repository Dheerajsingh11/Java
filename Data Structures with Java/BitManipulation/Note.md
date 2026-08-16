# Bit Manipulation

## What it is

Operating directly on the **binary representation** of integers rather than on their numeric value.
Java's `int` is 32 bits and `long` is 64, both **signed** and stored in **two's complement**.

## Why bother

Three genuine reasons — the first is the weakest and the last is the strongest:

1. **Speed.** Bitwise ops are single CPU instructions. `x << 1` beats `x * 2`. *But* modern compilers
   already do this — writing `x << 1` for speed is usually premature and hurts readability.
2. **Memory.** A `boolean[64]` costs ~64 bytes; one `long` holds the same 64 flags in 8. For huge
   sets (bloom filters, bitsets over millions of items) this is decisive.
3. **Expressiveness — the real reason.** Some problems are *naturally* about bits, and the bitwise
   solution is not just faster but conceptually simpler: subset enumeration, XOR-based cancellation,
   parity, and bitmask DP.

## Two's complement — the thing to actually understand

Negative numbers are stored as "flip all bits, add 1". Consequences that bite:

- `~x == -x - 1`. So `~5 == -6`, not `-5`.
- The **sign bit** is just the top bit, which is why `>>` and `>>>` differ.
- `Integer.MIN_VALUE` has no positive counterpart, so `Math.abs(Integer.MIN_VALUE)` is *negative*.
- Overflow **wraps silently** — no exception.

## The three shifts

| Operator | Name | Behaviour | Use |
|---|---|---|---|
| `<<` | left shift | multiply by 2ⁿ | scaling, building masks |
| `>>` | arithmetic right | divide by 2ⁿ, **keeps the sign** | signed division |
| `>>>` | logical right | fills with **zeros** | treating bits as unsigned |

**Critical:** when looping over the bits of a possibly-negative number, use `>>>`. With `>>` the sign
bit keeps refilling and the loop **never terminates** — a classic hang, and exactly why
`countSetBits.naive` uses `>>>=`.

## The idiom table

| Goal | Expression | Why it works |
|---|---|---|
| Test bit *i* | `(n >> i) & 1` | shift it to position 0, mask off the rest |
| Set bit *i* | `n \| (1 << i)` | OR forces that bit to 1 |
| Clear bit *i* | `n & ~(1 << i)` | AND with a mask of all-1s except that bit |
| Toggle bit *i* | `n ^ (1 << i)` | XOR with 1 flips |
| **Isolate lowest set bit** | `n & (-n)` | two's complement makes `-n` the flip+1, so only the lowest set bit survives |
| **Clear lowest set bit** | `n & (n - 1)` | borrowing turns the lowest 1 into 0 |
| Is a power of two | `n > 0 && (n & (n-1)) == 0` | powers of two have exactly **one** set bit |
| Is odd | `(n & 1) == 1` | cheaper and safer than `% 2` for negatives |

`n & (n-1)` and `n & (-n)` are the two worth memorizing — they appear constantly. `n & (n-1)` powers
**Brian Kernighan's** popcount (loop once per *set bit*, not once per bit) and `n & (-n)` powers the
**Fenwick tree** (`Trees/FenwickTree.java`), where it encodes the range each slot covers.

## Why XOR is the star

Two properties: `x ^ x = 0` and `x ^ 0 = x`. XOR is also commutative and associative, so **order does
not matter**.

Therefore XOR-ing a whole collection **cancels every value appearing an even number of times**. That
gives `singleNumberXor.java`: find the one unpaired element in **O(n) time and O(1) space** — no hash
set, no sorting. The naive approach needs O(n) memory; XOR needs a single variable.

The same cancellation underpins: swapping without a temp, parity checks, simple encryption, RAID
parity, and finding a missing number in `1..n`.

## When to use bit manipulation

- **Flags / permissions** packed into one integer (`READ | WRITE`).
- **Subset enumeration**: iterate `0 .. 2ⁿ-1` and let bit *j* mean "element j included" — this is the
  loop-based twin of the recursion in `Backtracking/subsets.java`, and the basis of **bitmask DP**.
- **Space-critical sets** (`java.util.BitSet`, bloom filters).
- Problems explicitly about parity, XOR, or powers of two.
- Low-level work: hashing, compression, graphics, protocol parsing.

## When NOT to use it

- **For "cleverness" in ordinary arithmetic.** `x << 1` instead of `x * 2` makes code harder to read
  for a gain the JIT already provides. Optimize only where measured.
- When a library call is clearer *and* faster: `Integer.bitCount`,
  `Integer.numberOfTrailingZeros`, `Long.highestOneBit` compile to single hardware instructions —
  they beat hand-written loops. **Write the loop to learn it, call the library in production.**
- When it obscures intent for the next reader (often yourself).

## Files in this folder

| File | Covers |
|---|---|
| `countSetBits` | popcount: naive bit-by-bit vs **Brian Kernighan** (`n & (n-1)`), plus `Integer.bitCount` |
| `powerOfTwo` | single-set-bit test, and why the `n > 0` guard is required |
| `singleNumberXor` | XOR cancellation — O(n) time, **O(1) space** vs the hash-set approach |

## Pitfalls

- Using `>>` instead of `>>>` when iterating bits of a negative number → **infinite loop**.
- `1 << 31` overflows a positive `int`; use `1L << 31` for 64-bit masks.
- Operator precedence: `&`, `|`, `^` bind **looser** than `==`. `a & b == c` parses as `a & (b == c)`.
  **Always parenthesize.**
- Forgetting `n > 0` in the power-of-two test — `0 & -1 == 0` would report true.
- Assuming `~x == -x`.

## Where bit manipulation is used

File permissions (Unix `rwx` bits), feature flags, network protocol headers and IP masking,
compression (Huffman codes are bit strings), cryptography, graphics (colour channel packing), chess
engines (bitboards — one `long` per piece type), hash functions, and bitmask DP for
travelling-salesman-style problems.
