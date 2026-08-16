// Problem  : Compute the GCD (greatest common divisor) and LCM (least common multiple) of two numbers.
// Approach : Two tiers for GCD. NAIVE tries every divisor down from min(a,b). EFFICIENT uses the
//            Euclidean algorithm: gcd(a, b) = gcd(b, a % b). LCM is derived from GCD.
// Intuition: Any common divisor of a and b also divides a % b (their remainder), so replacing the
//            larger with the remainder shrinks the problem fast without changing the GCD.
// Time     : naive O(min(a,b)); Euclid O(log(min(a,b)))   Space: O(1)
// Trade-off: Euclid is exponentially faster than trial division. LCM uses lcm(a,b) = a / gcd * b,
//            dividing FIRST to avoid overflow when a*b is large.

public class gcdLcm {

    // ---------- NAIVE GCD: trial division ----------
    static int gcdNaive(int a, int b) {
        int g = 1;
        for (int d = 1; d <= Math.min(a, b); d++) {
            if (a % d == 0 && b % d == 0) g = d; // keep the largest common divisor found
        }
        return g;
    }

    // ---------- EFFICIENT GCD: Euclidean algorithm ----------
    static int gcd(int a, int b) {
        while (b != 0) {
            int r = a % b;   // remainder carries all common divisors
            a = b;
            b = r;
        }
        return a;            // when b hits 0, a is the GCD
    }

    static long lcm(int a, int b) {
        // Divide before multiplying to reduce overflow risk (a/gcd is exact since gcd divides a).
        return (long) (a / gcd(a, b)) * b;
    }

    public static void main(String[] args) {
        System.out.println("gcd(48,18) = " + gcd(48, 18) + " / naive " + gcdNaive(48, 18)); // 6 / 6
        System.out.println("lcm(4,6)   = " + lcm(4, 6));   // 12
        System.out.println("gcd(17,5)  = " + gcd(17, 5));  // 1 (coprime)
    }
}
