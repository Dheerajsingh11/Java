// Problem  : For every window of size k sliding across an array, report the MAXIMUM in that window.
// Approach : Three tiers. NAIVE rescans each window (O(n*k)). MEDIUM keeps a max-heap (O(n log k)).
//            EFFICIENT uses a MONOTONIC DEQUE holding indices in decreasing value order (O(n)).
// Intuition: The key realization: if a smaller element enters the window AFTER a larger one, that
//            smaller element can NEVER be the maximum while the larger one is still in the window -
//            and the larger one leaves later, because it is further left... no: it leaves EARLIER.
//            So the rule is the reverse: any element already in the deque that is SMALLER than the
//            arriving one is useless forever, because the new element is both bigger and stays
//            longer. Discarding those keeps the deque in decreasing order, so its FRONT is always
//            the window's maximum.
// Time     : naive O(n*k); heap O(n log k); deque O(n) - each index is added once and removed once
// Space    : O(k) for the deque
// Trade-off: The deque version is optimal and is the reason `Deque` matters beyond stacks and queues.
//            It needs BOTH ends: the front to expire out-of-window indices, the back to discard
//            dominated ones - which is exactly what a plain stack or queue cannot do.

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.PriorityQueue;

public class slidingWindowMaximum {

    // ---------- NAIVE: recompute the max of every window ----------
    static int[] naive(int[] a, int k) {
        int n = a.length;
        int[] out = new int[n - k + 1];
        for (int i = 0; i + k <= n; i++) {
            int max = a[i];
            for (int j = i; j < i + k; j++) max = Math.max(max, a[j]);
            out[i] = max;
        }
        return out;
    }

    // ---------- MEDIUM: max-heap of {value, index}, discarding stale entries lazily ----------
    static int[] medium(int[] a, int k) {
        int n = a.length;
        int[] out = new int[n - k + 1];
        PriorityQueue<int[]> heap = new PriorityQueue<>((x, y) -> Integer.compare(y[0], x[0]));

        for (int i = 0; i < n; i++) {
            heap.offer(new int[]{ a[i], i });
            if (i >= k - 1) {
                // The heap may hold indices that have already slid out of the window. They are only
                // a problem if one reaches the TOP, so we discard them lazily rather than searching.
                while (heap.peek()[1] <= i - k) heap.poll();
                out[i - k + 1] = heap.peek()[0];
            }
        }
        return out;
    }

    // ---------- EFFICIENT: monotonic deque of INDICES, values decreasing front -> back ----------
    static int[] efficient(int[] a, int k) {
        int n = a.length;
        int[] out = new int[n - k + 1];
        Deque<Integer> dq = new ArrayDeque<>();     // stores INDICES, not values

        for (int i = 0; i < n; i++) {

            // STEP 1 - expire from the FRONT: the index at the front may have slid out of the window.
            if (!dq.isEmpty() && dq.peekFirst() <= i - k) {
                dq.pollFirst();
            }

            // STEP 2 - discard from the BACK: any index whose value is <= a[i] can never be the
            // maximum again. a[i] is larger AND stays in the window longer, so it dominates them
            // permanently. This is what keeps the deque decreasing.
            while (!dq.isEmpty() && a[dq.peekLast()] <= a[i]) {
                dq.pollLast();
            }

            dq.offerLast(i);

            // STEP 3 - once a full window exists, the front is its maximum by construction.
            if (i >= k - 1) {
                out[i - k + 1] = a[dq.peekFirst()];
            }
        }
        return out;
    }

    public static void main(String[] args) {
        int[] a = { 1, 3, -1, -3, 5, 3, 6, 7 };
        int k = 3;
        System.out.println("naive     : " + Arrays.toString(naive(a, k)));
        System.out.println("medium    : " + Arrays.toString(medium(a, k)));
        System.out.println("efficient : " + Arrays.toString(efficient(a, k)));
        // all three expected: [3, 3, 5, 5, 6, 7]

        int[] b = { 9, 8, 7, 6 };      // strictly decreasing: the front never gets dominated
        System.out.println(Arrays.toString(efficient(b, 2)));   // [9, 8, 7]

        int[] c = { 1, 2, 3, 4 };      // strictly increasing: each new element clears the deque
        System.out.println(Arrays.toString(efficient(c, 2)));   // [2, 3, 4]
    }
}

/* --------------------------- WHY AMORTIZED O(n) ---------------------------
 * The inner while loop looks like it could make this quadratic, but each index is added to the deque
 * exactly once and removed at most once. Across the whole run that is at most 2n deque operations,
 * so the total work is linear even though a single step may pop many elements.
 *
 * ------------------------- WHY A DEQUE AND NOT A STACK ---------------------
 * This problem needs removal at BOTH ends for two different reasons:
 *   FRONT - elements expire because the window moved past them (a time constraint)
 *   BACK  - elements are dominated by a newer, larger value (a value constraint)
 * A stack or a queue offers only one end, so neither suffices. This is the canonical example of a
 * problem that genuinely requires a double-ended queue.
 * --------------------------------------------------------------------------- */
