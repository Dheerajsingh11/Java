// Problem  : For each element, find the next element to its RIGHT that is strictly greater; use -1
//            if none exists.
// Approach : NAIVE - for each index i, scan everything to its right until a greater value is found.
// Intuition: The definition itself is "look right until something bigger" - so the brute force is
//            literally that double loop.
// Time     : O(n^2) - for each of n elements we may scan up to n others
// Space    : O(n) for the answer array (O(1) beyond the output)
// Trade-off: Dead simple, but quadratic. The Efficient version uses a stack to answer all queries
//            in a single O(n) pass.

import java.util.Arrays;

public class nextGreaterElementNaive {

    static int[] nextGreater(int[] a) {
        int n = a.length;
        int[] res = new int[n];
        for (int i = 0; i < n; i++) {
            res[i] = -1;                     // default: assume nothing greater to the right
            for (int j = i + 1; j < n; j++) { // scan strictly to the right of i
                if (a[j] > a[i]) {
                    res[i] = a[j];           // first greater one wins - stop scanning
                    break;
                }
            }
        }
        return res;
    }

    public static void main(String[] args) {
        int[] a = { 4, 5, 2, 25 };
        System.out.println(Arrays.toString(nextGreater(a))); // [5, 25, 25, -1]
        // 4 -> next greater is 5; 5 -> 25; 2 -> 25; 25 -> nothing to the right -> -1
        int[] b = { 13, 7, 6, 12 };
        System.out.println(Arrays.toString(nextGreater(b))); // [-1, 12, 12, -1]
    }
}
