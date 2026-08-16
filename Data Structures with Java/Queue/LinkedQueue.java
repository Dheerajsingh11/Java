// Problem  : Implement a FIFO queue using a linked list that grows unbounded.
// Approach : Keep pointers to BOTH ends - 'front' (dequeue here) and 'rear' (enqueue here) - so both
//            operations are O(1) without scanning the list.
// Intuition: A queue touches two ends. Storing a tail pointer avoids walking to the end on every
//            enqueue; storing a head pointer makes dequeue trivial.
// Time     : enqueue/dequeue/peek O(1)   Space: O(n)
// Trade-off: No capacity limit (unlike ArrayQueue) but each element costs an extra node/pointer and
//            nodes are scattered in memory. Must carefully keep 'rear' consistent when emptying.

public class LinkedQueue {

    private static class Node {
        int value;
        Node next;
        Node(int value) { this.value = value; }
    }

    private Node front; // remove from here (head)
    private Node rear;  // add to here (tail)
    private int size;

    boolean isEmpty() { return front == null; }

    // Append at the tail. O(1) thanks to the rear pointer.
    void enqueue(int x) {
        Node node = new Node(x);
        if (rear == null) {          // empty queue: node becomes both front and rear
            front = rear = node;
        } else {
            rear.next = node;        // link new node after the old tail...
            rear = node;             // ...and make it the new tail
        }
        size++;
    }

    // Remove from the head. O(1).
    int dequeue() {
        if (isEmpty()) throw new RuntimeException("Underflow: queue is empty");
        int v = front.value;
        front = front.next;          // advance the head
        if (front == null) rear = null; // Edge: queue became empty -> reset rear too (else it dangles)
        size--;
        return v;
    }

    int peek() {
        if (isEmpty()) throw new RuntimeException("Queue is empty");
        return front.value;
    }

    int size() { return size; }

    public static void main(String[] args) {
        LinkedQueue q = new LinkedQueue();
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        System.out.println("dequeue: " + q.dequeue()); // 1
        System.out.println("peek   : " + q.peek());    // 2
        q.enqueue(4);
        System.out.println("size   : " + q.size());    // 3  (2,3,4)
        System.out.println("dequeue: " + q.dequeue()); // 2
        System.out.println("dequeue: " + q.dequeue()); // 3
        System.out.println("dequeue: " + q.dequeue()); // 4
        System.out.println("empty? : " + q.isEmpty()); // true
    }
}
