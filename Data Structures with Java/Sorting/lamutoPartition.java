// Problem  : Partition an array in place around a pivot, returning the pivot's final index.
// Approach : LOMUTO scheme - pivot is the LAST element; a boundary index 'i' marks the end of the
//            "smaller than pivot" region, and every smaller element found is swapped just past it.
// Intuition: One pointer scans, one marks a boundary. The invariant is: everything in arr[l..i] is
//            < pivot, and everything in arr[i+1..j-1] is >= pivot. At the end the pivot is swapped
//            into position i+1, which is then its FINAL sorted position - that is why quicksort can
//            recurse on the two sides and never revisit it.
// Time     : THETA(n) - a single pass over the range
// Space    : O(1) - fully in place, the key advantage over partArray.java
// Trade-off: Simpler and more intuitive than Hoare's scheme, and it returns the pivot's exact final
//            index. But it performs MORE SWAPS than Hoare and degrades badly on arrays with many
//            duplicates (all-equal input gives worst-case O(n^2) quicksort). Hoare is faster on
//            average; Lomuto is easier to teach and to reason about.

public class lamutoPartition {
    public static void main(String[] args) {
        int arr[] = { 3, 8, 6, 12, 10, 7 };
        int p = lamPart(arr, 0, arr.length - 1);
        System.out.println("pivot final index = " + p);        // expected: 2 (value 7)
        System.out.println(java.util.Arrays.toString(arr));     // expected: [3, 6, 7, 12, 10, 8]
        // Everything left of index 2 is < 7; everything right is >= 7.
    }

    static int lamPart(int arr[], int l, int h) {
        int pivot = arr[h];   // Lomuto always uses the LAST element as the pivot
        int i = l - 1;        // boundary of the "< pivot" region; starts empty (before l)

        // Scan every element except the pivot itself.
        for (int j = l; j <= h - 1; j++) {
            if (arr[j] < pivot) {
                i++;                          // grow the smaller-region by one slot...
                int temp = arr[i];            // ...and swap this small element into it
                arr[i] = arr[j];
                arr[j] = temp;
            }
            // If arr[j] >= pivot we do nothing - j simply moves on, leaving it in the larger region.
        }

        // Finally place the pivot immediately after the smaller region. Everything left of it is
        // smaller, everything right is >= - so this index is the pivot's FINAL sorted position.
        int temp = arr[i + 1];
        arr[i + 1] = arr[h];
        arr[h] = temp;

        return (i + 1);
    }
    // Duplicate weakness: using strict '<' means elements EQUAL to the pivot all land in the right
    // partition. With an all-equal array every partition is maximally unbalanced -> O(n^2).
}
