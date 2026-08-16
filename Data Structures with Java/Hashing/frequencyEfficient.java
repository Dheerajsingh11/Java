// Problem  : Print how many times each element occurs in an array.
// Approach : EFFICIENT - one pass building a HashMap of value -> count, then print the entries.
// Intuition: Instead of recomputing each element's total by rescanning, we REMEMBER counts as we go.
//            Each element only needs to bump its own counter, which hashing makes an O(1) operation.
// Time     : THETA(n) - a single pass, each map update averaging O(1)
// Space    : O(n) - worst case one map entry per distinct value
// Trade-off: The canonical time-for-space trade: O(n^2) -> O(n) by spending O(n) memory. This
//            "frequency map" is one of the most reusable patterns in all of DSA - it underpins
//            anagram checks, majority element, top-K queries, and duplicate detection.

import java.util.HashMap;
import java.util.Map;

public class frequencyEfficient {
    public static void main(String[] args) {
        int[] arr = { 1, 2, 2, 3, 3, 3, 4, 4, 4, 4 };
        countFreq(arr, arr.length);
        // expected (iteration order is NOT guaranteed by HashMap):
        // 1 -> 1 / 2 -> 2 / 3 -> 3 / 4 -> 4
    }

    static void countFreq(int[] arr, int n) {
        HashMap<Integer, Integer> h = new HashMap<>();

        for (int x : arr) {
            // getOrDefault(x, 0) returns the running count, or 0 the first time x is seen - this
            // removes the need for an explicit "if (map.containsKey(x))" branch.
            h.put(x, h.getOrDefault(x, 0) + 1);

            // Equivalent modern idiom:  h.merge(x, 1, Integer::sum);
        }

        // entrySet() yields key+value together, which is cheaper than looking each key up again.
        for (Map.Entry<Integer, Integer> e : h.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }
        // NOTE: HashMap iteration order is unspecified and can change as the map resizes. Use a
        // LinkedHashMap to preserve insertion order, or a TreeMap to print keys in sorted order.
    }
}
