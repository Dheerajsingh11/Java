// Problem  : Declare, allocate, and initialize arrays in Java, and understand what memory is created.
// Approach : Show the three common ways to create an array and how to iterate one.
// Intuition: An array variable is a REFERENCE to a contiguous block on the heap. Declaring it does
//            not create the block - only "new" (or an array literal) allocates it, which is why an
//            array's length is fixed the moment it is created.
// Time     : allocation O(n) (the JVM zero-fills the block); access/update O(1) by index
// Space    : O(n)
// Trade-off: Contiguous storage buys O(1) random access and excellent cache locality, but the size
//            is FIXED - growing means allocating a bigger array and copying everything (O(n)).
//            ArrayList automates exactly that (see Java Core/09-Collections/ArrayListDemo.java).

public class createArray {

    public static void main(String[] args) {
        // Style 1: type followed by brackets on the VARIABLE (C-style).
        int arr[] = new int[5];      // allocated and AUTO-ZEROED -> {0, 0, 0, 0, 0}

        // Style 2: brackets on the TYPE - preferred in modern Java, since "int[]" reads as the type.
        int[] arr1 = new int[5];

        // Style 3: array literal - allocates AND fills in one step; size is inferred from the values.
        int[] arr2 = { 10, 20, 30, 40, 50 };

        // Java guarantees default values on allocation: 0 for numbers, false for boolean, null for
        // references. There is no "uninitialized garbage" as in C.
        System.out.println("default value of arr[0] = " + arr[0]); // expected: 0

        for (int i = 0; i < arr.length; i++) {   // 'length' is a FIELD on arrays, not a method
            arr[i] = i;                          // fill with 0..4
            arr1[i] = arr2[i];                   // copy element-by-element from arr2
        }

        System.out.println("arr  = " + java.util.Arrays.toString(arr));  // [0, 1, 2, 3, 4]
        System.out.println("arr1 = " + java.util.Arrays.toString(arr1)); // [10, 20, 30, 40, 50]
        System.out.println("arr2 = " + java.util.Arrays.toString(arr2)); // [10, 20, 30, 40, 50]

        // Edge: indices run 0..length-1. Touching arr[arr.length] throws
        // ArrayIndexOutOfBoundsException - Java bounds-checks every access (unlike C).
    }
}
