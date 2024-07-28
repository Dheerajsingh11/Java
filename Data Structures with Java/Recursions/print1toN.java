// program to print from 1 to N
public class print1toN {

    public static void main(String[] args) {
        int n = 5;
        printNto1(n); // calling function to print from 1 to N
    }

    static void printNto1(int n) {
        if (n == 0) { // Base case
            return; // if n is 0 then there is nothing to print
        }
        printNto1(n - 1); // calling function to till n-1 times more
        System.out.println(n); // print the current element which will begin from the last element because the
                               // print function will not be called till the last recursive function is reached
    }
}

// Time Complexity: THETA(n)
// Space complexity: O(n)
// ----------------------------IMPORTANT NOTE----------------------------------
// It is not tail recursive, as it have a print function to call after the
// recursive function call which will take bit more time than tail recursive
// to make it tail recursive the below function will work fine:
/*
 * static void printNto1(int n , int k) {
 * if (n == 0) { // Base case
 * return; // if n is 0 then there is nothing to print
 * }
 * System.out.println(k); // print the current k
 * printNto1(n - 1, k+1); // calling function to print k+1 element
 * }
 */