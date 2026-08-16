// Problem  : Generate all permutations (orderings) of a list of distinct numbers.
// Approach : Backtracking - build a permutation one position at a time; try each unused element,
//            recurse, then remove it to try the next.
// Intuition: A permutation is a sequence of choices "which unused element comes next?". Exploring
//            every choice, then undoing it, walks the full tree of orderings.
// Time     : O(n * n!) (n! permutations, O(n) to copy each)   Space: O(n) recursion + used[]
// Trade-off: n! output is unavoidable for full enumeration. The used[] marks keep each recursion
//            step O(1); the choose/recurse/un-choose skeleton is the reusable backtracking template.

import java.util.ArrayList;
import java.util.List;

public class permutations {

    static void backtrack(int[] nums, boolean[] used, List<Integer> current, List<List<Integer>> out) {
        if (current.size() == nums.length) {        // a full permutation is built
            out.add(new ArrayList<>(current));      // copy it (current will keep mutating)
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;                  // skip elements already placed in this branch

            used[i] = true;                         // choose nums[i]
            current.add(nums[i]);

            backtrack(nums, used, current, out);    // explore with this prefix fixed

            used[i] = false;                        // un-choose (backtrack) to try another element
            current.remove(current.size() - 1);
        }
    }

    static List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> out = new ArrayList<>();
        backtrack(nums, new boolean[nums.length], new ArrayList<>(), out);
        return out;
    }

    public static void main(String[] args) {
        List<List<Integer>> res = permute(new int[]{ 1, 2, 3 });
        System.out.println("count = " + res.size()); // 6 = 3!
        System.out.println(res); // [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
    }
}
