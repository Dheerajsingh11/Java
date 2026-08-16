// Problem  : Keep keys/elements in SORTED order and answer range/nearest queries.
// Approach : Use TreeMap (sorted map) and TreeSet (sorted set), both backed by a Red-Black tree.
// Intuition: A balanced BST keeps data ordered at all times, so "smallest", "largest", "just below
//            x", and "everything between a and b" are cheap - things a HashMap/HashSet cannot do.
// Time     : get/put/add/contains/floor/ceiling all O(log n)   Space: O(n)
// Trade-off: O(log n) instead of HashMap's O(1), in exchange for sorted iteration and navigation
//            (floor/ceiling/headMap/tailMap). Choose Tree* when ORDER or RANGE queries matter.

import java.util.TreeMap;
import java.util.TreeSet;

public class TreeMapTreeSetDemo {
    public static void main(String[] args) {
        // ---- TreeSet: sorted unique elements + navigation ----
        TreeSet<Integer> ts = new TreeSet<>();
        for (int x : new int[]{ 40, 10, 30, 20, 50 }) ts.add(x);

        System.out.println("sorted set : " + ts);          // [10, 20, 30, 40, 50] - auto-sorted
        System.out.println("first/last : " + ts.first() + "/" + ts.last()); // 10/50
        System.out.println("floor(35)  : " + ts.floor(35));   // 30  (largest element <= 35)
        System.out.println("ceiling(35): " + ts.ceiling(35)); // 40  (smallest element >= 35)
        System.out.println("higher(30) : " + ts.higher(30));  // 40  (strictly greater)
        System.out.println("lower(30)  : " + ts.lower(30));   // 20  (strictly smaller)
        System.out.println("headSet(30): " + ts.headSet(30)); // [10, 20]  (everything < 30)
        System.out.println("subSet     : " + ts.subSet(20, 50)); // [20, 30, 40]  ([from, to))

        // ---- TreeMap: sorted keys + the same navigation on keys ----
        TreeMap<String, Integer> scores = new TreeMap<>();
        scores.put("charlie", 3);
        scores.put("alice", 1);
        scores.put("bob", 2);
        System.out.println("sorted map : " + scores);          // keys in alphabetical order
        System.out.println("firstKey   : " + scores.firstKey()); // alice
        System.out.println("ceilingKey : " + scores.ceilingKey("b")); // bob (>= "b")

        // Edge: Tree* require elements to be Comparable (natural order) OR you must pass a
        // Comparator to the constructor; otherwise you get a ClassCastException at run time.
    }
}
