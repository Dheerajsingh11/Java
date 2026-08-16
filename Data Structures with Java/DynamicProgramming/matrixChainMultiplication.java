// Problem  : Given a chain of matrices, find the cheapest PARENTHESIZATION - the order of multiplying
//            them that minimizes the total scalar multiplications.
// Approach : Interval DP. dp[i][j] = minimum cost to multiply matrices i..j. Try every split point k
//            and take the best.
// Intuition: Matrix multiplication is ASSOCIATIVE - (AB)C and A(BC) give the same result - but the
//            COST differs enormously. Multiplying a p x q by a q x r matrix costs p*q*r scalar
//            operations, so the order determines how large the intermediate matrices get. We cannot
//            know the best outermost split in advance, so we try all of them and recurse.
// Time     : O(n^3) - O(n^2) intervals, each trying up to n split points   Space: O(n^2)
// Trade-off: The brute force over all parenthesizations is Catalan-numbered (roughly 4^n), so DP is
//            the difference between impossible and instant. This is the canonical INTERVAL DP: the
//            state is a RANGE rather than a prefix, which is what distinguishes it from knapsack-style
//            problems and makes the iteration order (by increasing length) necessary.

public class matrixChainMultiplication {

    // dims has length n+1: matrix i has dimensions dims[i-1] x dims[i].
    // So {10, 30, 5, 60} means A1 is 10x30, A2 is 30x5, A3 is 5x60.
    static int minCost(int[] dims) {
        int n = dims.length - 1;                  // number of matrices
        int[][] dp = new int[n + 1][n + 1];       // dp[i][j] = min cost for matrices i..j

        // A single matrix needs no multiplication, so dp[i][i] = 0 (already zero-initialized).

        // ITERATE BY CHAIN LENGTH, not by index. dp[i][j] depends on strictly SHORTER intervals, so
        // every sub-result must already be computed - this ordering is what guarantees that.
        for (int len = 2; len <= n; len++) {
            for (int i = 1; i + len - 1 <= n; i++) {
                int j = i + len - 1;
                dp[i][j] = Integer.MAX_VALUE;

                // Try every place to split the chain: (i..k)(k+1..j)
                for (int k = i; k < j; k++) {
                    // cost = solve left half + solve right half + multiply the two RESULTS.
                    // The two results have dimensions dims[i-1] x dims[k] and dims[k] x dims[j],
                    // so combining them costs dims[i-1] * dims[k] * dims[j].
                    int cost = dp[i][k] + dp[k + 1][j] + dims[i - 1] * dims[k] * dims[j];
                    dp[i][j] = Math.min(dp[i][j], cost);
                }
            }
        }
        return dp[1][n];
    }

    public static void main(String[] args) {
        // A1 = 10x30, A2 = 30x5, A3 = 5x60
        int[] dims = { 10, 30, 5, 60 };
        System.out.println("min cost = " + minCost(dims));   // expected: 4500
        // (A1 A2) A3 = 10*30*5 + 10*5*60 = 1500 + 3000 = 4500   <- best
        //  A1 (A2 A3) = 30*5*60 + 10*30*60 = 9000 + 18000 = 27000
        // Same answer, six times the work - purely from the parenthesization.

        int[] d2 = { 40, 20, 30, 10, 30 };
        System.out.println("min cost = " + minCost(d2));     // expected: 26000

        int[] d3 = { 5, 10 };
        System.out.println("single matrix = " + minCost(d3)); // expected: 0
    }
}

/* ----------------------------- WHY IT IS INTERVAL DP -----------------------------
 * Most DP here (knapsack, LCS, coin change) has a state that is a PREFIX or an index pair advancing
 * in one direction. Here the state is a contiguous RANGE [i..j], and the recurrence splits that range
 * at every interior point:
 *
 *     dp[i][j] = min over k in [i, j)  of  dp[i][k] + dp[k+1][j] + cost(i, k, j)
 *
 * That shape - "solve a range by trying every split" - recurs across a family of problems:
 *   - Burst balloons
 *   - Optimal binary search tree
 *   - Palindrome partitioning (minimum cuts)
 *   - Boolean parenthesization
 * Recognizing the shape matters more than memorizing this specific cost formula.
 *
 * The iteration MUST go by increasing length. Looping i and j directly in index order would read
 * dp[i][k] entries that have not been filled yet - a silent correctness bug rather than an error.
 * ---------------------------------------------------------------------------------- */
