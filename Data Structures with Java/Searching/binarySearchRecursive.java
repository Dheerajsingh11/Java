public class binarySearchRecursive {
    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        System.out.println(binarySearch(arr, 0, arr.length, 8));
    }

    static int binarySearch(int[] arr, int low, int high, int x) {
        if (low > high) {
            return -1;
        }
        int mid = (low + high) / 2;
        if (arr[mid] == x) {
            return mid;
        } else if (arr[mid] > x) {
            return binarySearch(arr, low, mid - 1, x);
        } else {
            return binarySearch(arr, mid + 1, high, x);
        }
    }
}

// Time complexity: O(log(n))
// Auxiliary space: O(log(n))