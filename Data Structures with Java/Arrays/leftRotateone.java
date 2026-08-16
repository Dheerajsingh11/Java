// Problem  : Left-rotate an array by ONE position (every element moves one slot left, the first
//            element wraps around to the end).
// Approach : Save the first element, shift everything left by one, then place the saved value last.
// Intuition: A rotation is just a shift with wraparound. Only ONE element would be lost by shifting
//            (the first), so a single temporary variable is enough to preserve it.
// Time     : O(n) - every element moves once
// Space    : O(1) - one temporary
// Trade-off: This is the atomic building block for rotating by d positions. Repeating it d times is
//            the naive strategy (leftRotateD1.java, O(n*d)); the smarter approaches avoid that
//            repetition entirely - see leftRotateD2.java (O(d) space) and leftRotateD3.java (optimal).

public class leftRotateone {

    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        rotate(arr);
        System.out.println(java.util.Arrays.toString(arr)); // expected: [2, 3, 4, 5, 6, 1]
    }

    static void rotate(int[] arr) {
        int n = arr.length;

        // Save the element that would otherwise be overwritten and lost.
        int temp = arr[0];

        // Shift left. Going FORWARD is correct here: arr[i] has already been saved or copied before
        // it is overwritten, so nothing is clobbered prematurely.
        for (int i = 0; i < n - 1; i++) {
            arr[i] = arr[i + 1];
        }

        arr[n - 1] = temp;   // the old first element wraps around to the last slot
        // Edge: an empty array would throw on arr[0]; a single element rotates to itself (no-op).
    }
}
