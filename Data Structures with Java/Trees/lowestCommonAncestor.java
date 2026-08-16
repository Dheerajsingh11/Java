// Problem  : Find the Lowest Common Ancestor (LCA) of two nodes - the deepest node that has both as
//            descendants.
// Approach : Two contexts. For a BST, use the ordering to walk down in O(h). For a general binary
//            tree, use a post-order recursion that reports where each target was found.
// Intuition: BST - the LCA is the first node where the two keys "split" (one goes left, one right).
//            General tree - the LCA is the lowest node whose subtrees contain the two targets
//            separately (or which is itself one of the targets).
// Time     : BST O(h); general tree O(n)   Space: O(h) recursion
// Trade-off: The BST method is faster (O(h)) but needs the ordering property; the general method
//            works on ANY binary tree at O(n). Use the one that matches your tree.

public class lowestCommonAncestor {

    static class Node {
        int value;
        Node left, right;
        Node(int value) { this.value = value; }
    }

    // ---- BST LCA: O(h), iterative, no extra space ----
    // Walk from the root: if both keys are smaller, the LCA is left; if both larger, it is right;
    // otherwise they split here (or one equals this node) -> this node is the LCA.
    static Node lcaBST(Node root, int p, int q) {
        Node cur = root;
        while (cur != null) {
            if (p < cur.value && q < cur.value)      cur = cur.left;
            else if (p > cur.value && q > cur.value) cur = cur.right;
            else return cur;                          // the split point = LCA
        }
        return null;
    }

    // ---- General binary tree LCA: O(n) post-order ----
    // Returns: the target node if this subtree contains p or q; the LCA if it contains BOTH;
    // null if it contains neither. The first node that "sees" both from different sides is the LCA.
    static Node lcaTree(Node n, int p, int q) {
        if (n == null) return null;
        if (n.value == p || n.value == q) return n;   // this node is one of the targets
        Node left = lcaTree(n.left, p, q);
        Node right = lcaTree(n.right, p, q);
        if (left != null && right != null) return n;  // targets found on BOTH sides -> n is the LCA
        return (left != null) ? left : right;         // otherwise bubble up whichever side found one
    }

    public static void main(String[] args) {
        //        50
        //       /  \
        //     30    70
        //    /  \   /  \
        //  20   40 60   80
        Node root = new Node(50);
        root.left = new Node(30);
        root.right = new Node(70);
        root.left.left = new Node(20);
        root.left.right = new Node(40);
        root.right.left = new Node(60);
        root.right.right = new Node(80);

        System.out.println("BST  LCA(20,40): " + lcaBST(root, 20, 40).value);  // 30
        System.out.println("BST  LCA(20,80): " + lcaBST(root, 20, 80).value);  // 50
        System.out.println("Tree LCA(60,80): " + lcaTree(root, 60, 80).value); // 70
        System.out.println("Tree LCA(20,70): " + lcaTree(root, 20, 70).value); // 50
    }
}
