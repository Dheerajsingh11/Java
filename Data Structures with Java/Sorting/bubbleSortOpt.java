// BubbleSort Algorithm Optimized

public class bubbleSortOpt {
    public static void main(String[] args) {
        int arr[] = { 1, 3, 2, 8, 4, 5 };
        bubbleSort(arr, arr.length);
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    static void bubbleSort(int[] arr, int n) {
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }
            if (swapped) { // If Swapped already, then will not do any more iteration
                break;
            }
        }
    }
}
// n-i-1 is optimized because i number of elements are already sorted, so no
// need to check them
// BubbleSort is stable as it doesn't change order of equal elements
// Time complexity: THETA(n^2)