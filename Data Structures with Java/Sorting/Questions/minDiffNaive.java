package Questions;

// Program to find the mininum difference between the two elements of an array
public class minDiffNaive {
    public static void main(String[] args) {
        int[] arr = { 1, 3, 6, 10, 15 };
        System.out.println(minDiff(arr)); // Calling function to find minimum difference
    }

    static int minDiff(int[] arr) {
        int res = Integer.MAX_VALUE;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < i; j++) {
                res = Math.min(res, Math.abs(arr[i] - arr[j]));
            }
        }
        return res;
    }
}

// Time complexity: O(n^2)