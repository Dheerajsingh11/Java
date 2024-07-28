// Program to convert decimal to binary
public class decimalToBinary {

    public static void main(String[] args) {
        int n = 10;
        decToBin(n); // calling function to convert decimal to binary
    }

    static void decToBin(int n) {
        if (n == 0) { // Base case
            return; // if n is 0 then return 0
        }
        decToBin(n / 2); // call function for n/2
        System.out.println(n % 2); // printing the n%2 in reverse order to print the binary representation
    }
}

// Time complexity: O(logn)