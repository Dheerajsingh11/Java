# Hashing
 - Average performance for Hashing functions like search, insert and delete will be in O(1) time

### Not useful for:
    - Finding the closest value
    - Sorted data
    - Prefix searching

## Hashing application:
- Dictionary
- Database Indexing
- Cryptography
- Caches
- Symbol tables in complires and interpreters
- Routers
- Getting data from the database

## Hashing Introduction
- Take large keys and return small values to store in the Hash table

### How Hash function works?
- Should always map a large key to same small key
- Should generate values from 0 to n-1
- Should be fast O(1) for integers and O(length) for string
- Should informly distribute large keys into the Hash slots

-> Example Hash Functions:
    - h(large_key) = large_key % m;
    - For String, Weighted sum
            str[] 'abcd'
        (str[0]*x + str[1]*(x^2) + str[2]*(x^3) + str[3]*x^4) % m;
    - Universal function

## Collision Handling
- If we already know the keys in advance, then we can prefer perfect Hashing.
- If we do not know the keys, then we use the following:
        - Chaining
            - Chain of colliding items
        - Open Addressing
            - Use the same array and if the position is occupied then insert at the next position found
            - Types:
                - Linear probing
                - Quadratic probing
                - Double Hashing

#### Birthday Paradox
- With 23 people in the room, there is 50% chance that there is someone in the room with same birthdays
- With 70 people, possiblity is 99.9%
- **Collision is bound to happen if we don't know the keys**

### More