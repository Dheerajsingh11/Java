// Program to check if any subarray is present in the array with sum equal to the given value

public class sumInArrayNaive {
    public static void main(String[] args) {
        int arr[] = {1, 2, 3, 4, 5, 6};
        int x = 12;
        System.out.println(subarrayExists(arr, x));
    }

    static boolean subarrayExists(int[] arr, int x){
        for(int i = 0; i < arr.length; i++){
            int sum = 0;
            for (int j = i; j < arr.length; j++){
                sum += arr[j];
                if(sum == x){
                    return true;
                }
            }
        }
        return false;
    }
}

// Time complexity: O(n^2)
// Auxiliary space: O(1)
