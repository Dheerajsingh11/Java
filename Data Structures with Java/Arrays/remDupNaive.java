
// Remove duplicates from a sorted array
public class remDupNaive {

    public static void main(String[] args) {
        int arr[] = {1, 1, 2, 3, 4, 4, 5};
        int n = remDup(arr);
        for (int i = 0; i < n; i++) {
            System.out.println(arr[i]);
        }
    }

    static int remDup(int[] arr) {
        int n = arr.length;
        int[] temp = new int[n];
        temp[0] = arr[0];
        int res = 1;
        for (int i = 1; i < n; i++) {
            if (temp[res - 1] != arr[i]) {
                temp[res] = arr[i];
                res++;
            }
        }
        for (int i = 0; i < res; i++) {
            arr[i] = temp[i];
        }
        return res;
    }
}
