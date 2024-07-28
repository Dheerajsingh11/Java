//program to check if the array is sorted or not
//EFFICIENT APPROACH
public class isSortedEff {
    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        int arr1[] = { 1, 2, 3, 4, 5, 2 };
        System.out.println(isSorted(arr)); // calling function to check if arr is sorted
        System.out.println(isSorted(arr1)); // calling function to check if arr1 is sorted
    }

    static boolean isSorted(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) { // iterating through each element of the array
            if (arr[i] > arr[i + 1]) { // if any two elements are in unsorted order then return false
                return false;
            }
        }
        return true; // true means all the elements are in sorted order
    }
}
