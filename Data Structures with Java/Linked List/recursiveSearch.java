// Problem  : Find the position of a value x in a singly linked list, using recursion.
// Approach : If the head matches, the answer is 1. Otherwise search the REST of the list and add 1
//            to whatever position it reports (unless it reports "not found").
// Intuition: Position within the whole list = 1 + position within the sub-list starting at the next
//            node. The +1 accounts for the node we just skipped, so the count is rebuilt on the way
//            back UP the call stack rather than tracked on the way down.
// Time     : O(n) - one call per node in the worst case
// Space    : O(n) - one stack frame per pending call (the iterative searchList.java is O(1))
// Trade-off: Demonstrates how recursion can compute an accumulated value during unwinding. In
//            production the iterative version is preferable: same time, constant space, no overflow.

class Node {
    int data;
    Node next;

    Node(int x) {
        data = x;
        next = null;
    }
}

public class recursiveSearch {
    public static void main(String[] args) {
        Node head = new Node(10);
        head.next = new Node(20);
        head.next.next = new Node(30);
        head.next.next.next = new Node(40);

        System.out.println(search(head, 30)); // expected: 3
        System.out.println(search(head, 10)); // expected: 1 (matches immediately)
        System.out.println(search(head, 99)); // expected: -1 (absent)
    }

    // Returns the 1-BASED position of x, or -1 if absent.
    static int search(Node head, int x) {
        // BASE CASE 1: ran off the end without finding x.
        if (head == null) {
            return -1;
        }
        // BASE CASE 2: found it at the front of THIS sub-list, so its local position is 1.
        if (head.data == x) {
            return 1;
        }

        // RECURSIVE CASE: ask the rest of the list where x is.
        int res = search(head.next, x);

        // Propagate "not found" unchanged - we must NOT add 1 to -1, or the sentinel would be
        // corrupted into 0, 1, 2... and wrongly look like a valid position.
        if (res == -1) {
            return -1;
        }
        // x sits at position 'res' in the sub-list, so it is one further along in THIS list.
        return res + 1;
    }
}
