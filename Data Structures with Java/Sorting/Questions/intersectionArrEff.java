package Questions;

// Efficient solution to find intersection of two sorted arrays
public class intersectionArrEff {
    public static void main(String[] args) {
        int a[] = { 1, 2, 3, 3, 6, 7, 7, 8 };
        int b[] = { 2, 3, 5, 7, 8, 9 };
        intersection(a, b, a.length, b.length);
    }

    static void intersection(int a[], int b[], int m, int n) {
        int i = 0, j = 0;
        while (i < m && j < n) {
            if (i > 0 && a[i] == a[i - 1]) {
                i++;
                continue;
            }
            if (a[i] < b[j]) {
                i++;
            } else if (a[i] > b[j]) {
                j++;
            } else {
                System.out.print(a[i] + " ");
                i++;
                j++;
            }
        }
    }
}
