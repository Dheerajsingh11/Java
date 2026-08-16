// Problem  : Merge two sorted singly linked lists into one sorted list.
// Approach : Use a dummy head and a 'tail' pointer; repeatedly append the smaller of the two front
//            nodes, then attach whatever remains.
// Intuition: Both lists are already sorted, so the next smallest overall is always one of the two
//            current heads. Splicing existing nodes (no new allocation) keeps it O(1) extra space.
// Time     : O(n + m)   Space: O(1) (re-links existing nodes; dummy avoids special-casing the head)
// Trade-off: The dummy-node pattern removes the awkward "is this the first node?" check. This merge
//            is the core subroutine of merge sort on linked lists.

public class mergeTwoSorted {

    static class Node {
        int value;
        Node next;
        Node(int value) { this.value = value; }
    }

    static Node merge(Node a, Node b) {
        Node dummy = new Node(0);   // a stand-in head; dummy.next will be the real result head
        Node tail = dummy;

        while (a != null && b != null) {
            if (a.value <= b.value) { // <= keeps the merge STABLE (a's equal elements come first)
                tail.next = a;
                a = a.next;
            } else {
                tail.next = b;
                b = b.next;
            }
            tail = tail.next;
        }
        tail.next = (a != null) ? a : b; // one list is exhausted; attach the remainder of the other
        return dummy.next;
    }

    static Node build(int... xs) {
        Node dummy = new Node(0), t = dummy;
        for (int x : xs) { t.next = new Node(x); t = t.next; }
        return dummy.next;
    }
    static void print(Node h) { for (; h != null; h = h.next) System.out.print(h.value + " "); System.out.println(); }

    public static void main(String[] args) {
        Node a = build(1, 3, 5, 7);
        Node b = build(2, 4, 6);
        print(merge(a, b)); // 1 2 3 4 5 6 7
    }
}
