// Problem  : Report the running MEDIAN after each new number arrives in a stream.
// Approach : Two tiers. NAIVE keeps a sorted list and inserts each element (O(n) per insert).
//            EFFICIENT balances two heaps - a max-heap for the lower half and a min-heap for the
//            upper half - so the median is always at the heap tops.
// Intuition: The median only needs the MIDDLE element(s). Split the data at the middle: the largest
//            of the low half and the smallest of the high half straddle the median. Heaps give those
//            extremes in O(1), and rebalancing keeps the split even.
// Time     : naive O(n) per element; efficient O(log n) per element   Space: O(n)
// Trade-off: The two-heap method is the standard streaming solution - O(log n) inserts, O(1) median.
//            The sorted-list version is simpler but too slow for large/continuous streams.

import java.util.Collections;
import java.util.PriorityQueue;

public class medianOfStream {

    // lower half: MAX-heap (its top is the biggest of the small numbers)
    private final PriorityQueue<Integer> low = new PriorityQueue<>(Collections.reverseOrder());
    // upper half: MIN-heap (its top is the smallest of the big numbers)
    private final PriorityQueue<Integer> high = new PriorityQueue<>();

    void add(int x) {
        // Step 1: place x. If it is <= current low-max it belongs to the lower half, else the upper.
        if (low.isEmpty() || x <= low.peek()) low.offer(x);
        else high.offer(x);

        // Step 2: rebalance so the two heaps differ in size by at most 1 (low may hold the extra).
        if (low.size() > high.size() + 1) high.offer(low.poll());
        else if (high.size() > low.size()) low.offer(high.poll());
    }

    double median() {
        // Odd count -> the low heap holds the extra element, so its top is the median.
        if (low.size() > high.size()) return low.peek();
        // Even count -> average the two middle elements (the two heap tops).
        return (low.peek() + high.peek()) / 2.0;
    }

    public static void main(String[] args) {
        medianOfStream m = new medianOfStream();
        int[] stream = { 5, 15, 1, 3 };
        for (int x : stream) {
            m.add(x);
            System.out.println("after " + x + " -> median " + m.median());
        }
        // expected:
        // after 5  -> median 5.0
        // after 15 -> median 10.0   (avg of 5 and 15)
        // after 1  -> median 5.0    (sorted 1,5,15)
        // after 3  -> median 4.0    (sorted 1,3,5,15 -> avg of 3 and 5)
    }
}
