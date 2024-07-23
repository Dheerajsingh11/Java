
public class remDupEff {

    public static void main(String[] args) {
        int arr[] = {1, 1, 2, 3, 3, 4, 5, 6, 6, 7};
        int n = remDups(arr);
        for (int i = 0; i < n; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    static int remDups(int[] arr) {
        int n = arr.length;
        int res = 1;
        for (int i = 1; i < n; i++) {
            if (arr[i] != arr[i - 1]) {
                arr[res] = arr[i];
                res++;
            }
        }
        return res;
    }
}
