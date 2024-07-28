//Program to find the largest element in the array
//EFFICIENT APPROACH
public class largestElementEff {

    public static void main(String[] args) {
        int arr[] = { 10, 12, 8, 21 };
        System.out.println(largestElement(arr)); // Calling function to return the largest element of the array
    }

    static int largestElement(int[] arr) {
        int max = arr[0]; // assuming the max is at 0 position
        for (int i = 0; i < arr.length; i++) { // iterating throught the elements of the array
            if (arr[i] > max) { // if any element is greater than the max
                max = arr[i]; // max will the larger element
            }
        }
        return max; // return the maximum element
    }
}

// Time complexity THETA(n)
