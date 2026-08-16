// Problem  : Decide whether a string of brackets ()[]{} is balanced.
// Approach : EFFICIENT - one pass with a stack: push openers, and on each closer check it matches
//            the most recent unmatched opener (the stack top).
// Intuition: The most recently opened bracket must be the first one closed (LIFO) - exactly what a
//            stack models. A mismatch or an empty stack on a closer means it is unbalanced.
// Time     : O(n) - each character is pushed/popped at most once
// Space    : O(n) - worst case all openers on the stack (e.g. "((((")
// Trade-off: Optimal single pass, versus the Naive O(n^2) repeated-deletion approach. This is the
//            standard interview solution.

import java.util.ArrayDeque;
import java.util.Deque;

public class balancedParenthesesEfficient {

    static boolean isBalanced(String s) {
        Deque<Character> stack = new ArrayDeque<>(); // top = most recent unmatched opener
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);                       // an opener waits for its match
            } else if (c == ')' || c == ']' || c == '}') {
                // A closer with nothing open -> unbalanced (e.g. leading ')').
                if (stack.isEmpty()) return false;
                char open = stack.pop();             // the opener this closer must match
                // Check the pair actually corresponds; reject e.g. '(' closed by ']'.
                if ((c == ')' && open != '(') ||
                    (c == ']' && open != '[') ||
                    (c == '}' && open != '{')) {
                    return false;
                }
            }
            // Any other character (letters, spaces) is ignored.
        }
        // Balanced only if every opener was matched -> stack empty at the end.
        return stack.isEmpty();
    }

    public static void main(String[] args) {
        System.out.println(isBalanced("{[()]}"));   // true
        System.out.println(isBalanced("{[(])}"));   // false - ']' tries to close '(' -> mismatch
        System.out.println(isBalanced("(()"));      // false - stack not empty at end
        System.out.println(isBalanced("a(b[c]d)e")); // true - non-brackets ignored
    }
}
