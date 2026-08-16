// Problem  : Insert a new element at a given position in an array that has spare capacity.
// Approach : Shift every element from the insertion point rightward by one slot, then write the new
//            value into the freed position.
// Intuition: An array is contiguous, so there is no "gap" to insert into - room must be MADE. Shifting
//            must run from the END backwards; going forwards would overwrite each element before it
//            had been copied.
// Time     : O(n) worst case (insert at the front shifts everything); O(1) best case (insert at the end)
// Space    : O(1) - done in place, no second array
// Trade-off: This O(n) shifting is the array's core weakness and exactly what a LINKED LIST avoids
//            (O(1) insertion once positioned - see Linked List/insertAtBegin.java). The array's
//            compensating strength is O(1) random access, which a linked list cannot offer.

public class insertArray {

    public static void main(String[] args) {
        int[] arr = new int[10];      // capacity 10, but only the first 5 slots are "in use"
        for (int i = 0; i < 5; i++) {
            arr[i] = i;               // logical contents: 0 1 2 3 4
        }

        // Insert value 10 at position 3 (1-based) into an array of logical size 5, capacity 10.
        int n = insert(arr, 5, 10, 10, 3);

        for (int i = 0; i < n; i++) System.out.print(arr[i] + " ");
        System.out.println();          // expected: 0 1 10 2 3 4
        System.out.println("new size = " + n); // expected: 6
    }

    // arr = storage, n = current logical size, x = value, cap = physical capacity, pos = 1-based position.
    // Returns the NEW logical size (unchanged if the insert could not happen).
    static int insert(int arr[], int n, int x, int cap, int pos) {
        // Guard: a full array has no spare slot, so the insert is rejected rather than overflowing.
        if (n == cap) {
            return n;
        }

        int idx = pos - 1;             // convert the 1-based position to a 0-based index

        // Shift RIGHT, walking BACKWARDS from the last element down to the insertion point.
        // Backwards is essential: each element is copied into the (already vacated) slot ahead of it.
        // Iterating forwards would clobber arr[i+1] before it had been moved.
        for (int i = n - 1; i >= idx; i--) {
            arr[i + 1] = arr[i];
        }

        arr[idx] = x;                  // the slot at idx is now free
        return (n + 1);                // one more element is in use
    }
    // Cost summary: insert at END = O(1) (no shifting); insert at FRONT = O(n) (everything shifts).
}
