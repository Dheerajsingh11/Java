// Problem  : Answer range queries (here: range sum) AND allow point updates, both efficiently, on
//            an array.
// Approach : A segment tree stores aggregate values for array segments in a binary-tree-shaped array;
//            each internal node covers the union of its children's ranges.
// Intuition: Any query range [l, r] can be assembled from O(log n) precomputed segments instead of
//            summing element by element. An update touches only the O(log n) nodes covering that index.
// Time     : build O(n), query O(log n), update O(log n)   Space: O(n) (~4n array)
// Trade-off: Beats a prefix-sum array when the data CHANGES (prefix sums are O(1) query but O(n)
//            update). Beats a Fenwick tree in flexibility (min/max/gcd, range updates) at more code.

public class SegmentTree {

    private final int[] tree; // node i covers a segment; children are 2i+1 and 2i+2
    private final int n;

    SegmentTree(int[] a) {
        n = a.length;
        tree = new int[4 * n]; // 4n is a safe upper bound for the implicit binary tree
        build(a, 0, 0, n - 1);
    }

    // Build node 'idx' which covers array range [lo, hi].
    private void build(int[] a, int idx, int lo, int hi) {
        if (lo == hi) {                       // leaf: covers a single element
            tree[idx] = a[lo];
            return;
        }
        int mid = (lo + hi) / 2;
        build(a, 2 * idx + 1, lo, mid);       // left child covers [lo, mid]
        build(a, 2 * idx + 2, mid + 1, hi);   // right child covers [mid+1, hi]
        tree[idx] = tree[2 * idx + 1] + tree[2 * idx + 2]; // internal node = sum of children
    }

    // Public range-sum query over [ql, qr].
    int query(int ql, int qr) { return query(0, 0, n - 1, ql, qr); }

    private int query(int idx, int lo, int hi, int ql, int qr) {
        if (qr < lo || hi < ql) return 0;     // this segment is completely OUTSIDE the query -> identity
        if (ql <= lo && hi <= qr) return tree[idx]; // completely INSIDE -> use the precomputed value
        int mid = (lo + hi) / 2;              // partial overlap -> split and combine
        return query(2 * idx + 1, lo, mid, ql, qr)
             + query(2 * idx + 2, mid + 1, hi, ql, qr);
    }

    // Set a[pos] = value and refresh every node on the path to the root.
    void update(int pos, int value) { update(0, 0, n - 1, pos, value); }

    private void update(int idx, int lo, int hi, int pos, int value) {
        if (lo == hi) {                       // reached the leaf for 'pos'
            tree[idx] = value;
            return;
        }
        int mid = (lo + hi) / 2;
        if (pos <= mid) update(2 * idx + 1, lo, mid, pos, value);
        else            update(2 * idx + 2, mid + 1, hi, pos, value);
        tree[idx] = tree[2 * idx + 1] + tree[2 * idx + 2]; // recombine on the way up
    }

    public static void main(String[] args) {
        int[] a = { 1, 3, 5, 7, 9, 11 };
        SegmentTree st = new SegmentTree(a);

        System.out.println("sum[1..3] = " + st.query(1, 3)); // 3+5+7 = 15
        System.out.println("sum[0..5] = " + st.query(0, 5)); // 36
        st.update(1, 10);                                     // a[1]: 3 -> 10
        System.out.println("after update, sum[1..3] = " + st.query(1, 3)); // 10+5+7 = 22
    }
}
