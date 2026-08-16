// Problem  : Fill a 9x9 Sudoku so each row, column, and 3x3 box contains digits 1-9 exactly once.
// Approach : Backtracking - find an empty cell, try digits 1-9 that are currently valid, recurse; if
//            no digit works, undo and report failure to the previous cell.
// Intuition: Sudoku is a constraint-satisfaction problem. We guess a digit, propagate by recursing,
//            and retract the guess the moment it leads to a dead end - systematically exploring only
//            partially-valid boards.
// Time     : exponential worst case, but constraints prune aggressively so real puzzles solve fast
// Space    : O(1) extra (board edited in place) + recursion depth up to 81
// Trade-off: Plain backtracking is enough for standard puzzles; heuristics (choose the most
//            constrained cell first) speed up hard ones. The isValid check keeps each try O(1)-ish.

public class sudokuSolver {

    static boolean solve(char[][] b) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (b[r][c] == '.') {                 // first empty cell
                    for (char d = '1'; d <= '9'; d++) {
                        if (isValid(b, r, c, d)) {
                            b[r][c] = d;              // choose digit d
                            if (solve(b)) return true; // explore the rest of the board
                            b[r][c] = '.';            // un-choose (backtrack): d didn't pan out
                        }
                    }
                    return false;                     // no digit fits here -> earlier guess was wrong
                }
            }
        }
        return true;                                  // no empty cells left -> solved
    }

    // Can digit d go at (r, c) without breaking Sudoku rules right now?
    static boolean isValid(char[][] b, int r, int c, char d) {
        int boxR = (r / 3) * 3, boxC = (c / 3) * 3;   // top-left of this cell's 3x3 box
        for (int i = 0; i < 9; i++) {
            if (b[r][i] == d) return false;           // same digit already in the row
            if (b[i][c] == d) return false;           // ...or the column
            if (b[boxR + i / 3][boxC + i % 3] == d) return false; // ...or the 3x3 box
        }
        return true;
    }

    public static void main(String[] args) {
        char[][] board = {
            "53..7....".toCharArray(), "6..195...".toCharArray(), ".98....6.".toCharArray(),
            "8...6...3".toCharArray(), "4..8.3..1".toCharArray(), "7...2...6".toCharArray(),
            ".6....28.".toCharArray(), "...419..5".toCharArray(), "....8..79".toCharArray()
        };
        if (solve(board)) {
            for (char[] row : board) System.out.println(new String(row));
        }
        // Prints the unique completed grid (first row becomes 534678912).
    }
}
