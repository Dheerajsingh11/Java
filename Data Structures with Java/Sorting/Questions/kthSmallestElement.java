package Questions;

// Basic solution to find kth Smallest
import java.util.Arrays;

public class kthSmallestElement {
    public static void main(String[] args) {
        int arr[] = { 7, 10, 4, 3, 20, 15 };
        int n = arr.length;
        int k = 3;
        System.out.println("Kth smallest element is: " + kthSmallest(arr, n, k));
    }

    static int kthSmallest(int arr[], int n, int k) {
        Arrays.sort(arr);
        return arr[k - 1];
    }
}

// Time complexity: O(n log n) due to sorting the array