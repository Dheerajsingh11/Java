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
