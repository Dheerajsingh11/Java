//Program to check if the array is sorted or not
//NAIVE APPROACH
public class isSortedNaive {
    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        int arr1[] = { 1, 2, 3, 4, 5, 2 };
        System.out.println(isSorted(arr)); // Calling the function to check if arr is sorted or not
        System.out.println(isSorted(arr1)); // Calling the function to check if arr1 is sorted or not
    }

    static boolean isSorted(int[] arr) {
        int n = arr.length; // n is array length
        for (int i = 0; i < n; i++) { // iterating through the element of the array
            for (int j = i + 1; j < n; j++) { // comparing current element with each element of the array from the
                                              // current position to end
                if (arr[j] < arr[i]) { // if any unsorted element is found
                    return false; // return false
                }
            }
        }
        return true; // return true means all the elements are sorted
    }
}

// Time Complexity: Theta(n^2)
