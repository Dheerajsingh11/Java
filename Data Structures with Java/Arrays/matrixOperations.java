// Problem  : Three classic 2-D array manipulations - spiral traversal, in-place 90-degree rotation,
//            and setting whole rows/columns to zero.
// Approach : Each is solved by a different idea: shrinking BOUNDARIES for the spiral, TRANSPOSE plus
//            REVERSE for the rotation, and using the matrix's own first row/column as MARKERS for
//            set-zeroes.
// Intuition: These problems look like fiddly index bookkeeping, but each has one insight that makes
//            the code short and obviously correct. Finding that insight is the point - brute-force
//            index juggling is where the bugs live.
// Time     : all O(rows * cols) - every cell touched a constant number of times
// Space    : spiral O(1) extra; rotation O(1) in place; setZeroes O(1) using the marker trick
// Trade-off: The naive versions of rotation (allocate a second matrix) and setZeroes (track rows and
//            columns in separate sets) are easier to write but use O(n^2) and O(m+n) extra memory.
//            The versions here achieve O(1) - a genuine improvement, not just cleverness.

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class matrixOperations {

    // ---------------- 1. SPIRAL TRAVERSAL ----------------
    // Maintain four boundaries and peel off one layer at a time, shrinking them as you go.
    static List<Integer> spiral(int[][] m) {
        List<Integer> out = new ArrayList<>();
        if (m.length == 0) return out;

        int top = 0, bottom = m.length - 1, left = 0, right = m[0].length - 1;

        while (top <= bottom && left <= right) {
            for (int c = left; c <= right; c++) out.add(m[top][c]);      // left -> right
            top++;
            for (int r = top; r <= bottom; r++) out.add(m[r][right]);    // top  -> bottom
            right--;

            // These two guards are ESSENTIAL, not defensive padding. After shrinking, a single
            // remaining row (or column) would otherwise be traversed a SECOND time in reverse,
            // duplicating its values. This is the classic spiral bug.
            if (top <= bottom) {
                for (int c = right; c >= left; c--) out.add(m[bottom][c]);   // right -> left
                bottom--;
            }
            if (left <= right) {
                for (int r = bottom; r >= top; r--) out.add(m[r][left]);     // bottom -> top
                left++;
            }
        }
        return out;
    }

    // ---------------- 2. ROTATE 90 DEGREES CLOCKWISE, IN PLACE ----------------
    // THE INSIGHT: rotation = TRANSPOSE (mirror across the main diagonal), then REVERSE each row.
    // Trying to move each element directly to its rotated position requires four-way cyclic swaps
    // and careful bounds; this decomposition needs neither.
    static void rotate90(int[][] m) {
        int n = m.length;

        // Transpose: swap m[i][j] with m[j][i]. Start j at i so each pair is swapped ONCE -
        // looping j from 0 would swap every pair twice and leave the matrix unchanged.
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int t = m[i][j]; m[i][j] = m[j][i]; m[j][i] = t;
            }
        }

        // Reverse each row.
        for (int[] row : m) {
            for (int l = 0, r = n - 1; l < r; l++, r--) {
                int t = row[l]; row[l] = row[r]; row[r] = t;
            }
        }
        // (Counter-clockwise: transpose, then reverse each COLUMN instead.)
    }

    // ---------------- 3. SET ZEROES: if a cell is 0, zero its whole row and column ----------------
    // THE TRAP: zeroing as you scan corrupts the data you are still reading - the new zeros look
    // like original ones and cascade until the whole matrix is zero. So the positions must be
    // RECORDED first and applied second.
    // THE TRICK: use row 0 and column 0 as the notepad, giving O(1) extra space.
    static void setZeroes(int[][] m) {
        int rows = m.length, cols = m[0].length;

        // The first row and column will themselves be used as markers, so remember their own
        // status separately before they get overwritten.
        boolean firstRowZero = false, firstColZero = false;
        for (int c = 0; c < cols; c++) if (m[0][c] == 0) firstRowZero = true;
        for (int r = 0; r < rows; r++) if (m[r][0] == 0) firstColZero = true;

        // PASS 1 - record: a zero at (r, c) sets the markers m[r][0] and m[0][c].
        for (int r = 1; r < rows; r++) {
            for (int c = 1; c < cols; c++) {
                if (m[r][c] == 0) { m[r][0] = 0; m[0][c] = 0; }
            }
        }

        // PASS 2 - apply, for the interior only.
        for (int r = 1; r < rows; r++) {
            for (int c = 1; c < cols; c++) {
                if (m[r][0] == 0 || m[0][c] == 0) m[r][c] = 0;
            }
        }

        // PASS 3 - handle the first row/column LAST, since they were serving as the notepad.
        if (firstRowZero) for (int c = 0; c < cols; c++) m[0][c] = 0;
        if (firstColZero) for (int r = 0; r < rows; r++) m[r][0] = 0;
    }

    static void print(int[][] m) { for (int[] row : m) System.out.println("  " + Arrays.toString(row)); }

    public static void main(String[] args) {
        int[][] a = { {1,2,3}, {4,5,6}, {7,8,9} };
        System.out.println("spiral: " + spiral(a));          // [1, 2, 3, 6, 9, 8, 7, 4, 5]

        // A non-square case, which is where the guard conditions matter.
        int[][] wide = { {1,2,3,4}, {5,6,7,8} };
        System.out.println("spiral: " + spiral(wide));       // [1, 2, 3, 4, 8, 7, 6, 5]

        int[][] b = { {1,2,3}, {4,5,6}, {7,8,9} };
        rotate90(b);
        System.out.println("rotated 90 clockwise:");
        print(b);            // [7,4,1] [8,5,2] [9,6,3]

        int[][] c = { {1,1,1}, {1,0,1}, {1,1,1} };
        setZeroes(c);
        System.out.println("after setZeroes:");
        print(c);            // [1,0,1] [0,0,0] [1,0,1]
    }
}
