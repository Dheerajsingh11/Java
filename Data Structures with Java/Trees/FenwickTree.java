// Problem  : Maintain prefix sums with point updates efficiently (same goal as a segment tree for
//            sums, but with far less code).
// Approach : A Fenwick tree (Binary Indexed Tree) stores partial sums indexed so that the lowest set
//            bit of an index tells you which range that slot covers.
// Intuition: Every prefix [1..i] can be split into O(log n) "power-of-two-sized" chunks. The trick
//            i & (-i) isolates the lowest set bit, which is exactly the size of the chunk stored at i,
//            letting us jump across those chunks to sum or update.
// Time     : update O(log n), prefix-sum query O(log n)   Space: O(n)
// Trade-off: Tiny and fast for prefix-sum/point-update, but less flexible than a segment tree
//            (no easy min/max or range-update out of the box). 1-INDEXED internally by convention.

public class FenwickTree {

    private final int[] bit; // 1-indexed; bit[i] covers the range (i - lowbit(i), i]
    private final int n;

    FenwickTree(int size) {
        n = size;
        bit = new int[n + 1]; // index 0 is unused
    }

    // Add 'delta' to position 'i' (1-indexed). Walk UP by adding the lowest set bit each step.
    void update(int i, int delta) {
        for (; i <= n; i += i & (-i)) { // i & (-i) = lowest set bit = size of the chunk at i
            bit[i] += delta;
        }
    }

    // Prefix sum of [1..i]. Walk DOWN by stripping the lowest set bit each step.
    int prefixSum(int i) {
        int sum = 0;
        for (; i > 0; i -= i & (-i)) {  // jump to the start of the previous covered chunk
            sum += bit[i];
        }
        return sum;
    }

    // Range sum [l..r] = prefix(r) - prefix(l-1).
    int rangeSum(int l, int r) {
        return prefixSum(r) - prefixSum(l - 1);
    }

    public static void main(String[] args) {
        int[] a = { 0, 1, 3, 5, 7, 9, 11 }; // a[1..6], index 0 ignored (1-indexed)
        FenwickTree ft = new FenwickTree(6);
        for (int i = 1; i <= 6; i++) ft.update(i, a[i]); // build by inserting each value

        System.out.println("prefix[1..3] = " + ft.prefixSum(3)); // 1+3+5 = 9
        System.out.println("range [2..5] = " + ft.rangeSum(2, 5)); // 3+5+7+9 = 24
        ft.update(2, 7);                                            // a[2] += 7 (3 -> 10)
        System.out.println("after +7 at 2, range[2..5] = " + ft.rangeSum(2, 5)); // 31
    }
}
