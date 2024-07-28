// Program to print from N to 1
public class printNto1 {

    public static void main(String[] args) {
        int n = 5;
        prrintNto1(n); // calling the function to print from N to 1
    }

    static void prrintNto1(int n) {
        if (n == 0) { // Base case
            return; // if n is 0 then there is nothing to print
        }
        System.out.println(n); // print the current element
        prrintNto1(n - 1); // recursive call to print the n-1 element
    }
}

// Time Complexity: THETA(n)
// Space complexity: THETA(n) - because all the recursive call with store in
// call stack