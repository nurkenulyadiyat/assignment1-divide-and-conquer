package daa;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

/** MergeSort and QuickSort are compared with Arrays.sort() on all input types. */
class SortingTest {
    private final Random random = new Random(7);

    private void checkAgainstArraysSort(Consumer<int[]> sorter, int[] input) {
        int[] expected = input.clone();
        Arrays.sort(expected);
        int[] actual = input.clone();
        sorter.accept(actual);
        assertArrayEquals(expected, actual);
    }

    private void runAllCases(Consumer<int[]> sorter) {
        Experiment gen = new Experiment();
        checkAgainstArraysSort(sorter, new int[0]);                 // empty
        checkAgainstArraysSort(sorter, new int[]{42});              // single element
        checkAgainstArraysSort(sorter, new int[]{2, 1});            // two elements
        checkAgainstArraysSort(sorter, new int[]{5, 5, 5, 5, 5});   // all equal
        checkAgainstArraysSort(sorter, new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1});
        for (int n : new int[]{3, 15, 16, 17, 100, 1_000, 10_000}) {
            for (String type : Experiment.INPUT_TYPES) {            // random, sorted, reverse, duplicates
                checkAgainstArraysSort(sorter, gen.generateArray(n, type));
            }
        }
        for (int t = 0; t < 50; t++) {                              // random sizes
            int n = random.nextInt(500);
            int[] a = new int[n];
            for (int i = 0; i < n; i++) {
                a[i] = random.nextInt(50) - 25;
            }
            checkAgainstArraysSort(sorter, a);
        }
    }

    @Test
    void mergeSortMatchesArraysSort() {
        MergeSorter sorter = new MergeSorter();
        runAllCases(sorter::sort);
    }

    @Test
    void quickSortMatchesArraysSort() {
        QuickSorter sorter = new QuickSorter(123);
        runAllCases(sorter::sort);
    }

    @Test
    void nullArrayDoesNotThrow() {
        new MergeSorter().sort(null);
        new QuickSorter().sort(null);
    }

    @Test
    void quickSortRecursionDepthIsLogarithmic() {
        QuickSorter sorter = new QuickSorter(1);
        int n = 100_000;
        for (String type : Experiment.INPUT_TYPES) {
            sorter.sort(new Experiment().generateArray(n, type));
            // smaller-first recursion guarantees depth <= log2(n) + 1
            assertTrue(sorter.getMaxDepth() <= (int) (Math.log(n) / Math.log(2)) + 1,
                    "depth " + sorter.getMaxDepth() + " for " + type);
        }
    }

    @Test
    void mergeSortRecursionDepthIsLogarithmic() {
        MergeSorter sorter = new MergeSorter();
        int n = 100_000;
        sorter.sort(new Experiment().generateArray(n, "random"));
        assertTrue(sorter.getMaxDepth() <= (int) Math.ceil(Math.log(n) / Math.log(2)) + 1);
    }
}
