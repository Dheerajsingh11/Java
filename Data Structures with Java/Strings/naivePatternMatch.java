// Problem  : Find all starting indices where a pattern occurs inside a text.
// Approach : NAIVE - try every possible alignment of the pattern against the text and compare
//            character by character.
// Intuition: Slide the pattern one position at a time; at each position, check if it matches. Simple
//            and always correct, but it re-examines characters after a partial mismatch.
// Time     : O(n * m) worst case (n = text length, m = pattern length)   Space: O(1)
// Trade-off: Easiest to understand; fine for short text/pattern. On adversarial inputs (e.g.
//            "aaaa...a" with pattern "aaab") it degrades to n*m. KMP (KMP.java) removes the redundant
//            re-checks for O(n + m).

import java.util.ArrayList;
import java.util.List;

public class naivePatternMatch {

    static List<Integer> search(String text, String pat) {
        List<Integer> hits = new ArrayList<>();
        int n = text.length(), m = pat.length();
        for (int i = 0; i + m <= n; i++) {   // each possible start position in the text
            int j = 0;
            while (j < m && text.charAt(i + j) == pat.charAt(j)) j++; // compare aligned chars
            if (j == m) hits.add(i);          // matched all m characters -> occurrence at i
            // Edge: on a mismatch we simply shift by 1 and recompare (the wasteful part vs KMP).
        }
        return hits;
    }

    public static void main(String[] args) {
        System.out.println(search("AABAACAADAABAABA", "AABA")); // [0, 9, 12]
        System.out.println(search("abcabcabc", "abc"));         // [0, 3, 6]
        System.out.println(search("abcd", "xyz"));              // [] (not found)
    }
}
