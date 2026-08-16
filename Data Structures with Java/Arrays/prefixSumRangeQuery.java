// Problem  : Answer many "sum of elements in range [l, r]" queries on a STATIC array quickly.
// Approach : Two tiers. NAIVE sums each query in O(n). EFFICIENT precomputes a prefix-sum array once,
//            then answers each query in O(1) as prefix[r+1] - prefix[l].
// Intuition: prefix[i] = sum of the first i elements. The sum of any range is the difference of two
//            prefix values, because the overlap cancels out.
// Time     : naive O(n) per query; efficient O(n) precompute + O(1) per query   Space: O(n)
// Trade-off: Prefix sums win big when there are MANY queries on unchanging data. If the array
//            changes between queries, use a Fenwick or segment tree instead (see Trees/).

public class prefixSumRangeQuery {

    // ---------- NAIVE ----------
    static int rangeSumNaive(int[] a, int l, int r) {
        int sum = 0;
        for (int i = l; i <= r; i++) sum += a[i]; // re-add every element in the range each time
        return sum;
    }

    // ---------- EFFICIENT ----------
    // prefix has length n+1; prefix[0] = 0; prefix[i] = a[0] + ... + a[i-1].
    static int[] buildPrefix(int[] a) {
        int[] prefix = new int[a.length + 1];
        for (int i = 0; i < a.length; i++) prefix[i + 1] = prefix[i] + a[i];
        return prefix;
    }
    static int rangeSumFast(int[] prefix, int l, int r) {
        return prefix[r + 1] - prefix[l]; // O(1): total up to r minus total before l
    }

    public static void main(String[] args) {
        int[] a = { 2, 4, 6, 8, 10 };
        int[] prefix = buildPrefix(a);

        System.out.println("naive [1..3] : " + rangeSumNaive(a, 1, 3));      // 4+6+8 = 18
        System.out.println("fast  [1..3] : " + rangeSumFast(prefix, 1, 3));  // 18
        System.out.println("fast  [0..4] : " + rangeSumFast(prefix, 0, 4));  // 30
        System.out.println("fast  [2..2] : " + rangeSumFast(prefix, 2, 2));  // 6
    }
}
