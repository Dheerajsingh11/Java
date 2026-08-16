// Problem  : Search for a value in a 2-D matrix whose rows and columns are both sorted.
// Approach : Two tiers for two different matrix guarantees.
//            EFFICIENT-A (fully sorted matrix, each row starts after the previous row ends):
//              treat the matrix as one flat sorted array and binary-search it - O(log(m*n)).
//            EFFICIENT-B (rows and columns sorted independently): start at the TOP-RIGHT corner and
//              walk - the "staircase" search - O(m + n).
// Intuition: For B, the top-right corner is special because it is the LARGEST in its row and the
//            SMALLEST in its column. So one comparison eliminates an entire row or an entire column:
//            too big -> the whole column can go; too small -> the whole row can go. Starting in the
//            middle gives no such certainty, which is why the corner matters.
// Time     : A: O(log(m*n));  B: O(m + n);  naive scan: O(m*n)   Space: O(1)
// Trade-off: Use A when the matrix is fully sorted end-to-end (it is strictly better). Use B when
//            only rows and columns are individually sorted - A would be incorrect there.

public class searchIn2DMatrix {

    // ---------- NAIVE - scan everything, ignoring the sorting ----------
    static boolean naive(int[][] m, int target) {
        for (int[] row : m)
            for (int v : row)
                if (v == target) return true;
        return false;
    }

    // ---------- EFFICIENT-A - fully sorted matrix, binary search on a virtual flat array ----------
    // Requires: every row is sorted AND row[i] ends before row[i+1] begins.
    static boolean binarySearchFlat(int[][] m, int target) {
        if (m.length == 0 || m[0].length == 0) return false;
        int rows = m.length, cols = m[0].length;
        int lo = 0, hi = rows * cols - 1;          // index into the imaginary 1-D array

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;          // overflow-safe midpoint
            // Map the flat index back to 2-D coordinates - the whole trick of this variant.
            int value = m[mid / cols][mid % cols];

            if (value == target)     return true;
            else if (value < target) lo = mid + 1;
            else                     hi = mid - 1;
        }
        return false;
    }

    // ---------- EFFICIENT-B - staircase search from the top-right corner ----------
    // Requires only: each row sorted left->right, each column sorted top->bottom.
    static boolean staircase(int[][] m, int target) {
        if (m.length == 0 || m[0].length == 0) return false;
        int row = 0, col = m[0].length - 1;         // START AT TOP-RIGHT

        while (row < m.length && col >= 0) {
            int value = m[row][col];

            if (value == target) return true;

            // value is the largest in this row: if it is still too big, the target cannot be
            // anywhere in this COLUMN either (everything below is even larger). Drop the column.
            else if (value > target) col--;

            // value is the smallest in this column: if it is too small, nothing in this ROW can
            // match (everything left is even smaller). Drop the row.
            else row++;
        }
        return false;
        // Each step removes one full row or column, so at most m + n steps.
    }

    public static void main(String[] args) {
        // Fully sorted matrix - suits both approaches.
        int[][] full = {
            {  1,  3,  5,  7 },
            { 10, 11, 16, 20 },
            { 23, 30, 34, 60 }
        };
        System.out.println("flat binary, find 16 : " + binarySearchFlat(full, 16));  // true
        System.out.println("flat binary, find 13 : " + binarySearchFlat(full, 13));  // false
        System.out.println("staircase,   find 16 : " + staircase(full, 16));         // true

        // Rows and columns sorted, but rows do NOT continue from one another.
        // Here the flat binary search is INVALID; the staircase still works.
        int[][] rowCol = {
            {  1,  4,  7, 11 },
            {  2,  5,  8, 12 },
            {  3,  6,  9, 16 },
            { 10, 13, 14, 17 }
        };
        System.out.println("staircase, find 5    : " + staircase(rowCol, 5));    // true
        System.out.println("staircase, find 15   : " + staircase(rowCol, 15));   // false
        System.out.println("naive,     find 5    : " + naive(rowCol, 5));        // true (agrees)
        System.out.println("flat binary on rowCol (WRONG method), find 5 : "
                + binarySearchFlat(rowCol, 5));   // false - demonstrates the precondition matters
    }
}
