// Program to find the subarray with zero sum
class zeroSumArrnaive{
    public static void main(String[] args) {
        int arr[] = {4, 2, -3, 1, 0, 6, 3, -2, 4};
        int n = arr.length;
        System.out.println(subarrayWithZeroSum(arr, n));
    }

    static int subarrayWithZeroSum(int arr[], int n) {
        for(int i = 0; i<n; i++){
            int sum = 0;
            for(int j = i; j<n; j++){
                sum += arr[j];
                if(sum == 0){
                    return j - i + 1;
                }
            }
        }
        return -1; // If no subarray with zero sum found
    }
}
// Time complexity: O(n^2)