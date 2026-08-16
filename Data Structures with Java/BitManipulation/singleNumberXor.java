// Problem  : Every element in an array appears twice except ONE that appears once. Find that element.
// Approach : Two tiers. NAIVE uses a hash set/count (O(n) time, O(n) space). EFFICIENT XORs all
//            elements together (O(n) time, O(1) space).
// Intuition: XOR has two magic properties: x ^ x == 0 (a value cancels itself) and x ^ 0 == x.
//            XOR-ing everything makes each PAIR cancel to 0, leaving only the lone element.
// Time     : O(n)   Space: naive O(n); efficient O(1)
// Trade-off: The XOR approach is optimal in space and famously elegant, but it relies on the exact
//            "everyone twice except one" structure - it does not generalize to other duplicate counts
//            without modification.

import java.util.HashSet;
import java.util.Set;

public class singleNumberXor {

    static int naive(int[] a) {
        Set<Integer> seen = new HashSet<>();
        for (int x : a) {
            if (!seen.add(x)) seen.remove(x); // add first time, remove on the second occurrence
        }
        return seen.iterator().next();        // the only survivor
    }

    static int efficient(int[] a) {
        int x = 0;
        for (int v : a) x ^= v;   // pairs cancel to 0; the unique value remains
        return x;
    }

    public static void main(String[] args) {
        int[] a = { 4, 1, 2, 1, 2 };
        System.out.println(naive(a) + " / " + efficient(a)); // 4 / 4
        int[] b = { 7, 3, 5, 3, 5 };
        System.out.println(efficient(b)); // 7
    }
}
