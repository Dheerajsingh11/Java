// Problem  : Declare, initialize, and use 1-D, 2-D, and jagged arrays, plus the Arrays utility class.
// Approach : Show each array shape with iteration, and the most useful java.util.Arrays helpers.
// Intuition: An array is a fixed-size, contiguous block with O(1) index access. A 2-D array in Java is
//            really an "array of arrays", which is why rows can have different lengths (jagged).
// Time     : access O(1); fill/sort/copy O(n) / O(n log n)   Space: O(n)
// Trade-off: Arrays are fast and memory-compact but FIXED in size - to grow, use ArrayList (see
//            Java Core/09-Collections). Multi-dim arrays trade a little indirection for flexibility.

import java.util.Arrays;

public class ArrayBasics {
    public static void main(String[] args) {
        // ---- 1-D arrays ----
        int[] a = new int[5];              // all zeros by default
        int[] b = { 10, 20, 30, 40 };      // array literal
        b[1] = 99;                          // update by index (O(1))
        System.out.println("b = " + Arrays.toString(b)); // [10, 99, 30, 40]
        System.out.println("length = " + b.length);      // 4 (a field, not a method!)

        // ---- 2-D array (rectangular) ----
        int[][] grid = { { 1, 2, 3 }, { 4, 5, 6 } };     // 2 rows x 3 cols
        System.out.println("grid[1][2] = " + grid[1][2]); // 6
        for (int[] row : grid) System.out.println(Arrays.toString(row));

        // ---- Jagged array (rows of different lengths) ----
        int[][] jagged = new int[3][];     // rows not yet sized
        jagged[0] = new int[]{ 1 };
        jagged[1] = new int[]{ 1, 2 };
        jagged[2] = new int[]{ 1, 2, 3 };  // each row its own length
        System.out.println("jagged rows: " + jagged[0].length + "," + jagged[1].length + "," + jagged[2].length);

        // ---- Arrays utility methods ----
        int[] c = { 5, 3, 8, 1 };
        Arrays.sort(c);                    // in-place ascending -> [1, 3, 5, 8]
        System.out.println("sorted: " + Arrays.toString(c));
        System.out.println("binarySearch 5: " + Arrays.binarySearch(c, 5)); // index 2 (array must be sorted)
        int[] d = Arrays.copyOf(c, 6);     // copy + grow (extra slots zero-filled)
        System.out.println("copyOf: " + Arrays.toString(d)); // [1, 3, 5, 8, 0, 0]
        int[] e = new int[4];
        Arrays.fill(e, 7);                 // [7, 7, 7, 7]
        System.out.println("fill: " + Arrays.toString(e));
        System.out.println("equals: " + Arrays.equals(new int[]{1,2}, new int[]{1,2})); // true

        // Edge: arrays are fixed size; indexing out of range throws ArrayIndexOutOfBoundsException.
    }
}
