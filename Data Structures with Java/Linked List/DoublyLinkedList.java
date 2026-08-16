// Problem  : Implement a doubly linked list where each node links to BOTH its next and previous node.
// Approach : Maintain head and tail pointers; each Node has prev/next so we can walk and delete in
//            both directions in O(1) once positioned.
// Intuition: The extra 'prev' pointer lets us go backward and remove a node without first finding its
//            predecessor - the main advantage over a singly linked list.
// Time     : addFirst/addLast O(1); delete a known node O(1); search O(n)   Space: O(n) + extra pointer
// Trade-off: More flexible than a singly list (bidirectional traversal, O(1) delete of a given node)
//            at the cost of one extra pointer per node and more bookkeeping on insert/delete.

public class DoublyLinkedList {

    static class Node {
        int value;
        Node prev, next;
        Node(int value) { this.value = value; }
    }

    private Node head, tail;
    private int size;

    void addFirst(int x) {
        Node node = new Node(x);
        if (head == null) {                 // empty list: node is both head and tail
            head = tail = node;
        } else {
            node.next = head;               // new node points forward to old head
            head.prev = node;               // old head points back to new node
            head = node;
        }
        size++;
    }

    void addLast(int x) {
        Node node = new Node(x);
        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        size++;
    }

    // Delete the first node holding 'x'. O(1) relinking once found.
    boolean delete(int x) {
        Node cur = head;
        while (cur != null && cur.value != x) cur = cur.next; // find it (O(n))
        if (cur == null) return false;

        if (cur.prev != null) cur.prev.next = cur.next; else head = cur.next; // fix predecessor/head
        if (cur.next != null) cur.next.prev = cur.prev; else tail = cur.prev; // fix successor/tail
        size--;
        return true;
    }

    void printForward()  { for (Node c = head; c != null; c = c.next) System.out.print(c.value + " "); System.out.println(); }
    void printBackward() { for (Node c = tail; c != null; c = c.prev) System.out.print(c.value + " "); System.out.println(); }

    public static void main(String[] args) {
        DoublyLinkedList dll = new DoublyLinkedList();
        dll.addLast(2); dll.addLast(3); dll.addFirst(1); dll.addLast(4); // 1 2 3 4
        dll.printForward();   // 1 2 3 4
        dll.printBackward();  // 4 3 2 1  (only possible thanks to prev pointers)
        dll.delete(3);        // remove the middle node in O(1) relinking
        dll.printForward();   // 1 2 4
    }
}
