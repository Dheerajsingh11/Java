// Problem  : Build the most basic singly linked list by hand and understand its memory model.
// Approach : Define a Node (data + a reference to the next Node), then wire three nodes together.
// Intuition: Unlike an array, a linked list does NOT need one contiguous block of memory. Each node
//            is allocated separately anywhere on the heap, and the 'next' reference is the only thing
//            that stitches them into a sequence.
// Time     : creating/linking each node O(1)   Space: O(n) - one node object per element
// Trade-off: This dynamic layout is why a linked list can grow without ever "resizing and copying"
//            (which an array must do), but it also means you LOSE O(1) index access - reaching the
//            k-th element requires following k references. See Note.md for the full comparison.

// A Node is one "link" in the chain: it stores a value and points at the following node.
class Node {
    int data;    // the value this node holds
    Node next;   // reference to the NEXT node; null means "this is the last node"

    Node(int x) {
        data = x;
        next = null; // a brand-new node is not attached to anything yet
    }
}

public class linkedList {
    public static void main(String[] args) {
        // Build:  head(10) -> temp1(20) -> temp2(30) -> null
        Node head = new Node(10);   // 'head' is our only handle on the list; lose it and the list is garbage

        Node temp1 = new Node(20);
        head.next = temp1;          // link node 1 to node 2 (this reference IS the list structure)

        Node temp2 = new Node(30);
        temp1.next = temp2;         // link node 2 to node 3; temp2.next stays null = end of list

        // Walk the chain to prove the links are correct.
        System.out.println(head.data);                     // expected: 10
        System.out.println(head.next.data);                // expected: 20
        System.out.println(head.next.next.data);           // expected: 30
        System.out.println(head.next.next.next);           // expected: null (end marker)
    }
    // Key mental model: the three Node objects may sit far apart in memory. What makes them a "list"
    // is purely the chain of 'next' references - which is also why traversal is not cache-friendly
    // the way an array scan is.
}
