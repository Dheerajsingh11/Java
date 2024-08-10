// Program to count distinct elements in array

public class countDistinctNaive {
    public static void main(String[] args) {
        int[] arr = { 1, 1, 2, 3, 2, 4, 5 };
        System.out.println(countDistinct(arr)); // calling function to count distinct elements in the Array
    }

    static int countDistinct(int[] arr) {
        int n = arr.length;
        int res = 0;
        for (int i = 0; i < n; i++) { // loop to iterate the Array on by one
            boolean isDistinct = true; // bool flag to set as true by default means element is distinct
            for (int j = 0; j < i; j++) { // loop to iterate the array to compare other elements
                if (arr[i] == arr[j]) {
                    isDistinct = false; // if any element is found same as arr[i] the isDistinct will be changed to
                                        // false
                    break;
                }
            }
            if (isDistinct) { // if isDistinct is true, then it means the element is unique
                res++; // increment the res
            }
        }
        return res; // returning the final result
    }
}

// Time complexity: THETA(n^2)
// Auxiliary space: THETA(1)