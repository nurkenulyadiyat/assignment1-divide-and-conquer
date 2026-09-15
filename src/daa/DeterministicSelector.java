package daa;

/**
 * Deterministic Select (Median-of-Medians).
 * Groups of 5, median-of-medians pivot, in-place 3-way partition,
 * continues only into the part that contains the k-th element.
 * Worst-case time: Theta(n).
 */
public class DeterministicSelector {
    private static final int GROUP = 5;

    private long comparisons;
    private int maxDepth;

    /**
     * Returns the k-th smallest element (0-based).
     * The array is reordered in place; pass a copy if the order matters.
     */
    public int select(int[] a, int k) {
        if (a == null || a.length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        if (k < 0 || k >= a.length) {
            throw new IllegalArgumentException("k out of range: " + k);
        }
        comparisons = 0;
        maxDepth = 0;
        return select(a, 0, a.length - 1, k, 1);
    }

    private int select(int[] a, int lo, int hi, int k, int depth) {
        maxDepth = Math.max(maxDepth, depth);
        while (true) {
            if (hi - lo + 1 <= GROUP) {
                insertionSort(a, lo, hi);
                return a[k];
            }

            int pivot = medianOfMedians(a, lo, hi, depth);

            // 3-way partition around the pivot value (handles duplicates)
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

            // Only the part that contains index k is processed further
            if (k < lt) {
                hi = lt - 1;
            } else if (k > gt) {
                lo = gt + 1;
            } else {
                return pivot;
            }
        }
    }

    /** Sorts each group of 5, moves the group medians to the front and selects their median recursively. */
    private int medianOfMedians(int[] a, int lo, int hi, int depth) {
        int m = lo;
        for (int start = lo; start <= hi; start += GROUP) {
            int end = Math.min(start + GROUP - 1, hi);
            insertionSort(a, start, end);
            swap(a, m++, (start + end) >>> 1);
        }
        int count = m - lo;
        return select(a, lo, m - 1, lo + (count - 1) / 2, depth + 1);
    }

    private void insertionSort(int[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= lo) {
                comparisons++;
                if (a[j] <= key) {
                    break;
                }
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    public long getComparisons() {
        return comparisons;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
