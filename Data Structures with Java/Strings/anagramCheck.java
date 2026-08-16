// Problem  : Decide whether two strings are anagrams (same characters, same counts, any order).
// Approach : Two tiers. NAIVE sorts both and compares (O(n log n)). EFFICIENT counts character
//            frequencies in one pass (O(n)).
// Intuition: Anagrams have identical multisets of characters. Sorting normalizes order; a frequency
//            table captures the same information without sorting - increment for one string,
//            decrement for the other, and expect all zeros.
// Time     : naive O(n log n); efficient O(n)   Space: O(1) for a fixed alphabet (here 26 lowercase)
// Trade-off: Counting is faster and streams in one pass; sorting is shorter to write and handles any
//            character set without a fixed-size table.

import java.util.Arrays;

public class anagramCheck {

    static boolean naive(String a, String b) {
        if (a.length() != b.length()) return false;
        char[] x = a.toCharArray(), y = b.toCharArray();
        Arrays.sort(x); Arrays.sort(y);      // normalize order
        return Arrays.equals(x, y);
    }

    static boolean efficient(String a, String b) {
        if (a.length() != b.length()) return false; // different lengths -> impossible
        int[] freq = new int[26];            // counts for 'a'..'z'
        for (int i = 0; i < a.length(); i++) {
            freq[a.charAt(i) - 'a']++;        // add for string a
            freq[b.charAt(i) - 'a']--;        // remove for string b
        }
        for (int f : freq) if (f != 0) return false; // any nonzero -> mismatch in counts
        return true;
    }

    public static void main(String[] args) {
        System.out.println(efficient("listen", "silent")); // true
        System.out.println(efficient("hello", "world"));   // false
        System.out.println(naive("triangle", "integral")); // true
    }
}
