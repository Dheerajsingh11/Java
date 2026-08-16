// Problem  : Sort an array containing only 0s, 1s, and 2s in a single pass (the "sort colors" /
//            Dutch National Flag problem).
// Approach : Three pointers (low, mid, high) partition the array into 0s | 1s | unknown | 2s and
//            sweep mid once, swapping elements to their region.
// Intuition: With only three values we do not need comparison sorting. Maintain three zones and,
//            as 'mid' scans, push 0s to the front zone and 2s to the back zone; 1s stay put.
// Time     : O(n) single pass   Space: O(1) in place
// Trade-off: Beats counting sort's two passes and any O(n log n) sort for this special 3-value case.
//            The only subtlety: after swapping a 2 to the back, do NOT advance mid (the swapped-in
//            value is unexamined).

import java.util.Arrays;

public class dutchNationalFlag {

    static void sort012(int[] a) {
        int low = 0;              // everything before low is 0
        int mid = 0;              // low..mid-1 is 1; mid is the current element
        int high = a.length - 1;  // everything after high is 2

        while (mid <= high) {
            if (a[mid] == 0) {
                swap(a, low, mid); // send the 0 to the front zone
                low++;
                mid++;             // the swapped-in element (from low) is already examined (a 1)
            } else if (a[mid] == 1) {
                mid++;             // 1 is already in its correct middle zone
            } else {               // a[mid] == 2
                swap(a, mid, high); // send the 2 to the back zone
                high--;
                // do NOT advance mid: the element swapped in from 'high' is not yet examined
            }
        }
    }

    private static void swap(int[] a, int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }

    public static void main(String[] args) {
        int[] a = { 2, 0, 2, 1, 1, 0, 2, 1, 0 };
        sort012(a);
        System.out.println(Arrays.toString(a)); // [0, 0, 0, 1, 1, 1, 2, 2, 2]
    }
}
