// Problem  : Store a collection of UNIQUE elements with fast membership tests.
// Approach : Use java.util.HashSet (backed by a HashMap) for average O(1) add/contains.
// Intuition: A set is a map with keys but no values - it only answers "is this element present?".
//            Duplicates are silently ignored.
// Time     : add/remove/contains average O(1)   Space: O(n)
// Trade-off: No ordering and no index access. Use LinkedHashSet to keep insertion order, or
//            TreeSet for sorted iteration + range queries (at O(log n) per op).

import java.util.HashSet;
import java.util.Set;

public class HashSetDemo {
    public static void main(String[] args) {
        Set<Integer> seen = new HashSet<>();

        System.out.println(seen.add(5));   // true  - 5 was new
        System.out.println(seen.add(3));   // true
        System.out.println(seen.add(5));   // false - 5 already present, add is a no-op
        System.out.println("set       : " + seen);
        System.out.println("contains 3: " + seen.contains(3)); // true (O(1) membership)

        // Classic use: detect duplicates / count distinct in one pass.
        int[] arr = { 4, 2, 4, 7, 2, 9 };
        Set<Integer> distinct = new HashSet<>();
        boolean hasDup = false;
        for (int x : arr) {
            if (!distinct.add(x)) {  // add returns false if x was already present -> a duplicate
                hasDup = true;
            }
        }
        System.out.println("distinct count: " + distinct.size()); // 4  ({4,2,7,9})
        System.out.println("has duplicate : " + hasDup);          // true

        // Set algebra: retainAll = intersection, addAll = union, removeAll = difference.
        Set<Integer> a = new HashSet<>(Set.of(1, 2, 3, 4));
        Set<Integer> b = new HashSet<>(Set.of(3, 4, 5, 6));
        Set<Integer> inter = new HashSet<>(a);
        inter.retainAll(b);      // keep only elements also in b
        System.out.println("intersection  : " + inter); // [3, 4]
    }
}
