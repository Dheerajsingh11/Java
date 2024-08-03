// Program to count distinct elements in array

public class countDistinctNaive {
    public static void main(String[] args) {
        int[] arr = { 1, 1, 2, 3, 2, 4, 5 };
        System.out.println(countDistinct(arr));
    }

    static int countDistinct(int[] arr) {
        int n = arr.length;
        int res = 0;
        for (int i = 0; i < n; i++) {
            boolean isDistinct = true;
            for (int j = 0; j < i; j++) {
                if (arr[i] == arr[j]) {
                    isDistinct = false;
                    break;
                }
            }
            if (isDistinct) {
                res++;
            }
        }
        return res;
    }
}

// Time complexity: THETA(n^2)
// Auxiliary space: THETA(1)