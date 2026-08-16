// Problem  : Count how many DISTINCT values an array contains.
// Approach : EFFICIENT - insert every element into a HashSet and return its size.
// Intuition: A set is defined by uniqueness: adding a value that is already present is a no-op. So
//            after one pass the set holds exactly the distinct values, and its size IS the answer -
//            no comparisons between elements are ever needed.
// Time     : O(n) - one pass, with each add averaging O(1) thanks to hashing
// Space    : O(n) - the set may hold every element if all values are unique
// Trade-off: Turns the naive O(n^2) into O(n) by SPENDING MEMORY - the classic time/space trade-off.
//            Worth it almost always. Caveats: hashing gives O(1) only on AVERAGE (pathological hash
//            collisions degrade it), and the set loses ordering. If the value range is small and
//            known, a boolean/count array is even faster and avoids boxing overhead.

import java.util.HashSet;

public class countDistinctEfficient {
    public static void main(String[] args) {
        int[] arr = { 1, 1, 2, 3, 2, 4, 5 };
        System.out.println(countDistinct(arr));              // expected: 5
        System.out.println(countDistinct(new int[]{ 7, 7 })); // expected: 1
    }

    static int countDistinct(int[] arr) {
        // HashSet rejects duplicates automatically, so we never have to compare elements ourselves.
        HashSet<Integer> set = new HashSet<>();

        for (int num : arr) {
            set.add(num);      // add() returns false if num was already present - here we can ignore
                               // the result, since the set silently keeps only one copy either way.
        }
        return set.size();     // the number of unique values that survived
    }
    // Why hashing gives O(1) per operation: the element's hashCode selects a bucket directly, so the
    // set inspects only that one bucket rather than scanning everything - the same idea implemented
    // by hand in myHash.java and Chaining.java.
}
