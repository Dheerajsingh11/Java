// Problem  : Find all occurrences of a pattern in a text in linear time.
// Approach : EFFICIENT - Knuth-Morris-Pratt. Precompute an LPS ("longest proper prefix that is also a
//            suffix") array for the pattern, then scan the text without ever moving backward.
// Intuition: When a mismatch happens after matching some prefix, we already KNOW that prefix's
//            characters. The LPS array tells us the longest already-matched prefix we can reuse, so
//            we skip re-comparing those characters instead of restarting.
// Time     : O(n + m) - LPS build O(m), scan O(n)   Space: O(m) for the LPS array
// Trade-off: Optimal linear time versus the naive O(n*m). The only cost is understanding/building the
//            LPS table; the payoff is never re-reading a text character.

import java.util.ArrayList;
import java.util.List;

public class KMP {

    // LPS[i] = length of the longest proper prefix of pat[0..i] that is also a suffix of it.
    static int[] buildLPS(String pat) {
        int m = pat.length();
        int[] lps = new int[m];
        int len = 0;                     // length of the current matching prefix
        for (int i = 1; i < m; i++) {
            while (len > 0 && pat.charAt(i) != pat.charAt(len)) {
                len = lps[len - 1];      // fall back to the next-longest candidate prefix
            }
            if (pat.charAt(i) == pat.charAt(len)) len++; // extend the matching prefix
            lps[i] = len;
        }
        return lps;
    }

    static List<Integer> search(String text, String pat) {
        List<Integer> hits = new ArrayList<>();
        int n = text.length(), m = pat.length();
        if (m == 0) return hits;
        int[] lps = buildLPS(pat);

        int j = 0;                       // number of pattern chars currently matched
        for (int i = 0; i < n; i++) {    // i never goes backward -> linear scan
            while (j > 0 && text.charAt(i) != pat.charAt(j)) {
                j = lps[j - 1];          // reuse the known prefix instead of rescanning the text
            }
            if (text.charAt(i) == pat.charAt(j)) j++;
            if (j == m) {                // full pattern matched
                hits.add(i - m + 1);
                j = lps[j - 1];          // continue searching for overlapping matches
            }
        }
        return hits;
    }

    public static void main(String[] args) {
        System.out.println(java.util.Arrays.toString(buildLPS("AABAA"))); // [0, 1, 0, 1, 2]
        System.out.println(search("AABAACAADAABAABA", "AABA"));           // [0, 9, 12]
        System.out.println(search("aaaaa", "aa"));                        // [0, 1, 2, 3] (overlaps)
    }
}
