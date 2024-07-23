
public class leftRotateD2 {

    public static void main(String[] args) {
        int arr[] = {1, 2, 3, 4, 5, 6};
        rotate(arr, 2);
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }

    static void rotate(int[] arr, int d) {
        int n = arr.length;
        int temp[] = new int[d];
        for (int i = 0; i < d; i++) {
            temp[i] = arr[i];
        }
        for (int i = d; i < n; i++) {
            arr[i - d] = arr[i];
        }
        for (int i = 0; i < d; i++) {
            arr[(n - d + i)] = temp[i];
        }
    }
}
