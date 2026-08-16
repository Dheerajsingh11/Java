// Problem  : Generate all subsets (the power set) of a set of distinct numbers.
// Approach : Backtracking - at each index decide to INCLUDE or EXCLUDE that element, recording the
//            current subset at every node of the decision tree.
// Intuition: Each element is an independent yes/no choice, giving 2^n subsets. The recursion tree has
//            one branch per choice; every node along the way is itself a valid subset.
// Time     : O(n * 2^n) (2^n subsets, O(n) to copy each)   Space: O(n) recursion depth
// Trade-off: 2^n output is inherent to enumerating the power set. A neat alternative is the BITMASK
//            method (iterate 0..2^n-1, use bit j to decide element j) - shown here as a second view.

import java.util.ArrayList;
import java.util.List;

public class subsets {

    // ---- Backtracking (include/exclude) ----
    static void backtrack(int[] nums, int start, List<Integer> current, List<List<Integer>> out) {
        out.add(new ArrayList<>(current));          // every prefix state is a subset (incl. empty)
        for (int i = start; i < nums.length; i++) {
            current.add(nums[i]);                   // choose nums[i]
            backtrack(nums, i + 1, current, out);   // explore subsets that include it
            current.remove(current.size() - 1);     // un-choose (backtrack)
        }
    }

    static List<List<Integer>> subsetsBacktrack(int[] nums) {
        List<List<Integer>> out = new ArrayList<>();
        backtrack(nums, 0, new ArrayList<>(), out);
        return out;
    }

    // ---- Bitmask view: number j's bits pick which elements are in subset j ----
    static List<List<Integer>> subsetsBitmask(int[] nums) {
        int n = nums.length;
        List<List<Integer>> out = new ArrayList<>();
        for (int mask = 0; mask < (1 << n); mask++) {      // 2^n masks
            List<Integer> sub = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if ((mask & (1 << j)) != 0) sub.add(nums[j]); // bit j set -> include element j
            }
            out.add(sub);
        }
        return out;
    }

    public static void main(String[] args) {
        int[] a = { 1, 2, 3 };
        System.out.println("backtrack count = " + subsetsBacktrack(a).size()); // 8 = 2^3
        System.out.println(subsetsBacktrack(a)); // [[],[1],[1,2],[1,2,3],[1,3],[2],[2,3],[3]]
        System.out.println("bitmask   count = " + subsetsBitmask(a).size());   // 8
    }
}
