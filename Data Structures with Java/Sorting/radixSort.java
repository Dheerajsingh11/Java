// Radix Sort
public class radixSort {
    public static void main(String[] args) {
        int arr[] = { 1, 3, 2, 8, 4, 5 };
        radixsort(arr, arr.length);
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    static void radixsort(int arr[], int n) {
        int max = arr[0];
        for (int i = 1; i < n; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }

        for (int ex = 1; max / ex > 0; ex = ex * 10) {
            countSort(arr, n, ex);
        }
    }

    static void countSort(int[] arr, int n, int k) {
        int[] count = new int[10];

        for (int i = 0; i < 10; i++) {
            count[i] = 0;
        }

        for (int i = 0; i < n; i++) {
            count[(arr[i] / k) % 10]++;
        }

        for (int i = 1; i < 10; i++) {
            count[i] = count[i] + count[i - 1];
        }

        int output[] = new int[n];

        for (int i = n - 1; i >= 0; i--) {
            output[count[(arr[i] / k) % 10] - 1] = arr[i];
            count[(arr[i] / k) % 10]--;
        }

        for (int i = n - 1; i >= 0; i--) {
            arr[i] = output[i];
        }
    }
}

// Counting sort is not reliable for large numbers, the time complexity can
// become n^3 or n^4
// To overcome this, we use Radix sort
// Time complexity: THETA(d*(n+b))
// Auxiliary space: THETA(n+b)