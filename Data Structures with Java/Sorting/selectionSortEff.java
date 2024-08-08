// In-place Implementation of Selection sort

public class selectionSortEff {
    public static void main(String[] args) {
        int arr[] = { 1, 3, 2, 8, 4, 5 };
        selectionSort(arr, arr.length);
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    static void selectionSort(int[] arr, int n) {
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[min]) {
                    min = j;
                }
            }
            int temp = arr[min];
            arr[min] = arr[i];
            arr[i] = temp;
        }
    }
}
// Time complexity: THETA(n^2)