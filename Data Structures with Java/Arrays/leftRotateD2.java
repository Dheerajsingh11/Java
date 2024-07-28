//Program to left rotate an array by D 
//Solution 2
public class leftRotateD2 {

    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        rotate(arr, 2); // calling the function to left rotate an array by 2 position
        for (int i = 0; i < arr.length; i++) { // priting the array after the rotation
            System.out.println(arr[i]);
        }
    }

    static void rotate(int[] arr, int d) {
        int n = arr.length;
        int temp[] = new int[d]; // creating a temp array
        for (int i = 0; i < d; i++) {
            temp[i] = arr[i]; // store the first d elements in the temporary array
        }
        for (int i = d; i < n; i++) {
            arr[i - d] = arr[i]; // rotate the remaining elements by d elements to the left
        }
        for (int i = 0; i < d; i++) {
            arr[(n - d + i)] = temp[i]; // last d postion of the original array will be equal to temporary array
                                        // elements
        }
    }
}

// Time Complexity: THETA(n)
// Space complexity: THETA(d) - d: number of times array needs to be rotated