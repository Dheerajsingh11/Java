// Program to left rotate an array by 1
public class leftRotateone {

    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        rotate(arr); // calling the function to rotate the array by 1
        for (int i = 0; i < arr.length; i++) { // priting the array after the rotation
            System.out.println(arr[i]);
        }
    }

    static void rotate(int[] arr) {
        int n = arr.length;
        int temp = arr[0]; // taking the temp as arr[0]
        for (int i = 0; i < n - 1; i++) {
            arr[i] = arr[i + 1]; // swapping each element by 1 index backwards
        }
        arr[n - 1] = temp; // making the last element equal to the first element i.e. temp
    }
}

// Time Complexity: O(n)
