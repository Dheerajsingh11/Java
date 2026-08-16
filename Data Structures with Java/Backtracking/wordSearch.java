// Problem  : Given a grid of characters, decide whether a word can be spelled by moving between
//            adjacent cells (up/down/left/right), using each cell at most once per path.
// Approach : Backtracking DFS from every cell that matches the first letter, marking cells as
//            in-use during the descent and UNMARKING them on the way back out.
// Intuition: This is path-finding with a constraint. From a matching cell we try all four
//            directions; if none completes the word we must UNDO the mark so the cell is available
//            to a different path. Marking without unmarking would let one failed attempt
//            permanently block cells that a later, successful path needs.
// Time     : O(rows * cols * 4^L) worst case, L = word length - though pruning cuts this hugely
// Space    : O(L) recursion depth (the in-place marking avoids a separate visited array)
// Trade-off: The classic "mark, recurse, unmark" shape. The in-place trick (temporarily overwriting
//            the character) saves an O(rows*cols) visited array but MUTATES the input during the
//            search - fine here because the original value is always restored.

public class wordSearch {

    static boolean exist(char[][] board, String word) {
        if (word.isEmpty()) return true;
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                // Only start where the first letter matches - the cheapest possible pruning.
                if (board[r][c] == word.charAt(0) && dfs(board, word, r, c, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean dfs(char[][] board, String word, int r, int c, int i) {
        // SUCCESS: every character has been matched.
        if (i == word.length()) return true;

        // PRUNE: off the grid, or this cell does not match the character we need.
        // Note board[r][c] == '#' (our in-use marker) can never equal a real letter, so the
        // "already on this path" case is rejected by the same comparison - no extra check needed.
        if (r < 0 || c < 0 || r >= board.length || c >= board[0].length
                || board[r][c] != word.charAt(i)) {
            return false;
        }

        char saved = board[r][c];
        board[r][c] = '#';          // CHOOSE: mark as in-use for this path

        // EXPLORE all four neighbours; any success short-circuits the rest via ||.
        boolean found = dfs(board, word, r + 1, c, i + 1)
                     || dfs(board, word, r - 1, c, i + 1)
                     || dfs(board, word, r, c + 1, i + 1)
                     || dfs(board, word, r, c - 1, i + 1);

        board[r][c] = saved;        // UN-CHOOSE: restore, so other paths may use this cell

        return found;
    }

    public static void main(String[] args) {
        char[][] board = {
            { 'A', 'B', 'C', 'E' },
            { 'S', 'F', 'C', 'S' },
            { 'A', 'D', 'E', 'E' }
        };

        System.out.println("ABCCED : " + exist(board, "ABCCED"));  // true
        System.out.println("SEE    : " + exist(board, "SEE"));     // true
        System.out.println("ABCB   : " + exist(board, "ABCB"));    // false - would reuse the 'B'
        System.out.println("ASAD   : " + exist(board, "ASAD"));    // true  (down, down, right)
        System.out.println("XYZ    : " + exist(board, "XYZ"));     // false

        // Confirm the board was fully restored - proof that every mark was undone.
        System.out.print("board intact: ");
        for (char[] row : board) System.out.print(new String(row) + " ");
        System.out.println();      // expected: ABCE SFCS ADEE
    }
}

/* --------------------------- WHY "ABCB" MUST FAIL ---------------------------
 * The path A(0,0) -> B(0,1) -> C(0,2) would then need another 'B', and the only 'B' is the one
 * already used at (0,1). Because that cell is marked '#' for the current path, the comparison
 * board[r][c] != word.charAt(i) rejects it. Remove the marking and the search would happily walk
 * back onto the same cell, wrongly reporting true - this is precisely the constraint backtracking
 * exists to enforce.
 *
 * ------------------------------ THE UNMARK MATTERS ---------------------------
 * If the restore line were omitted, a failed exploration would leave '#' behind permanently. A
 * later start position needing that cell would then fail, producing false negatives that depend on
 * search order - one of the most confusing bug classes in backtracking, because the code "works"
 * on many inputs.
 *
 * ---------------------------------- VARIANTS ---------------------------------
 *   - Word Search II (many words at once): build a TRIE of the words and walk the grid ONCE against
 *     it, instead of re-running this per word. See Trees/Trie.java.
 *   - Allow diagonal moves: extend to eight directions.
 *   - Count all paths instead of existence: drop the short-circuit and sum the results.
 * ----------------------------------------------------------------------------- */
