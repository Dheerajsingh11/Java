// Problem  : Sum all the decimal digits of a number (e.g. 12345 -> 1+2+3+4+5 = 15).
// Approach : Peel off the last digit with n % 10, then recurse on the remaining number n / 10.
// Intuition: Integer division by 10 removes the last digit; modulo 10 extracts it. So "sum of digits
//            of n" = "last digit" + "sum of digits of everything before it" - a clean recurrence.
// Time     : THETA(d) where d = number of digits (d = floor(log10 n) + 1, so it is logarithmic in n)
// Space    : THETA(d) for the call stack (the iterative version below is THETA(1))
// Trade-off: The recursion is compact and mirrors the definition, but the iterative loop achieves the
//            same O(d) time with O(1) space and no stack risk - prefer it in practice. Note d grows
//            only logarithmically with n, so even huge numbers recurse only ~10-19 levels deep.

public class sumOfDigits {
    public static void main(String[] args) {
        int n = 12345;
        System.out.println(getSum(n));          // expected: 15
        System.out.println(getSumIterative(n)); // expected: 15 (same answer, O(1) space)
        System.out.println(getSum(0));          // expected: 0  (base case fires immediately)
        System.out.println(getSum(7));          // expected: 7  (single digit)
    }

    static int getSum(int n) {
        // BASE CASE: no digits left to add once the number has been fully divided down to 0.
        if (n == 0) {
            return 0;
        }
        // RECURSIVE CASE:
        //   n / 10  -> drops the last digit   (12345 -> 1234)
        //   n % 10  -> extracts the last digit (12345 -> 5)
        return getSum(n / 10) + n % 10;
    }

    // Iterative equivalent: identical logic driven by a loop instead of the call stack.
    static int getSumIterative(int n) {
        int sum = 0;
        while (n != 0) {
            sum += n % 10;   // take the last digit
            n /= 10;         // then remove it
        }
        return sum;
    }
    // Edge: for NEGATIVE n both versions misbehave (% returns a negative remainder and the loop
    // never reaches 0 cleanly). Guard with n = Math.abs(n) first if negatives are possible.
}
