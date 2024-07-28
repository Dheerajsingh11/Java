// Program to move the zeroes to the end
// EFFICIENT APPROACH
public class zeroesToEndEff {

    public static void main(String[] args) {
        int arr[] = { 1, 2, 0, 4, 3, 0, 5, 0 };
        zeroesToEnd(arr); // calling function to move the elements to the end
        for (int i = 0; i < arr.length; i++) { // printing the array after movig the zeroes
            System.out.println(arr[i]);
        }
    }

    static void zeroesToEnd(int[] arr) {
        int j = 0;
        for (int i = 0; i < arr.length; i++) { // iterating through the array
            if (arr[i] != 0) { // if current element is not 0
                int temp = arr[i]; // then swap the current element with the element at j
                arr[i] = arr[j];
                arr[j] = temp;
                j++; // increase the j by 1
            }
        }
    }
}

// Time complexity: O(n)
// Space complexity: O(1)