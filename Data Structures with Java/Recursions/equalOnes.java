// Program to find a subarray with equal number of zeroes and ones
public class equalOnes {
    public static void main(String[] args) {
        int[] arr = { 1, 1, 0, 1, 0, 0, 1 };
        int n = arr.length;
        System.out.println(longSubArr(arr, n));
    }

    static int longSubArr(int arr[], int n) {
        int res = 0;
        for (int i = 0; i < n; i++) {
            int count0 = 0, count1 = 0;
            for (int j = i; j < n; j++) {
                if (arr[j] == 0)
                    count0++;
                else
                    count1++;
                if (count0 == count1)
                    res = Math.max(res, j - i + 1);
            }
        }
        return res;
    }
}

// Time complexity: THETA(n^2)
// space complexity: THETA(1)