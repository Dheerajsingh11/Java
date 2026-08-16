// Problem  : Check whether a singly linked list reads the same forwards and backwards.
// Approach : Two tiers. NAIVE copies values into an array and checks with two indices (O(n) space).
//            EFFICIENT finds the middle, reverses the second half, and compares halves (O(1) space).
// Intuition: A singly list cannot be traversed backward, so either we materialize it (array) or we
//            physically reverse half of it so both halves can be walked forward in lockstep.
// Time     : O(n)   Space: naive O(n); efficient O(1)
// Trade-off: The in-place method is optimal in space but MUTATES the list (reversing half); restore
//            it afterward if the original order must be preserved. The array method is simpler and
//            non-destructive.

public class palindromeLinkedList {

    static class Node {
        int value;
        Node next;
        Node(int value) { this.value = value; }
    }

    static boolean isPalindrome(Node head) {
        if (head == null || head.next == null) return true;

        // 1) Find the middle with slow/fast pointers.
        Node slow = head, fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // 2) Reverse the second half (starting after slow).
        Node second = reverse(slow.next);

        // 3) Compare the first half with the reversed second half.
        Node p = head, q = second;
        boolean ok = true;
        while (q != null) {                 // second half is the shorter/equal one -> drives the loop
            if (p.value != q.value) { ok = false; break; }
            p = p.next;
            q = q.next;
        }
        // (Optional) restore: slow.next = reverse(second);  // left out for brevity
        return ok;
    }

    static Node reverse(Node head) {
        Node prev = null;
        while (head != null) {
            Node nxt = head.next;
            head.next = prev;
            prev = head;
            head = nxt;
        }
        return prev;
    }

    static Node build(int... xs) {
        Node dummy = new Node(0), t = dummy;
        for (int x : xs) { t.next = new Node(x); t = t.next; }
        return dummy.next;
    }

    public static void main(String[] args) {
        System.out.println(isPalindrome(build(1, 2, 3, 2, 1))); // true  (odd length)
        System.out.println(isPalindrome(build(1, 2, 2, 1)));    // true  (even length)
        System.out.println(isPalindrome(build(1, 2, 3)));       // false
    }
}
