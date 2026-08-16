// Problem  : Keep a BST balanced so operations stay O(log n) even on sorted input.
// Approach : An AVL tree stores each node's height and, after every insert, rebalances via
//            rotations whenever a node's two subtrees differ in height by more than 1.
// Intuition: A plain BST degrades to a linked list (height n) on sorted keys. AVL enforces the
//            invariant |height(left) - height(right)| <= 1 at every node, bounding height to O(log n)
//            and restoring it with O(1) local rotations.
// Time     : search/insert O(log n) guaranteed   Space: O(n)
// Trade-off: Stricter balance than a Red-Black tree -> shorter trees and faster lookups, but more
//            rotations on insert/delete. Great for read-heavy workloads.

public class AVLTree {

    static class Node {
        int key, height;   // height cached to compute balance in O(1)
        Node left, right;
        Node(int key) { this.key = key; this.height = 1; } // a leaf has height 1 here
    }

    private Node root;

    private int h(Node n) { return n == null ? 0 : n.height; }
    private int balance(Node n) { return n == null ? 0 : h(n.left) - h(n.right); } // >1 left-heavy, <-1 right-heavy
    private void update(Node n) { n.height = 1 + Math.max(h(n.left), h(n.right)); }

    // Right rotation - fixes a LEFT-heavy imbalance. Pivot y's left child x becomes the new root.
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node t = x.right;
        x.right = y;          // y goes down-right
        y.left = t;           // y adopts x's old right subtree
        update(y);            // update the LOWER node first...
        update(x);            // ...then the new subtree root
        return x;
    }

    // Left rotation - mirror image, fixes a RIGHT-heavy imbalance.
    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node t = y.left;
        y.left = x;
        x.right = t;
        update(x);
        update(y);
        return y;
    }

    private Node insert(Node n, int key) {
        // 1) Ordinary BST insert.
        if (n == null) return new Node(key);
        if (key < n.key)      n.left = insert(n.left, key);
        else if (key > n.key) n.right = insert(n.right, key);
        else return n;                             // no duplicates

        // 2) Update height and check balance on the way back up.
        update(n);
        int b = balance(n);

        // 3) Four rotation cases, decided by WHERE the new key landed:
        if (b > 1 && key < n.left.key)  return rotateRight(n);            // Left-Left
        if (b < -1 && key > n.right.key) return rotateLeft(n);           // Right-Right
        if (b > 1 && key > n.left.key) {                                 // Left-Right
            n.left = rotateLeft(n.left);
            return rotateRight(n);
        }
        if (b < -1 && key < n.right.key) {                              // Right-Left
            n.right = rotateRight(n.right);
            return rotateLeft(n);
        }
        return n;                                  // already balanced
    }
    void insert(int key) { root = insert(root, key); }

    private void inorder(Node n) {
        if (n == null) return;
        inorder(n.left);
        System.out.print(n.key + " ");
        inorder(n.right);
    }

    public static void main(String[] args) {
        AVLTree t = new AVLTree();
        // Inserting sorted keys 1..7 would make a plain BST a straight line (height 7).
        for (int k = 1; k <= 7; k++) t.insert(k);

        t.inorder(t.root); System.out.println();     // 1 2 3 4 5 6 7 (still a valid BST)
        System.out.println("root key   : " + t.root.key);    // 4  (balanced -> middle at the top)
        System.out.println("root height: " + t.root.height); // 3  (~log2(7)+1, not 7)
    }
}
