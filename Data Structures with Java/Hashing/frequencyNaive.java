// Program to count frequency of each element in array

public class frequencyNaive {
    public static void main(String[] args){
        int arr[] = {1,1,2,2,3,2,1,4,5};
        printFreq(arr, arr.length);
    }

    static void printFreq(int arr[], int n){
        for(int i = 0; i < n; i++){
            int count = 0;
            for(int j = 0; j < n; j++){
                if(arr[i] == arr[j]){
                    count++;
                }
            }
            System.out.println("Frequency of "+ arr[i] +" element: " +count);    
    }
}
}
//Time complexity: THETA(n^2)
