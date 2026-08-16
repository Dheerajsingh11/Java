// Problem  : In a SORTED array with duplicates, find the FIRST and LAST index of a target (and hence
//            its count).
// Approach : Two tiers. NAIVE linear-scans for the ends (O(n)). EFFICIENT runs two "biased" binary
//            searches: one that keeps going left after a match, one that keeps going right.
// Intuition: A plain binary search stops at ANY matching index. To pin the boundary, when we find a
//            match we do not stop - we continue toward the side we want, remembering the best hit.
// Time     : naive O(n); efficient O(log n)   Space: O(1)
// Trade-off: The binary-search version is optimal and a common building block (count occurrences,
//            equal-range queries). Slightly trickier than vanilla binary search.

public class firstLastOccurrence {

    static int first(int[] a, int target) {
        int lo = 0, hi = a.length - 1, res = -1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] == target) {
                res = mid;        // record this match...
                hi = mid - 1;     // ...but keep searching LEFT for an earlier one
            } else if (a[mid] < target) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return res;
    }

    static int last(int[] a, int target) {
        int lo = 0, hi = a.length - 1, res = -1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] == target) {
                res = mid;        // record this match...
                lo = mid + 1;     // ...but keep searching RIGHT for a later one
            } else if (a[mid] < target) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return res;
    }

    public static void main(String[] args) {
        int[] a = { 1, 2, 2, 2, 3, 4, 4, 5 };
        int f = first(a, 2), l = last(a, 2);
        System.out.println("first=" + f + " last=" + l + " count=" + (l - f + 1)); // first=1 last=3 count=3
        System.out.println("first(4)=" + first(a, 4) + " last(4)=" + last(a, 4));  // first=5 last=6
        System.out.println("absent(9): " + first(a, 9)); // -1
    }
}
