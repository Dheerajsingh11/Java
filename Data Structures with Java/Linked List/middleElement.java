// Problem  : Find the middle node of a singly linked list in one pass.
// Approach : Two tiers. NAIVE counts the length, then walks to n/2. EFFICIENT uses slow/fast pointers
//            (slow moves 1, fast moves 2) so slow lands on the middle when fast reaches the end.
// Intuition: If one pointer moves twice as fast, it covers the whole list in half the steps - so when
//            the fast pointer finishes, the slow pointer is exactly halfway.
// Time     : O(n)   Space: O(1)
// Trade-off: The two-pointer trick finds the middle in a SINGLE pass without knowing the length -
//            the same tortoise-and-hare idea used for cycle detection (detectCycle.java).

public class middleElement {

    static class Node {
        int value;
        Node next;
        Node(int value) { this.value = value; }
    }

    static Node middleTwoPointer(Node head) {
        Node slow = head, fast = head;
        // For even length, this returns the SECOND of the two middle nodes (stop condition choice).
        while (fast != null && fast.next != null) {
            slow = slow.next;         // 1 step
            fast = fast.next.next;    // 2 steps
        }
        return slow;                  // slow is at the middle when fast falls off the end
    }

    static Node build(int... xs) {
        Node dummy = new Node(0), t = dummy;
        for (int x : xs) { t.next = new Node(x); t = t.next; }
        return dummy.next;
    }

    public static void main(String[] args) {
        System.out.println(middleTwoPointer(build(1, 2, 3, 4, 5)).value); // 3 (odd length)
        System.out.println(middleTwoPointer(build(1, 2, 3, 4)).value);    // 3 (even -> second middle)
    }
}
