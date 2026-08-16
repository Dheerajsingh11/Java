// Problem  : Implement a stack (LIFO) using a singly linked list, so it grows unbounded.
// Approach : Push/pop at the HEAD of the list, where insert/remove is O(1).
// Intuition: The list head is the perfect "top of stack": prepending a node and removing the first
//            node both just relink one pointer, with no fixed capacity.
// Time     : push/pop/peek O(1)   Space: O(n) (one node per element)
// Trade-off: No overflow (grows as needed) but each element costs an extra pointer + object header,
//            and nodes are scattered in memory (less cache-friendly than ArrayStack).

public class LinkedStack {

    // A node holds one value and a link to the node below it in the stack.
    private static class Node {
        int value;
        Node next;
        Node(int value, Node next) { this.value = value; this.next = next; }
    }

    private Node head;   // top of the stack (null when empty)
    private int size;

    boolean isEmpty() { return head == null; }

    // Prepend a new node -> it becomes the new top. O(1).
    void push(int x) {
        head = new Node(x, head); // new node points to the old top, then becomes the head
        size++;
    }

    // Remove and return the head value. O(1).
    int pop() {
        if (isEmpty()) throw new RuntimeException("Underflow: stack is empty");
        int v = head.value;
        head = head.next;         // drop the first node; the next one becomes the top
        size--;
        return v;
    }

    int peek() {
        if (isEmpty()) throw new RuntimeException("Stack is empty");
        return head.value;
    }

    int size() { return size; }

    public static void main(String[] args) {
        LinkedStack st = new LinkedStack();
        st.push(1);
        st.push(2);
        st.push(3);
        System.out.println("peek: " + st.peek()); // 3
        System.out.println("pop : " + st.pop());  // 3
        System.out.println("pop : " + st.pop());  // 2
        System.out.println("size: " + st.size()); // 1
        System.out.println("empty? " + st.isEmpty()); // false
    }
}
