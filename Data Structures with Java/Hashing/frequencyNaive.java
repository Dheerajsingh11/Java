public class frequencyNaive {
    public static void main(String[] args){
        int arr[] = {1,1,2,2,3,2,1,4,5};
        printFreq(arr, arr.length);
    }

    static void printFreq(int arr[], int n){
        for(int i = 0; i < n; i++){
            boolean flag = false;
            for(int j = 0; j < n; j++){
                if(arr[i] == arr[j]){
                    flag = true;
                    break;
                }
            }
            if(flag == true){
                continue;
            }
            int freq = 1;
            for(int j = i+1; j<n; j++){
                if(arr[j] == arr[j]){
                    freq++;
                }
                System.out.println("Frequency of "+ arr[i] +" element: " +freq);
            }
        }
    }
}
