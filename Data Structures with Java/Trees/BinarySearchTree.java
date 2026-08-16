// Problem  : Maintain a set of ordered keys with fast search, insert, and delete.
// Approach : A Binary Search Tree keeps the invariant: left subtree < node < right subtree, so each
//            comparison discards half the remaining tree.
// Intuition: The ordering turns search into "go left if smaller, right if bigger" - a decision tree.
//            An inorder traversal therefore yields keys in sorted order.
// Time     : search/insert/delete O(h); h = O(log n) if balanced, O(n) if skewed   Space: O(h) recursion
// Trade-off: Simple and dynamic, but a plain BST degrades to a linked list on sorted input (h = n).
//            Self-balancing variants (AVLTree.java, Red-Black) guarantee O(log n).

public class BinarySearchTree {

    static class Node {
        int key;
        Node left, right;
        Node(int key) { this.key = key; }
    }

    private Node root;

    // Insert returns the (possibly new) subtree root, so the parent can relink in one line.
    private Node insert(Node n, int key) {
        if (n == null) return new Node(key);        // reached an empty spot -> place the new node
        if (key < n.key)      n.left = insert(n.left, key);   // smaller -> go/insert left
        else if (key > n.key) n.right = insert(n.right, key); // larger  -> go/insert right
        // key == n.key -> duplicate; here we ignore it (a BST set)
        return n;
    }
    void insert(int key) { root = insert(root, key); }

    // Search: follow the ordering; O(h).
    boolean search(int key) {
        Node n = root;
        while (n != null) {
            if (key == n.key) return true;
            n = (key < n.key) ? n.left : n.right;   // discard half the tree each step
        }
        return false;
    }

    // Smallest key = leftmost node.
    private Node min(Node n) {
        while (n.left != null) n = n.left;
        return n;
    }

    // Delete handles the three classic cases: leaf, one child, two children.
    private Node delete(Node n, int key) {
        if (n == null) return null;                 // key not found
        if (key < n.key) {
            n.left = delete(n.left, key);
        } else if (key > n.key) {
            n.right = delete(n.right, key);
        } else {
            // Found the node to delete.
            if (n.left == null) return n.right;     // 0 or 1 child: splice in the non-null child
            if (n.right == null) return n.left;
            // Two children: replace with the INORDER SUCCESSOR (smallest key in the right subtree),
            // then delete that successor from the right subtree. This preserves the BST ordering.
            Node succ = min(n.right);
            n.key = succ.key;
            n.right = delete(n.right, succ.key);
        }
        return n;
    }
    void delete(int key) { root = delete(root, key); }

    // Inorder prints keys ascending - a quick correctness check.
    private void inorder(Node n) {
        if (n == null) return;
        inorder(n.left);
        System.out.print(n.key + " ");
        inorder(n.right);
    }
    void printSorted() { inorder(root); System.out.println(); }

    public static void main(String[] args) {
        BinarySearchTree bst = new BinarySearchTree();
        for (int k : new int[]{ 50, 30, 70, 20, 40, 60, 80 }) bst.insert(k);

        bst.printSorted();                       // 20 30 40 50 60 70 80 (sorted via inorder)
        System.out.println("search 60: " + bst.search(60)); // true
        System.out.println("search 25: " + bst.search(25)); // false

        bst.delete(20);                          // leaf case
        bst.delete(30);                          // one-child case
        bst.delete(50);                          // two-children case (root)
        bst.printSorted();                       // 40 60 70 80
    }
}
