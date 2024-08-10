package Questions;

// Efficient Solution to sort 0s 1s and 2s in an Array
// Dutch national flag algorithm
public class sortArr012Eff {
    public static void main(String[] args) {
        int arr[] = { 0, 1, 1, 0, 1, 2, 2, 2, 0, 1 };
        int n = arr.length;
        sort012(arr, n);
        for (int i = 0; i < n; i++)
            System.out.print(arr[i] + " ");
    }

    static void sort012(int arr[], int n) {
        int lo = 0, mid = 0, hi = n - 1;
        while (mid <= hi) {
            if (arr[mid] == 0) {
                int temp = arr[mid];
                arr[mid] = arr[lo];
                arr[lo] = temp;
                lo++;
                mid++;
            } else if (arr[mid] == 1) {
                mid++;
            } else {
                int temp = arr[mid];
                arr[mid] = arr[hi];
                arr[hi] = temp;
                hi--;
            }
        }
    }
}

// Time complexity: O(n) - one traversal
// Auxiliary space: O(1)