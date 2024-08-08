
import java.util.Arrays;

// Merge two sorted Array
public class mergeArrNaive {
    public static void main(String[] args) {
        int arr1[] = { 1, 2, 3, 4, 5 };
        int arr2[] = { 6, 7 };
        Merge(arr1, arr2, arr1.length, arr2.length);
    }

    static void Merge(int[] a, int[] b, int m, int n) {
        int c[] = new int[m + n];
        for (int i = 0; i < m; i++) {
            c[i] = a[i];
        }

        for (int i = 0; i < n; i++) {
            c[m + i] = b[i];
        }

        Arrays.sort(c);
        for (int i = 0; i < m + n; i++) {
            System.out.print(c[i] + " ");
        }
    }
}

// Time complexity: O((m+n)*log(m+n))
// Auxiliary space: THETA(m+n)