
public class getSecLargNaive {

    public static void main(String[] args) {
        int arr[] = {1, 2, 3, 4, 5, 6};
        System.out.println(secondLargest(arr));
    }

    static int getLargest(int[] arr, int size) {
        int max = 0;
        for (int i = 0; i < size; i++) {
            if (arr[i] > arr[max]) {
                max = i;
            }
        }
        return max;
    }

    static int secondLargest(int[] arr) {
        int n = arr.length;
        int max = getLargest(arr, n);
        int res = -1;
        for (int i = 0; i < n; i++) {
            if (arr[i] != arr[max]) {
                if (res == -1) {
                    res = i;
                } else if (arr[i] > arr[res]) {
                    res = i;
                }
            }
        }
        return (res + 1);
    }
}
