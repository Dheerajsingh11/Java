class myHash{
    int[] arr;
    int size, cap ;
    myHash(int c){
        cap = c;
        size = 0;
        arr = new int[cap];
        for(int i = 0; i< cap; i++){
            arr[i] = -1; // Initialize all slots as empty
        }
    }

        int hash(int key){
            return key % cap; // simple hash function
        }

        boolean search(int key){
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

        boolean insert(int key){
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

        boolean delete(int key){
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
    
}
