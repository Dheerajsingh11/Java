// Problem  : Move every 0 in the array to the END, keeping the non-zero elements in order.
// Approach : NAIVE - for each zero found, scan forward for the next non-zero and swap them.
// Intuition: A zero sitting in the "wrong" place should trade positions with the nearest non-zero to
//            its right. Repeating that for every zero eventually pushes them all to the back.
// Time     : THETA(n^2) worst case - each zero may trigger a scan across the remaining array
//            (e.g. {0,0,0,...,1} makes every zero search far to the right)
// Space    : THETA(1) - in place
// Trade-off: Correct and order-preserving, but the repeated forward scanning is wasted work. The
//            EFFICIENT version achieves the same result in ONE pass by tracking where the next
//            non-zero belongs, instead of searching for it each time.

public class zeroesToEndNaive {

    public static void main(String[] args) {
        int arr[] = { 1, 2, 0, 4, 3, 0, 5, 0 };
        zeroesToEnd(arr);
        System.out.println(java.util.Arrays.toString(arr));
        // expected: [1, 2, 4, 3, 5, 0, 0, 0] - non-zeroes keep their relative order
    }

    static void zeroesToEnd(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 0) {                          // found a zero that may need moving

                // Search RIGHT for the nearest non-zero to trade places with. This inner scan is
                // what makes the algorithm quadratic.
                for (int j = i + 1; j < arr.length; j++) {
                    if (arr[j] != 0) {
                        int temp = arr[i];
                        arr[i] = arr[j];
                        arr[j] = temp;
                        break;                          // one swap fixes position i - move on
                    }
                }
                // If no non-zero exists to the right, everything from i onward is already zero,
                // so the array is finished (the loop simply idles through the remaining zeroes).
            }
        }
    }
    // Stability note: swapping with the NEAREST non-zero preserves the relative order of the
    // non-zero elements, which is normally a requirement of this problem.
}
