// Problem  : Find a target in a sorted array, especially when its size is unknown or unbounded, or
//            the target is near the beginning.
// Approach : Double an index (1, 2, 4, 8, ...) until the value there exceeds the target, then binary
//            search within the last doubling interval.
// Intuition: Exponential jumps quickly bracket the target's position; once we have a range that must
//            contain it, ordinary binary search finishes the job.
// Time     : O(log i) where i is the target's position (often << n)   Space: O(1)
// Trade-off: Faster than plain binary search when the target is close to the front, and it works on
//            UNBOUNDED/streamed sorted data where n is not known up front. Slight overhead otherwise.

public class exponentialSearch {

    static int search(int[] a, int target) {
        int n = a.length;
        if (n == 0) return -1;
        if (a[0] == target) return 0;

        // Phase 1: find a range [bound/2, bound] whose end is >= target by doubling the bound.
        int bound = 1;
        while (bound < n && a[bound] < target) {
            bound *= 2;               // exponential jump: 1, 2, 4, 8, ...
        }

        // Phase 2: binary search inside [bound/2, min(bound, n-1)].
        int lo = bound / 2;
        int hi = Math.min(bound, n - 1);
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2; // (hi-lo)/2 form avoids lo+hi overflow
            if (a[mid] == target) return mid;
            if (a[mid] < target) lo = mid + 1;
            else hi = mid - 1;
        }
        return -1;
    }

    public static void main(String[] args) {
        int[] a = { 2, 3, 4, 10, 40, 50, 60, 70 };
        System.out.println(search(a, 10)); // 3
        System.out.println(search(a, 2));  // 0
        System.out.println(search(a, 70)); // 7
        System.out.println(search(a, 5));  // -1
    }
}
