// Program to create my own Hash
class myHash{
    int[] arr; // initializing the array for the Hash
    int size, cap ;
    myHash(int c){ // constructor to create a new Hash
        cap = c;
        size = 0;
        arr = new int[cap]; // creating the array with given capacity
        for(int i = 0; i< cap; i++){
            arr[i] = -1; // Initialize all slots as empty
        }
    }

        int hash(int key){ // function for getting hash function to get the position for a key
            return key % cap; // simple hash function
        }

        boolean search(int key){ // function to search for a key
            int h = hash(key);
            int i = h;
            while(arr[i]!= -1){
                if(arr[i] == key)
                    return true;
                i = (i + 1) % cap; // linear probing
                if(i==h){
                    return false; // key not found
                }
            }
            return false;
        }

        boolean insert(int key){ // function to insert a key into the Hash
            if(size==cap){
                return false;
            }
            int i = hash(key);
            while(arr[i]!=-1 && arr[i]!=-2 && arr[i]!=key){
                i = (i + 1) % cap; // linear probing
            }
            if(arr[i]==key){
                return false; // key already exists
            }
            else{
                arr[i] = key;
                size++;
                return true;
            }
        }

        boolean delete(int key){ // function to delete a key from the Hash
            int i = hash(key);
            while(arr[i]!= -1){
                if(arr[i] == key){
                    arr[i] = -2; // mark as deleted
                    size--;
                    return true;
                }
                i = (i + 1) % cap; // linear probing
                if(i==hash(key)){
                    return false; // key not found
                }
            }
            return false; // key not found
        }
    }

public class linearProbing {
    public static void main(String[] args) {
        myHash mh = new myHash(7); // object mh to create a Hash
        mh.insert(49); // insert a key
        mh.insert(30);
        if(mh.search(30)== true){ // find a key
            System.out.println("Element found");
        }
        else{
            System.out.println("Element not found");
        }
        mh.delete(30);
        if(mh.search(30)== true){
            System.out.println("Element found after deletion");
        }
        else{
            System.out.println("Element not found after deletion");
        }
    }

}
