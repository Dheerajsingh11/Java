public class isSortedEff {
    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        int arr1[] = { 1, 2, 3, 4, 5, 2 };
        System.out.println(isSorted(arr));
        System.out.println(isSorted(arr1));
    }

    static boolean isSorted(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                return false;
            }
        }
        return true;
    }
}
