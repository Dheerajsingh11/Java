// Problem  : Print the binary representation of a decimal number.
// Approach : Recursively divide by 2, printing the remainders on the way BACK UP the call stack.
// Intuition: Repeatedly dividing by 2 produces the binary digits - but in REVERSE order (least
//            significant first). Recursing before printing defers every output until unwinding, which
//            naturally reverses them back into the correct most-significant-first order.
// Time     : O(log n) - each call halves n, so there are log2(n) calls (one per bit)
// Space    : O(log n) - one frame per bit
// Trade-off: The recursion elegantly solves the "digits come out backwards" problem for free. An
//            iterative version would need a StringBuilder (and a .reverse()) or a stack to achieve
//            the same ordering - which is really just simulating this call stack by hand.

public class decimalToBinary {

    public static void main(String[] args) {
        System.out.print("10 in binary = ");
        decToBin(10);
        System.out.println();      // expected: 1010

        System.out.print("7 in binary  = ");
        decToBin(7);
        System.out.println();      // expected: 111

        System.out.print("0 in binary  = ");
        decToBin(0);               // prints nothing - see the edge-case note below
        System.out.println("(nothing printed for 0)");
    }

    static void decToBin(int n) {
        // BASE CASE: nothing left to convert once we have divided down to 0.
        if (n == 0) {
            return;
        }

        decToBin(n / 2);              // recurse FIRST - drives all the way to the most significant bit
        System.out.print(n % 2);      // print while UNWINDING, so bits emerge MSB -> LSB
    }
    // Edge case: decToBin(0) prints nothing, because the base case fires immediately. If you want
    // "0" displayed for zero, special-case it at the call site (or use Integer.toBinaryString(n),
    // which is the library way to do this).
}
