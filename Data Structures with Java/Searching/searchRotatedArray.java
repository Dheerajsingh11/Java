// Problem  : Search a target in a sorted array that has been ROTATED at some unknown pivot
//            (e.g. [4,5,6,7,0,1,2]) in O(log n).
// Approach : Modified binary search. At each step, one half is still sorted; decide which half is
//            sorted, then check whether the target lies within that sorted half.
// Intuition: A rotation breaks the array into two sorted runs. At any mid, comparing a[lo]..a[mid]
//            tells us which side is properly sorted; within a sorted side we can range-check the
//            target and discard the other side - preserving the O(log n) halving.
// Time     : O(log n)   Space: O(1)
// Trade-off: Same speed as binary search on unrotated data, without paying O(n) to find the pivot
//            first. The logic is subtler - the key is identifying the sorted half each iteration.

public class searchRotatedArray {

    static int search(int[] a, int target) {
        int lo = 0, hi = a.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] == target) return mid;

            // Is the LEFT half [lo..mid] sorted? (No rotation point inside it.)
            if (a[lo] <= a[mid]) {
                // Target within the sorted left half? Then search there, else go right.
                if (a[lo] <= target && target < a[mid]) hi = mid - 1;
                else lo = mid + 1;
            } else {
                // Otherwise the RIGHT half [mid..hi] is sorted.
                if (a[mid] < target && target <= a[hi]) lo = mid + 1;
                else hi = mid - 1;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int[] a = { 4, 5, 6, 7, 0, 1, 2 };
        System.out.println(search(a, 0)); // 4
        System.out.println(search(a, 6)); // 2
        System.out.println(search(a, 3)); // -1
        System.out.println(search(a, 4)); // 0
    }
}
