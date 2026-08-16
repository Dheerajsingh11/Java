// Problem  : Compute base^exp (optionally mod m) efficiently.
// Approach : Two tiers. NAIVE multiplies exp times (O(exp)). EFFICIENT uses binary/fast exponentiation
//            by squaring (O(log exp)).
// Intuition: x^exp can be split by the bits of exp: square the base each step, and multiply it into
//            the result whenever the current lowest bit of exp is 1. This halves the exponent each
//            round instead of decrementing it.
// Time     : naive O(exp); efficient O(log exp)   Space: O(1)
// Trade-off: Fast exponentiation is exponentially faster and essential for large exponents (e.g.
//            modular exponentiation in cryptography/hashing). Use 'long' and mod to avoid overflow.

public class fastExponentiation {

    static long naive(long base, int exp) {
        long result = 1;
        for (int i = 0; i < exp; i++) result *= base; // exp multiplications
        return result;
    }

    // (base^exp) % mod via squaring. Handles large exponents without overflow (mod each step).
    static long powMod(long base, long exp, long mod) {
        long result = 1 % mod;
        base %= mod;
        while (exp > 0) {
            if ((exp & 1) == 1) {            // current lowest bit of exp is 1 -> include this power
                result = (result * base) % mod;
            }
            base = (base * base) % mod;      // square the base for the next bit
            exp >>= 1;                       // move to the next bit of the exponent
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(naive(2, 10));            // 1024
        System.out.println(powMod(2, 10, 1000));     // 24  (1024 % 1000)
        System.out.println(powMod(3, 200, 1_000_000_007L)); // large modular power, computed fast
    }
}
