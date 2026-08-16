// Problem  : Always retrieve the smallest (or largest) element next, efficiently.
// Approach : Use java.util.PriorityQueue, a binary-heap implementation of a priority queue.
// Intuition: A heap keeps the extreme element (min by default) at the root, so peeking is O(1) and
//            removing it is O(log n) while it re-heapifies. It does NOT keep everything sorted -
//            only the head is guaranteed ordered.
// Time     : offer O(log n), poll O(log n), peek O(1)   Space: O(n)
// Trade-off: Faster than sorting when you only need the top element repeatedly (e.g. Dijkstra,
//            k-largest, scheduling). Iteration order is NOT sorted - only poll() yields sorted order.

import java.util.Collections;
import java.util.PriorityQueue;

public class PriorityQueueDemo {
    public static void main(String[] args) {
        // ---- Min-heap (default): smallest comes out first ----
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        for (int x : new int[]{ 40, 10, 30, 20 }) minHeap.offer(x);
        System.out.println("min peek : " + minHeap.peek()); // 10 (smallest)
        System.out.print("min poll order: ");
        while (!minHeap.isEmpty()) System.out.print(minHeap.poll() + " ");
        System.out.println(); // expected: 10 20 30 40 (poll yields ascending order)

        // ---- Max-heap: pass a reverse comparator ----
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        for (int x : new int[]{ 40, 10, 30, 20 }) maxHeap.offer(x);
        System.out.println("max peek : " + maxHeap.peek()); // 40 (largest)

        // ---- Classic use: k largest elements using a size-k MIN-heap ----
        // Keep the heap at size k; the smallest of the "top k so far" sits at the root, ready to be
        // kicked out when a bigger number arrives. This is O(n log k), better than sorting for small k.
        int[] data = { 3, 1, 7, 4, 9, 2, 8 };
        int k = 3;
        PriorityQueue<Integer> topK = new PriorityQueue<>(); // min-heap
        for (int x : data) {
            topK.offer(x);
            if (topK.size() > k) topK.poll(); // drop the current smallest, keep the k largest
        }
        System.out.println(k + " largest (heap): " + topK); // contains {7, 8, 9} in heap order
    }
}
