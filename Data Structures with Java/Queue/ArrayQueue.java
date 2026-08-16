// Problem  : Implement a FIFO queue using a fixed array with front/rear indices.
// Approach : Track 'front' (next to dequeue) and 'size'; compute rear positions with MODULO so the
//            array wraps around (a circular buffer) instead of wasting freed front slots.
// Intuition: In a queue we add at the rear and remove from the front. A naive array leaves growing
//            unusable gaps at the front; wrapping indices with % reuses that space, giving true O(1).
// Time     : enqueue/dequeue/peek O(1)   Space: O(capacity)
// Trade-off: Fixed capacity (can be full). This IS the circular-queue technique - without the
//            modulo wrap, a plain array queue either wastes space or costs O(n) to shift elements.

public class ArrayQueue {
    private int[] data;
    private int front;   // index of the current front element
    private int size;    // number of elements currently stored

    ArrayQueue(int capacity) {
        data = new int[capacity];
        front = 0;
        size = 0;
    }

    boolean isEmpty() { return size == 0; }
    boolean isFull()  { return size == data.length; }

    // Add x at the rear. Rear index = (front + size) wrapped into the array.
    void enqueue(int x) {
        if (isFull()) {                          // Edge: queue full -> overflow
            System.out.println("Overflow: cannot enqueue " + x);
            return;
        }
        int rear = (front + size) % data.length; // wrap around to reuse freed front slots
        data[rear] = x;
        size++;
    }

    // Remove and return the front. Advance front with wraparound.
    int dequeue() {
        if (isEmpty()) throw new RuntimeException("Underflow: queue is empty");
        int v = data[front];
        front = (front + 1) % data.length;       // move front forward, wrapping at the end
        size--;
        return v;
    }

    int peek() {
        if (isEmpty()) throw new RuntimeException("Queue is empty");
        return data[front];
    }

    int size() { return size; }

    public static void main(String[] args) {
        ArrayQueue q = new ArrayQueue(3);
        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);
        System.out.println("dequeue: " + q.dequeue()); // 10 (oldest out first)
        q.enqueue(40);                                  // reuses the slot freed by 10 (wraparound)
        System.out.println("peek   : " + q.peek());     // 20
        System.out.println("dequeue: " + q.dequeue());  // 20
        System.out.println("dequeue: " + q.dequeue());  // 30
        System.out.println("dequeue: " + q.dequeue());  // 40
        System.out.println("empty? : " + q.isEmpty());  // true
    }
}
