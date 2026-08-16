// Problem  : Delete the first occurrence of a given VALUE from an array.
// Approach : Linear-search for the value, then shift every later element one slot to the LEFT to
//            close the hole, and report the reduced logical size.
// Intuition: Arrays cannot have holes - the elements must stay contiguous. So removal is really
//            "overwrite the victim by sliding the tail left". Here the shift runs FORWARDS, the
//            opposite of insertion, because each element moves into an already-read slot.
// Time     : O(n) - O(n) to find the value plus O(n) to shift
// Space    : O(1) - in place
// Trade-off: The array's physical length never changes in Java; we simply track a smaller LOGICAL
//            size and ignore the stale value left at the end. Deleting from the FRONT is worst case
//            (shift everything), from the END is O(1). A linked list deletes in O(1) once positioned.

public class delete {

    public static void main(String[] args) {
        int[] arr = new int[10];
        for (int i = 0; i < 10; i++) arr[i] = i;   // 0 1 2 3 4 5 6 7 8 9

        int n = deleteArray(arr, 10, 5);           // remove the VALUE 5 (not index 5)

        for (int i = 0; i < n; i++) System.out.print(arr[i] + " ");
        System.out.println();                      // expected: 0 1 2 3 4 6 7 8 9
        System.out.println("new size = " + n);     // expected: 9

        int m = deleteArray(arr, n, 99);           // value not present
        System.out.println("size after deleting absent value = " + m); // expected: 9 (unchanged)
    }

    // Returns the new logical size; unchanged if the element was not found.
    static int deleteArray(int arr[], int n, int element) {
        int i;
        // Phase 1 - FIND the first occurrence. 'i' is declared outside the loop so its final value
        // survives afterwards and tells us whether the search succeeded.
        for (i = 0; i < n; i++) {
            if (arr[i] == element) {
                break;                       // stop at the first match
            }
        }

        // If the loop ran to completion, i == n means the value was never found.
        if (i == n) {
            return n;                        // nothing removed, size unchanged
        }

        // Phase 2 - SHIFT LEFT to close the gap at index i. Forwards is correct here: arr[j] has
        // already been consumed, so overwriting it with arr[j+1] loses nothing.
        for (int j = i; j < n - 1; j++) {
            arr[j] = arr[j + 1];
        }

        // The last slot still holds a stale duplicate, but it now sits OUTSIDE the logical size,
        // so it is simply ignored (Java arrays cannot physically shrink).
        return (n - 1);
    }
}
