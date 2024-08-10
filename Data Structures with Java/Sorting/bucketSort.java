import java.util.ArrayList;
import java.util.Collections;

public class bucketSort {
    public static void main(String[] args) {
        int[] arr = { 19, 78, 10, 85, 72, 99, 28 };
        int k = 5;
        int n = arr.length;
        bucketsort(arr, n, k);
        for (int i = 0; i < n; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    static void bucketsort(int[] arr, int n, int k) {
        int max = arr[0];
        for (int i = 1; i < n; i++) {
            max = Math.max(max, arr[i]);
        }
        max++;

        ArrayList<ArrayList<Integer>> bucket = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < k; i++) {
            bucket.add(new ArrayList<Integer>());
        }
        for (int i = 0; i < n; i++) {
            int bi = (arr[i] * k) / max;
            bucket.get(bi).add(arr[i]);
        }

        for (int i = 0; i < k; i++) {
            Collections.sort(bucket.get(i));
        }

        int index = 0;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < bucket.get(i).size(); j++) {
                arr[index++] = bucket.get(i).get(j);
            }
        }
    }
}

// Time Complexity Best case: O(n)
// Worst case: All the items in the same bucket, so it is based on the type of
// algorithm used to sort the bucket items