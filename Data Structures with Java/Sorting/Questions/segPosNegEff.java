package Questions;

// Efficient solution to segregate positive and negative elements of an Arrays
public class segPosNegEff {
    public static void main(String[] args) {
        int[] arr = { -1, 2, -4, 5, 6, -3, 8, -9, 10 };
        int n = arr.length;
        segposneg(arr, n);
        for (int i = 0; i < n; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    static void segposneg(int[] arr, int n) {
        int i = -1, j = n;
        while (true) {
            do {
                i++;
            } while (arr[i] < 0);
            do {
                j--;
            } while (arr[j] >= 0);
            if (i >= j) {
                return;
            }
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
}

// Time complexity: THETA(n) - single traversal
// Auxiliary space: THETA(1)