// Problem  : Process collections declaratively with the Streams API, and handle "maybe absent" values
//            with Optional.
// Approach : Show filter/map/reduce/collect pipelines and Optional's safe-access methods.
// Intuition: A stream is a pipeline: a SOURCE, zero or more LAZY intermediate ops (filter/map), and
//            one TERMINAL op (collect/reduce) that triggers the work. Optional is a box that either
//            holds a value or is empty, forcing you to handle the empty case instead of hitting null.
// Time     : O(n) over the elements (per pass)   Space: depends on the terminal collector
// Trade-off: Streams are expressive and parallelizable but can be slower/harder to debug than plain
//            loops for simple tasks; use them when the pipeline reads clearly. Optional documents
//            "may be absent" in the type, reducing NullPointerExceptions.

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StreamsAndOptional {
    public static void main(String[] args) {
        List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // ---- filter -> map -> collect ----
        List<Integer> evenSquares = nums.stream()
                .filter(n -> n % 2 == 0)   // keep evens (lazy)
                .map(n -> n * n)           // square them (lazy)
                .collect(Collectors.toList()); // terminal: runs the pipeline
        System.out.println(evenSquares); // [4, 16, 36, 64, 100]

        // ---- reduce: fold the stream into one value ----
        int sum = nums.stream().reduce(0, Integer::sum);
        System.out.println("sum = " + sum); // 55

        // ---- count / anyMatch ----
        long oddCount = nums.stream().filter(n -> n % 2 == 1).count();
        System.out.println("odd count = " + oddCount); // 5
        System.out.println("any > 8? " + nums.stream().anyMatch(n -> n > 8)); // true

        // ---- grouping ----
        var byParity = nums.stream().collect(Collectors.groupingBy(n -> n % 2 == 0 ? "even" : "odd"));
        System.out.println(byParity); // {odd=[1,3,5,7,9], even=[2,4,6,8,10]}

        // ---- Optional: avoid nulls ----
        Optional<Integer> firstBig = nums.stream().filter(n -> n > 100).findFirst();
        System.out.println("present? " + firstBig.isPresent());   // false
        System.out.println("orElse: " + firstBig.orElse(-1));     // -1 (safe default)

        Optional<Integer> found = nums.stream().filter(n -> n == 7).findFirst();
        found.ifPresent(v -> System.out.println("found " + v));   // found 7
        // Edge: firstBig.get() would throw NoSuchElementException - prefer orElse/ifPresent/orElseThrow.
    }
}
