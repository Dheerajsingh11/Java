// Selection Sort algorithm NAIVE

public class selectionSortNaive {
    public static void main(String[] args) {
        int arr[] = { 1, 3, 2, 8, 4, 5 };
        selectionSort(arr, arr.length);
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    static void selectionSort(int[] arr, int n) {
        int temp[] = new int[n];
        for (int i = 0; i < n; i++) {
            int min_ind = 0;
            for (int j = 1; j < n; j++) {
                if (arr[j] < arr[min_ind]) {
                    min_ind = j;
                }
            }
            temp[i] = arr[min_ind];
            arr[min_ind] = Integer.MAX_VALUE;
        }
        for (int i = 0; i < n; i++) {
            arr[i] = temp[i];
        }
    }
}

// Time complexity: THETA(n^2)
