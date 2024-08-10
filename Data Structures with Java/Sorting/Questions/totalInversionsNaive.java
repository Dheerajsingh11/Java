package Questions;

// Program to find total inversions in an Array
public class totalInversionsNaive {
    public static void main(String[] args) {
        int arr[] = { 8, 4, 2, 1, 5, 6, 3, 7 };
        int n = arr.length;
        System.out.println("Total inversions are: " + inversions(arr, n));
    }

    static int inversions(int[] arr, int n) {
        int res = 0;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (arr[i] > arr[j])
                    res++;
            }
        }
        return res;
    }
}
