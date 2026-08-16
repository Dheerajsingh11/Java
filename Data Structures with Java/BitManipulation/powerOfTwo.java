// Problem  : Decide whether a positive integer is a power of two (1, 2, 4, 8, 16, ...).
// Approach : EFFICIENT bit trick - a power of two has EXACTLY ONE set bit, so n & (n-1) == 0.
// Intuition: A power of two in binary is 1000...0. Subtracting 1 turns it into 0111...1, so ANDing
//            them gives 0. Any number with more than one set bit leaves a nonzero AND.
// Time     : O(1)   Space: O(1)
// Trade-off: A single operation versus an O(log n) division loop. Must guard n > 0, since 0 and
//            negatives would sneak past the bit test.

public class powerOfTwo {

    static boolean isPowerOfTwoNaive(int n) {
        if (n < 1) return false;
        while (n % 2 == 0) n /= 2; // strip factors of two
        return n == 1;             // power of two iff we end at exactly 1
    }

    static boolean isPowerOfTwo(int n) {
        // n > 0 guard: 0 & -1 == 0 would falsely report true; negatives are never powers of two.
        return n > 0 && (n & (n - 1)) == 0;
    }

    public static void main(String[] args) {
        for (int x : new int[]{ 1, 2, 3, 8, 16, 18, 0, -4 }) {
            System.out.println(x + " -> " + isPowerOfTwo(x) + " / " + isPowerOfTwoNaive(x));
        }
        // 1->true, 2->true, 3->false, 8->true, 16->true, 18->false, 0->false, -4->false
    }
}
