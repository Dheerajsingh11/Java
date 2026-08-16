// Problem  : Find a target in a SORTED array faster than linear search, using fixed-size jumps.
// Approach : Jump ahead in blocks of size sqrt(n) until the block that could contain the target is
//            found, then linear-scan within that block.
// Intuition: On sorted data, jumping in big steps skips whole blocks that are too small, then a
//            short local scan finds the exact spot. Block size sqrt(n) balances the two phases.
// Time     : O(sqrt(n))   Space: O(1)
// Trade-off: Between linear O(n) and binary O(log n). Useful when jumping BACK is expensive (e.g.
//            data on tape/sequential storage) because it only ever steps forward then scans.

public class jumpSearch {

    static int search(int[] a, int target) {
        int n = a.length;
        int step = (int) Math.floor(Math.sqrt(n)); // optimal block size ~ sqrt(n)
        int prev = 0;

        // Phase 1: jump forward until the block's last element is >= target (or we run off the end).
        while (prev < n && a[Math.min(step, n) - 1] < target) {
            prev = step;             // this block is entirely too small -> skip it
            step += (int) Math.floor(Math.sqrt(n));
            if (prev >= n) return -1; // jumped past the end -> not present
        }

        // Phase 2: linear scan within the candidate block [prev, min(step, n)).
        for (int i = prev; i < Math.min(step, n); i++) {
            if (a[i] == target) return i;
        }
        return -1;
    }

    public static void main(String[] args) {
        int[] a = { 1, 3, 5, 7, 9, 11, 13, 15, 17, 19 };
        System.out.println(search(a, 13)); // 6
        System.out.println(search(a, 1));  // 0
        System.out.println(search(a, 8));  // -1 (absent)
    }
}
