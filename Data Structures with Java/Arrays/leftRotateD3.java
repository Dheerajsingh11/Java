// Problem  : Left-rotate an array by D positions.
// Approach : EFFICIENT (tier 3 of 3) - the REVERSAL ALGORITHM: reverse the first d elements, reverse
//            the remaining n-d, then reverse the whole array.
// Intuition: Reversing a block puts its elements in the right RELATIVE order but backwards. Reversing
//            the two blocks separately and then reversing everything flips them back to forward order
//            while SWAPPING the positions of the two blocks - which is exactly what a rotation is.
//            Walk through [1,2,3,4,5,6] with d = 2:
//              reverse(0, d-1)  -> [2,1 | 3,4,5,6]
//              reverse(d, n-1)  -> [2,1 | 6,5,4,3]
//              reverse(0, n-1)  -> [3,4,5,6 | 1,2]   <- the rotated result
// Time     : THETA(n) - each element is touched at most twice across the three reversals
// Space    : THETA(1) - everything happens in place, no temporary array
// Trade-off: OPTIMAL in both time and space - the best of the three tiers. The only cost is that the
//            trick is non-obvious; the dry run above is what makes it click.

public class leftRotateD3 {

    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        rotate(arr, 2);
        System.out.println(java.util.Arrays.toString(arr)); // expected: [3, 4, 5, 6, 1, 2]

        int b[] = { 1, 2, 3, 4, 5 };
        rotate(b, 7);   // 7 % 5 = 2, so this is equivalent to rotating by 2
        System.out.println(java.util.Arrays.toString(b));   // expected: [3, 4, 5, 1, 2]
    }

    static void rotate(int[] arr, int d) {
        int n = arr.length;
        d = d % n;                    // normalize so d > n (or d == n) behaves correctly
        if (d == 0) return;           // nothing to do

        reverse(arr, 0, d - 1);       // reverse the block that will move to the BACK
        reverse(arr, d, n - 1);       // reverse the block that will move to the FRONT
        reverse(arr, 0, n - 1);       // reverse everything - restores order and swaps the blocks
    }

    // Standard in-place two-pointer reversal of the inclusive range [start, end].
    static void reverse(int[] arr, int start, int end) {
        while (start < end) {
            int temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
            start++;
            end--;
        }
    }
    // Bonus: to RIGHT-rotate by d, either rotate left by (n - d), or reverse the blocks in the
    // opposite order - the same three-reversal machinery applies.
}
