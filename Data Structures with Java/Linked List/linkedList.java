// Create our own linked list in java
class Node {
    int data;
    Node next;

    Node(int x) {
        data = x;
        next = null; // Initialize next as null
    }
}

public class linkedList {
    public static void main(String[] args) {
        Node head = new Node(10);
        Node temp1 = new Node(20);
        head.next = temp1; // Link first node with second node
        Node temp2 = new Node(30);
        temp1.next = temp2; // Link second node with third node
    }
}
// Memory allocation will be done dynamically and variables can be stored
// anywhere
