// Problem  : Implement a circular singly linked list - the last node points back to the first.
// Approach : Keep only a 'tail' pointer; tail.next is the head. This gives O(1) insertion at both
//            the front and the back.
// Intuition: Because the list loops, from the tail we can reach the head in one hop (tail.next). So a
//            single tail reference is enough to insert at either end without walking the list.
// Time     : addFirst/addLast O(1); traversal O(n)   Space: O(n)
// Intuition on termination: traversal must stop after returning to the start, NOT at null (there is
//            no null next in a non-empty circular list).
// Trade-off: Natural fit for round-robin scheduling and cyclic buffers, but you must guard against
//            infinite loops - always stop when you come back to the starting node.

public class CircularLinkedList {

    static class Node {
        int value;
        Node next;
        Node(int value) { this.value = value; }
    }

    private Node tail;   // tail.next is the head; null when empty
    private int size;

    void addLast(int x) {
        Node node = new Node(x);
        if (tail == null) {
            tail = node;
            tail.next = tail;        // single node points to itself
        } else {
            node.next = tail.next;   // new node -> current head
            tail.next = node;        // old tail -> new node
            tail = node;             // new node becomes the tail
        }
        size++;
    }

    void addFirst(int x) {
        addLast(x);                  // insert as tail...
        tail = tail.next;            // ...then shift tail back so the new node is the head
    }

    void print() {
        if (tail == null) { System.out.println("(empty)"); return; }
        Node cur = tail.next;        // start at the head
        do {
            System.out.print(cur.value + " ");
            cur = cur.next;
        } while (cur != tail.next);  // stop when we loop back to the head
        System.out.println();
    }

    public static void main(String[] args) {
        CircularLinkedList cll = new CircularLinkedList();
        cll.addLast(2); cll.addLast(3); cll.addFirst(1); cll.addLast(4);
        cll.print();                 // 1 2 3 4
        System.out.println("tail.next (head) = " + cll.tail.next.value); // 1 (confirms the loop)
    }
}
