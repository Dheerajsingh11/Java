//Program to remove duplicates from a sorted array
//NAIVE APPROACH
public class remDupNaive {

    public static void main(String[] args) {
        int arr[] = { 1, 1, 2, 3, 4, 4, 5 };
        int n = remDup(arr); // calling function to remove the duplicates from the array
        for (int i = 0; i < n; i++) { // printing the elements of the array after removing duplicates
            System.out.println(arr[i]);
        }
    }

    static int remDup(int[] arr) {
        int n = arr.length;
        int[] temp = new int[n]; // creating a new temporary array of same length as the original array
        temp[0] = arr[0]; // taking the initial element of temp array same as the original
        int res = 1; // res equal to 1 because the next insertion will happen at 1 index
        for (int i = 1; i < n; i++) { // iterating over the array
            if (temp[res - 1] != arr[i]) { // if temp[res - 1] is not equal to arr[i] that means it is not a duplicate
                                           // element
                temp[res] = arr[i]; // insert the element at res index
                res++; // increase the res by 1
            }
        }
        for (int i = 0; i < res; i++) { // copying the temporary array into the original array
            arr[i] = temp[i];
        }
        return res; // returning the new length of the array
    }
}

// Time Complexity: O(n)
// Space complexity: O(n)