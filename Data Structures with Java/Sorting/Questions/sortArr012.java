package Questions;

// Sorting 0s 1s and 2s in an Array
public class sortArr012 {
    public static void main(String[] args) {
        int arr[] = { 0, 1, 1, 0, 1, 2, 2, 2, 0, 1 };
        int n = arr.length;
        sort012(arr, n);
        for (int i = 0; i < n; i++)
            System.out.print(arr[i] + " ");
    }

    static void sort012(int arr[], int n) {
        int temp[] = new int[n];
        int i = 0;
        for (int j = 0; j < n; j++) {
            if (arr[j] == 0) {
                temp[i++] = arr[j];
            }
        }
        for (int j = 0; j < n; j++) {
            if (arr[j] == 1) {
                temp[i++] = arr[j];
            }
        }
        for (int j = 0; j < n; j++) {
            if (arr[j] == 2) {
                temp[i++] = arr[j];
            }
        }

        for (int j = 0; j < n; j++) {
            arr[j] = temp[j];
        }
    }
}

// Time complexity: O(n) - four traversals
// Auxiliary space: O(n)