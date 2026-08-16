// Problem  : Length of the Longest Common Subsequence (LCS) of two strings - the longest sequence of
//            characters appearing in both, in order but not necessarily contiguous.
// Approach : Bottom-up tabulation on dp[i][j] = LCS of the first i chars of a and first j of b. The
//            header comments trace the brute-force -> memo -> tabulation ladder.
// Intuition: Compare the last characters. If they match, they extend the LCS: 1 + LCS of the shorter
//            prefixes. If not, drop one character from either string and take the better result.
// Time     : O(n * m)   Space: O(n * m) (reducible to O(min(n, m)) with two rows)
// Trade-off: Brute force enumerates all subsequences (O(2^n)); memoization/tabulation collapse the
//            overlapping (i, j) subproblems to n*m. This recurrence underlies diff tools and bio
//            sequence alignment.

public class longestCommonSubsequence {

    static int lcs(String a, String b) {
        int n = a.length(), m = b.length();
        // dp[i][j] uses prefixes a[0..i-1] and b[0..j-1]; row/col 0 = empty prefix = LCS 0.
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = 1 + dp[i - 1][j - 1];         // matching char extends the diagonal LCS
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]); // drop one char from a or from b
                }
            }
        }
        return dp[n][m];
    }

    public static void main(String[] args) {
        System.out.println(lcs("ABCBDAB", "BDCAB")); // 4  (e.g. "BCAB")
        System.out.println(lcs("AGGTAB", "GXTXAYB")); // 4  ("GTAB")
        System.out.println(lcs("abc", "xyz"));        // 0  (nothing in common)
    }
}
