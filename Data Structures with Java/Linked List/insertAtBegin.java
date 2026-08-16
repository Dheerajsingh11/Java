// Problem  : Insert a new element at the FRONT of a singly linked list.
// Approach : Create the node, point its 'next' at the current head, and return it as the new head.
// Intuition: The front is the one place a singly linked list can be modified without walking it -
//            we already hold the head reference, so no searching is needed. Only two pointer
//            assignments are involved regardless of list length.
// Time     : O(1) - constant, independent of list size
// Space    : O(1) extra (one new node is allocated, but no extra working memory)
// Trade-off: This is the linked list's signature advantage. Inserting at the front of an ARRAY costs
//            O(n) because every existing element must shift right; here it is O(1). That is exactly
//            why a linked list is the natural backing store for a stack (see Stack/LinkedStack.java).

class Node {
    int data;
    Node next;

    Node(int d) {
        data = d;
        next = null;
    }
}

public class insertAtBegin {
    public static void main(String[] args) {
        Node head = null;                 // start with an EMPTY list

        // Each call returns the NEW head, so we must reassign. Inserting 30, then 20, then 10
        // produces 10 -> 20 -> 30, i.e. the reverse of the insertion order (LIFO behaviour).
        head = insertBegin(head, 30);     // 30
        head = insertBegin(head, 20);     // 20 -> 30
        head = insertBegin(head, 10);     // 10 -> 20 -> 30

        for (Node c = head; c != null; c = c.next) System.out.print(c.data + " ");
        System.out.println();             // expected: 10 20 30
    }

    // Returns the new head of the list. We RETURN it (rather than mutating a parameter) because Java
    // is pass-by-value: reassigning the local 'head' parameter inside this method would NOT change
    // the caller's variable. See Java Core/06-Methods/MethodsDemo.java for that rule.
    static Node insertBegin(Node head, int x) {
        Node temp = new Node(x);
        temp.next = head;                 // new node points at the old first node...
        return temp;                      // ...and becomes the new first node
        // Edge case: if head was null (empty list), temp.next = null and temp is a correct 1-node
        // list - the same two lines handle both cases with no special branch.
    }
}
