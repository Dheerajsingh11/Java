// Problem  : Reverse a singly linked list.
// Approach : Two tiers. ITERATIVE re-points each node's next to its predecessor in one pass.
//            RECURSIVE reverses the rest, then fixes the current link.
// Intuition: Reversing means every arrow flips direction. Iteratively we carry a 'prev' pointer and
//            redirect each node to it; recursively we trust the recursion to reverse the tail, then
//            make the next node point back at us.
// Time     : O(n)   Space: iterative O(1); recursive O(n) call stack
// Trade-off: The iterative version is the practical choice (constant space, no stack-overflow risk).
//            The recursive version is elegant but risks overflow on very long lists.

public class reverseLinkedList {

    static class Node {
        int value;
        Node next;
        Node(int value) { this.value = value; }
    }

    // ---- Iterative: O(1) space ----
    static Node reverseIterative(Node head) {
        Node prev = null, cur = head;
        while (cur != null) {
            Node nextTemp = cur.next; // remember the rest before we overwrite the link
            cur.next = prev;          // flip this node's arrow to point backward
            prev = cur;               // advance prev
            cur = nextTemp;           // advance cur
        }
        return prev;                  // prev is the new head (old tail)
    }

    // ---- Recursive ----
    static Node reverseRecursive(Node head) {
        if (head == null || head.next == null) return head; // base: empty or single node
        Node newHead = reverseRecursive(head.next);         // reverse everything after head
        head.next.next = head;      // make the next node point back to head
        head.next = null;           // head becomes the new tail
        return newHead;             // the deepest node bubbles up as the new head
    }

    static Node build(int... xs) {
        Node dummy = new Node(0), t = dummy;
        for (int x : xs) { t.next = new Node(x); t = t.next; }
        return dummy.next;
    }
    static void print(Node h) { for (; h != null; h = h.next) System.out.print(h.value + " "); System.out.println(); }

    public static void main(String[] args) {
        print(reverseIterative(build(1, 2, 3, 4, 5))); // 5 4 3 2 1
        print(reverseRecursive(build(1, 2, 3)));       // 3 2 1
    }
}
