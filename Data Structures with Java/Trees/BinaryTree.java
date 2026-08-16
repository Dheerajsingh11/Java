// Problem  : Represent a binary tree and traverse it every standard way (DFS + BFS), plus compute
//            height and size.
// Approach : A Node has a value and left/right child links. Recursion handles the three DFS orders
//            and the size/height; a queue handles level-order (BFS).
// Intuition: A tree is defined recursively (a node plus two subtrees), so recursive traversal is the
//            natural fit. The only difference between pre/in/post-order is WHEN you visit the node
//            relative to its subtrees.
// Time     : every traversal O(n) (each node visited once)   Space: O(h) recursion stack (h = height),
//            or O(w) queue width for BFS
// Trade-off: Recursion is clean but risks stack overflow on a very deep/skewed tree (height ~ n);
//            iterative versions (TreeTraversalsIterative.java) avoid that.

import java.util.ArrayDeque;
import java.util.Queue;

public class BinaryTree {

    static class Node {
        int value;
        Node left, right;
        Node(int value) { this.value = value; }
    }

    // ---- Depth-First traversals (differ only by node-visit position) ----

    // Preorder: NODE, then left, then right. Used to COPY/serialize a tree.
    static void preorder(Node n) {
        if (n == null) return;          // base case: empty subtree contributes nothing
        System.out.print(n.value + " "); // visit the node FIRST
        preorder(n.left);
        preorder(n.right);
    }

    // Inorder: left, NODE, right. For a BST this prints values in SORTED order.
    static void inorder(Node n) {
        if (n == null) return;
        inorder(n.left);
        System.out.print(n.value + " "); // visit in the MIDDLE
        inorder(n.right);
    }

    // Postorder: left, right, NODE. Used to DELETE/free a tree (children before parent).
    static void postorder(Node n) {
        if (n == null) return;
        postorder(n.left);
        postorder(n.right);
        System.out.print(n.value + " "); // visit LAST
    }

    // ---- Breadth-First traversal (level by level) using a queue ----
    static void levelOrder(Node root) {
        if (root == null) return;
        Queue<Node> q = new ArrayDeque<>();
        q.offer(root);
        while (!q.isEmpty()) {
            Node cur = q.poll();          // take the front of the current frontier
            System.out.print(cur.value + " ");
            if (cur.left != null) q.offer(cur.left);   // enqueue next level's nodes
            if (cur.right != null) q.offer(cur.right);
        }
    }

    // Number of nodes = 1 + nodes in each subtree.
    static int size(Node n) {
        if (n == null) return 0;
        return 1 + size(n.left) + size(n.right);
    }

    // Height (edges on the longest root->leaf path) = 1 + max child height. Empty tree = -1.
    static int height(Node n) {
        if (n == null) return -1;         // convention: empty = -1 so a single node has height 0
        return 1 + Math.max(height(n.left), height(n.right));
    }

    public static void main(String[] args) {
        //        1
        //       / \
        //      2   3
        //     / \
        //    4   5
        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);
        root.left.left = new Node(4);
        root.left.right = new Node(5);

        System.out.print("preorder  : "); preorder(root);  System.out.println(); // 1 2 4 5 3
        System.out.print("inorder   : "); inorder(root);   System.out.println(); // 4 2 5 1 3
        System.out.print("postorder : "); postorder(root); System.out.println(); // 4 5 2 3 1
        System.out.print("levelOrder: "); levelOrder(root);System.out.println(); // 1 2 3 4 5
        System.out.println("size   : " + size(root));   // 5
        System.out.println("height : " + height(root)); // 2 (edges: 1->2->4)
    }
}
