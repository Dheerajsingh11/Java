// Problem  : Use the standard library's stack, queue, and double-ended queue.
// Approach : Show ArrayDeque as both a stack (LIFO) and a queue (FIFO), plus the legacy Stack class.
// Intuition: A stack removes the MOST recently added item (LIFO); a queue removes the OLDEST (FIFO).
//            ArrayDeque supports both ends in O(1), so it serves as either.
// Time     : push/pop/offer/poll/peek all O(1) amortized   Space: O(n)
// Trade-off: Prefer ArrayDeque over the old java.util.Stack (which is synchronized and extends
//            Vector, leaking list operations that break stack semantics). ArrayDeque is faster and
//            cleaner - the recommended choice for both stack and queue.

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import java.util.Stack;

public class StackQueueDeque {
    public static void main(String[] args) {
        // ---- Stack (LIFO) via ArrayDeque ----
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(1);   // top -> [1]
        stack.push(2);   // top -> [2, 1]
        stack.push(3);   // top -> [3, 2, 1]
        System.out.println("stack peek: " + stack.peek()); // 3 (most recent)
        System.out.println("stack pop : " + stack.pop());  // 3  -> removes top
        System.out.println("stack pop : " + stack.pop());  // 2
        System.out.println("stack now : " + stack);        // [1]

        // ---- Queue (FIFO) via ArrayDeque ----
        Queue<Integer> queue = new ArrayDeque<>();
        queue.offer(1); // [1]      (offer = enqueue at tail)
        queue.offer(2); // [1, 2]
        queue.offer(3); // [1, 2, 3]
        System.out.println("queue peek: " + queue.peek()); // 1 (oldest)
        System.out.println("queue poll: " + queue.poll()); // 1  -> removes front
        System.out.println("queue poll: " + queue.poll()); // 2
        System.out.println("queue now : " + queue);        // [3]

        // ---- Deque (both ends) ----
        Deque<Integer> dq = new ArrayDeque<>();
        dq.offerFirst(1); // [1]
        dq.offerLast(2);  // [1, 2]
        dq.offerFirst(0); // [0, 1, 2]
        System.out.println("deque     : " + dq + " first=" + dq.peekFirst() + " last=" + dq.peekLast());

        // ---- Legacy Stack (shown for awareness; avoid in new code) ----
        Stack<Integer> legacy = new Stack<>();
        legacy.push(10);
        legacy.push(20);
        System.out.println("legacy pop: " + legacy.pop()); // 20

        // Edge: on an empty ArrayDeque, offer/poll/peek use null (poll/peek return null), while
        // push/pop/element THROW. Also ArrayDeque forbids null elements (null is its "empty" signal).
    }
}
