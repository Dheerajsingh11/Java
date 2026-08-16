package Questions;

// Problem  : Count INVERSIONS - pairs (i, j) with i < j but arr[i] > arr[j].
// Approach : EFFICIENT - piggyback on MERGE SORT. Inversions are counted during the merge step, in
//            bulk, while the array is being sorted.
// Intuition: THE KEY INSIGHT: during a merge, both halves are already sorted. If we take an element
//            from the RIGHT half, it is smaller than the current left element - and therefore smaller
//            than EVERY remaining element in the left half, since the left half is sorted. So that
//            one comparison reveals (n1 - i) inversions at once, instead of finding them one by one.
// Time     : O(n log n) - the merge sort recurrence T(n) = 2T(n/2) + O(n)
// Space    : O(n) - the temporary L/R arrays used by the merge
// Trade-off: Dramatically better than the naive O(n^2) for large arrays. The price is O(n) extra
//            memory and a MUTATED (sorted) input array. This "count while you merge" idea is the
//            classic example of getting extra information for free from a divide-and-conquer sort.

public class totalInversionsEfficient {
    public static void main(String[] args) {
        int arr[] = { 8, 4, 2, 1, 5, 6, 3, 7 };
        System.out.println("Total inversions are: " + inversions(arr, 0, arr.length - 1)); // 13
        int b[] = { 3, 2, 1 };
        System.out.println(inversions(b, 0, b.length - 1)); // expected: 3 (matches the naive count)
    }

    // Sorts arr[left..right] AND returns the number of inversions found within it.
    static int inversions(int[] arr, int left, int right) {
        int count = 0;

        if (left < right) {                       // base case: a 0/1-element range has no inversions
            int m = left + (right - left) / 2;    // overflow-safe midpoint

            count += inversions(arr, left, m);        // inversions entirely inside the left half
            count += inversions(arr, m + 1, right);   // inversions entirely inside the right half
            count += countmerge(arr, left, m, right); // inversions SPANNING the two halves
            // Every inversion falls into exactly one of these three categories, so the sum is exact.
        }
        return count;
    }

    static int countmerge(int[] arr, int left, int m, int right) {
        int n1 = m - left + 1;
        int n2 = right - m;
        int L[] = new int[n1];
        int R[] = new int[n2];

        for (int i = 0; i < n1; i++) L[i] = arr[left + i];
        for (int i = 0; i < n2; i++) R[i] = arr[m + 1 + i];

        int count = 0, i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                arr[k] = L[i];     // no inversion: the left element is correctly ordered first
                i++;
            } else {
                arr[k] = R[j];
                j++;
                // R[j] jumped ahead of L[i]..L[n1-1]. Because L is SORTED, every one of those
                // (n1 - i) remaining left elements is also greater than R[j] - so this single
                // comparison accounts for (n1 - i) inversions at once. That bulk counting is
                // exactly what makes the algorithm O(n log n) instead of O(n^2).
                count += (n1 - i);
            }
            k++;
        }

        // Drain the leftovers - these contribute no further inversions.
        while (i < n1) { arr[k] = L[i]; i++; k++; }
        while (j < n2) { arr[k] = R[j]; j++; k++; }

        return count;
    }
}
