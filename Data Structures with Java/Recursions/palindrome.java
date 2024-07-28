// Function to check if the string is palindrome or not
public class palindrome {
    public static void main(String[] args) {
        String s = "ababa";
        int n = s.length() - 1;
        String s1 = "abab";
        int n1 = s1.length() - 1;
        System.out.println(isPalindrome(s, 0, n)); // Calling function to check if the string is palindrome
        System.out.println(isPalindrome(s1, 0, n1));
    }

    static boolean isPalindrome(String s, int start, int end) {
        if (start >= end) { // Base case
            return true; // if string is of 0 length or 1 length, then return true
        }
        return (s.charAt(start) == s.charAt(end) && isPalindrome(s, start + 1, end - 1)); // recursive call to check
                                                                                          // charAt(start) ==
                                                                                          // charAt(end) and increase
                                                                                          // start by 1 and decrease end
                                                                                          // by 1
    }
}

// T(n) = T(n-2) + THETA(1) because in every recursive call, there is always
// doing theta 1 work
// Time Complexity: THETA(n)
// Auxiliary space: THETA(n)