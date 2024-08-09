// Counting Sort efficient solution

public class countingSortEff {
    public static void main(String[] args) {
        int[] arr = { 1, 5, 1, 2, 0, 3, 4 };
        int k = 6;
        int n = arr.length;
        countSort(arr, n, k);
        for (int i = 0; i < n; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    static void countSort(int[] arr, int n, int k) {
        int[] count = new int[k];

        for (int i = 0; i < n; i++) {
            count[arr[i]]++;
        }

        for (int i = 1; i < k; i++) {
            count[i] += count[i - 1];
        }

        int output[] = new int[n];

        for (int i = n - 1; i >= 0; i--) {
            output[count[arr[i]] - 1] = arr[i];
            count[arr[i]]--;
        }

        // for (int i = 0; i < output.length; i++) {
        // System.out.print(output[i] + " ");
        // }
        for (int i = n - 1; i >= 0; i--) {
            arr[i] = output[i];
        }
    }
}
