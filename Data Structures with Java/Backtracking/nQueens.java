// Problem  : Place N queens on an N x N board so none attack another (no shared row, column, or
//            diagonal). Count/print the solutions.
// Approach : Backtracking - place one queen per row; for each row try every column that is safe,
//            recurse to the next row, and UNDO the placement before trying the next column.
// Intuition: Since exactly one queen goes per row, we only choose a column for each row. Trying a
//            column, recursing, then removing it explores the whole tree of possibilities while
//            abandoning ("pruning") any partial board that already conflicts.
// Time     : O(N!) worst case (pruning removes most branches)   Space: O(N) recursion + O(N) marks
// Trade-off: Backtracking is far better than brute-forcing all C(N*N, N) placements because it prunes
//            invalid partial boards early. Constraint sets (columns/diagonals) make "is it safe?" O(1).

public class nQueens {

    static int count = 0;
    // Occupancy marks for O(1) safety checks:
    static boolean[] col;       // is a column used?
    static boolean[] diag;      // "/" diagonals, indexed by row + col
    static boolean[] antiDiag;  // "\" diagonals, indexed by row - col + (N-1)

    static void solve(int row, int n, int[] placement) {
        if (row == n) {                        // all rows filled -> a complete valid arrangement
            count++;
            return;
        }
        for (int c = 0; c < n; c++) {
            int d = row + c, ad = row - c + (n - 1);
            if (col[c] || diag[d] || antiDiag[ad]) continue; // pruned: this square is attacked

            // Choose: place the queen and mark its lines occupied.
            col[c] = diag[d] = antiDiag[ad] = true;
            placement[row] = c;

            solve(row + 1, n, placement);      // Explore: fill the next row

            // Un-choose (BACKTRACK): free the lines so the next column can be tried.
            col[c] = diag[d] = antiDiag[ad] = false;
        }
    }

    public static void main(String[] args) {
        int n = 8;
        col = new boolean[n];
        diag = new boolean[2 * n];
        antiDiag = new boolean[2 * n];
        solve(0, n, new int[n]);
        System.out.println("Solutions for " + n + "-Queens: " + count); // 92 for N=8
    }
}
