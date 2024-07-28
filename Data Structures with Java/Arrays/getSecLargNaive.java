//Program to get the second largest element in the array
//NAIVE APPROACH
public class getSecLargNaive {

    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        System.out.println(secondLargest(arr)); // calling the function to return second largest value
    }

    static int getLargest(int[] arr, int size) { // getting the largest element of the array
        int max = 0;
        for (int i = 0; i < size; i++) { // iterating through the array and finding the largest element
            if (arr[i] > arr[max]) {
                max = i;
            }
        }
        return max; // returning the maximum element
    }

    static int secondLargest(int[] arr) {
        int n = arr.length;
        int max = getLargest(arr, n); // to get the second largest, fetch the largest element of the array
        int res = -1; // initializing the result
        for (int i = 0; i < n; i++) { // iterating over the elements
            if (arr[i] != arr[max]) { // check if the element is not equal to the largest element beacuse we are
                                      // fetching the second largest element
                if (res == -1) { // check if the result is -1 then result will be the position current element
                    res = i;
                } else if (arr[i] > arr[res]) { // if arr[i] is greater than arr[res] and also arr[i] is not equal to
                                                // the max element then result will be the current position
                    res = i;
                }
            }
        }
        return (res + 1); // return res+1 because indexing starts from 0 position
    }
}

// Time Complexity: O(n)