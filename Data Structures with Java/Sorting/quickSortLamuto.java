// Quick Sort using Lamuto Partition

public class quickSortLamuto {
    public static void main(String[] args) {
        int arr[] = { 1, 3, 2, 8, 4, 5 };
        quickSort(arr, 0, arr.length - 1);
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    static void quickSort(int[] arr, int l, int h) {
        if (l < h) {
            int p = lamPart(arr, l, h);
            quickSort(arr, l, p - 1);
            quickSort(arr, p + 1, h);
        }
    }

    static int lamPart(int arr[], int l, int h) {
        int pivot = arr[h];
        int i = l - 1;
        for (int j = l; j <= h - 1; j++) {
            if (arr[j] < pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        int temp = arr[i + 1];
        arr[i + 1] = arr[h];
        arr[h] = temp;
        return (i + 1);
    }
}
// Worst Case: O(n^2)
// Best Case: THETA(log(n))
// Average Case: O(nlog(n))