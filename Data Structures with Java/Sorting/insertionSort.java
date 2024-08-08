// Insertion Sort Algorithm
public class insertionSort {
    public static void main(String[] args) {
        int arr[] = { 1, 3, 2, 8, 4, 5 };
        insertSort(arr, arr.length);
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    static void insertSort(int[] arr, int n) {
        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }
}

// Time complexity: O(n^2)
// Worst Case: O(n^2)
// Best Case: O(n)