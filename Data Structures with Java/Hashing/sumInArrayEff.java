
import java.util.HashSet;
import java.util.Set;

// Program to check if any subarray is present in the array with sum equal to the given value
public class sumInArrayEff {
    public static void main(String[] args) {
        int arr[] = {1, 2, 3, 4, 5, 6};
        int x = 12;
        System.out.println(subarrayExists(arr, x));
    }

    static boolean subarrayExists(int arr[], int sum) {
        int n = arr.length;
        Set<Integer> set = new HashSet<Integer>();
        int curr_sum = 0;
        for(int x: arr) {
            curr_sum += x;
            if(curr_sum == sum) {
                return true;
        } 
        if( set.contains(curr_sum-sum)){
            return true;
        }
        set.add(curr_sum);
    }
    return false;
}
}

//Time complexity: O(n)
//space complexity: O(n)