package Questions;

// Problem  : Find the k-th SMALLEST element of an array (k is 1-based).
// Approach : EFFICIENT - QUICKSELECT. Partition like quicksort, but recurse into only the ONE side
//            that can contain position k-1.
// Intuition: Lomuto's partition puts the pivot at its final sorted index p for free. If p == k-1 we
//            are done. If p is too far right the answer lies left, and vice versa - so half the array
//            can be discarded each round WITHOUT sorting it. Quicksort recurses into both sides;
//            quickselect only needs one, which is what drops n log n to n.
// Time     : O(n) AVERAGE - work halves each round: n + n/2 + n/4 + ... = 2n. O(n^2) worst case
//            (consistently terrible pivots); randomizing the pivot makes that practically impossible.
// Space    : O(1) - iterative, in place
// Trade-off: Beats the sort-everything approach when you need ONE order statistic. It mutates the
//            array and gives no ordering guarantees elsewhere. For a STREAM or for repeated queries,
//            a size-k heap is the better tool (see Heap/kLargestElements.java).
// FIX NOTE : The loop condition was "while (l < r)", so a range that narrowed to a single element
//            exited without returning it and fell through to -1. Corrected to "l <= r".

public class kthSmallestElementEfficient {
    public static void main(String[] args) {
        System.out.println("Kth smallest element is: "
                + kthSmallest(new int[]{ 7, 10, 4, 3, 20, 15 }, 6, 3));   // expected: 7
        System.out.println("Smallest: "
                + kthSmallest(new int[]{ 7, 10, 4, 3, 20, 15 }, 6, 1));   // expected: 3
        System.out.println("Largest : "
                + kthSmallest(new int[]{ 7, 10, 4, 3, 20, 15 }, 6, 6));   // expected: 20
    }

    static int kthSmallest(int[] arr, int n, int k) {
        int l = 0, r = n - 1;

        // '<=' matters: when l == r the range holds exactly one element, which IS the answer.
        while (l <= r) {
            int p = lamPart(arr, l, r);   // p is the pivot's FINAL sorted index

            if (p == k - 1) {
                return arr[p];            // the pivot landed exactly on the position we want
            } else if (p > k - 1) {
                r = p - 1;                // target is to the LEFT - discard the right half entirely
            } else {
                l = p + 1;                // target is to the RIGHT
            }
        }
        return -1;                        // only reachable if k was out of range
    }

    // Standard Lomuto partition (see Sorting/lamutoPartition.java for the full explanation).
    static int lamPart(int arr[], int l, int h) {
        int pivot = arr[h];
        int i = l - 1;
        for (int j = l; j <= h - 1; j++) {
            if (arr[j] < pivot) {
                i++;
                int temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
            }
        }
        int temp = arr[i + 1]; arr[i + 1] = arr[h]; arr[h] = temp;
        return (i + 1);
    }
}
