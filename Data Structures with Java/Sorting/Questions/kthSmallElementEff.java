package Questions;

// Efficient solution to find kth smallest element in array
public class kthSmallElementEff {
    public static void main(String[] args) {
        int arr[] = { 7, 10, 4, 3, 20, 15 };
        int n = arr.length;
        int k = 3;
        System.out.println("Kth smallest element is: " + kthSmallest(arr, n, k));
    }

    static int kthSmallest(int[] arr, int n, int k) {
        int l = 0, r = n - 1;
        while (l < r) {
            int p = lamPart(arr, l, r);
            if (p == k - 1) {
                return arr[p];
            } else if (p > k - 1) {
                r = p - 1;
            } else {
                l = p + 1;
            }
        }
        return -1;
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

// Time complexity: O(n) in average case, but worst case is O(n^2) when array is
// already sorted or reverse sorted.
