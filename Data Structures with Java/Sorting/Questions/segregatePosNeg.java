package Questions;

// Sort an Array with two types
// Segregate positive and negative numbers
public class segregatePosNeg {
    public static void main(String[] args) {
        int[] arr = { -1, 2, -4, 5, 6, -3, 8, -9, 10 };
        int n = arr.length;
        segposneg(arr, n);
    }

    static void segposneg(int[] arr, int n) {
        int i = 0;
        int[] temp = new int[n];
        for (int j = 0; j < n; j++) {
            if (arr[j] < 0) {
                temp[i++] = arr[j];
            }
        }
        for (int j = 0; j < n; j++) {
            if (arr[j] >= 0) {
                temp[i++] = arr[j];
            }
        }

        for (int j = 0; j < n; j++) {
            arr[j] = temp[j];
            System.out.print(arr[j] + " ");
        }
    }
}

// Time complexity: THETA(n) - Three traversals
// Auxiliary space: THETA(n)