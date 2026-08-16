// Problem  : Search a SORTED, UNIFORMLY DISTRIBUTED array faster than binary search.
// Approach : Instead of always probing the middle, ESTIMATE where the target should be, using linear
//            interpolation between the values at the two ends.
// Intuition: This is how a person uses a phone book. Looking for "Zhang" you open near the END, not
//            the middle - because you use the VALUE, not just the position. Binary search ignores
//            what the values actually are; interpolation search uses them to guess the location.
// Time     : O(log log n) average on uniformly distributed data - remarkably fast;
//            O(n) worst case on skewed data   Space: O(1)
// Trade-off: Beats binary search only when values are roughly evenly spaced. On skewed data (e.g.
//            exponential growth) the estimate is consistently poor and it degrades to a linear scan,
//            which is WORSE than binary search's guaranteed O(log n). Use it only when you know the
//            distribution; binary search remains the safe default.

public class interpolationSearch {

    static int search(int[] a, int target) {
        int low = 0, high = a.length - 1;

        // Two guards beyond the usual low <= high:
        //   target must lie within [a[low], a[high]], otherwise the interpolation formula would
        //   produce an index outside the array.
        while (low <= high && target >= a[low] && target <= a[high]) {

            // Equal values at both ends would divide by zero; if the range is flat the target is
            // either a[low] or absent.
            if (a[low] == a[high]) {
                return a[low] == target ? low : -1;
            }

            // THE FORMULA: how far along the VALUE range does the target sit? Apply that same
            // fraction to the INDEX range.
            //   pos = low + (target - a[low]) / (a[high] - a[low]) * (high - low)
            // Computed with long to avoid overflow in the multiplication.
            int pos = low + (int) (((long) (target - a[low]) * (high - low))
                                   / (a[high] - a[low]));

            if (a[pos] == target)      return pos;
            else if (a[pos] < target)  low = pos + 1;    // guessed too low
            else                       high = pos - 1;   // guessed too high
        }
        return -1;
    }

    public static void main(String[] args) {
        // Uniformly spaced - the ideal case. The first probe usually lands on or near the target.
        int[] uniform = new int[100];
        for (int i = 0; i < 100; i++) uniform[i] = i * 10;      // 0, 10, 20, ... 990

        System.out.println("find 700 : index " + search(uniform, 700));  // expected: 70
        System.out.println("find 0   : index " + search(uniform, 0));    // expected: 0
        System.out.println("find 990 : index " + search(uniform, 990));  // expected: 99
        System.out.println("find 705 : index " + search(uniform, 705));  // expected: -1 (absent)

        // Skewed data - the estimate is poor, so this behaves closer to a linear scan.
        int[] skewed = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 1000000 };
        System.out.println("skewed, find 9 : index " + search(skewed, 9));       // 8, but slowly
        System.out.println("skewed, find 1000000 : index " + search(skewed, 1000000)); // 9
    }
}

/* ------------------------- COMPARED WITH BINARY SEARCH -------------------------
 *                       Binary search            Interpolation search
 *   Probe position      always the middle        estimated from the VALUES
 *   Uniform data        O(log n)                 O(log log n)   <- much faster
 *   Skewed data         O(log n)                 O(n)           <- much slower
 *   Requires            sorted                   sorted AND roughly uniform
 *
 * To feel the difference: for n = 1,000,000 binary search needs ~20 probes and interpolation search
 * needs about 4-5 on uniform data. But on adversarial input interpolation search can inspect every
 * element, so it trades a guarantee for an average-case win.
 *
 * WHEN TO USE: large sorted datasets with a known, near-uniform distribution - database record IDs,
 * evenly sampled time series, dense numeric keys. When the distribution is unknown or irregular,
 * prefer binary search (binarySearchIterative.java) for its guarantee.
 * ------------------------------------------------------------------------------- */
