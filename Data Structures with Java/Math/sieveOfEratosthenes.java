// Problem  : List all prime numbers up to N.
// Approach : Two tiers. NAIVE tests each number for primality individually. EFFICIENT is the Sieve of
//            Eratosthenes: mark all multiples of each prime as composite in one sweep.
// Intuition: Instead of asking "is k prime?" for each k, start from the smallest prime and cross out
//            its multiples; whatever is never crossed out is prime. Each composite is eliminated by
//            its prime factors.
// Time     : naive O(n*sqrt(n)); sieve O(n log log n)   Space: O(n) boolean array
// Trade-off: The sieve is dramatically faster for listing many primes, at the cost of O(n) memory.
//            Start crossing from p*p (smaller multiples already handled by smaller primes).

import java.util.ArrayList;
import java.util.List;

public class sieveOfEratosthenes {

    static List<Integer> sieve(int n) {
        boolean[] composite = new boolean[n + 1]; // composite[k] == true means k is NOT prime
        List<Integer> primes = new ArrayList<>();

        for (int p = 2; p <= n; p++) {
            if (!composite[p]) {                   // p survived -> it is prime
                primes.add(p);
                // Cross out multiples starting at p*p (smaller ones already marked by smaller primes).
                // (long) guards against p*p overflowing int for large n.
                for (long m = (long) p * p; m <= n; m += p) {
                    composite[(int) m] = true;
                }
            }
        }
        return primes;
    }

    public static void main(String[] args) {
        System.out.println(sieve(30)); // [2, 3, 5, 7, 11, 13, 17, 19, 23, 29]
        System.out.println("primes up to 100: " + sieve(100).size()); // 25
    }
}
