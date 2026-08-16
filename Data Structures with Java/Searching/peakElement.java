// Problem  : Find a PEAK element - one that is >= its neighbours - in O(log n). (Array need not be
//            sorted; any array has at least one peak.)
// Approach : Binary search on the SLOPE: compare mid with its right neighbour and move toward the
//            higher side, which must contain a peak.
// Intuition: If a[mid] < a[mid+1], the array is rising at mid, so a peak must exist to the right
//            (the values cannot rise forever within the bounds). Symmetric on the other side. This
//            lets us halve the search space even without global sorting.
// Time     : O(log n)   Space: O(1)
// Trade-off: Far faster than an O(n) scan, but returns ANY one peak (there may be several). Works
//            because array ends act as -infinity walls, guaranteeing a peak in the chosen half.

public class peakElement {

    static int findPeak(int[] a) {
        int lo = 0, hi = a.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] < a[mid + 1]) {
                lo = mid + 1;   // rising slope -> a peak lies to the right
            } else {
                hi = mid;       // a[mid] >= a[mid+1] -> mid could be a peak; keep it in range
            }
        }
        return lo;              // lo == hi -> a peak index
    }

    public static void main(String[] args) {
        int[] a = { 1, 3, 20, 4, 1, 0 };
        int p = findPeak(a);
        System.out.println("peak index " + p + " value " + a[p]); // index 2 value 20
        int[] b = { 1, 2, 3, 4, 5 };       // strictly increasing -> peak is the last element
        int q = findPeak(b);
        System.out.println("peak index " + q + " value " + b[q]); // index 4 value 5
    }
}
