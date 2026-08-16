# Math / Number Theory

## What it is

Numeric algorithms that recur throughout computer science: divisibility, primality, and fast
exponentiation. Small in code, disproportionately useful.

## Why these specific algorithms

Each one replaces a naive loop with a **structural insight**, and the speedups are dramatic:

| Problem | Naive | Better | Insight |
|---|---|---|---|
| GCD | test every divisor, O(min(a,b)) | **Euclid, O(log n)** | common divisors survive the remainder |
| Is n prime? | test up to n, O(n) | **O(√n)** | divisors come in pairs around √n |
| All primes ≤ N | test each, O(N√N) | **Sieve, O(N log log N)** | eliminate multiples instead of testing |
| bᵉ | multiply e times, O(e) | **O(log e)** | square repeatedly, using e's binary digits |

These are worth knowing not as trivia but because each demonstrates a reusable idea: reduce, bound,
eliminate in bulk, and halve.

## Euclid's algorithm — why it works

`gcd(a, b) = gcd(b, a % b)`

**Why:** any number dividing both `a` and `b` also divides `a % b` (which is `a - qb`). So the set of
common divisors is unchanged, while the numbers shrink rapidly. It terminates when `b` hits 0, and
`a` is then the GCD.

`lcm(a, b) = a / gcd(a, b) * b` — **divide before multiplying**, because `a * b` can overflow even
when the LCM comfortably fits.

## Primality: why √n is enough

If `n = p × q` then one of `p, q` must be ≤ √n. So if no divisor exists at or below √n, none exists
above it either. Checking 2 separately and then only **odd** divisors halves the work again.

For n = 1,000,000 that is ~500 checks instead of a million.

## The Sieve: eliminate rather than test

To find *many* primes, stop asking "is k prime?" one at a time. Start from the smallest prime and
**cross out its multiples**; whatever is never crossed out is prime.

Two details in the implementation:
- Start crossing at **p²**, not 2p — smaller multiples were already removed by smaller primes.
- Compute `p * p` as `(long) p * p`, or it **overflows `int`** for large N.

The O(N log log N) bound comes from summing N/p over all primes p ≤ N — a famously slow-growing series.

## Fast exponentiation — the binary insight

To compute `b^e`, look at `e` in binary. Square the base at each step and multiply it into the result
whenever the current bit is 1:

```
3^13, 13 = 1101₂  ->  3^8 · 3^4 · 3^1
```

13 multiplications become 4. For e = 1,000,000 it is ~20 instead of a million.

**Modular exponentiation** (`powMod`) takes `% mod` after every multiplication, which keeps values
small and prevents overflow. This is the operation RSA is built on, and it is why public-key
cryptography is computationally feasible at all.

## Overflow: the recurring hazard in this folder

Java's `int` silently wraps past ~2.1 billion. In numeric code this bites constantly:

- `a * b` for LCM → divide first, or use `long`.
- `p * p` in the sieve → cast to `long`.
- `base * base` in exponentiation → use `long` and take the modulus each step.
- `Math.abs(Integer.MIN_VALUE)` is **negative**.

**Default to `long` in numeric algorithms** unless you have proven the range is safe.

## When to use what

| Need | Use |
|---|---|
| GCD / reduce a fraction | Euclid (`gcdLcm`) |
| Test **one** number for primality | √n check (`primeCheck`) |
| **Many** primes, or repeated queries up to N | **Sieve** (`sieveOfEratosthenes`) |
| Large powers, especially modular | fast exponentiation (`fastExponentiation`) |
| Cryptographic-strength primality | Miller-Rabin (probabilistic) — beyond this folder |

**The one-vs-many rule:** a single primality test → √n. Testing thousands of numbers up to N → sieve
once, then answer each in O(1).

## When NOT to use these

- **Sieve with huge N** — it needs O(N) memory. For N = 10¹² use a segmented sieve or per-number tests.
- **`int` arithmetic** anywhere values can approach 2³¹.
- **Hand-rolled crypto.** These teach the mathematics; real systems must use vetted libraries
  (`java.security`, `BigInteger.modPow`). Textbook implementations leak timing information.
- Naive exponentiation for large exponents — it is not merely slower, it overflows immediately.

## Files in this folder

| File | Problem | Efficient time |
|---|---|---|
| `gcdLcm` | GCD (trial division vs **Euclid**) and LCM | O(log min(a,b)) |
| `primeCheck` | primality in three tiers: O(n) → O(n/2) → **O(√n)** | O(√n) |
| `sieveOfEratosthenes` | all primes up to N | O(N log log N) |
| `fastExponentiation` | `bᵉ` and `bᵉ mod m` by squaring | O(log e) |

## Where these are used

**Cryptography** — RSA and Diffie-Hellman are modular exponentiation; **hashing** — Rabin-Karp uses
modular powers for its rolling hash (`Strings/rabinKarp.java`); fraction arithmetic and aspect ratios
(GCD); random number generators; competitive programming (combinatorics under a prime modulus);
checksums; and scheduling problems built on LCM cycles.
