//program to generate subset of strings
//I/P: s= "AB"
// O/P: "" "A" "B" "AB"
public class generateSubsets {
    public static void main(String[] args){
        String s = "AB";
        subset(s," ",0); //calling function to create subsets
    }

    static void subset(String s , String curr, int i){       
        int n = s.length();      
        if(i==n){ // Base case
            System.out.print(curr + " "); //if value to i reaches the end of the string then print the current value and return
            return;
        }
        subset(s, curr, i+1); //calling the subset method to print single subsets
        subset(s, curr + s.charAt(i),i+1); //calling the subset method to print multiple char subsets
    }
}