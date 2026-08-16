// Problem  : Implement a stack (LIFO) from scratch using a fixed array.
// Approach : Keep an array plus a "top" index; push writes at top+1, pop reads and shrinks top.
// Intuition: A stack only ever touches ONE end. Tracking that end with a single index makes every
//            operation O(1) - no shifting, no searching.
// Time     : push/pop/peek O(1)   Space: O(capacity)
// Trade-off: An array stack is simple and cache-friendly but has a FIXED capacity (overflow when
//            full). A linked stack (LinkedStack.java) grows unbounded at the cost of node memory.

public class ArrayStack {
    private int[] data;   // backing storage
    private int top;      // index of the current top element; -1 means empty

    ArrayStack(int capacity) {
        data = new int[capacity];
        top = -1;         // empty: no element yet
    }

    boolean isEmpty() { return top == -1; }
    boolean isFull()  { return top == data.length - 1; }

    // Add x on top. O(1): just advance the index and store.
    void push(int x) {
        if (isFull()) {                       // Edge: pushing to a full stack = overflow
            System.out.println("Overflow: cannot push " + x);
            return;
        }
        data[++top] = x;                      // pre-increment: move top up, then write
    }

    // Remove and return the top. O(1).
    int pop() {
        if (isEmpty()) {                      // Edge: popping empty = underflow
            throw new RuntimeException("Underflow: stack is empty");
        }
        return data[top--];                   // read top, then move index down (element is now "gone")
    }

    // Look at the top without removing it.
    int peek() {
        if (isEmpty()) throw new RuntimeException("Stack is empty");
        return data[top];
    }

    int size() { return top + 1; }

    public static void main(String[] args) {
        ArrayStack st = new ArrayStack(3);
        st.push(10);
        st.push(20);
        st.push(30);
        st.push(40);               // Overflow (capacity is 3) -> prints the overflow message
        System.out.println("peek: " + st.peek()); // 30
        System.out.println("pop : " + st.pop());  // 30
        System.out.println("pop : " + st.pop());  // 20
        System.out.println("size: " + st.size()); // 1  (only 10 remains)
    }
}
