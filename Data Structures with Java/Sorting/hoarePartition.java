// Problem  : Partition an array in place around a pivot, returning a SPLIT POINT.
// Approach : HOARE scheme - pivot is the FIRST element; two pointers start outside opposite ends and
//            move toward each other, swapping the out-of-place pairs they find.
// Intuition: i scans right for something that belongs on the right side (>= pivot), j scans left for
//            something that belongs on the left (<= pivot). When both stop, those two elements are
//            each on the wrong side, so swapping fixes BOTH at once - which is why Hoare needs about
//            three times fewer swaps than Lomuto on average.
// Time     : THETA(n) - the two pointers together traverse the range once
// Space    : O(1) - in place
// Trade-off: Faster than Lomuto (fewer swaps) and it handles DUPLICATES gracefully, because elements
//            equal to the pivot stop both pointers and get distributed evenly across both sides.
//            IMPORTANT CAVEAT: the returned index j is NOT the pivot's final position - it is only a
//            boundary. Quicksort must therefore recurse on [l..j] and [j+1..h], NOT skip j.

public class hoarePartition {
    public static void main(String[] args) {
        int arr[] = { 10, 9, 11, 8, 6, 7, 12, 3 };
        int p = hoarePart(arr, 0, arr.length - 1);
        System.out.println("split point = " + p);
        System.out.println(java.util.Arrays.toString(arr));
        // Everything at indices <= p is <= pivot(10); everything after is >= pivot.
    }

    static int hoarePart(int[] arr, int l, int h) {
        int pivot = arr[l];        // Hoare uses the FIRST element as the pivot
        int i = l - 1, j = h + 1;  // start OUTSIDE both ends, since each loop pre-increments

        while (true) {
            // Move i right until it finds an element that does NOT belong on the left side.
            // The do-while guarantees i advances at least once, preventing an infinite loop.
            do {
                i++;
            } while (arr[i] < pivot);

            // Move j left until it finds an element that does NOT belong on the right side.
            do {
                j--;
            } while (arr[j] > pivot);

            // Pointers met or crossed - the partition boundary has been found.
            if (i >= j) {
                return j;
            }

            // Both arr[i] and arr[j] are on the wrong side, so ONE swap corrects both.
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
    // Why elements EQUAL to the pivot help here: they fail both "strictly less" and "strictly
    // greater" tests, so they halt the pointers and end up split between the partitions. That keeps
    // the two sides balanced on duplicate-heavy input - exactly where Lomuto degrades to O(n^2).
}
