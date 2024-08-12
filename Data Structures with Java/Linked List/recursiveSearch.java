// Recursively look up for the element in the Linked list
class Node {
    int data;
    Node next;

    Node(int x) {
        data = x;
        next = null;
    }
}

public class recursiveSearch {
    public static void main(String[] args) {
        Node head = new Node(10);
        head.next = new Node(20);
        head.next.next = new Node(30);
        head.next.next.next = new Node(40);
        System.out.print(search(head, 30));
    }

    static int search(Node head, int x) {
        if (head == null) {
            return -1;
        }
        if (head.data == x) {
            return 1;
        } else {
            int res = search(head.next, x);
            if (res == -1) {
                return -1;
            } else {
                return res + 1;
            }
        }
    }
}

// Time complexity: O(n) where n is the number of nodes in the linked list.
// Auxiliary space: O(n)