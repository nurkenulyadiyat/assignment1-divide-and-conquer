package daa;

/**
 * MergeSort with a linear merge, one reusable auxiliary buffer
 * and an Insertion Sort cutoff for small sub-arrays.
 * Time: Theta(n log n). Extra space: Theta(n) buffer + O(log n) stack.
 */
public class MergeSorter {
    static final int CUTOFF = 16;

    private long comparisons;
    private int maxDepth;

    public void sort(int[] a) {
        comparisons = 0;
        maxDepth = 0;
        if (a == null || a.length < 2) {
            return;
        }
        int[] buffer = new int[a.length];      // allocated once, reused by every merge
        sort(a, buffer, 0, a.length - 1, 1);
    }

    private void sort(int[] a, int[] buffer, int lo, int hi, int depth) {
        maxDepth = Math.max(maxDepth, depth);
        if (hi - lo + 1 <= CUTOFF) {
            insertionSort(a, lo, hi);
            return;
        }
        int mid = (lo + hi) >>> 1;
        sort(a, buffer, lo, mid, depth + 1);
        sort(a, buffer, mid + 1, hi, depth + 1);
        merge(a, buffer, lo, mid, hi);
    }

    /** Linear merge of a[lo..mid] and a[mid+1..hi]. */
    private void merge(int[] a, int[] buffer, int lo, int mid, int hi) {
        System.arraycopy(a, lo, buffer, lo, hi - lo + 1);
        int i = lo, j = mid + 1, k = lo;
        while (i <= mid && j <= hi) {
            comparisons++;
            if (buffer[i] <= buffer[j]) {      // <= keeps the sort stable
                a[k++] = buffer[i++];
            } else {
                a[k++] = buffer[j++];
            }
        }
        while (i <= mid) {
            a[k++] = buffer[i++];
        }
        // whatever is left in the right half is already in place
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

    public long getComparisons() {
        return comparisons;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
