// Program to allocate memory for Arrays
public class createArray {

    public static void main(String[] args) {
        int arr[] = new int[10]; // Array1
        int[] arr1 = new int[10]; // Array2
        int arr2[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }; // Array3

        // To print the array we need to iterate over each element of the array and
        // print
        for (int i = 0; i < 10; i++) {
            arr[i] = i;
            arr1[i] = arr2[i];
            System.out.println(arr[i]);
            System.out.println(arr1[i]);
            System.out.println(arr2[i]);
        }
    }
}
