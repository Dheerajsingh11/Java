// Problem  : Sort an array using Shell sort - an optimization of insertion sort.
// Approach : Do insertion sort on elements spaced a "gap" apart, shrinking the gap to 1. Early large
//            gaps move far-apart elements into rough order cheaply, so the final gap-1 pass has
//            little work left.
// Intuition: Plain insertion sort only swaps ADJACENT elements, so a small value far to the right
//            takes many steps to reach the front. Comparing gap-apart elements lets values leap
//            toward their place in big strides first.
// Time     : depends on the gap sequence; ~O(n^1.25) to O(n^2)   Space: O(1) in place
// Trade-off: Much faster than insertion sort on medium arrays, in-place, and simple - but not stable
//            and beaten by O(n log n) sorts on large inputs. The gap sequence choice matters a lot.

import java.util.Arrays;

public class shellSort {

    static void sort(int[] a) {
        int n = a.length;
        // Start with a large gap (half the array) and halve it each round down to 1.
        for (int gap = n / 2; gap > 0; gap /= 2) {
            // Gapped insertion sort: for each element, shift its gap-apart predecessors that are larger.
            for (int i = gap; i < n; i++) {
                int temp = a[i];
                int j = i;
                while (j >= gap && a[j - gap] > temp) {
                    a[j] = a[j - gap]; // slide the larger gap-apart element up
                    j -= gap;
                }
                a[j] = temp;           // drop temp into its gapped position
            }
            // When gap == 1 this is ordinary insertion sort, but on an already nearly-sorted array.
        }
    }

    public static void main(String[] args) {
        int[] a = { 9, 8, 3, 7, 5, 6, 4, 1, 2 };
        sort(a);
        System.out.println(Arrays.toString(a)); // [1, 2, 3, 4, 5, 6, 7, 8, 9]
    }
}
