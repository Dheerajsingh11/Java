// Problem  : Count how many DISTINCT values an array contains.
// Approach : NAIVE - for each element, look back at everything before it; count it only if no earlier
//            copy exists.
// Intuition: The first occurrence of a value is the one we count. By checking only the elements to
//            the LEFT, every duplicate group is counted exactly once - at its leftmost member.
// Time     : THETA(n^2) - element i compares against i earlier elements, and 0+1+2+...+(n-1) = n(n-1)/2
// Space    : THETA(1) - no extra data structures, which is its one advantage
// Trade-off: Uses no extra memory, but the quadratic scan is far too slow for large arrays. The
//            EFFICIENT version trades O(n) space for O(n) time using a HashSet - the classic
//            time-versus-space swap, and almost always the right call here.

public class countDistinctNaive {
    public static void main(String[] args) {
        int[] arr = { 1, 1, 2, 3, 2, 4, 5 };
        System.out.println(countDistinct(arr));           // expected: 5  ({1,2,3,4,5})
        System.out.println(countDistinct(new int[]{}));    // expected: 0  (empty array)
        System.out.println(countDistinct(new int[]{7,7,7})); // expected: 1
    }

    static int countDistinct(int[] arr) {
        int n = arr.length;
        int res = 0;

        for (int i = 0; i < n; i++) {
            boolean isDistinct = true;       // assume this is the first sighting of arr[i]

            // Look only at elements BEFORE i. That is what makes each duplicate group counted once:
            // the leftmost copy finds nothing earlier, while every later copy finds it.
            for (int j = 0; j < i; j++) {
                if (arr[i] == arr[j]) {
                    isDistinct = false;
                    break;                   // one earlier copy is enough - stop scanning
                }
            }

            if (isDistinct) {
                res++;                       // count only first occurrences
            }
        }
        return res;
    }
}
