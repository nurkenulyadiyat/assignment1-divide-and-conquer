package daa;

import java.util.Random;

/**
 * Randomized QuickSort with in-place 3-way partitioning.
 * Recurses on the smaller part and loops over the larger one,
 * so the recursion depth is at most about log2(n).
 * Expected time: O(n log n). Worst case: O(n^2).
 */
public class QuickSorter {
    private final Random random;

    private long comparisons;
    private long swaps;
    private int maxDepth;

    public QuickSorter() {
        this.random = new Random();
    }

    public QuickSorter(long seed) {
        this.random = new Random(seed);
    }

    public void sort(int[] a) {
        comparisons = 0;
        swaps = 0;
        maxDepth = 0;
        if (a == null || a.length < 2) {
            return;
        }
        sort(a, 0, a.length - 1, 1);
    }

    private void sort(int[] a, int lo, int hi, int depth) {
        maxDepth = Math.max(maxDepth, depth);
        while (lo < hi) {
            int pivot = a[lo + random.nextInt(hi - lo + 1)];   // randomized pivot

            // 3-way partition: a[lo..lt-1] < pivot, a[lt..gt] == pivot, a[gt+1..hi] > pivot
            int lt = lo, i = lo, gt = hi;
            while (i <= gt) {
                comparisons++;
                if (a[i] < pivot) {
                    swap(a, lt++, i++);
                } else {
                    comparisons++;
                    if (a[i] > pivot) {
                        swap(a, i, gt--);
                    } else {
                        i++;
                    }
                }
            }

            // Recurse into the smaller side, iterate over the larger side
            if (lt - lo < hi - gt) {
                sort(a, lo, lt - 1, depth + 1);
                lo = gt + 1;
            } else {
                sort(a, gt + 1, hi, depth + 1);
                hi = lt - 1;
            }
        }
    }

    private void swap(int[] a, int i, int j) {
        swaps++;
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getSwaps() {
        return swaps;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
