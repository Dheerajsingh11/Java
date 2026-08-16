// Problem  : Design a stack that also returns its MINIMUM element in O(1) at any time.
// Approach : MEDIUM - keep a second "min stack" that stores the running minimums; push onto it only
//            when a new value is <= the current min, and pop it in lockstep when that min leaves.
// Intuition: We do not need a min stored per element - we only need the sequence of minimums as
//            they CHANGE. The min stack's top is always the current overall minimum.
// Time     : push/pop/top/getMin all O(1)   Space: O(n) worst case, but usually less than Naive
// Trade-off: Less memory than the Naive pair-per-element approach on average (the min stack only
//            grows when the minimum drops), still O(1) everywhere. The Efficient version removes the
//            second stack entirely using an encoding trick (O(1) extra space).

import java.util.ArrayDeque;
import java.util.Deque;

public class minStackMedium {

    private final Deque<Integer> main = new ArrayDeque<>(); // the actual values
    private final Deque<Integer> mins = new ArrayDeque<>(); // history of minimums

    void push(int x) {
        main.push(x);
        // Push onto mins when x ties or beats the current min, so the min-history stays correct even
        // with duplicate minimums (using <= is important so equal mins pop in matching counts).
        if (mins.isEmpty() || x <= mins.peek()) {
            mins.push(x);
        }
    }

    int pop() {
        int v = main.pop();
        // If we just removed the current minimum, retire it from the min stack too.
        if (v == mins.peek()) {
            mins.pop();
        }
        return v;
    }

    int top()    { return main.peek(); }
    int getMin() { return mins.peek(); } // O(1)

    public static void main(String[] args) {
        minStackMedium s = new minStackMedium();
        s.push(5); s.push(3); s.push(3); s.push(7);
        System.out.println("min : " + s.getMin()); // 3
        s.pop();                                    // removes 7, min unchanged
        System.out.println("min : " + s.getMin()); // 3
        s.pop();                                    // removes one 3 -> another 3 still present
        System.out.println("min : " + s.getMin()); // 3  (duplicate min handled by <=)
        s.pop();                                    // removes the other 3
        System.out.println("min : " + s.getMin()); // 5
    }
}
