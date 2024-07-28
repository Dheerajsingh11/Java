// Function to print the current element of the fibonacci sequence
public class fibonacci {
    public static void main(String[] args) {
        int n = 5;
        System.out.println(fib(n)); // calling the function to print the 5th element of fibonacci sequence
    }

    static int fib(int n) {
        if (n == 0) { // Base case 1
            return 0;// if n is 0, then return 0
        }
        if (n == 1) { // Base case 2
            return 1; // if n is 1, then return 1
        }
        return fib(n - 1) + fib(n - 2); // recursive call to return fibonacci series i.e. sum of previous 2 elements
    }
}

// If wrong base case is passed then it will cause issues like stack overflow
// due to infinite loop