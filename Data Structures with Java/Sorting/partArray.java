// Naive method for partition of Array

public class partArray {
    public static void main(String[] args) {
        int arr[] = { 3, 8, 6, 12, 10, 7 };
        partition(arr, 0, arr.length - 1, 5);
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    static void partition(int[] arr, int l, int h, int p) {
        int temp[] = new int[h - l + 1], index = 0;
        for (int i = l; i <= h; i++) {
            if (arr[i] <= arr[p]) {
                temp[index] = arr[i];
                index++;
            }
        }

        for (int i = l; i <= h; i++) {
            if (arr[i] > arr[p]) {
                temp[index] = arr[i];
                index++;
            }
        }

        for (int i = l; i <= h; i++) {
            arr[i] = temp[i - l];
        }
    }
}

// Time complexity: O(n)
// Auxiliary space: O(n)