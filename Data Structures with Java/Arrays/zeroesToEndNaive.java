// Program to move the zeroes in the array to the end
// NAIVE APPROACH
public class zeroesToEndNaive {

    public static void main(String[] args) {
        int arr[] = { 1, 2, 0, 4, 3, 0, 5, 0 };
        zeroesToEnd(arr); // calling the function to move the zeroes to the end
        for (int i = 0; i < arr.length; i++) { // printing the elements of the array after moving the zeroes
            System.out.println(arr[i]);
        }
    }

    static void zeroesToEnd(int[] arr) {
        for (int i = 0; i < arr.length; i++) { // iterating through the array
            if (arr[i] == 0) { // if current element is 0 then
                for (int j = i + 1; j < arr.length; j++) { // iterate through the remaining elements
                    if (arr[j] != 0) { // if current j is not 0 then switch the element with zero
                        int temp = arr[i];
                        arr[i] = arr[j];
                        arr[j] = temp;
                        break;
                    }
                }
            }
        }
    }
}

// Time complexity: THETA(n^2) - due to nested loops