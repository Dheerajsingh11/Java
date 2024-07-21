
public class insertArray {

    public static void main(String[] args) {
        int[] arr = new int[10];
        for (int i = 0; i < 5; i++) {
            arr[i] = i;
        }
        int n = insert(arr, 5, 10, 10, 3);
        for (int i = 0; i < n; i++) {
            System.out.println(arr[i]);
        }
    }

    static int insert(int arr[], int n, int x, int cap, int pos) {
        if (n == cap) {
            return n;
        }
        int idx = pos - 1;
        for (int i = n - 1; i >= idx; i--) {
            arr[i + 1] = arr[i];
        }
        arr[idx] = x;
        return (n + 1);
    }
}
