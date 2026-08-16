// Problem  : Implement a min-heap (priority queue) from scratch on an array.
// Approach : Store a COMPLETE binary tree in an array; for index i, children are 2i+1/2i+2 and the
//            parent is (i-1)/2. Keep the heap property (parent <= children) via sift-up/sift-down.
// Intuition: A complete tree packs perfectly into an array (no gaps), so parent/child links are pure
//            arithmetic - no node objects. The smallest element always sits at the root (index 0).
// Time     : insert O(log n), extractMin O(log n), peek O(1)   Space: O(n)
// Trade-off: Not fully sorted (only the root is guaranteed smallest), but far cheaper than sorting
//            when you repeatedly need the minimum. This is what java.util.PriorityQueue does inside.

import java.util.Arrays;

public class MinHeap {

    private int[] a;
    private int size;

    MinHeap(int capacity) { a = new int[capacity]; size = 0; }

    private int parent(int i) { return (i - 1) / 2; }
    private int left(int i)   { return 2 * i + 1; }
    private int right(int i)  { return 2 * i + 2; }
    private void swap(int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }

    // Insert: place at the end, then "sift up" while it is smaller than its parent.
    void insert(int x) {
        if (size == a.length) a = Arrays.copyOf(a, a.length * 2); // grow if full
        a[size] = x;
        int i = size;
        size++;
        while (i > 0 && a[i] < a[parent(i)]) { // bubble up toward the root
            swap(i, parent(i));
            i = parent(i);
        }
    }

    int peekMin() {
        if (size == 0) throw new RuntimeException("heap empty");
        return a[0]; // root is always the minimum
    }

    // Remove the min: move the last element to the root, then "sift down" to restore order.
    int extractMin() {
        if (size == 0) throw new RuntimeException("heap empty");
        int min = a[0];
        a[0] = a[size - 1]; // last leaf takes the root's place
        size--;
        siftDown(0);
        return min;
    }

    // Push a[i] down, always swapping with its SMALLER child, until both children are >= it.
    private void siftDown(int i) {
        while (true) {
            int smallest = i;
            int l = left(i), r = right(i);
            if (l < size && a[l] < a[smallest]) smallest = l;
            if (r < size && a[r] < a[smallest]) smallest = r;
            if (smallest == i) break;          // heap property satisfied here
            swap(i, smallest);
            i = smallest;                      // continue from the child we sank into
        }
    }

    int size() { return size; }

    public static void main(String[] args) {
        MinHeap h = new MinHeap(10);
        for (int x : new int[]{ 5, 2, 8, 1, 9, 3 }) h.insert(x);

        System.out.println("peekMin: " + h.peekMin()); // 1
        System.out.print("extract order: ");
        while (h.size() > 0) System.out.print(h.extractMin() + " ");
        System.out.println(); // expected: 1 2 3 5 8 9 (ascending -> this is heap-sort order)
    }
}
