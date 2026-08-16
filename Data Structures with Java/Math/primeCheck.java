// Problem  : Test whether a single number is prime.
// Approach : Three tiers. NAIVE checks divisors up to n-1. MEDIUM stops at n/2. EFFICIENT checks only
//            up to sqrt(n), skipping evens after 2.
// Intuition: Divisors come in pairs (d, n/d) straddling sqrt(n), so if no divisor exists at or below
//            sqrt(n), none exists above it either. That collapses the work from O(n) to O(sqrt(n)).
// Time     : naive O(n); medium O(n/2); efficient O(sqrt(n))   Space: O(1)
// Trade-off: The sqrt bound is the standard single-number test. For testing MANY numbers up to N,
//            precompute with the Sieve instead (see sieveOfEratosthenes.java).

public class primeCheck {

    static boolean naive(int n) {
        if (n < 2) return false;
        for (int d = 2; d < n; d++) if (n % d == 0) return false;
        return true;
    }

    static boolean medium(int n) {
        if (n < 2) return false;
        for (int d = 2; d <= n / 2; d++) if (n % d == 0) return false; // a factor can't exceed n/2
        return true;
    }

    static boolean efficient(int n) {
        if (n < 2) return false;
        if (n < 4) return true;             // 2 and 3 are prime
        if (n % 2 == 0) return false;       // rule out all evens once
        for (int d = 3; (long) d * d <= n; d += 2) { // only odd divisors up to sqrt(n)
            if (n % d == 0) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        for (int x : new int[]{ 1, 2, 3, 4, 17, 20, 97, 100 }) {
            System.out.println(x + " -> " + efficient(x));
        }
        // 1->false, 2->true, 3->true, 4->false, 17->true, 20->false, 97->true, 100->false
    }
}
