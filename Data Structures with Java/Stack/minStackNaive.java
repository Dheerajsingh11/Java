// Problem  : Design a stack that also returns its MINIMUM element in O(1) at any time.
// Approach : NAIVE - store, with EACH pushed value, the minimum of the stack at that moment (pairs).
// Intuition: If every element remembers "the min of everything at or below me", then the top's
//            remembered min is the current overall min - readable in O(1).
// Time     : push/pop/top/getMin all O(1)   Space: O(n) EXTRA (a second number per element)
// Trade-off: Dead simple and O(1) for every operation, but doubles the storage (a min alongside
//            every value). The Medium version shares one auxiliary stack; the Efficient version
//            uses O(1) extra space via an encoding trick.

import java.util.ArrayDeque;
import java.util.Deque;

public class minStackNaive {

    // Each stack entry is a pair: the value, and the running minimum up to this point.
    private final Deque<int[]> stack = new ArrayDeque<>(); // int[]{ value, minSoFar }

    void push(int x) {
        int newMin = stack.isEmpty() ? x : Math.min(x, stack.peek()[1]);
        stack.push(new int[]{ x, newMin }); // remember the min alongside the value
    }

    int pop()    { return stack.pop()[0]; }
    int top()    { return stack.peek()[0]; }
    int getMin() { return stack.peek()[1]; } // O(1): the top already knows the min below it

    public static void main(String[] args) {
        minStackNaive s = new minStackNaive();
        s.push(5); s.push(3); s.push(7); s.push(2);
        System.out.println("min : " + s.getMin()); // 2
        s.pop();                                    // removes 2
        System.out.println("min : " + s.getMin()); // 3
        System.out.println("top : " + s.top());    // 7
    }
}
