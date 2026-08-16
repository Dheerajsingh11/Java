// Problem  : Left-rotate an array by D positions.
// Approach : NAIVE (tier 1 of 3) - call "rotate by one" d times.
// Intuition: If rotating by 1 works, then doing it d times must rotate by d. Simple to reason about,
//            but it re-touches the ENTIRE array on every one of those d passes.
// Time     : THETA(n * d) - d passes, each shifting all n elements. For d ~ n this becomes O(n^2)
// Space    : THETA(1)
// Trade-off: Easiest to derive, worst to run. The two better tiers avoid the repetition entirely:
//              MEDIUM    (leftRotateD2.java) - O(n) time, O(d) extra space via a temp buffer
//              EFFICIENT (leftRotateD3.java) - O(n) time, O(1) space via the reversal trick

public class leftRotateD1 {

    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        rotate(arr, 2);
        System.out.println(java.util.Arrays.toString(arr)); // expected: [3, 4, 5, 6, 1, 2]
    }

    // The building block: one left rotation, O(n).
    static void rotateBy1(int[] arr) {
        int n = arr.length;
        int temp = arr[0];
        for (int i = 1; i < n; i++) {
            arr[i - 1] = arr[i];
        }
        arr[n - 1] = temp;
    }

    static void rotate(int[] arr, int d) {
        // Normalize first: rotating by n returns the array to its original state, so only d % n
        // actually matters. Without this, a large d wastes full passes doing nothing.
        d = d % arr.length;

        // The costly part: d separate O(n) passes -> O(n*d) overall.
        for (int i = 0; i < d; i++) {
            rotateBy1(arr);
        }
    }
    // Concretely: for n = 1000 and d = 500 this performs ~500,000 element moves, whereas the
    // reversal method in leftRotateD3.java needs only ~1,500.
}
