// Quick Sort using Hoare Partition

public class quickSortHoare {
    public static void main(String[] args) {
        int arr[] = { 1, 3, 2, 8, 4, 5 };
        quickSort(arr, 0, arr.length - 1);
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    static void quickSort(int[] arr, int l, int h) {
        if (l < h) {
            int p = hoarePart(arr, l, h);
            quickSort(arr, l, p - 1);
            quickSort(arr, p + 1, h);
        }
    }

    static int hoarePart(int[] arr, int l, int h) {
        int pivot = arr[l];
        int i = l - 1, j = h + 1;
        while (true) {
            do {
                i++;
            } while (arr[i] < pivot);

            do {
                j--;
            } while (arr[j] > pivot);

            if (i >= j) {
                return j;
            }

            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
}
// Worst Case: O(n^2)
// Best Case: THETA(log(n))
// Average Case: O(nlog(n))