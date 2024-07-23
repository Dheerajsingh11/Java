//left rotate an array by D places

public class leftRotateD1 {

    public static void main(String[] args) {
        int arr[] = {1, 2, 3, 4, 5, 6};
        rotate(arr, 2);
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }

    static void rotateBy1(int[] arr) {
        int n = arr.length;
        int temp = arr[0];
        for (int i = 1; i < n; i++) {
            arr[i - 1] = arr[i];
        }
        arr[n - 1] = temp;
    }

    static void rotate(int[] arr, int d) {
        // int n = arr.length;
        for (int i = 0; i < d; i++) {
            rotateBy1(arr);
        }
    }

}
