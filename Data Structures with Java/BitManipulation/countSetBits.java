// Problem  : Count the number of 1-bits (set bits / "population count") in an integer.
// Approach : Two tiers. NAIVE checks every bit position (O(number of bits)). EFFICIENT uses
//            Brian Kernighan's trick to loop only ONCE PER SET BIT.
// Intuition: n & (n-1) clears the LOWEST set bit of n. So repeatedly doing that counts exactly the
//            set bits, skipping all the zeros in between.
// Time     : naive O(32); efficient O(number of set bits)   Space: O(1)
// Trade-off: Kernighan's wins when few bits are set. In real code, Integer.bitCount(n) is a single
//            hardware instruction and should be preferred - these are for understanding.

public class countSetBits {

    // ---------- NAIVE: test every bit ----------
    static int naive(int n) {
        int count = 0;
        while (n != 0) {
            count += (n & 1);   // add the lowest bit (0 or 1)
            n >>>= 1;           // UNSIGNED shift so a negative n's sign bit fills with 0 (else infinite loop)
        }
        return count;
    }

    // ---------- EFFICIENT: Brian Kernighan ----------
    static int kernighan(int n) {
        int count = 0;
        while (n != 0) {
            n &= (n - 1);       // drop the lowest set bit
            count++;            // one iteration per set bit only
        }
        return count;
    }

    public static void main(String[] args) {
        System.out.println(naive(13) + " " + kernighan(13));   // 3 3  (1101)
        System.out.println(naive(255) + " " + kernighan(255)); // 8 8
        System.out.println(kernighan(-1));                     // 32 (all bits set)
        System.out.println("library: " + Integer.bitCount(13)); // 3
    }
}
