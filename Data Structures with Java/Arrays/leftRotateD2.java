// Problem  : Left-rotate an array by D positions.
// Approach : MEDIUM (tier 2 of 3) - copy the first d elements into a temporary buffer, slide the
//            remaining n-d elements left, then append the buffer at the end.
// Intuition: A left rotation by d simply moves the first d elements to the back. If we park those d
//            elements somewhere safe, the rest can slide left in one clean pass - so every element
//            is touched only ONCE instead of d times.
// Time     : THETA(n) - three passes (copy d, shift n-d, restore d), which is linear overall
// Space    : THETA(d) - the temporary buffer; this is the one weakness
// Trade-off: A huge improvement over the naive O(n*d), and easy to follow. The remaining cost is the
//            O(d) buffer, which for d ~ n means allocating a second array. leftRotateD3.java removes
//            even that, achieving O(n) time with O(1) space.

public class leftRotateD2 {

    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        rotate(arr, 2);
        System.out.println(java.util.Arrays.toString(arr)); // expected: [3, 4, 5, 6, 1, 2]
    }

    static void rotate(int[] arr, int d) {
        int n = arr.length;
        if (n == 0) return;              // guard before the modulo - "d % 0" throws
        d = d % n;                       // normalize: rotating by n is a no-op
        if (d == 0) return;

        // STEP 1 - park the first d elements, which would otherwise be overwritten by the shift.
        int temp[] = new int[d];
        for (int i = 0; i < d; i++) {
            temp[i] = arr[i];
        }

        // STEP 2 - slide the surviving n-d elements left by exactly d slots. Moving forward is safe
        // because every source index (i) is ahead of its destination (i - d).
        for (int i = d; i < n; i++) {
            arr[i - d] = arr[i];
        }

        // STEP 3 - drop the parked elements into the freed tail, preserving their original order.
        for (int i = 0; i < d; i++) {
            arr[(n - d) + i] = temp[i];
        }
    }
}
