// Program to return the largest element in the array
//NAIVE APPROACH: beginner apprach
public class largestElementNaive {

    public static void main(String[] args) {
        int arr[] = { 10, 12, 8, 21 };
        System.out.println(largestElement(arr)); // Calling function to return the largest element
    }

    static int largestElement(int[] arr) {
        int n = arr.length; // Number of elements
        for (int i = 0; i < n; i++) { // iterating through the elements of the array
            boolean flag = true; // Flag indicating whether the element is largest of not
            for (int j = 0; j < n; j++) { // iteration through the elements of the array to compare it with current
                                          // position
                if (arr[j] > arr[i]) { // compare current element with all the elements of the array
                    flag = false; // if larger is found then return false
                    break;
                }
            }
            if (flag == true) {
                return arr[i]; // if final flag comes true then return current position
            }
        }
        return -1; // else return false.
    }
}

// Best case time complexity THETA(n)
// Worst case time complexity THETA(n^2)
// Space complexity THETA(1)
