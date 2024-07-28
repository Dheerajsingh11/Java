// Program to left rotate array by D
//Solution 3
public class leftRotateD3 {

    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        rotate(arr, 2); // calling the function to left rotate an array by 2
        for (int i = 0; i < arr.length; i++) { // printing the elements after rotation
            System.out.println(arr[i]);
        }
    }

    static void rotate(int[] arr, int d) {
        int n = arr.length;
        reverse(arr, 0, d - 1); // reverse the elements from 0 to d index
        reverse(arr, d, n - 1); // reverse the elements from d to n index
        reverse(arr, 0, n - 1); // reverse the all the elements
    }

    static void reverse(int[] arr, int start, int end) {
        while (start < end) { // program to reverse the elements
            int temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
            start++;
            end--;
        }
    }
}

// Time complexity: THETA(n)
// Auxiliary space: THETA(1)