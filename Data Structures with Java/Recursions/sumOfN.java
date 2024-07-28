//Program for sum of N natural numbers
public class sumOfN {
    public static void main(String[] args) {
        int n = 5;
        int sum = sumN(n); // calling the function to return sum of 5 natural numbers
        System.out.println("Sum of first " + n + " natural numbers is: " + sum);
    }

    static int sumN(int n) {
        if (n == 0) { // Base case
            return 0; // if n is 0, then return 0
        }
        return n + sumN(n - 1); // recursive call to add n and n-1 till n is 0
    }
}

// T(n) = T(n-1) + THETA(1)
//Time complexity: THETA(n)
//Auxiliary space: THETA(n)