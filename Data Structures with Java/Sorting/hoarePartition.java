// Hoare Partition Algorithm

public class hoarePartition {
    public static void main(String[] args) {
        int arr[] = { 10, 9, 11, 8, 6, 7, 12, 3 };
        int p = hoarePart(arr, 0, arr.length - 1);
        System.out.println(p);
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    static int hoarePart(int[] arr, int l, int h) {
        int pivot = arr[l];
        int i = l - 1, j = h + 1;
        while (true) {
            do {
                i++;
            } while (arr[i] < pivot);

            do {
                j--;
            } while (arr[j] > pivot);

            if (i >= j) {
                return j;
            }

            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
}
