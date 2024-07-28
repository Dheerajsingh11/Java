// Rope cutting Problem
// -> Cut a rope in a way that length of every peice after cuts shouble in set [a,b,c] and return maximum number of cuts 
// I/P: n = 5, a = 2, b = 5, c = 1
// O/P: 5 // we make 5 peieces of length 1
// I/P: n = 23, a = 12, b = 9, c = 11
// O/P: 2 // we make 2 peieces of length 11 & 12
public class ropeCutting {
    public static void main(String[] args) {
        int n = 23, a = 12, b = 9, c = 11;
        System.out.println(maxCuts(n, a, b, c)); // calling the function to return max number of cuts
    }

    static int maxCuts(int n, int a, int b, int c) {
        if (n == 0) { // Base case 1
            return 0; // if n is 0, then return 0
        }
        if (n < 0) { // Base case 2
            return -1; // if n goes below 0, then return -1
        }

        // return the result, max of all the three cuts, or -1 if there is no possible
        // combination
        int res = Math.max(Math.max(maxCuts(n - a, a, b, c), maxCuts(n - b, a, b, c)), maxCuts(n - c, a, b, c));
        if (res == -1) {
            return -1;
        }
        return res + 1;
    }
}

// Time Complexity: O(3^n)
