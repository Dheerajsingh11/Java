package Questions;

// Efficient solution for Union of two sorted arrays
public class unionEff {
    public static void main(String[] args) {
        int a[] = { 1, 2, 3, 4, 5 };
        int b[] = { 3, 4, 5, 6, 7 };
        union(a, b, a.length, b.length);
    }

    static void union(int[] a, int[] b, int m, int n) {
        int i = 0, j = 0;
        while (i < n && j < m) {
            if (i > 0 && a[i] == a[i - 1]) {
                i++;
                continue;
            }
            if (j > 0 && b[j] == b[j - 1]) {
                j++;
                continue;
            }
            if (a[i] < b[j]) {
                System.out.print(a[i] + " ");
                i++;
            } else if (a[i] > b[j]) {
                System.out.print(b[j] + " ");
                j++;
            } else {
                System.out.print(a[i] + " ");
                i++;
                j++;
            }
        }
        while (i < m) {
            if (i > 0 && a[i] != a[i - 1]) {
                System.out.print(a[i] + " ");
                i++;
            }
        }
        while (j < n) {
            if (j > 0 && b[j] != b[j - 1]) {
                System.out.print(b[j] + " ");
                j++;
            }
        }
    }
}

// Time complexity: O(m+n)
// Auxiliary space: O(1)