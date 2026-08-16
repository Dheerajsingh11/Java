// Problem  : Find the position of a value x in a singly linked list.
// Approach : ITERATIVE linear search - walk from the head, counting positions, until x is found.
// Intuition: A linked list has no index and no ordering guarantee, so there is nothing to exploit -
//            we must inspect nodes one by one. This is the list equivalent of array linear search.
// Time     : O(n) worst case (x is last or absent), O(1) best case (x is first)
// Space    : O(1) - just a cursor and a counter
// Trade-off: Unlike a sorted ARRAY, a linked list can NEVER use binary search, because binary search
//            needs O(1) access to the middle element and here reaching the middle is already O(n).
//            That is a core reason to choose an array when you need fast lookup.

class Node {
    int data;
    Node next;

    Node(int x) {
        data = x;
        next = null;
    }
}

public class searchList {
    public static void main(String[] args) {
        Node head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        head.next.next.next = new Node(4);
        head.next.next.next.next = new Node(5);

        System.out.println(search(head, 3));  // expected: 3  (1-based position)
        System.out.println(search(head, 1));  // expected: 1  (best case, first node)
        System.out.println(search(head, 99)); // expected: -1 (absent, full traversal)
    }

    // Returns the 1-BASED position of x, or -1 if not present.
    static int search(Node head, int x) {
        int pos = 1;                       // positions are counted from 1, matching the printed output
        Node curr = head;                  // separate cursor so 'head' is never lost

        while (curr != null) {
            if (curr.data == x) {
                return pos;                // first match wins - we stop as soon as it is found
            }
            pos++;                         // advance the counter and the cursor together, in lockstep
            curr = curr.next;
        }
        return -1;                         // walked off the end without a match
        // Edge cases: empty list -> loop skipped -> -1. Duplicates -> returns the EARLIEST position.
    }
}
