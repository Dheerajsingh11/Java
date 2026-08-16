// Problem  : Print every node of a singly linked list using recursion instead of a loop.
// Approach : Print the current node, then recursively print the rest of the list. Base case: null.
// Intuition: A linked list is a RECURSIVE structure - it is either empty (null), or a node followed
//            by another linked list. Recursion mirrors that definition exactly, which is why the
//            method body is only two lines.
// Time     : O(n) - one call per node
// Space    : O(n) - each pending call holds a stack frame until the base case is reached. This is
//            genuinely O(n), unlike the ITERATIVE version which is O(1).
// Trade-off: Elegant and definition-shaped, but it consumes stack proportional to list LENGTH, so a
//            very long list (~10k+ nodes) can throw StackOverflowError. Prefer the iterative version
//            (traverseLinkedList.java) in real code; learn this one for the recursive mental model.

class Node {
    int data;
    Node next;

    Node(int x) {
        data = x;
        next = null;
    }
}

public class recursivePrint {
    public static void main(String[] args) {
        Node head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        printList(head);       // expected: 1 2 3
        System.out.println();
        printList(null);       // expected: nothing (base case fires immediately)
    }

    static void printList(Node node) {
        // BASE CASE: an empty list has nothing to print, and it stops the recursion. Without this,
        // the calls would run off the end of the list and throw NullPointerException.
        if (node == null) {
            return;
        }

        System.out.print(node.data + " "); // process THIS node first...
        printList(node.next);              // ...then delegate the remaining sub-list to recursion

        // Because we print BEFORE recursing, output is head-to-tail (pre-order).
        // Swapping these two lines would print the list BACKWARDS (tail-to-head) - a neat trick,
        // since the printing then happens as the call stack unwinds.
    }
}
