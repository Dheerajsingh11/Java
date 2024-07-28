//Program to return the sum of all the digits of a number
public class sumOfDigits {
    public static void main(String[] args) {
        int n = 12345;
        System.out.println(getSum(n)); // calling function to return sum of the digits
    }

    static int getSum(int n) {
        if (n == 0) { // Base case
            return 0; // if n is 0 then return 0
        } else {
            return getSum(n / 10) + n % 10; // else recursive call of getSum for n/10 + add n%10 to it
        }
    }
}
// Time complexity: THETA(d)
// Auxiliary space: THETA(d) - d: number of digits
/*
 * Iterative Solution
 * static int getSumIterative(int n) {
 * int sum = 0;
 * while (n != 0) {
 * sum += n % 10; // add last digit to sum
 * n /= 10; // remove last digit
 * }
 * return sum;
 * }
 * }
 */
// Time complexity: O(d)
// Auxiliary space: O(1) - constant space
// d is the number of digits in the given number n.
// The iterative solution has a time complexity of O(d) and an auxiliary space
// of O(1).
// The recursive solution has a time complexity of O(d) and an auxiliary space
// of O(d) due to the call stack.
// The iterative solution is more efficient for large numbers compared to the
// recursive solution.