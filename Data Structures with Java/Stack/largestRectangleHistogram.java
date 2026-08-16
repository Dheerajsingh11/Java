// Problem  : Given bar heights in a histogram, find the largest rectangle that fits entirely inside.
// Approach : Two tiers. NAIVE expands from each bar to find how far it can extend (O(n^2)).
//            EFFICIENT uses a MONOTONIC STACK to find, for every bar, the nearest smaller bar on each
//            side in a single pass (O(n)).
// Intuition: Every maximal rectangle has a height equal to some bar's height - the SHORTEST bar it
//            spans. So for each bar, ask "how far left and right can I extend while staying at least
//            this tall?" The boundaries are exactly the nearest strictly-smaller bar on each side.
//            A stack kept in increasing height finds both boundaries in one sweep: when a smaller bar
//            arrives it is the right boundary for everything taller being popped, and whatever
//            remains beneath is the left boundary.
// Time     : naive O(n^2); efficient O(n) - each bar is pushed once and popped once   Space: O(n)
// Trade-off: This is the hardest of the monotonic-stack problems, and the payoff is the general
//            technique - the same shape solves next-greater-element, stock span, trapping rain water,
//            and maximal rectangle in a binary matrix (by running this once per row).

import java.util.ArrayDeque;
import java.util.Deque;

public class largestRectangleHistogram {

    // ---------- NAIVE: for each bar, expand outward while bars stay at least as tall ----------
    static int naive(int[] h) {
        int best = 0;
        for (int i = 0; i < h.length; i++) {
            int left = i, right = i;
            while (left  - 1 >= 0        && h[left  - 1] >= h[i]) left--;
            while (right + 1 < h.length  && h[right + 1] >= h[i]) right++;
            best = Math.max(best, h[i] * (right - left + 1));
        }
        return best;
    }

    // ---------- EFFICIENT: monotonic increasing stack of INDICES ----------
    static int efficient(int[] h) {
        Deque<Integer> stack = new ArrayDeque<>();   // indices, with strictly increasing heights
        int best = 0;
        int n = h.length;

        // i == n acts as a sentinel bar of height 0, which forces every remaining bar to be popped
        // and measured. Without it, bars left on the stack at the end would never be evaluated.
        for (int i = 0; i <= n; i++) {
            int current = (i == n) ? 0 : h[i];

            // The arriving bar is shorter, so it is the RIGHT boundary for every taller bar on the
            // stack. Pop and finalize each of them.
            while (!stack.isEmpty() && h[stack.peek()] >= current) {
                int height = h[stack.pop()];

                // The LEFT boundary is whatever is now on top of the stack - the nearest bar to the
                // left that is strictly shorter. If the stack is empty, this bar extends all the way
                // to index 0, so the width is simply i.
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;

                best = Math.max(best, height * width);
            }
            stack.push(i);
        }
        return best;
    }

    public static void main(String[] args) {
        int[] h = { 2, 1, 5, 6, 2, 3 };
        System.out.println("naive     : " + naive(h));       // 10  (bars 5 and 6, height 5, width 2)
        System.out.println("efficient : " + efficient(h));   // 10

        int[] h2 = { 2, 4 };
        System.out.println(naive(h2) + " / " + efficient(h2));      // 4 / 4

        int[] flat = { 3, 3, 3, 3 };
        System.out.println(naive(flat) + " / " + efficient(flat));  // 12 / 12 (whole histogram)

        int[] inc = { 1, 2, 3, 4, 5 };
        System.out.println(naive(inc) + " / " + efficient(inc));    // 9 / 9  (heights 3,4,5 -> 3*3)
    }
}

/* ------------------------------- WHY THE WIDTH FORMULA -------------------------------
 * When bar `height` is popped at index i:
 *   - RIGHT boundary: i, the first bar to the right that is shorter (that is why we are popping).
 *   - LEFT boundary : stack.peek(), the nearest bar to the left that is shorter (everything between
 *                     it and i was already popped, so all of it was >= height).
 * The rectangle therefore spans the OPEN interval between them:
 *      width = i - stack.peek() - 1
 * and when the stack is empty there is no shorter bar to the left at all, so it starts at index 0:
 *      width = i
 *
 * ------------------------------------ APPLICATIONS -----------------------------------
 * Maximal rectangle of 1s in a binary matrix (run this per row, treating consecutive 1s as heights);
 * skyline problems; and layout/packing questions in graphics.
 * ------------------------------------------------------------------------------------- */
