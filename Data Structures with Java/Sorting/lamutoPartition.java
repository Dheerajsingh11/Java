// Lamuto Partition Algorithm

public class lamutoPartition {
    public static void main(String[] args) {
        int arr[] = { 3, 8, 6, 12, 10, 7 };
        int p = lamPart(arr, 0, arr.length - 1);
        System.out.println(p);
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    static int lamPart(int arr[], int l, int h) {
        int pivot = arr[h];
        int i = l - 1;
        for (int j = l; j <= h - 1; j++) {
            if (arr[j] < pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        int temp = arr[i + 1];
        arr[i + 1] = arr[h];
        arr[h] = temp;
        return (i + 1);
    }
}

// Time complexity: THETA(n)