// Problem  : Store key -> value pairs with fast lookup by key.
// Approach : Use java.util.HashMap, which hashes keys into buckets for average O(1) access.
// Intuition: The key's hashCode picks a bucket; equals() distinguishes keys within a bucket. So
//            "which value goes with this key?" is answered without scanning everything.
// Time     : get/put/containsKey average O(1), worst O(n) (or O(log n) with treeified buckets)
// Space    : O(n)
// Trade-off: Unordered (iteration order is not predictable) and needs good hashCode/equals on keys.
//            Use LinkedHashMap to preserve insertion order, or TreeMap for sorted keys.

import java.util.HashMap;
import java.util.Map;

public class HashMapDemo {
    public static void main(String[] args) {
        Map<String, Integer> stock = new HashMap<>();

        stock.put("apple", 50);      // insert
        stock.put("banana", 20);
        stock.put("apple", 75);      // same key -> REPLACES the old value (keys are unique)
        System.out.println("map       : " + stock);
        System.out.println("apples    : " + stock.get("apple"));      // 75
        System.out.println("missing   : " + stock.get("cherry"));     // null (key absent)

        // getOrDefault avoids null handling when a key may be missing.
        System.out.println("cherry?   : " + stock.getOrDefault("cherry", 0)); // 0

        // Classic frequency-count idiom: merge() adds to the current value (or seeds it).
        String[] words = { "a", "b", "a", "a", "b", "c" };
        Map<String, Integer> freq = new HashMap<>();
        for (String w : words) {
            freq.merge(w, 1, Integer::sum); // if absent -> 1, else old + 1
        }
        System.out.println("freq      : " + freq); // {a=3, b=2, c=1} (order may vary)

        System.out.println("containsKey b: " + stock.containsKey("banana")); // true
        stock.remove("banana");
        System.out.println("after remove : " + stock);

        // Iterate entries (key + value together).
        for (Map.Entry<String, Integer> e : stock.entrySet()) {
            System.out.println("  " + e.getKey() + " -> " + e.getValue());
        }
        // Edge: a HashMap allows ONE null key and multiple null values. Iteration order is NOT
        // insertion order and may change as the map resizes - never rely on it.
    }
}
