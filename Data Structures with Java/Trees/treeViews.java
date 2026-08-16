// Problem  : Produce the standard "views" of a binary tree - level order, zigzag, left view, right
//            view, top view - plus a level-order representation grouped by depth.
// Approach : All of them are BFS with one small twist each. Processing a whole LEVEL at a time is the
//            shared idea: record the queue size before the loop, then dequeue exactly that many.
// Intuition: A plain BFS gives one flat sequence with no notion of depth. Capturing the queue size at
//            the start of each round turns that flat stream into distinct levels, and once you have
//            levels every view is a trivial selection: first of each level, last of each level,
//            alternate the direction, and so on.
// Time     : O(n) for each view   Space: O(w) where w is the maximum level width
// Trade-off: The level-size trick is worth internalizing because it converts one traversal into a
//            whole family of answers. Left/right views can also be done with DFS by tracking depth,
//            which uses O(h) space instead of O(w) - better for wide, shallow trees.

import java.util.*;

public class treeViews {

    static class Node {
        int value;
        Node left, right;
        Node(int value) { this.value = value; }
    }

    // ---- Level order, grouped by depth ----
    static List<List<Integer>> levelOrder(Node root) {
        List<List<Integer>> out = new ArrayList<>();
        if (root == null) return out;
        Queue<Node> q = new ArrayDeque<>();
        q.offer(root);

        while (!q.isEmpty()) {
            int size = q.size();          // THE KEY LINE: exactly this many nodes form the current level
            List<Integer> level = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                Node n = q.poll();
                level.add(n.value);
                if (n.left != null)  q.offer(n.left);   // children belong to the NEXT level
                if (n.right != null) q.offer(n.right);
            }
            out.add(level);
        }
        return out;
    }

    // ---- Zigzag: alternate direction each level ----
    static List<List<Integer>> zigzag(Node root) {
        List<List<Integer>> out = new ArrayList<>();
        if (root == null) return out;
        Queue<Node> q = new ArrayDeque<>();
        q.offer(root);
        boolean leftToRight = true;

        while (!q.isEmpty()) {
            int size = q.size();
            LinkedList<Integer> level = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                Node n = q.poll();
                // Same traversal every time - only the INSERTION END flips. Reversing afterwards
                // would also work but costs an extra pass.
                if (leftToRight) level.addLast(n.value);
                else             level.addFirst(n.value);
                if (n.left != null)  q.offer(n.left);
                if (n.right != null) q.offer(n.right);
            }
            out.add(level);
            leftToRight = !leftToRight;
        }
        return out;
    }

    // ---- Left view: the first node of every level (what you see standing to the left) ----
    static List<Integer> leftView(Node root) {
        List<Integer> out = new ArrayList<>();
        for (List<Integer> level : levelOrder(root)) out.add(level.get(0));
        return out;
    }

    // ---- Right view: the last node of every level ----
    static List<Integer> rightView(Node root) {
        List<Integer> out = new ArrayList<>();
        for (List<Integer> level : levelOrder(root)) out.add(level.get(level.size() - 1));
        return out;
    }

    // ---- Top view: the first node encountered at each horizontal distance ----
    // Horizontal distance: root = 0, left child = hd - 1, right child = hd + 1. Because BFS visits
    // shallower nodes first, the FIRST node seen at a given hd is the one visible from above.
    static List<Integer> topView(Node root) {
        if (root == null) return List.of();
        Map<Integer, Integer> firstAtDistance = new TreeMap<>();   // TreeMap keeps hd sorted left->right
        Queue<Node> nodes = new ArrayDeque<>();
        Queue<Integer> dists = new ArrayDeque<>();
        nodes.offer(root); dists.offer(0);

        while (!nodes.isEmpty()) {
            Node n = nodes.poll();
            int hd = dists.poll();
            firstAtDistance.putIfAbsent(hd, n.value);   // putIfAbsent = keep the SHALLOWEST node
            if (n.left != null)  { nodes.offer(n.left);  dists.offer(hd - 1); }
            if (n.right != null) { nodes.offer(n.right); dists.offer(hd + 1); }
        }
        return new ArrayList<>(firstAtDistance.values());
    }

    public static void main(String[] args) {
        //            1
        //          /   \
        //         2     3
        //        / \     \
        //       4   5     6
        //          /
        //         7
        Node root = new Node(1);
        root.left = new Node(2);       root.right = new Node(3);
        root.left.left = new Node(4);  root.left.right = new Node(5);
        root.right.right = new Node(6);
        root.left.right.left = new Node(7);

        System.out.println("level order : " + levelOrder(root)); // [[1], [2, 3], [4, 5, 6], [7]]
        System.out.println("zigzag      : " + zigzag(root));     // [[1], [3, 2], [4, 5, 6], [7]]
        System.out.println("left view   : " + leftView(root));   // [1, 2, 4, 7]
        System.out.println("right view  : " + rightView(root));  // [1, 3, 6, 7]
        System.out.println("top view    : " + topView(root));    // [4, 2, 1, 3, 6]
    }
}
