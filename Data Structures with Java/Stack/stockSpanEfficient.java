// Problem  : Stock span - for each day, count consecutive prior days (incl. today) with price <=
//            today's price.
// Approach : EFFICIENT - keep a stack of INDICES of previous days with strictly higher prices
//            (a monotonic-decreasing-price stack). The span is the gap to the nearest such day.
// Intuition: The span for day i ends right before the closest earlier day with a HIGHER price.
//            Days that are lower-or-equal than a later day can never bound anyone again, so we pop
//            them. The stack thus always holds the "previous greater" boundaries.
// Time     : O(n) - each index is pushed once and popped at most once
// Space    : O(n) for the stack
// Trade-off: Optimal versus Naive O(n^2). Same monotonic-stack idea as next-greater-element.

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class stockSpanEfficient {

    static int[] span(int[] price) {
        int n = price.length;
        int[] res = new int[n];
        Deque<Integer> stack = new ArrayDeque<>(); // stores day INDICES, prices strictly decreasing

        for (int i = 0; i < n; i++) {
            // Pop days whose price is <= today's: they are "covered" by today and no longer bound it.
            while (!stack.isEmpty() && price[stack.peek()] <= price[i]) {
                stack.pop();
            }
            // If nothing higher exists to the left, the streak reaches the very start -> span = i+1.
            // Otherwise it reaches just after the nearest higher day at index stack.peek().
            res[i] = stack.isEmpty() ? (i + 1) : (i - stack.peek());
            stack.push(i);                     // today may bound future days
        }
        return res;
    }

    public static void main(String[] args) {
        int[] price = { 100, 80, 60, 70, 60, 75, 85 };
        System.out.println(Arrays.toString(span(price))); // [1, 1, 1, 2, 1, 4, 6]
    }
}
