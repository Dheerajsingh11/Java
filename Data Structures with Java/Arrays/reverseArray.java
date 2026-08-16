// Problem  : Reverse the elements of an array in place.
// Approach : TWO POINTERS - one at each end, swap the pair, then move both inward until they meet.
// Intuition: Reversing means the element at position i must end up at position n-1-i. Rather than
//            building a second array, we pair up those two positions and swap them directly. Each
//            swap places TWO elements correctly at once, so only n/2 swaps are needed.
// Time     : THETA(n) - n/2 swaps, each O(1), which is linear
// Space    : THETA(1) - only a temp variable; the array is modified in place
// Trade-off: In-place beats the obvious "copy into a new array backwards" (O(n) extra space). The
//            two-pointer technique here is the same pattern used in palindrome checks, trapping rain
//            water, and partitioning - one of the most reusable array idioms.

public class reverseArray {

    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        reverseArr(arr);
        System.out.println(java.util.Arrays.toString(arr)); // expected: [10, 9, 8, 7, 6, 5, 4, 3, 2, 1]

        int odd[] = { 1, 2, 3 };
        reverseArr(odd);
        System.out.println(java.util.Arrays.toString(odd)); // expected: [3, 2, 1] (middle stays put)
    }

    static void reverseArr(int[] arr) {
        int low = 0, high = arr.length - 1;   // pointers start at the two ends

        // Condition is low < high (NOT <=). When they are equal we are on the single middle element
        // of an odd-length array, and swapping it with itself would be wasted work.
        while (low < high) {
            // Classic three-step swap via a temporary.
            int temp = arr[low];
            arr[low] = arr[high];
            arr[high] = temp;

            low++;                             // converge from both sides
            high--;
        }
        // Edge cases: an empty array (high = -1) and a single element (low == high) both skip the
        // loop entirely - correct with no special-case code.
    }
}
