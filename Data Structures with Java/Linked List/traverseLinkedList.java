// Program to traverse the linked list
class Node {
    int data;
    Node next;

    Node(int x) {
        data = x;
        next = null; // Initialize next as null
    }
}

public class traverseLinkedList {
    public static void main(String[] args) {
        Node head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        head.next.next.next = new Node(4);
        printList(head);
    }

    public static void printList(Node head) {
        Node curr = head;
        while (curr != null) {
            System.out.print(curr.data + " ");
            curr = curr.next; // Move to next node
        }
    }
}
