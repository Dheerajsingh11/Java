//program to delete an element from the Array
public class delete {

    public static void main(String[] args) {
        int[] arr = new int[10];
        for (int i = 0; i < 10; i++) {
            arr[i] = i;
        }
        int n = deleteArray(arr, 10, 5); // Calling the function to delete the element
        for (int i = 0; i < n; i++) { // Printing the array elements after deletion function
            System.out.println(arr[i]);
        }
    }

    static int deleteArray(int arr[], int n, int element) {
        int i;
        for (i = 0; i < n; i++) { // searching for the element in the array
            if (arr[i] == element) {
                break; // once element is found break the array
            }
        }
        if (i == n) { // no element found
            return n;
        }
        for (int j = i; j < n - 1; j++) { // moving the elements to one position backwards from the position of the
                                          // element
            arr[j] = arr[j + 1];
        }
        return (n - 1); // returning the new length of the array after deletion
    }
}

// Time Complexity: O(n)