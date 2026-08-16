// Problem  : For every position i in a string, compute Z[i] = the length of the longest substring
//            starting at i that is also a PREFIX of the whole string. Then use it for pattern matching.
// Approach : Maintain a "Z-box" [l, r] - the rightmost prefix-match window found so far - and reuse
//            the already-computed Z values inside it instead of re-comparing characters.
// Intuition: If position i lies inside a window that we already know matches the prefix, then the
//            characters at i mirror characters near the START of the string. So Z[i] can be seeded
//            from Z[i - l] for free. Only when that seed reaches the window's edge do we compare
//            new characters - which is why the total comparison count stays linear.
// Time     : O(n) to build Z; O(n + m) for pattern matching   Space: O(n + m)
// Trade-off: Z-algorithm and KMP are both linear-time exact matchers. Z is often considered easier
//            to derive (one array, one clear meaning) while KMP's LPS table is more subtle. Z is
//            also directly useful for other tasks - periodicity, distinct substrings, string
//            comparison - whereas KMP's table is more specialized to matching.

import java.util.Arrays;

public class zAlgorithm {

    static int[] buildZ(String s) {
        int n = s.length();
        int[] z = new int[n];
        z[0] = n;                 // convention: the whole string is trivially its own prefix

        int l = 0, r = 0;         // the current Z-box: s[l..r] matches a prefix of s
        for (int i = 1; i < n; i++) {

            if (i < r) {
                // i is INSIDE the known window, so s[i..] mirrors s[i-l..]. Reuse that value, but
                // never claim more than the window has left (r - i), because beyond r we have no
                // verified information.
                z[i] = Math.min(r - i, z[i - l]);
            }

            // Extend by direct comparison. Thanks to the seed above this loop starts from an
            // already-verified length, so across the whole run r only ever moves FORWARD - giving
            // an amortized O(n) total rather than O(n^2).
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }

            // If this match extends past the old window, it becomes the new rightmost Z-box.
            if (i + z[i] > r) {
                l = i;
                r = i + z[i];
            }
        }
        return z;
    }

    // Pattern matching: build Z over "pattern + separator + text". Any position whose Z value equals
    // the pattern length marks a full occurrence.
    static void search(String text, String pattern) {
        // The separator must NOT appear in either string, or a match could run across the boundary
        // and report a false positive. U+0001 is a control character that never
        // appears in ordinary text, which makes it a safe delimiter.
        // (char) 1 rather than an invisible literal or a \\u escape: Java processes \\uXXXX before
        // tokenizing, so writing it numerically is both clearer and unambiguous.
        final char SEP = (char) 1;
        String combined = pattern + SEP + text;
        int[] z = buildZ(combined);
        int m = pattern.length();

        System.out.print("occurrences of \"" + pattern + "\" : [");
        boolean first = true;
        for (int i = m + 1; i < combined.length(); i++) {
            if (z[i] == m) {
                if (!first) System.out.print(", ");
                System.out.print(i - m - 1);      // convert back to an index in the original text
                first = false;
            }
        }
        System.out.println("]");
    }

    public static void main(String[] args) {
        String s = "aabxaabxcaabxaabxay";
        System.out.println("string : " + s);
        System.out.println("Z      : " + Arrays.toString(buildZ(s)));
        // Z[4] = 4 because "aabx" at index 4 matches the prefix "aabx".

        System.out.println();
        System.out.println("Z of \"aaaaa\" : " + Arrays.toString(buildZ("aaaaa"))); // [5,4,3,2,1]

        System.out.println();
        search("AABAACAADAABAABA", "AABA");   // expected: [0, 9, 12]
        search("aaaaa", "aa");                // expected: [0, 1, 2, 3] - overlaps included
        search("abcdef", "xyz");              // expected: []
    }
}

/* -------------------------- READING THE Z ARRAY --------------------------
 * For s = "aabxaabxcaabxaabxay":
 *   Z[0] = 19  by convention (the whole string)
 *   Z[1] = 1   "a" matches the prefix "a", but "ab" != "aa"
 *   Z[4] = 4   "aabx" at index 4 equals the prefix "aabx"
 *   Z[9] = 8   an 8-character prefix match starting at index 9
 *
 * ------------------------------ BEYOND MATCHING ---------------------------
 * The Z array answers several other questions directly:
 *   - PERIODICITY: s has period p if Z[p] == n - p (used to find the smallest repeating unit).
 *   - LONGEST COMMON PREFIX of s with each of its own suffixes - that is literally the definition.
 *   - DISTINCT SUBSTRINGS, and comparing two strings by concatenating them with a separator.
 *
 * ------------------------------ Z vs KMP ---------------------------------
 * Same O(n + m) guarantee. Z computes "match length with the prefix" at every position; KMP's LPS
 * computes "longest proper prefix that is also a suffix". Z tends to be easier to reason about and
 * more reusable; KMP is more common in textbooks and streams naturally without concatenation.
 * -------------------------------------------------------------------------- */
