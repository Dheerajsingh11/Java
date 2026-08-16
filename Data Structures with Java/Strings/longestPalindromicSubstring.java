// Problem  : Find the longest contiguous substring that reads the same forwards and backwards.
// Approach : Three tiers. NAIVE checks every substring (O(n^3)). MEDIUM is DP over ranges (O(n^2)
//            time and space). EFFICIENT is EXPAND-AROUND-CENTRE (O(n^2) time, O(1) space).
// Intuition: The efficient version inverts the question. Instead of testing substrings and asking
//            "is this a palindrome?", it starts from each possible CENTRE and grows outward while
//            the characters match. Every palindrome has exactly one centre, so trying all 2n-1
//            centres (n single characters plus n-1 gaps) finds them all - and each expansion stops
//            the instant it fails, rather than re-scanning.
// Time     : naive O(n^3); DP O(n^2); expand O(n^2) but with far better constants and O(1) space
// Space    : DP O(n^2); expand O(1)
// Trade-off: Expand-around-centre is the practical choice - same asymptotic bound as the DP but no
//            table, so it is faster and lighter. Manacher's algorithm achieves true O(n) but is
//            considerably more intricate and rarely worth it outside competitive programming.

public class longestPalindromicSubstring {

    // ---------- NAIVE: test every substring ----------
    static String naive(String s) {
        String best = "";
        for (int i = 0; i < s.length(); i++) {
            for (int j = i; j < s.length(); j++) {
                String sub = s.substring(i, j + 1);
                if (isPalindrome(sub) && sub.length() > best.length()) best = sub;
            }
        }
        return best;
    }
    static boolean isPalindrome(String s) {
        int i = 0, j = s.length() - 1;
        while (i < j) if (s.charAt(i++) != s.charAt(j--)) return false;
        return true;
    }

    // ---------- MEDIUM: DP over ranges ----------
    // dp[i][j] is true when s[i..j] is a palindrome. It depends on the SHORTER range dp[i+1][j-1],
    // so lengths must be filled in increasing order.
    static String dp(String s) {
        int n = s.length();
        if (n == 0) return "";
        boolean[][] dp = new boolean[n][n];
        int start = 0, maxLen = 1;

        for (int i = 0; i < n; i++) dp[i][i] = true;                    // every single char

        for (int len = 2; len <= n; len++) {
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;
                // Ends must match AND the interior must already be a palindrome. For len == 2 the
                // interior is empty, which counts as a palindrome - hence the len == 2 shortcut.
                if (s.charAt(i) == s.charAt(j) && (len == 2 || dp[i + 1][j - 1])) {
                    dp[i][j] = true;
                    if (len > maxLen) { start = i; maxLen = len; }
                }
            }
        }
        return s.substring(start, start + maxLen);
    }

    // ---------- EFFICIENT: expand around every centre ----------
    static String expand(String s) {
        if (s.isEmpty()) return "";
        int start = 0, maxLen = 1;

        for (int i = 0; i < s.length(); i++) {
            // TWO centre kinds are required, which is the detail people miss:
            //   ODD-length  palindromes ("aba")  are centred on a CHARACTER  -> (i, i)
            //   EVEN-length palindromes ("abba") are centred on a GAP        -> (i, i+1)
            int odd  = expandFrom(s, i, i);
            int even = expandFrom(s, i, i + 1);
            int len = Math.max(odd, even);

            if (len > maxLen) {
                maxLen = len;
                // Recover the start index from the centre and the length. The -1 handles the
                // even case, where the centre sits between two characters.
                start = i - (len - 1) / 2;
            }
        }
        return s.substring(start, start + maxLen);
    }

    // Grow outward while the characters match; return the resulting palindrome length.
    static int expandFrom(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        // The loop exits one step PAST the valid range on both sides, so the length is
        // (right - 1) - (left + 1) + 1 = right - left - 1.
        return right - left - 1;
    }

    public static void main(String[] args) {
        String[] tests = { "babad", "cbbd", "forgeeksskeegfor", "a", "ac", "aaaa", "" };
        for (String t : tests) {
            System.out.printf("%-18s naive=%-12s dp=%-12s expand=%s%n",
                    "\"" + t + "\"", "\"" + naive(t) + "\"", "\"" + dp(t) + "\"", "\"" + expand(t) + "\"");
        }
        // "babad" -> "bab" or "aba" (both length 3, either is correct)
        // "cbbd"  -> "bb"          (even-length centre)
        // "forgeeksskeegfor" -> "geeksskeeg"
    }
}

/* -------------------------- WHY TWO CENTRE KINDS --------------------------
 * A palindrome's centre is either a character (odd length) or the gap between two characters
 * (even length). Checking only character centres would find "aba" but MISS "abba" entirely - a
 * subtle bug that passes many test cases. With n characters and n-1 gaps there are 2n-1 centres,
 * so the sweep remains O(n) centres x O(n) expansion = O(n^2).
 *
 * ----------------------- SUBSTRING vs SUBSEQUENCE -------------------------
 * This problem is about a SUBSTRING - contiguous characters. The longest palindromic SUBSEQUENCE
 * (characters in order but not necessarily adjacent) is a different problem, solved with DP as
 * LCS(s, reverse(s)). Confusing the two is a common misstep; see
 * DynamicProgramming/longestCommonSubsequence.java.
 * --------------------------------------------------------------------------- */
