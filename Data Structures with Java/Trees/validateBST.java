// Problem  : Decide whether a binary tree is a valid Binary Search Tree.
// Approach : Two tiers. NAIVE checks only each node against its immediate children (which is WRONG).
//            EFFICIENT passes down a valid (min, max) RANGE that every node must satisfy.
// Intuition: The BST property is not local. A node must be greater than EVERYTHING in its left
//            subtree and smaller than EVERYTHING in its right subtree - not merely greater than its
//            left child. Carrying an allowed range down the recursion captures that global constraint.
// Time     : O(n) - each node visited once   Space: O(h) recursion depth
// Trade-off: The naive local check is the single most common wrong answer to this problem. It passes
//            on most random trees and fails on a specific shape (shown below), which is exactly what
//            makes it dangerous. An inorder traversal is an equally valid O(n) alternative.

public class validateBST {

    static class Node {
        int value;
        Node left, right;
        Node(int value) { this.value = value; }
    }

    // ---------- NAIVE (INCORRECT - kept to show the trap) ----------
    // Only compares a node with its DIRECT children, so a violation deeper in the tree slips through.
    static boolean isBSTNaive(Node n) {
        if (n == null) return true;
        if (n.left != null && n.left.value >= n.value) return false;
        if (n.right != null && n.right.value <= n.value) return false;
        return isBSTNaive(n.left) && isBSTNaive(n.right);
    }

    // ---------- EFFICIENT (correct) - range propagation ----------
    // Long bounds are used so that Integer.MIN_VALUE / MAX_VALUE can appear as legitimate node values
    // without colliding with the sentinels.
    static boolean isBST(Node n, long min, long max) {
        if (n == null) return true;                       // an empty subtree is trivially valid

        // The node must lie strictly inside the range inherited from its ancestors.
        if (n.value <= min || n.value >= max) return false;

        // Going LEFT tightens the upper bound to this node's value; going RIGHT tightens the lower
        // bound. That is how an ancestor's constraint reaches every descendant.
        return isBST(n.left, min, n.value)
            && isBST(n.right, n.value, max);
    }
    static boolean isBST(Node root) { return isBST(root, Long.MIN_VALUE, Long.MAX_VALUE); }

    // ---------- ALTERNATIVE - inorder must be strictly increasing ----------
    private static Integer prev;
    static boolean isBSTInorder(Node n) {
        if (n == null) return true;
        if (!isBSTInorder(n.left)) return false;
        if (prev != null && n.value <= prev) return false;   // an inorder walk of a BST is sorted
        prev = n.value;
        return isBSTInorder(n.right);
    }

    public static void main(String[] args) {
        // A genuine BST:        5
        //                     /   \
        //                    3     8
        //                   / \   / \
        //                  1   4 7   9
        Node good = new Node(5);
        good.left = new Node(3);  good.right = new Node(8);
        good.left.left = new Node(1); good.left.right = new Node(4);
        good.right.left = new Node(7); good.right.right = new Node(9);

        // NOT a BST - and the naive check cannot see it:
        //        5
        //      /   \
        //     3     8
        //    / \
        //   1   6      <- 6 > 5, so it must NOT be in the LEFT subtree of 5,
        //                 yet 6 > 3 so it is a valid right child of 3.
        Node bad = new Node(5);
        bad.left = new Node(3);   bad.right = new Node(8);
        bad.left.left = new Node(1); bad.left.right = new Node(6);

        System.out.println("valid tree  - naive   : " + isBSTNaive(good));   // true
        System.out.println("valid tree  - correct : " + isBST(good));        // true

        System.out.println("broken tree - naive   : " + isBSTNaive(bad));    // true  <-- WRONG
        System.out.println("broken tree - correct : " + isBST(bad));         // false correct

        prev = null;
        System.out.println("broken tree - inorder : " + isBSTInorder(bad));  // false correct
    }
}
