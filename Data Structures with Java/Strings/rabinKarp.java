// Problem  : Find all occurrences of a pattern in a text using hashing.
// Approach : Rabin-Karp - hash the pattern and each length-m window of the text with a ROLLING hash,
//            comparing full strings only when hashes match (to rule out hash collisions).
// Intuition: Comparing whole windows is slow; comparing small hash NUMBERS is fast. A rolling hash
//            updates in O(1) as the window slides (drop the leaving char, add the entering one), so
//            most windows are rejected by a single integer comparison.
// Time     : O(n + m) average, O(n * m) worst (many collisions)   Space: O(1)
// Trade-off: Great when searching for MULTIPLE patterns at once (hash them all) or as a building
//            block (plagiarism/dedup). Needs a good modulus/base to keep collisions rare; verify on
//            a hash hit because equal hashes do not guarantee equal strings.

import java.util.ArrayList;
import java.util.List;

public class rabinKarp {

    static final int BASE = 256;          // treat characters as base-256 digits
    static final long MOD = 1_000_000_007L; // large prime modulus to limit collisions

    static List<Integer> search(String text, String pat) {
        List<Integer> hits = new ArrayList<>();
        int n = text.length(), m = pat.length();
        if (m == 0 || m > n) return hits;

        // Precompute BASE^(m-1) % MOD, used to remove the leading char when rolling.
        long highPow = 1;
        for (int i = 0; i < m - 1; i++) highPow = (highPow * BASE) % MOD;

        // Initial hashes of the pattern and the first window.
        long patHash = 0, winHash = 0;
        for (int i = 0; i < m; i++) {
            patHash = (patHash * BASE + pat.charAt(i)) % MOD;
            winHash = (winHash * BASE + text.charAt(i)) % MOD;
        }

        for (int i = 0; i + m <= n; i++) {
            if (patHash == winHash && text.substring(i, i + m).equals(pat)) {
                hits.add(i);              // verify on hash match to defeat rare collisions
            }
            // Roll the window forward: drop text[i], add text[i+m].
            if (i + m < n) {
                winHash = (winHash - text.charAt(i) * highPow % MOD + MOD) % MOD; // remove leading char
                winHash = (winHash * BASE + text.charAt(i + m)) % MOD;           // append new char
            }
        }
        return hits;
    }

    public static void main(String[] args) {
        System.out.println(search("AABAACAADAABAABA", "AABA")); // [0, 9, 12]
        System.out.println(search("GEEKS FOR GEEKS", "GEEK"));  // [0, 10]
    }
}
