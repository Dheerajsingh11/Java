// Problem  : Compute a binary tree's height and its DIAMETER (longest path between any two nodes,
//            which need not pass through the root).
// Approach : Two tiers. NAIVE recomputes height at every node (O(n^2)); EFFICIENT computes height and
//            diameter together in one post-order pass (O(n)).
// Intuition: The longest path through a given node = (height of left subtree) + (height of right
//            subtree) + 2 edges. The diameter is the maximum of that quantity over all nodes.
// Time     : see each method   Space: O(h) recursion
// Trade-off: The naive version is easy but repeats height work; the efficient version returns height
//            upward and updates a shared max in the same recursion - the standard O(n) solution.

public class treeHeightAndDiameter {

    static class Node {
        int value;
        Node left, right;
        Node(int value) { this.value = value; }
    }

    static int height(Node n) {
        if (n == null) return -1;                  // empty = -1 edge-count convention
        return 1 + Math.max(height(n.left), height(n.right));
    }

    // ---------- NAIVE: O(n^2) ----------
    // At each node, ask "path through me = leftHeight + rightHeight + 2", but height() itself walks
    // the subtree, so we re-walk nodes many times.
    static int diameterNaive(Node n) {
        if (n == null) return 0;
        int through = height(n.left) + height(n.right) + 2; // longest path bending at this node (edges)
        int inLeft = diameterNaive(n.left);
        int inRight = diameterNaive(n.right);
        return Math.max(through, Math.max(inLeft, inRight));
    }

    // ---------- EFFICIENT: O(n) ----------
    // One post-order pass. The helper RETURNS the node's height and, as a side effect, updates the
    // best diameter seen so far - so height is computed exactly once per node.
    static int best;
    static int heightForDiameter(Node n) {
        if (n == null) return -1;
        int lh = heightForDiameter(n.left);        // height of left subtree
        int rh = heightForDiameter(n.right);       // height of right subtree
        best = Math.max(best, lh + rh + 2);        // path bending here = lh + rh + 2 edges
        return 1 + Math.max(lh, rh);               // this node's height, passed to its parent
    }
    static int diameterEfficient(Node root) {
        best = 0;
        heightForDiameter(root);
        return best;
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

        System.out.println("height           : " + height(root));            // 2
        System.out.println("diameter (naive) : " + diameterNaive(root));     // 3 (4->2->5 ... 4->2->1->3)
        System.out.println("diameter (eff)   : " + diameterEfficient(root)); // 3 (path 4-2-1-3, 3 edges)
    }
}
