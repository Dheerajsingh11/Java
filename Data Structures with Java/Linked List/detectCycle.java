// Problem  : Detect whether a linked list has a cycle, and find the node where the cycle begins.
// Approach : Two tiers. NAIVE stores visited nodes in a set (O(n) space). EFFICIENT is Floyd's
//            "tortoise and hare" - two pointers at different speeds (O(1) space).
// Intuition: On a cyclic list, a fast pointer (2 steps) laps a slow one (1 step) and they MEET inside
//            the loop. A neat number-theory fact then lets us find the loop's start: reset one pointer
//            to the head and advance both by 1 - they meet exactly at the cycle entry.
// Time     : O(n)   Space: naive O(n); efficient O(1)
// Trade-off: Floyd's is optimal in space and the standard answer. The cycle-start step is non-obvious
//            but follows from the distances covered when the two pointers first meet.

import java.util.HashSet;
import java.util.Set;

public class detectCycle {

    static class Node {
        int value;
        Node next;
        Node(int value) { this.value = value; }
    }

    // ---- Naive: hash set of seen nodes ----
    static boolean hasCycleNaive(Node head) {
        Set<Node> seen = new HashSet<>();
        for (Node c = head; c != null; c = c.next) {
            if (!seen.add(c)) return true; // node seen before -> cycle
        }
        return false;
    }

    // ---- Efficient: Floyd's tortoise and hare ----
    static Node detectCycleStart(Node head) {
        Node slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;         // 1 step
            fast = fast.next.next;    // 2 steps
            if (slow == fast) {       // they met -> a cycle exists
                // Find the entry: move one pointer to head, advance both by 1 until they meet.
                Node p = head;
                while (p != slow) { p = p.next; slow = slow.next; }
                return p;             // the cycle's starting node
            }
        }
        return null;                  // fast reached the end -> no cycle
    }

    public static void main(String[] args) {
        Node a = new Node(1), b = new Node(2), c = new Node(3), d = new Node(4);
        a.next = b; b.next = c; c.next = d;
        System.out.println("cycle? " + hasCycleNaive(a)); // false (linear list)

        d.next = b;                   // create a cycle: ...4 -> back to node 2
        Node start = detectCycleStart(a);
        System.out.println("cycle starts at value: " + (start == null ? "none" : start.value)); // 2
    }
}
