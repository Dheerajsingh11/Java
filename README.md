# Java
---
# Java Algorithms Repository

## Introduction

Welcome to the Java Algorithms Repository! This project contains a collection of classic algorithms and data structures implemented in Java. Whether you're a student learning algorithms or a developer looking for reference implementations, this repository provides a variety of examples and explanations. I’m currently expanding my knowledge of Java, focusing on its various use cases and applications. In parallel, I am working on implementing and understanding data structures using Java. I regularly update this repository to reflect my learning journey and progress in mastering Java. Follow along to see my evolving work and contributions!

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Examples](#examples)
- [Contributing](#contributing)
- [Acknowledgements](#acknowledgements)
- [Contact](#contact)

## Features

- Basic and Advanced core Java
- Implementations of common algorithms (sorting, searching, etc.)
- Data structures (linked lists, trees, graphs, etc.)
- Code examples and explanations

## Installation

To get started with this repository, follow these steps:

1. **Clone the repository:**
    ```sh
    https://github.com/Dheerajsingh11/Java.git
    ```
2. **Navigate to the project directory:**
    ```sh
    cd "Data Structures with Java/Arrays"
    ```
3. **Compile the Java files:**
    ```sh
    javac src/*.java
    ```

## Usage

You can run individual algorithms by executing their main methods. For example:
```sh
cd "Data Structures with Java/Arrays"
```


```sh
java basicSearch
```

Replace `basicSearch` with the appropriate class name to test different algorithms.

## Examples

Here are a few examples of algorithms included in this repository:

### Bubble Sort

**File:** `Data Structures with Java/Arrays/basicSearch.java`

**Description:** Implements linear Search in Arrays.

**Usage:**
```java
class basicSearch {

    public static void main(String[] args) {
        int[] a = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        System.out.println(linearSearch(a, 5));
    }

    static int linearSearch(int[] arr, int x) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == x) {
                return (i + 1);
            }
        }
        return -1;
    }
}
```

### Binary Search

**File:** `Data Structures with Java/Recursions/fibonacci.java`

**Description:** Prints the number according to fibonacci series.

**Usage:**
```java
public class fibonacci {
    public static void main(String[] args) {
        int n = 5;
        System.out.println(fib(n));
    }

    static int fib(int n) {
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        return fib(n - 1) + fib(n - 2);
    }
}
```

## Contributing

I welcome contributions to enhance this repository. Please follow these steps to contribute:

1. **Fork the repository.**
2. **Create a new branch:**
    ```sh
    git checkout -b feature/your-feature
    ```
3. **Commit your changes:**
    ```sh
    git commit -am 'Add new algorithm'
    ```
4. **Push to the branch:**
    ```sh
    git push origin feature/your-feature
    ```
5. **Create a new Pull Request.**

## Acknowledgements

- [GeeksforGeeks](https://www.geeksforgeeks.org/) - For providing detailed explanations and examples.
- [Stack Overflow](https://stackoverflow.com/) - For community support and discussion.
- [Java Point](https://www.javatpoint.com/java-tutorial) - For Basic Java tutorials
- Will add more as we move forward with this repository

## Contact

For any questions or feedback, please contact [dheerajsinghchauhan05@gmail.com](dheerajsinghchauhan05@gmail.com).

---
