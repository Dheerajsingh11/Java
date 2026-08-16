// Problem  : For each element, find the next strictly greater element to its RIGHT (-1 if none).
// Approach : EFFICIENT - sweep RIGHT to LEFT keeping a stack of "candidates greater than what we
//            have seen so far", popping everything not greater than the current element.
// Intuition: An element's next-greater can only be someone to its right that is bigger. Anything to
//            the right that is smaller-or-equal can NEVER be the answer for elements further left
//            (the current element blocks it), so we discard those from the stack. This "monotonic
//            stack" keeps only useful candidates, in decreasing order from top.
// Time     : O(n) - each element is pushed once and popped at most once (amortized O(1) per step)
// Space    : O(n) for the stack
// Trade-off: Optimal versus the Naive O(n^2). The monotonic-stack pattern generalizes to stock
//            span, largest rectangle in histogram, daily temperatures, etc.

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class nextGreaterElementEfficient {

    static int[] nextGreater(int[] a) {
        int n = a.length;
        int[] res = new int[n];
        Deque<Integer> stack = new ArrayDeque<>(); // holds VALUES still eligible to be an answer

        // Process from the right so that, when we reach i, the stack already summarizes its right side.
        for (int i = n - 1; i >= 0; i--) {
            // Remove candidates that cannot be the next greater for a[i]: anything <= a[i] is blocked
            // by a[i] for everyone to the left, so it is useless from here on.
            while (!stack.isEmpty() && stack.peek() <= a[i]) {
                stack.pop();
            }
            // Whatever survives on top is the nearest strictly-greater value to the right.
            res[i] = stack.isEmpty() ? -1 : stack.peek();
            // a[i] itself becomes a candidate for elements further to the left.
            stack.push(a[i]);
        }
        return res;
    }

    public static void main(String[] args) {
        int[] a = { 4, 5, 2, 25 };
        System.out.println(Arrays.toString(nextGreater(a))); // [5, 25, 25, -1]
        int[] b = { 13, 7, 6, 12 };
        System.out.println(Arrays.toString(nextGreater(b))); // [-1, 12, 12, -1]
    }
}
