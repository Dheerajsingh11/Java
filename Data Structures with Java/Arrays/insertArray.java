// Program to insert an element in the array
public class insertArray {

    public static void main(String[] args) {
        int[] arr = new int[10]; // Creating a new Array
        for (int i = 0; i < 5; i++) { // Entering elements in Array and make sure there is empty place in array for
                                      // new element
            arr[i] = i;
        }
        int n = insert(arr, 5, 10, 10, 3); // Calling insert function to insert new element
        for (int i = 0; i < n; i++) { // Printing the array
            System.out.println(arr[i]);
        }
    }

    static int insert(int arr[], int n, int x, int cap, int pos) {
        if (n == cap) { // array is full then return length of array
            return n;
        }
        int idx = pos - 1;
        for (int i = n - 1; i >= idx; i--) { // moving the elements by one index from the position of new element
            arr[i + 1] = arr[i];
        }
        arr[idx] = x; // inserting the element at position
        return (n + 1); // length of array will increase by 1
    }
}

// Time complexity: O(n)
// Insert at the end: O(1)
// Insert at the beginning: O(n)
