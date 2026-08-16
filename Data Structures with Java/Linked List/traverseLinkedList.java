// Problem  : Visit and print every node of a singly linked list, front to back.
// Approach : ITERATIVE traversal - start at head and follow 'next' references until null.
// Intuition: The only way into a linked list is the head, and the only way forward is the 'next'
//            reference. So traversal is a simple "walk the chain until you fall off the end".
// Time     : O(n) - each node is visited exactly once
// Space    : O(1) - only ONE extra reference (curr) exists no matter how long the list is.
//            (The original note here said O(n); that is incorrect - nothing grows with n.)
// Trade-off: Iterative traversal is the right default: constant space and no stack-overflow risk.
//            The recursive equivalent (recursivePrint.java) reads more elegantly but costs O(n) stack.

class Node {
    int data;
    Node next;

    Node(int x) {
        data = x;
        next = null;
    }
}

public class traverseLinkedList {
    public static void main(String[] args) {
        Node head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        head.next.next.next = new Node(4);
        printList(head);            // expected: 1 2 3 4
        System.out.println();
        printList(null);            // expected: (prints nothing - empty list handled cleanly)
    }

    public static void printList(Node head) {
        // Use a SEPARATE cursor instead of moving 'head' itself. If we advanced head, we would lose
        // our only reference to the start of the list and could never traverse it again.
        Node curr = head;

        while (curr != null) {      // null is the end-of-list sentinel, so it doubles as the loop guard
            System.out.print(curr.data + " ");
            curr = curr.next;       // hop to the next node; this is the single "step" of the walk
        }
        // Edge case: an empty list (head == null) skips the loop entirely and prints nothing -
        // no special-case code needed.
    }
}
