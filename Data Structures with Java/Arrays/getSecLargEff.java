//Program to get the second largest element of the array
//EFFICIENT APPROACH
public class getSecLargEff {
    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        System.out.println(secondLargest(arr)); // calling the function to return the second largest element of the
                                                // array
    }

    static int secondLargest(int arr[]) {
        int res = -1, largest = 0; // let the result be -1 and largest element at 0 position
        for (int i = 1; i <= arr.length - 1; i++) { // iterating through the elements of the array
            if (arr[i] > arr[largest]) { // if any element is greater than the largest element
                res = largest; // the pervious largest will become the second largest
                largest = i; // the new largest will change the current largest
            } else if (arr[i] != arr[largest]) { // check if arr[i] is not equal to the largest array
                if (res == -1 || arr[i] > arr[res]) { // and result is not -1 or arr[i] is greater than arr[res]
                    res = i; // result will be i
                }
            }
        }
        return (res + 1); // return res+1
    }
}
// Time complexity: THETA(n)
// Auxiliary Space: THETA(1)