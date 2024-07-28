//program to revese the array
public class reverseArray {

    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        reverseArr(arr); // calling the function to reverse the array
        for (int i = 0; i < arr.length; i++) { // printing the array after the reverse operation
            System.out.println(arr[i]);
        }
    }

    static void reverseArr(int[] arr) {
        int low = 0, high = arr.length - 1; // tale low as 1st position and high as last position
        while (low < high) { // loop till the low is smaller than high
            int temp = arr[low];
            arr[low] = arr[high];
            arr[high] = temp; // swap the current low and high elements
            low++; // increase the low
            high--; // decrease the high
        }
    }
}

// Time Complexity: THETA(n)
// Auxiliary Space: THETA(1)