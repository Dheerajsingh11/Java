// Problem  : Implement a double-ended queue (deque) - add/remove at BOTH the front and the rear.
// Approach : Circular array with a 'front' index and a 'size', mirroring ArrayQueue but supporting
//            insertion/deletion at either end via modulo wraparound.
// Intuition: A deque generalizes both stack and queue. With a circular buffer, moving 'front'
//            backward (with wrap) inserts at the front; using (front+size) inserts at the rear.
// Time     : all four end operations O(1)   Space: O(capacity)
// Trade-off: One structure serves as stack OR queue OR sliding-window buffer, but fixed capacity.
//            java.util.ArrayDeque is the production-grade, auto-resizing version of this idea.
// NOTE     : named Deque (class name = file name); unrelated to java.util.Deque, not imported here.

public class Deque {
    private int[] data;
    private int front;
    private int size;

    Deque(int capacity) {
        data = new int[capacity];
        front = 0;
        size = 0;
    }

    boolean isEmpty() { return size == 0; }
    boolean isFull()  { return size == data.length; }

    void addFirst(int x) {
        if (isFull()) { System.out.println("Overflow"); return; }
        // Move front one step BACK with wraparound: (front - 1 + n) % n avoids a negative index.
        front = (front - 1 + data.length) % data.length;
        data[front] = x;
        size++;
    }

    void addLast(int x) {
        if (isFull()) { System.out.println("Overflow"); return; }
        int rear = (front + size) % data.length; // one past the current last element
        data[rear] = x;
        size++;
    }

    int removeFirst() {
        if (isEmpty()) throw new RuntimeException("Underflow");
        int v = data[front];
        front = (front + 1) % data.length;       // advance front
        size--;
        return v;
    }

    int removeLast() {
        if (isEmpty()) throw new RuntimeException("Underflow");
        int rear = (front + size - 1) % data.length; // index of the current last element
        size--;
        return data[rear];
    }

    int peekFirst() { if (isEmpty()) throw new RuntimeException("empty"); return data[front]; }
    int peekLast()  { if (isEmpty()) throw new RuntimeException("empty"); return data[(front + size - 1) % data.length]; }

    public static void main(String[] args) {
        Deque dq = new Deque(5);
        dq.addLast(2);    // [2]
        dq.addLast(3);    // [2, 3]
        dq.addFirst(1);   // [1, 2, 3]
        dq.addFirst(0);   // [0, 1, 2, 3]
        System.out.println("first/last: " + dq.peekFirst() + "/" + dq.peekLast()); // 0/3
        System.out.println("removeFirst: " + dq.removeFirst()); // 0
        System.out.println("removeLast : " + dq.removeLast());  // 3
        System.out.println("first/last: " + dq.peekFirst() + "/" + dq.peekLast()); // 1/2
    }
}
