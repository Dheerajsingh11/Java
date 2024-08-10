package Questions;

// Program to find intersection of two sorted Arrays
public class intersectionArrNaive {
    public static void main(String[] args) {
        int a[] = { 1, 2, 3, 3, 6, 7, 7, 8 };
        int b[] = { 2, 3, 5, 7, 8, 9 };
        intersection(a, b, a.length, b.length);
    }

    static void intersection(int[] a, int[] b, int m, int n) {
        for (int i = 0; i < m; i++) {
            if (i > 0 && a[i] == a[i - 1]) {
                continue;
            }
            for (int j = 0; j < n; j++) {
                if (a[i] == b[j]) {
                    System.out.print(a[i] + " ");
                    break;
                }
            }
        }
    }
}

// Time complexity: O(n*m)