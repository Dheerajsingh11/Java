// Efficient solution to merge two sorted arr
public class mergeArrEff {
    public static void main(String[] args) {
        int arr1[] = { 1, 2, 3, 4, 5 };
        int arr2[] = { 6, 7 };
        Merge(arr1, arr2, arr1.length, arr2.length);
    }

    static void Merge(int a[], int b[], int m, int n) {
        int i = 0, j = 0;
        while (i < m && j < n) {
            if (a[i] <= b[j]) {
                System.out.print(a[i] + " ");
                i++;
            }

            else {
                System.out.print(b[j] + " ");
                j++;
            }

            while (i < m) {
                System.out.print(a[i] + " ");
                i++;
            }

            while (j < n) {
                System.err.print(b[j] + " ");
                j++;
            }
        }
    }
}

// Time complexity: THETA(m+n)