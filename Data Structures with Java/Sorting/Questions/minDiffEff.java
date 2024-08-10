package Questions;

import java.util.Arrays;

// Efficient Solution to find the minimum difference between two elements in an Array
public class minDiffEff {
    public static void main(String[] args) {
        int arr[] = { 1, 5, 3, 19, 18, 25 };
        System.out.println("Minimum difference is: " + minDiff(arr));
    }

    static int minDiff(int arr[]) {
        int n = arr.length;
        Arrays.sort(arr); // Sorting the array in ascending order
        int min_diff = Integer.MAX_VALUE;
        for (int i = 1; i < n; i++) {
            min_diff = Math.min(min_diff, arr[i] - arr[i - 1]);
        }
        return min_diff; // Returning the minimum difference
    }
}

// Time complexity: O(n log n) due to sorting the array
