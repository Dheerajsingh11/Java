// Problem  : Remove the n-th node from the END of a singly linked list in one pass.
// Approach : Two-pointer gap. Advance a 'fast' pointer n nodes ahead, then move 'fast' and 'slow'
//            together; when fast hits the end, slow sits just before the node to remove.
// Intuition: Keeping the two pointers exactly n apart means that when the leader reaches the end, the
//            follower is n from the end - precisely the predecessor of the target. A dummy head
//            gracefully handles removing the very first node.
// Time     : O(n) single pass   Space: O(1)
// Trade-off: One pass instead of the two passes (count length, then walk) the obvious method needs.
//            The dummy node avoids special-casing head removal.

public class removeNthFromEnd {

    static class Node {
        int value;
        Node next;
        Node(int value) { this.value = value; }
    }

    static Node removeNth(Node head, int n) {
        Node dummy = new Node(0);
        dummy.next = head;
        Node fast = dummy, slow = dummy;

        for (int i = 0; i < n; i++) fast = fast.next; // move fast n steps ahead (open the gap)

        // Move both until fast is at the last node; slow is then before the target.
        while (fast.next != null) {
            fast = fast.next;
            slow = slow.next;
        }
        slow.next = slow.next.next;   // unlink the n-th-from-end node
        return dummy.next;            // dummy.next handles the case where the head was removed
    }

    static Node build(int... xs) {
        Node dummy = new Node(0), t = dummy;
        for (int x : xs) { t.next = new Node(x); t = t.next; }
        return dummy.next;
    }
    static void print(Node h) { for (; h != null; h = h.next) System.out.print(h.value + " "); System.out.println(); }

    public static void main(String[] args) {
        print(removeNth(build(1, 2, 3, 4, 5), 2)); // 1 2 3 5  (removes the 4)
        print(removeNth(build(1, 2, 3), 3));       // 2 3      (removes the head)
    }
}
