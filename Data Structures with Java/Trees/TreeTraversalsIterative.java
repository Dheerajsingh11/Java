// Problem  : Do preorder, inorder, and postorder DFS traversals WITHOUT recursion.
// Approach : Use an explicit stack to simulate the call stack that recursion would have used.
// Intuition: Recursion is just a stack of "where to resume". By pushing nodes ourselves we control
//            that stack directly, avoiding deep recursion (which can overflow on a skewed tree).
// Time     : O(n) each   Space: O(h) stack (h = height), same asymptotics as recursion
// Trade-off: More code and less obvious than recursion, but safe for very deep trees and sometimes
//            faster (no call overhead). Postorder iterative is the trickiest - shown via reversal.

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.ArrayList;
import java.util.List;

public class TreeTraversalsIterative {

    static class Node {
        int value;
        Node left, right;
        Node(int value) { this.value = value; }
    }

    // Preorder (NODE,left,right): push right BEFORE left so left is processed first (LIFO).
    static List<Integer> preorder(Node root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Node n = stack.pop();
            out.add(n.value);                       // visit on pop
            if (n.right != null) stack.push(n.right); // right pushed first...
            if (n.left != null) stack.push(n.left);   // ...so left pops first
        }
        return out;
    }

    // Inorder (left,NODE,right): go as far left as possible, then visit and turn right.
    static List<Integer> inorder(Node root) {
        List<Integer> out = new ArrayList<>();
        Deque<Node> stack = new ArrayDeque<>();
        Node cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {                   // push the whole left spine
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();                      // leftmost unvisited node
            out.add(cur.value);                     // visit it
            cur = cur.right;                        // then explore its right subtree
        }
        return out;
    }

    // Postorder (left,right,NODE): easiest trick = do a "NODE,right,left" preorder-like pass, then
    // REVERSE it. Reversing (node,right,left) yields (left,right,node) = postorder.
    static List<Integer> postorder(Node root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Node n = stack.pop();
            out.add(n.value);
            if (n.left != null) stack.push(n.left);   // push left first...
            if (n.right != null) stack.push(n.right); // ...so right comes out first -> node,right,left
        }
        Collections.reverse(out);                     // reverse into left,right,node
        return out;
    }

    public static void main(String[] args) {
        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);
        root.left.left = new Node(4);
        root.left.right = new Node(5);

        System.out.println("preorder  : " + preorder(root));  // [1, 2, 4, 5, 3]
        System.out.println("inorder   : " + inorder(root));   // [4, 2, 5, 1, 3]
        System.out.println("postorder : " + postorder(root)); // [4, 5, 2, 3, 1]
    }
}
