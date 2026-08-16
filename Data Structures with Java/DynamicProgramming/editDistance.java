// Problem  : Edit distance (Levenshtein) - minimum single-character INSERT/DELETE/REPLACE operations
//            to turn string a into string b.
// Approach : Tabulation. dp[i][j] = edit distance between the first i chars of a and first j of b.
// Intuition: Look at the last characters. If they match, no cost - carry dp[i-1][j-1]. If not, the
//            best is 1 + the cheapest of: replace (dp[i-1][j-1]), delete from a (dp[i-1][j]), or
//            insert into a (dp[i][j-1]).
// Time     : O(n * m)   Space: O(n * m) (reducible to O(min(n, m)))
// Trade-off: Brute-force recursion branches 3 ways per step (exponential); the DP reuses overlapping
//            prefix subproblems for polynomial time. Powers spell-check, DNA alignment, and diffs.

public class editDistance {

    static int distance(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];

        // Base cases: transforming to/from an empty string costs its length (all inserts or deletes).
        for (int i = 0; i <= n; i++) dp[i][0] = i;   // delete all i chars of a
        for (int j = 0; j <= m; j++) dp[0][j] = j;   // insert all j chars of b

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];     // characters match -> no operation needed
                } else {
                    int replace = dp[i - 1][j - 1];
                    int delete  = dp[i - 1][j];
                    int insert  = dp[i][j - 1];
                    dp[i][j] = 1 + Math.min(replace, Math.min(delete, insert)); // cheapest edit + 1
                }
            }
        }
        return dp[n][m];
    }

    public static void main(String[] args) {
        System.out.println(distance("horse", "ros"));     // 3 (horse->rorse->rose->ros)
        System.out.println(distance("intention", "execution")); // 5
        System.out.println(distance("abc", "abc"));       // 0
    }
}
