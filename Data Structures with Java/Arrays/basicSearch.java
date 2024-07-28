// Program for Basic search operations on the Array
class basicSearch {

    public static void main(String[] args) {
        int[] a = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }; // Creating an array
        System.out.println(linearSearch(a, 5)); // Calling linear seach function to search an element
    }

    // lets create linear search function
    static int linearSearch(int[] arr, int x) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == x) {
                return (i + 1);
            }
        }
        return -1;
    }
}

// Output: 5, the element 5 is found at index 5 in the given array.
// Complexity: O(n)
