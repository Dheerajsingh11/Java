// Problem  : Check whether a string reads the same forwards and backwards, using recursion.
// Approach : Compare the outermost pair of characters, then recurse inward on the substring between
//            them. Base case: the pointers meet or cross.
// Intuition: A string is a palindrome exactly when its first and last characters match AND the part
//            in between is itself a palindrome. That "AND the rest" phrasing is a recurrence, so it
//            maps straight onto recursion with two pointers moving toward each other.
// Time     : THETA(n) - each call does O(1) work and removes TWO characters, so T(n) = T(n-2) + O(1)
//            which unrolls to n/2 calls -> linear
// Space    : THETA(n) - n/2 stack frames (an iterative two-pointer loop would be O(1))
// Trade-off: Short-circuit evaluation of && matters here: on the first mismatch the recursive call is
//            never made, so a non-palindrome can exit immediately instead of scanning everything.

public class palindrome {
    public static void main(String[] args) {
        String s = "ababa";
        String s1 = "abab";
        System.out.println(isPalindrome(s, 0, s.length() - 1));   // expected: true
        System.out.println(isPalindrome(s1, 0, s1.length() - 1)); // expected: false
        System.out.println(isPalindrome("", 0, -1));              // expected: true (empty is a palindrome)
        System.out.println(isPalindrome("x", 0, 0));              // expected: true (single char)
    }

    static boolean isPalindrome(String s, int start, int end) {
        // BASE CASE: pointers met (odd length, middle char - always fine) or crossed (even length,
        // everything already matched). Zero- and one-character strings land here immediately.
        if (start >= end) {
            return true;
        }

        // Compare the current outer pair, then shrink the window inward by one on each side.
        // The && SHORT-CIRCUITS: if the characters differ, the recursive call is skipped entirely
        // and false propagates straight back up - no wasted work.
        return s.charAt(start) == s.charAt(end)
            && isPalindrome(s, start + 1, end - 1);
    }
    // Iterative alternative (same O(n) time, but O(1) space - preferred in production):
    //   while (start < end) { if (s.charAt(start++) != s.charAt(end--)) return false; }
    //   return true;
}
