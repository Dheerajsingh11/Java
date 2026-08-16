# Math / Number Theory

Common numeric algorithms that appear throughout DSA, competitive programming, and cryptography.

## Files

| File | Problem | Efficient time |
|------|---------|----------------|
| `gcdLcm.java` | GCD (Euclid) & LCM | O(log min(a,b)) |
| `primeCheck.java` | is one number prime | O(√n) |
| `sieveOfEratosthenes.java` | all primes up to N | O(n log log n) |
| `fastExponentiation.java` | base^exp (mod m) | O(log exp) |

## Key results

- **Euclid's algorithm**: `gcd(a, b) = gcd(b, a % b)`. `lcm(a, b) = a / gcd(a, b) * b`
  (divide first to avoid overflow).
- **Primality**: a composite n has a factor ≤ √n, so testing up to √n suffices. To test *many*
  numbers, sieve once instead of checking each.
- **Fast exponentiation**: use the binary expansion of the exponent — square the base per bit,
  multiply into the result on set bits.

## Overflow discipline (very important in Java)

- `int` overflows silently around ±2.1 billion. Use `long` for products/powers, or take `% mod`
  after each multiplication in modular arithmetic.
- In the sieve, `p * p` can overflow `int` — compute it as `(long) p * p`.

## Choosing

- One primality test → √n check. Many primes / repeated queries → sieve.
- Big exponents (especially modular) → fast exponentiation, never the naive loop.

## Applications

- Cryptography (modular exponentiation, RSA), hashing (Rabin-Karp uses modular powers), fraction
  reduction (GCD), combinatorics, and number-theoretic competitive-programming problems.
