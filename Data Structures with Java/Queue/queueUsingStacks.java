// Problem  : Build a FIFO queue using only two LIFO stacks.
// Approach : Two tiers. NAIVE makes enqueue costly; EFFICIENT makes dequeue amortized O(1) by only
//            moving elements between stacks when necessary.
// Intuition: Reversing a stack into another stack flips LIFO into FIFO. The question is WHEN to pay
//            for that reversal - on every enqueue (naive) or lazily on dequeue (efficient).
// Time     : see each method   Space: O(n)
// Trade-off: The efficient version has O(1) AMORTIZED dequeue (each element moves between stacks at
//            most once) but an occasional O(n) transfer; the naive version keeps the queue ready at
//            all times but pays O(n) on every enqueue. A classic "where do you pay the cost?" trade.

import java.util.ArrayDeque;
import java.util.Deque;

public class queueUsingStacks {

    // ---------- EFFICIENT: costly-dequeue (amortized O(1)) ----------
    // 'in' collects new items; 'out' serves them in FIFO order. We only refill 'out' when it is empty.
    static class QueueEfficient {
        private final Deque<Integer> in = new ArrayDeque<>();
        private final Deque<Integer> out = new ArrayDeque<>();

        void enqueue(int x) { in.push(x); }      // O(1): just drop it on the 'in' stack

        int dequeue() {
            if (out.isEmpty()) {                 // 'out' empty: reverse 'in' into 'out' ONCE
                while (!in.isEmpty()) out.push(in.pop()); // this flips the order -> oldest on top
            }
            if (out.isEmpty()) throw new RuntimeException("Underflow");
            return out.pop();                    // amortized O(1): each element is moved just once
        }

        int peek() {
            if (out.isEmpty()) while (!in.isEmpty()) out.push(in.pop());
            if (out.isEmpty()) throw new RuntimeException("empty");
            return out.peek();
        }
    }

    // ---------- NAIVE: costly-enqueue (keeps oldest always on top) ----------
    static class QueueNaive {
        private final Deque<Integer> main = new ArrayDeque<>();
        private final Deque<Integer> temp = new ArrayDeque<>();

        void enqueue(int x) {                    // O(n): rebuild so the newest ends up at the bottom
            while (!main.isEmpty()) temp.push(main.pop());
            main.push(x);
            while (!temp.isEmpty()) main.push(temp.pop());
        }

        int dequeue() { return main.pop(); }     // O(1): oldest is already on top
        int peek()    { return main.peek(); }
    }

    public static void main(String[] args) {
        QueueEfficient q = new QueueEfficient();
        q.enqueue(1); q.enqueue(2); q.enqueue(3);
        System.out.println("dequeue: " + q.dequeue()); // 1 (FIFO)
        q.enqueue(4);
        System.out.println("dequeue: " + q.dequeue()); // 2
        System.out.println("peek   : " + q.peek());    // 3

        QueueNaive n = new QueueNaive();
        n.enqueue(1); n.enqueue(2); n.enqueue(3);
        System.out.println("naive dequeue: " + n.dequeue()); // 1
        System.out.println("naive dequeue: " + n.dequeue()); // 2
    }
}
