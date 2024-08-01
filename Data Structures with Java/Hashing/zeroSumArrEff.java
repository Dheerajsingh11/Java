
import java.util.HashSet;

// Program to find the subarray with zero sum
public class zeroSumArrEff {
    public static void main(String[] args) {
        int arr[] = {4, 2, -3, 1, 0, 6, 3, -2, 4};
        int n = arr.length;
        System.out.println(subarrayWithZeroSum(arr, n));
    }

    static boolean subarrayWithZeroSum(int arr[], int n) {
        HashSet<Integer> set = new HashSet<Integer>();
        int pre_sum = 0;
        for(int i=0; i<arr.length; i++) {
            pre_sum += arr[i];
            if(set.contains(pre_sum)){
                return true;//i - set.indexOf(pre_sum - 0);
            }
            set.add(pre_sum);
        }
        return false;
    }
}

// Time complexity: O(n)