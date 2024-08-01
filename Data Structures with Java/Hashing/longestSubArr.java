// return the size of longest subarray with given sum
public class longestSubArr {
    public static void main(String[] args) {
        int arr[] = {1,1,2,3,4,5,3,7};
        int sum = 10;
        System.out.println(subArray(arr, sum));
    }

    static int subArray(int[] arr, int sum){
    int res = 0;
    for(int i = 0; i < arr.length; i++){
        int currSum = 0;
        for(int j = i; j < arr.length; j++){
            currSum += arr[j];
            if(currSum == sum){
                res = Math.max(res, j - i + 1);
            }
        }
    }
    return res;
    }
}
