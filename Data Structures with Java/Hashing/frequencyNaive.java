// Problem  : Print how many times each element occurs in an array.
// Approach : NAIVE - for every element, rescan the whole array counting matches. A "seen" check
//            prevents reporting the same value more than once.
// Intuition: Without a lookup structure there is no way to remember counts, so each element's total
//            must be recomputed from scratch by comparing it against all others.
// Time     : THETA(n^2) - n elements, each scanned against all n
// Space    : THETA(1) - no auxiliary structures
// Trade-off: Zero extra memory, but quadratic time. The EFFICIENT version uses a HashMap to do this
//            in a single O(n) pass - strictly better unless memory is severely constrained.
// FIX NOTE : The original version printed a line for EVERY element, so a value appearing 3 times was
//            reported 3 times. The inner "already reported" check below fixes that.

public class frequencyNaive {
    public static void main(String[] args) {
        int arr[] = { 1, 1, 2, 2, 3, 2, 1, 4, 5 };
        printFreq(arr, arr.length);
        // expected (each value reported ONCE):
        // 1 -> 3
        // 2 -> 3
        // 3 -> 1
        // 4 -> 1
        // 5 -> 1
    }

    static void printFreq(int arr[], int n) {
        for (int i = 0; i < n; i++) {

            // Skip this element if an identical value appeared EARLIER - that occurrence already
            // printed the count, and without this guard duplicates get reported repeatedly.
            boolean alreadyReported = false;
            for (int j = 0; j < i; j++) {
                if (arr[j] == arr[i]) { alreadyReported = true; break; }
            }
            if (alreadyReported) continue;

            // Count every occurrence of arr[i] across the WHOLE array (this rescan is the O(n) inner
            // cost that makes the algorithm quadratic overall).
            int count = 0;
            for (int j = 0; j < n; j++) {
                if (arr[i] == arr[j]) {
                    count++;
                }
            }
            System.out.println(arr[i] + " -> " + count);
        }
    }
}
