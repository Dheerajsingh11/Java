
import java.util.HashMap;
import java.util.Map;

// return the size of longest subarray with given sum
public class longestSubArrEff {
    public static void main(String[] args) {
        int arr[] = {1,1,2,3,4,5,3,7};
        int sum = 10;
        System.out.println(subArray(arr, sum));
    }

    static int subArray(int[] arr, int sum){
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        int pre_sum = 0;
        int res = 0;
        for(int i = 0; i < arr.length; i++){
            pre_sum += arr[i];
            if(pre_sum == sum){
                res = i + 1;
            }
            if(map.containsKey(pre_sum)==false){
                map.put(pre_sum, i);
            }
            if(map.containsKey(pre_sum - sum)){
                res = Math.max(res, i - map.get(pre_sum - sum));
            }
        }
        return res;
    }
}
// Time Complexity: O(n)
// Auxiliary space: O(n)