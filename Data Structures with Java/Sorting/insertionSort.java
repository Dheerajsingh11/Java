// Problem  : Sort an array using Insertion Sort.
// Approach : Grow a sorted prefix one element at a time. Take the next element ("key") and shift
//            every larger element in the prefix one slot right, then drop the key into the gap.
// Intuition: Exactly how people sort a hand of playing cards - pick up the next card and slide it
//            left past the bigger ones until it sits in the right place. The prefix arr[0..i-1] is
//            always sorted, so only that region needs shifting.
// Time     : O(n) BEST case (already sorted - the while loop never runs, just n-1 comparisons);
//            O(n^2) average and worst (reverse-sorted input shifts everything every time)
// Space    : O(1) - in place
// Trade-off: The best of the simple O(n^2) sorts in practice. It is STABLE, ADAPTIVE (nearly-sorted
//            input is close to linear), and ONLINE (it can sort a stream as elements arrive). This
//            is why real libraries - including Java's TimSort - switch to insertion sort for small
//            or nearly-sorted subarrays, where it beats O(n log n) algorithms due to low overhead.

public class insertionSort {
    public static void main(String[] args) {
        int arr[] = { 1, 3, 2, 8, 4, 5 };
        insertSort(arr, arr.length);
        System.out.println(java.util.Arrays.toString(arr)); // expected: [1, 2, 3, 4, 5, 8]

        int sorted[] = { 1, 2, 3, 4, 5 };   // best case: no shifting occurs at all
        insertSort(sorted, sorted.length);
        System.out.println(java.util.Arrays.toString(sorted)); // expected: [1, 2, 3, 4, 5]
    }

    static void insertSort(int[] arr, int n) {
        // Start at 1: a single element (arr[0]) is trivially a sorted prefix already.
        for (int i = 1; i < n; i++) {

            int key = arr[i];   // save the value being inserted - its slot gets overwritten below
            int j = i - 1;      // rightmost index of the sorted prefix

            // Shift every prefix element GREATER than key one position right, opening a gap.
            // Two stop conditions: j >= 0 guards the array start, and arr[j] > key means we have
            // reached the correct insertion point.
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }

            // The loop exited because arr[j] <= key (or we ran off the front), so the gap at j+1 is
            // exactly where key belongs.
            arr[j + 1] = key;
        }
    }
    // Why it is STABLE: the condition is strictly '>', so shifting stops when an EQUAL element is
    // met. The key therefore lands AFTER its equal, preserving original relative order.
}
