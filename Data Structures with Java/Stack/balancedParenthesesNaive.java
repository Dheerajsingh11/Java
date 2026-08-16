// Problem  : Decide whether a string of brackets ()[]{} is balanced (every opener has a matching
//            closer, correctly nested).
// Approach : NAIVE - repeatedly delete adjacent matching pairs "()", "[]", "{}" until none remain;
//            balanced iff the string becomes empty.
// Intuition: A balanced string always contains at least one innermost pair with nothing between
//            its brackets. Removing such pairs is safe; if everything cancels out, it was balanced.
// Time     : O(n^2) - each pass is O(n) and may remove only one pair, so up to n/2 passes
// Space    : O(n) for the working string
// Trade-off: Very easy to understand but slow because of repeated full-string rebuilding. The
//            Efficient version does it in a single O(n) pass with a stack.

public class balancedParenthesesNaive {

    static boolean isBalanced(String s) {
        // Keep removing innermost pairs until the string stops changing.
        String prev;
        do {
            prev = s;
            s = s.replace("()", "")   // each replace scans the whole string (O(n))
                 .replace("[]", "")
                 .replace("{}", "");
        } while (!s.equals(prev));      // stop when a full pass removed nothing

        // If only matched pairs existed, they all cancelled and nothing is left.
        return s.isEmpty();
    }

    public static void main(String[] args) {
        System.out.println(isBalanced("{[()]}"));  // true  - properly nested
        System.out.println(isBalanced("{[(])}"));  // false - crossed nesting, nothing cancels fully
        System.out.println(isBalanced("(()"));     // false - one opener left over
        System.out.println(isBalanced(""));        // true  - empty is trivially balanced
    }
}
