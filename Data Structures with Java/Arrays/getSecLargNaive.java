// Problem  : Find the SECOND largest DISTINCT element of an array (returns its 1-based POSITION).
// Approach : NAIVE / two-pass - first locate the largest, then scan again for the biggest element
//            that is not equal to it.
// Intuition: "Second largest" = "largest among everything that is not the maximum". Two independent
//            passes express that definition directly. The != check is what makes duplicates of the
//            maximum ineligible, so {5,5,3} correctly yields 3 rather than 5.
// Time     : O(n) - two separate linear passes, so 2n comparisons overall
// Space    : O(1)
// Trade-off: Already linear, so the EFFICIENT version does not improve the big-O - it improves the
//            CONSTANT by tracking both the largest and second largest in a SINGLE pass (n vs 2n).
//            That matters when the data is huge or can only be streamed once.
// RETURNS  : a 1-BASED POSITION, not the value. (For the demo array they coincidentally match.)

public class getSecLargNaive {

    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        System.out.println(secondLargest(arr));                  // expected: 5 (position of value 5)
        System.out.println(secondLargest(new int[]{ 10, 10, 7 })); // expected: 3 (duplicates of max skipped)
    }

    // Returns the INDEX of the largest element.
    static int getLargest(int[] arr, int size) {
        int max = 0;                          // track an INDEX, not a value, so the caller can compare positions
        for (int i = 0; i < size; i++) {
            if (arr[i] > arr[max]) {
                max = i;
            }
        }
        return max;
    }

    static int secondLargest(int[] arr) {
        int n = arr.length;
        int max = getLargest(arr, n);         // PASS 1: find the maximum
        int res = -1;                         // -1 = "no valid candidate seen yet"

        for (int i = 0; i < n; i++) {         // PASS 2: best element that is NOT equal to the max
            // Compare by VALUE (arr[i] != arr[max]), not by index. This correctly excludes every
            // DUPLICATE of the maximum, not merely the one occurrence we happened to find.
            if (arr[i] != arr[max]) {
                if (res == -1) {
                    res = i;                  // first eligible candidate
                } else if (arr[i] > arr[res]) {
                    res = i;                  // a better candidate
                }
            }
        }
        return (res + 1);                     // convert index -> 1-based position
        // Edge: if every element is identical, no candidate qualifies, res stays -1, and this
        // returns 0 - the caller's signal that no second-distinct value exists.
    }
}
