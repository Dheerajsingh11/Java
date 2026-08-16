// Problem  : Store a sequence optimized for fast insertion/removal at both ends.
// Approach : Use java.util.LinkedList, a doubly-linked list implementing both List and Deque.
// Intuition: Elements are separate nodes each pointing to their neighbours. Adding/removing at an
//            end just relinks a couple of pointers - no array shifting.
// Time     : addFirst/addLast/removeFirst/removeLast O(1); get(index) O(n)   Space: O(n) + node overhead
// Trade-off: Great for queue/deque/stack use (O(1) at ends) but random access is O(n) (must walk
//            the chain) and each node costs extra memory. Prefer ArrayList for index-heavy access.

import java.util.LinkedList;

public class LinkedListDemo {
    public static void main(String[] args) {
        LinkedList<String> dq = new LinkedList<>();

        // Deque operations: both ends are O(1) because only pointers get relinked.
        dq.addLast("B");     // [B]
        dq.addLast("C");     // [B, C]
        dq.addFirst("A");    // [A, B, C]   <- front insert is O(1), unlike ArrayList's O(n)
        System.out.println("deque     : " + dq);

        System.out.println("peekFirst : " + dq.peekFirst()); // A (look without removing)
        System.out.println("peekLast  : " + dq.peekLast());  // C

        dq.removeFirst();    // [B, C]
        dq.removeLast();     // [B]
        System.out.println("after ends removed: " + dq);     // [B]

        // As a List it still supports index access, but get(i) walks the chain -> O(n).
        dq.addLast("X");
        dq.addLast("Y");
        System.out.println("get(1)    : " + dq.get(1));       // X (O(n) traversal, not O(1))

        // Edge: peek*/poll* return null on an empty list (no exception), while
        // getFirst/removeFirst THROW NoSuchElementException when empty - pick based on preference.
        System.out.println("final     : " + dq);              // [B, X, Y]
    }
}
