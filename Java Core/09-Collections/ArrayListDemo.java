// Problem  : Store an ordered, resizable sequence of elements and operate on it.
// Approach : Use java.util.ArrayList - a growable array behind the List interface.
// Intuition: A plain array has a fixed length; ArrayList wraps an array and reallocates a bigger
//            one when it fills up, giving you "resize for free" with array-like index access.
// Time     : get/set O(1); add at end amortized O(1); add/remove in middle O(n)   Space: O(n)
// Trade-off: Fast random access and cache-friendly, but inserting/removing in the middle shifts
//            elements (O(n)). If you mostly add/remove at the FRONT, prefer a LinkedList/ArrayDeque.

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class ArrayListDemo {
    public static void main(String[] args) {
        // Program to the interface (List), instantiate the implementation (ArrayList). Generics
        // <Integer> make it type-safe: only Integers go in, no casting comes out.
        List<Integer> nums = new ArrayList<>();

        nums.add(30);            // [30]        - append at end, amortized O(1)
        nums.add(10);            // [30, 10]
        nums.add(20);            // [30, 10, 20]
        nums.add(1, 99);         // [30, 99, 10, 20] - insert at index 1 shifts the rest right (O(n))

        System.out.println("list      : " + nums);            // [30, 99, 10, 20]
        System.out.println("get(2)    : " + nums.get(2));     // 10  (O(1) random access)
        System.out.println("size      : " + nums.size());     // 4
        System.out.println("contains 20: " + nums.contains(20)); // true (linear scan, O(n))
        System.out.println("indexOf 10: " + nums.indexOf(10));   // 2

        nums.set(0, 5);          // replace element at index 0 -> [5, 99, 10, 20]
        nums.remove(Integer.valueOf(99)); // remove the VALUE 99, not index 99 -> [5, 10, 20]
        // Edge/gotcha: remove(int) removes by INDEX, remove(Object) removes by VALUE. For a
        // List<Integer> these overloads collide - wrap in Integer.valueOf(x) to remove the value x.

        Collections.sort(nums);  // natural (ascending) order -> [5, 10, 20]
        System.out.println("sorted    : " + nums);

        // Iterate (for-each is cleanest for read-only traversal).
        int sum = 0;
        for (int x : nums) sum += x;
        System.out.println("sum       : " + sum); // expected: 35
    }
}
