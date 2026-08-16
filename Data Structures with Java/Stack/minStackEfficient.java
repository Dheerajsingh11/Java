// Problem  : Design a stack that returns its MINIMUM in O(1) using O(1) EXTRA space (no second stack).
// Approach : EFFICIENT - store a single "min" variable, and when a new value becomes the min, push
//            an ENCODED marker (2*x - min) instead of x. The encoding lets pop() recover the
//            previous min without any auxiliary structure.
// Intuition: A value pushed as the new minimum is encoded BELOW the true value (2x - oldMin < x when
//            x < oldMin). Seeing an element smaller than the current min on pop signals "this was a
//            min-change marker", and oldMin = 2*min - encoded restores the previous minimum.
// Time     : push/pop/top/getMin all O(1)   Space: O(1) EXTRA (just the min variable)
// Trade-off: Optimal space, but the encoding needs care against integer OVERFLOW (2*x can overflow
//            int for extreme values - use long if inputs are near Integer limits). Trickier to read
//            than the two-stack Medium version; use that unless space is truly critical.

import java.util.ArrayDeque;
import java.util.Deque;

public class minStackEfficient {

    private final Deque<Long> stack = new ArrayDeque<>(); // longs to reduce 2*x overflow risk
    private long min;                                     // current minimum

    void push(int x) {
        if (stack.isEmpty()) {
            stack.push((long) x);
            min = x;
        } else if (x >= min) {
            stack.push((long) x);            // normal push: value stored as-is
        } else {
            // New minimum: store an encoded value that is LOWER than x, then update min.
            stack.push(2L * x - min);        // decode later as: oldMin = 2*min - encoded
            min = x;
        }
    }

    int pop() {
        long enc = stack.pop();
        if (enc >= min) {
            return (int) enc;                // normal element: it equals the real value
        } else {
            // Encoded marker: the real value is the current min; restore the previous min.
            int real = (int) min;
            min = 2 * min - enc;             // undo the encoding to get the earlier minimum
            return real;
        }
    }

    int top() {
        long enc = stack.peek();
        return enc >= min ? (int) enc : (int) min; // encoded top means the real top IS the min
    }

    int getMin() { return (int) min; } // O(1), O(1) extra space

    public static void main(String[] args) {
        minStackEfficient s = new minStackEfficient();
        s.push(5); s.push(3); s.push(7); s.push(2);
        System.out.println("min : " + s.getMin()); // 2
        System.out.println("top : " + s.top());    // 2
        s.pop();                                    // removes 2 (restores min 3)
        System.out.println("min : " + s.getMin()); // 3
        System.out.println("top : " + s.top());    // 7
    }
}
