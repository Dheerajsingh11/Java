// Problem  : Find the k largest elements of an array.
// Approach : Three tiers. NAIVE sorts descending and takes k (O(n log n)). MEDIUM uses a size-k
//            MIN-heap kept over one pass (O(n log k)). EFFICIENT uses Quickselect to partition around
//            the k-th largest (O(n) average).
// Intuition: You do not need the whole array sorted - only the top k. A size-k min-heap remembers
//            "the k best so far" and evicts its smallest when something bigger arrives. Quickselect
//            goes further: it positions the k-th largest without sorting either side fully.
// Time     : naive O(n log n); medium O(n log k); efficient O(n) average (O(n^2) worst)   Space: O(k)/O(1)
// Trade-off: For small k, the heap (medium) is simplest and streaming-friendly. Quickselect is the
//            fastest on average but mutates the array and has a bad worst case. Sorting is fine when
//            you also want them ordered.

import java.util.Arrays;
import java.util.PriorityQueue;

public class kLargestElements {

    // ---------- NAIVE: sort then slice ----------
    static int[] naive(int[] a, int k) {
        int[] copy = a.clone();
        Arrays.sort(copy);                 // ascending
        int[] res = new int[k];
        for (int i = 0; i < k; i++) res[i] = copy[copy.length - 1 - i]; // take the top k from the end
        return res;
    }

    // ---------- MEDIUM: size-k min-heap ----------
    static int[] medium(int[] a, int k) {
        PriorityQueue<Integer> heap = new PriorityQueue<>(); // min-heap: smallest of the top-k on top
        for (int x : a) {
            heap.offer(x);
            if (heap.size() > k) heap.poll(); // drop the smallest -> only the k largest survive
        }
        int[] res = new int[k];
        for (int i = 0; i < k; i++) res[i] = heap.poll(); // ascending order out of the min-heap
        return res;
    }

    // ---------- EFFICIENT: Quickselect for the k-th largest boundary ----------
    // Partition until the (n-k)-th smallest is in place; everything to its right is the k largest.
    static int[] efficient(int[] a, int k) {
        int[] arr = a.clone();
        int n = arr.length, target = n - k; // index that separates the largest k
        int lo = 0, hi = n - 1;
        while (lo < hi) {
            int p = partition(arr, lo, hi); // Lomuto partition; arr[p] lands in its sorted position
            if (p == target) break;
            else if (p < target) lo = p + 1; // the boundary is to the right
            else hi = p - 1;                 // to the left
        }
        int[] res = Arrays.copyOfRange(arr, n - k, n); // the top-k slice (not sorted among themselves)
        return res;
    }

    private static int partition(int[] arr, int lo, int hi) {
        int pivot = arr[hi], i = lo;
        for (int j = lo; j < hi; j++) {
            if (arr[j] < pivot) { int t = arr[i]; arr[i] = arr[j]; arr[j] = t; i++; }
        }
        int t = arr[i]; arr[i] = arr[hi]; arr[hi] = t;
        return i;
    }

    public static void main(String[] args) {
        int[] a = { 3, 1, 7, 4, 9, 2, 8 };
        int k = 3;
        System.out.println("naive     : " + Arrays.toString(naive(a, k)));     // [9, 8, 7]
        System.out.println("medium    : " + Arrays.toString(medium(a, k)));    // [7, 8, 9]
        System.out.println("efficient : " + Arrays.toString(efficient(a, k))); // {7,8,9} in some order
        // All three return the same SET {7, 8, 9}; only the ordering within the result differs.
    }
}
