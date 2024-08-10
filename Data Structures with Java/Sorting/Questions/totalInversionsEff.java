package Questions;

// Efficient solutions to find total inversions in an Array
public class totalInversionsEff {
    public static void main(String[] args) {
        int arr[] = { 8, 4, 2, 1, 5, 6, 3, 7 };
        int n = arr.length;
        System.out.println("Total inversions are: " + inversions(arr, 0, n - 1));
    }

    static int inversions(int[] arr, int left, int right) {
        int count = 0;
        if (left < right) {
            int m = left + (right - left) / 2;
            count += inversions(arr, left, m);
            count += inversions(arr, m + 1, right);
            count += countmerge(arr, left, m, right);
        }
        return count;
    }

    static int countmerge(int[] arr, int left, int m, int right) {
        int n1 = m - left + 1;
        int n2 = right - m;
        int L[] = new int[n1];
        int R[] = new int[n2];
        for (int i = 0; i < n1; i++) {
            L[i] = arr[left + i];
        }

        for (int i = 0; i < n2; i++) {
            R[i] = arr[m + 1 + i];
        }
        int count = 0, i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
                count += (n1 - i);
            }
            k++;
        }

        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }
        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
        }
        return count;
    }
}

// Time complexity: O(nlog(n))
// Auxiliary space: O(n)