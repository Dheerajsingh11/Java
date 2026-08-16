// Problem  : Traverse a collection and safely remove elements during traversal.
// Approach : Use an Iterator explicitly, contrasted with the for-each loop's hidden iterator.
// Intuition: An Iterator is a cursor over a collection: hasNext() asks "more left?", next() returns
//            the next element and advances. Because the collection owns the cursor, it can remove
//            through it safely (unlike modifying the collection directly mid-loop).
// Time     : O(n) to traverse   Space: O(1)
// Trade-off: for-each is cleaner for read-only passes; use an explicit Iterator when you must
//            REMOVE during iteration (or a ListIterator to also insert/replace and go backwards).

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IteratorDemo {
    public static void main(String[] args) {
        List<Integer> nums = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));

        // for-each uses an iterator under the hood - great for reading, but you must NOT call
        // nums.remove(...) inside it: that triggers ConcurrentModificationException.
        System.out.print("read-only: ");
        for (int x : nums) System.out.print(x + " ");
        System.out.println();

        // Explicit iterator lets us remove SAFELY via iterator.remove().
        Iterator<Integer> it = nums.iterator();
        while (it.hasNext()) {
            int x = it.next();
            if (x % 2 == 0) {
                it.remove(); // removes the element next() just returned - the safe way
            }
        }
        System.out.println("after removing evens: " + nums); // [1, 3, 5]

        // Why the exception exists: collections keep a modCount; the iterator caches it and checks
        // on each next(). A direct nums.remove() bumps modCount without the iterator knowing, so it
        // "fails fast" rather than silently skipping elements. iterator.remove() updates both.

        // Modern shortcut for the same "remove matching" task:
        List<Integer> more = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
        more.removeIf(x -> x % 2 == 0); // predicate-based removal, no manual iterator
        System.out.println("removeIf evens     : " + more); // [1, 3, 5]
    }
}
