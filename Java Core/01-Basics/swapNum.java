// Problem  : Swap the values held by two variables.
// Approach : Show three ways - a temporary variable (clearest), arithmetic (no temp), and XOR.
// Intuition: "Swapping" just means each variable ends up holding what the other held. The only
//            trick is not to overwrite one value before you have safely stashed it.
// Time     : O(1)   Space: O(1)
// Trade-off: The temp-variable version is the one to use in real code - it is clear and works for
//            any type. The arithmetic/XOR tricks avoid a temp but are error-prone (overflow,
//            aliasing) and only work for numbers; they are shown for understanding, not for use.

public class swapNum {
    public static void main(String[] args) {
        int a = 10;
        int b = 20;
        System.out.println("Before: a = " + a + ", b = " + b);

        // ---- Method 1: temporary variable (recommended) ----
        // Stash a's value so it is not lost when we overwrite a.
        int temp = a;   // temp = 10
        a = b;          // a = 20 (a's old value is safe in temp)
        b = temp;       // b = 10
        System.out.println("After temp-swap: a = " + a + ", b = " + b); // a = 20, b = 10

        // ---- Method 2: arithmetic, no temp ----
        // Works but can OVERFLOW if a + b exceeds int range. Also breaks if a and b are the SAME
        // variable (aliased), which cannot happen with two locals but can with array elements.
        a = a + b;      // a = 30 (holds the sum)
        b = a - b;      // b = 30 - 10 = 20  (original a)
        a = a - b;      // a = 30 - 20 = 10  (original b)
        System.out.println("After arithmetic-swap: a = " + a + ", b = " + b); // back to a = 10, b = 20

        // ---- Method 3: XOR, no temp (integers only) ----
        // XOR is self-inverse: x ^ y ^ y == x. No overflow risk, but unreadable and it zeroes the
        // value if a and b are aliased (same memory) - so avoid in practice.
        a = a ^ b;      // a = 10 ^ 20
        b = a ^ b;      // b = (10 ^ 20) ^ 20 = 10
        a = a ^ b;      // a = (10 ^ 20) ^ 10 = 20
        System.out.println("After XOR-swap: a = " + a + ", b = " + b); // a = 20, b = 10
    }
}
