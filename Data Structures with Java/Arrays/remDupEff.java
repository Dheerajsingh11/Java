// Program to remove the duplicate elements from the array 
//EFFICIENT APPROACH
public class remDupEff {

    public static void main(String[] args) {
        int arr[] = { 1, 1, 2, 3, 3, 4, 5, 6, 6, 7 };
        int n = remDups(arr); // calling the function to delete the duplicates array
        for (int i = 0; i < n; i++) { // printing the array after removing the duplicates
            System.out.print(arr[i] + " ");
        }
    }

    static int remDups(int[] arr) {
        int n = arr.length;
        int res = 1; // taking res as 1 index
        for (int i = 1; i < n; i++) { // iterating over the array
            if (arr[i] != arr[i - 1]) { // if previous element is not equal to the current element
                arr[res] = arr[i]; // then insert the current element at res index
                res++; // increase the res by 1
            }
        }
        return res; // return the new length of the array
    }
}

// Time complexity: O(n)
// Space complexity: O(1)