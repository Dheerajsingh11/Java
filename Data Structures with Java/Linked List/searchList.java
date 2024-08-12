// Program to search for element in Linked list
// Iterative solution
class Node {
    int data;
    Node next;

    Node(int x) {
        data = x;
        next = null;
    }
}

public class searchList {
    public static void main(String[] args) {
        Node head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        head.next.next.next = new Node(4);
        head.next.next.next.next = new Node(5);
        System.out.print(search(head, 3));
    }

    static int search(Node head, int x) {
        int pos = 1;
        Node curr = head;
        while (curr != null) {
            if (curr.data == x) {
                return pos;
            } else {
                pos++;
                curr = curr.next;
            }
        }
        return -1; // Element not found in list
    }
}

// Output: 3, the element 3 is found at position 3 in the given linked list.
// Time complexity: O(n)
// Auxiliary space: O(1)