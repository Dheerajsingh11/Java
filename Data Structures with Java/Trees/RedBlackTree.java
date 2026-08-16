// Problem  : Keep a BST balanced with FEWER rotations than AVL, so writes stay cheap.
// Approach : A Red-Black tree colours every node red or black and enforces five invariants that
//            together bound the height at 2*log2(n+1). Insertion fixes violations by recolouring
//            first and rotating only when recolouring cannot resolve it.
// Intuition: AVL enforces a strict height difference of at most 1, which needs frequent rotations.
//            Red-Black relaxes the rule to "no root-to-leaf path is more than twice as long as any
//            other", enforced via colours. That looser bound is still O(log n) but is reachable with
//            far less restructuring - recolouring is O(1) and often enough on its own.
// Time     : search / insert O(log n) guaranteed   Space: O(n) (one colour bit per node)
// Trade-off: Red-Black gives ~2-3x fewer rotations than AVL on insert-heavy workloads, at the cost of
//            a slightly TALLER tree and therefore slightly slower lookups. Java's TreeMap/TreeSet and
//            C++'s std::map both choose Red-Black because mixed read/write is the common case; prefer
//            AVL when lookups dominate. (This file implements insertion; deletion is considerably
//            more involved and is omitted deliberately.)

public class RedBlackTree {

    private static final boolean RED = true, BLACK = false;

    static class Node {
        int key;
        Node left, right, parent;
        boolean colour = RED;      // new nodes start RED - see why in the fixup notes below
        Node(int key) { this.key = key; }
    }

    private Node root;

    private boolean isRed(Node n) { return n != null && n.colour == RED; }

    // ---- Rotations: restructure locally while PRESERVING the BST ordering ----
    private void rotateLeft(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != null) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == null)            root = y;
        else if (x == x.parent.left)     x.parent.left = y;
        else                             x.parent.right = y;
        y.left = x;
        x.parent = y;
    }

    private void rotateRight(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != null) y.right.parent = x;
        y.parent = x.parent;
        if (x.parent == null)            root = y;
        else if (x == x.parent.right)    x.parent.right = y;
        else                             x.parent.left = y;
        y.right = x;
        x.parent = y;
    }

    public void insert(int key) {
        // Phase 1 - ordinary BST insert, colouring the new node RED.
        Node parent = null, cur = root;
        while (cur != null) {
            parent = cur;
            if (key < cur.key)      cur = cur.left;
            else if (key > cur.key) cur = cur.right;
            else return;                              // no duplicates
        }
        Node node = new Node(key);
        node.parent = parent;
        if (parent == null)          root = node;
        else if (key < parent.key)   parent.left = node;
        else                         parent.right = node;

        fixup(node);
    }

    // Phase 2 - restore the invariants. The ONLY rule a red insert can break is "no red node has a
    // red parent" (rule 4); inserting black would instead break the black-height rule everywhere,
    // which is much harder to repair. That is why new nodes are red.
    private void fixup(Node z) {
        while (isRed(z.parent)) {
            Node grandparent = z.parent.parent;

            if (z.parent == grandparent.left) {
                Node uncle = grandparent.right;

                if (isRed(uncle)) {
                    // CASE 1 - red uncle: just RECOLOUR. Push the "redness" up one level and
                    // continue checking from the grandparent. No rotation needed - this is the case
                    // that makes Red-Black cheaper than AVL.
                    z.parent.colour = BLACK;
                    uncle.colour = BLACK;
                    grandparent.colour = RED;
                    z = grandparent;
                } else {
                    if (z == z.parent.right) {
                        // CASE 2 - "inner" grandchild: rotate to convert it into case 3.
                        z = z.parent;
                        rotateLeft(z);
                    }
                    // CASE 3 - "outer" grandchild: recolour and one rotation finishes it.
                    z.parent.colour = BLACK;
                    z.parent.parent.colour = RED;
                    rotateRight(z.parent.parent);
                }
            } else {
                // Mirror image of the three cases.
                Node uncle = grandparent.left;
                if (isRed(uncle)) {
                    z.parent.colour = BLACK;
                    uncle.colour = BLACK;
                    grandparent.colour = RED;
                    z = grandparent;
                } else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rotateRight(z);
                    }
                    z.parent.colour = BLACK;
                    z.parent.parent.colour = RED;
                    rotateLeft(z.parent.parent);
                }
            }
        }
        root.colour = BLACK;      // rule 2 - the root is always black
    }

    public boolean search(int key) {
        Node n = root;
        while (n != null) {
            if (key == n.key) return true;
            n = key < n.key ? n.left : n.right;
        }
        return false;
    }

    private void inorder(Node n, StringBuilder sb) {
        if (n == null) return;
        inorder(n.left, sb);
        sb.append(n.key).append(n.colour == RED ? "(R) " : "(B) ");
        inorder(n.right, sb);
    }

    private int height(Node n) {
        return n == null ? 0 : 1 + Math.max(height(n.left), height(n.right));
    }

    // Verify the invariants actually hold: returns the black-height, or -1 if the tree is invalid.
    private int checkBlackHeight(Node n) {
        if (n == null) return 1;
        if (isRed(n) && (isRed(n.left) || isRed(n.right))) return -1;   // rule 4 violated
        int lh = checkBlackHeight(n.left), rh = checkBlackHeight(n.right);
        if (lh == -1 || rh == -1 || lh != rh) return -1;                // rule 5 violated
        return lh + (n.colour == BLACK ? 1 : 0);
    }

    public static void main(String[] args) {
        RedBlackTree t = new RedBlackTree();

        // Sorted input is the worst case for a plain BST (it degenerates into a linked list).
        for (int i = 1; i <= 10; i++) t.insert(i);

        StringBuilder sb = new StringBuilder();
        t.inorder(t.root, sb);
        System.out.println("inorder      : " + sb.toString().trim());   // sorted -> valid BST
        System.out.println("root         : " + t.root.key + " (black=" + (t.root.colour == BLACK) + ")");
        System.out.println("height       : " + t.height(t.root) + "  (a plain BST would be 10)");
        System.out.println("valid RB tree: " + (t.checkBlackHeight(t.root) != -1));
        System.out.println("search 7     : " + t.search(7));
        System.out.println("search 99    : " + t.search(99));
    }
}

/* --------------------------- THE FIVE INVARIANTS ---------------------------
 * 1. Every node is either RED or BLACK.
 * 2. The root is BLACK.
 * 3. Every leaf (null) counts as BLACK.
 * 4. A RED node's children are both BLACK  (equivalently: no two reds in a row).
 * 5. Every path from a node down to its descendant leaves contains the SAME number of BLACK nodes
 *    (the "black-height").
 *
 * WHY THESE BOUND THE HEIGHT: by rule 5 all paths have equal black-height b, and by rule 4 reds
 * cannot be adjacent, so no path can be more than twice as long as the shortest. Hence
 * height <= 2*log2(n+1) - still O(log n), just with a larger constant than AVL's 1.44*log2(n).
 *
 * ------------------------------ AVL vs RED-BLACK ------------------------------
 *                     AVL                        Red-Black
 *   Balance rule      strict (diff <= 1)         relaxed (2x path ratio)
 *   Height            ~1.44 log n (shorter)      ~2 log n (taller)
 *   Lookups           FASTER                     slightly slower
 *   Insert/delete     more rotations             FEWER rotations, often just recolouring
 *   Used by           read-heavy indexes         java.util.TreeMap, C++ std::map, Linux scheduler
 *
 * Choose AVL for read-dominated workloads, Red-Black for mixed or write-heavy ones.
 * ---------------------------------------------------------------------------- */
