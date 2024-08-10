package Questions;

import java.util.Arrays;

// Basic solution to find the union of two sorted arrays
public class unionNaive {
    public static void main(String[] args) {
        int a[] = { 1, 2, 3, 4, 5 };
        int b[] = { 3, 4, 5, 6, 7 };
        union(a, b, a.length, b.length);
    }

    static void union(int a[], int b[], int m, int n) {
        int c[] = new int[m + n];
        for (int i = 0; i < m; i++) {
            c[i] = a[i];
        }

        for (int i = 0; i < n; i++) {
            c[m + i] = b[i];
        }

        Arrays.sort(c);
        for (int i = 0; i < m + n; i++) {
            if (i == 0 || c[i] != c[i - 1]) {
                System.out.print(c[i] + " ");
            }
        }
    }
}

// Time complexity: O((m+n)*log(n))
// Auxiliary space: O((m+n)