// Program to print the linked list recursively
class Node {
    int data;
    Node next;

    Node(int x) {
        data = x;
        next = null;
    }
}

public class recursivePrint {
    public static void main(String[] args) {
        Node head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        printList(head);
    }

    static void printList(Node node) {
        if (node == null) {
            return;
        }
        System.out.print(node.data + " ");
        printList(node.next);
    }
}
// Time complexity: O(n)
// Auxiliary space: O(n)