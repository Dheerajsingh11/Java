//left rotate an array by D places
//Approach 1
public class leftRotateD1 {

    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        rotate(arr, 2); // calling the function to left rotate the array by 2 elements
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }

    static void rotateBy1(int[] arr) { // creating the function to left rotate by 1
        int n = arr.length;
        int temp = arr[0];
        for (int i = 1; i < n; i++) {
            arr[i - 1] = arr[i];
        }
        arr[n - 1] = temp;
    }

    static void rotate(int[] arr, int d) {
        // int n = arr.length;
        for (int i = 0; i < d; i++) { // looping the number of times the array needs to be rotated
            rotateBy1(arr); // calling the function to rotate by 1
        }
    }
}

// Time complexity: THETA(nd) - n: number of elements, d: number of times to
// rotate
// Space complexity: THETA(1)