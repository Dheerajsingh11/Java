package Questions;

// Problem  : Sort an array containing only the values 0, 1 and 2.
// Approach : NAIVE - three passes into a temporary array (all 0s, then all 1s, then all 2s), then
//            copy back.
// Intuition: With only three distinct values there is nothing to compare - we can simply collect
//            each value's occurrences in order. This is really a counting sort written out longhand.
// Time     : O(n) - four linear traversals   Space: O(n) - the temporary array
// Trade-off: Already linear, so the EFFICIENT version does not improve the big-O. What it improves is
//            just as important: it uses O(1) space and needs only ONE pass instead of four. See
//            sortArr012Efficient.java (the Dutch National Flag algorithm).

public class sortArr012Naive {
    public static void main(String[] args) {
        int arr[] = { 0, 1, 1, 0, 1, 2, 2, 2, 0, 1 };
        sort012(arr, arr.length);
        System.out.println(java.util.Arrays.toString(arr));
        // expected: [0, 0, 0, 1, 1, 1, 1, 2, 2, 2]
    }

    static void sort012(int arr[], int n) {
        int temp[] = new int[n];
        int i = 0;                    // write cursor into temp

        // PASS 1 - every 0 goes to the front.
        for (int j = 0; j < n; j++) if (arr[j] == 0) temp[i++] = arr[j];
        // PASS 2 - then every 1.
        for (int j = 0; j < n; j++) if (arr[j] == 1) temp[i++] = arr[j];
        // PASS 3 - then every 2.
        for (int j = 0; j < n; j++) if (arr[j] == 2) temp[i++] = arr[j];

        // PASS 4 - copy the result back into the caller's array.
        for (int j = 0; j < n; j++) arr[j] = temp[j];
    }
    // Because each pass preserves the original relative order within a value group, this version is
    // STABLE - relevant if the values were keys attached to real records.
}
