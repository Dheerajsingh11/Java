
public class delete {

    public static void main(String[] args) {
        int[] arr = new int[10];
        for (int i = 0; i < 10; i++) {
            arr[i] = i;
        }
        int n = deleteArray(arr, 10, 5);
        for (int i = 0; i < n; i++) {
            System.out.println(arr[i]);
        }
    }

    static int deleteArray(int arr[], int n, int element) {
        int i;
        for (i = 0; i < n; i++) {
            if (arr[i] == element) {
                break;
            }
        }
        if (i == n) {
            return n;
        }
        for (int j = i; j < n - 1; j++) {
            arr[j] = arr[j + 1];
        }
        return (n - 1);
    }
}
