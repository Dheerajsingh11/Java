// Problem  : Turn an arbitrary array into a heap. Compare inserting one-by-one vs bottom-up heapify.
// Approach : NAIVE inserts each element (each an O(log n) sift-up) for O(n log n). EFFICIENT sifts
//            DOWN from the last internal node up to the root, which is a surprising O(n).
// Intuition: Most nodes in a heap are near the BOTTOM, where sift-down does little work. Summing the
//            work by level gives a convergent series that totals O(n), beating repeated insertion.
// Time     : naive O(n log n); efficient O(n)   Space: O(1) (in place)
// Trade-off: When you already have all the data up front, bottom-up heapify (the efficient version)
//            is strictly better. Repeated insertion only wins when elements arrive one at a time.

import java.util.Arrays;

public class buildHeap {

    // ---------- EFFICIENT: bottom-up heapify, O(n) ----------
    // Start at the last node that HAS a child (n/2 - 1) and sift each down; leaves are already heaps.
    static void buildMinHeapEfficient(int[] a) {
        int n = a.length;
        for (int i = n / 2 - 1; i >= 0; i--) { // process internal nodes from bottom to top
            siftDown(a, i, n);
        }
    }

    private static void siftDown(int[] a, int i, int n) {
        while (true) {
            int smallest = i, l = 2 * i + 1, r = 2 * i + 2;
            if (l < n && a[l] < a[smallest]) smallest = l;
            if (r < n && a[r] < a[smallest]) smallest = r;
            if (smallest == i) break;
            int t = a[i]; a[i] = a[smallest]; a[smallest] = t;
            i = smallest;
        }
    }

    // ---------- NAIVE: repeated sift-up, O(n log n) ----------
    // Treat a[0..i] as a growing heap; sift each new element up into place.
    static void buildMinHeapNaive(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int j = i;
            while (j > 0 && a[j] < a[(j - 1) / 2]) { // bubble up
                int t = a[j]; a[j] = a[(j - 1) / 2]; a[(j - 1) / 2] = t;
                j = (j - 1) / 2;
            }
        }
    }

    // Verify the min-heap property: every parent <= its children.
    static boolean isMinHeap(int[] a) {
        for (int i = 0; i < a.length; i++) {
            int l = 2 * i + 1, r = 2 * i + 2;
            if (l < a.length && a[i] > a[l]) return false;
            if (r < a.length && a[i] > a[r]) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        int[] a = { 9, 4, 7, 1, 8, 2, 6, 3 };
        int[] b = a.clone();

        buildMinHeapEfficient(a);
        System.out.println("efficient heap: " + Arrays.toString(a) + " valid=" + isMinHeap(a)); // valid=true

        buildMinHeapNaive(b);
        System.out.println("naive heap    : " + Arrays.toString(b) + " valid=" + isMinHeap(b)); // valid=true
        // Both are valid min-heaps (the exact array layout may differ); the efficient build is O(n).
    }
}
