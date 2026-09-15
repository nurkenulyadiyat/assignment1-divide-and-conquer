package daa;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Closest Pair of Points, divide and conquer.
 * Points are sorted by x once; during recursion the halves are merged by y,
 * so the strip is already in y-order and every point is compared
 * only with a few neighbours above it. Time: Theta(n log n).
 */
public class ClosestPairSolver {
    private static final int BRUTE_FORCE_LIMIT = 3;

    private long distanceChecks;
    private int maxDepth;

    /** Returns the smallest distance between two points (infinity if fewer than 2 points). */
    public double solve(Point[] points) {
        distanceChecks = 0;
        maxDepth = 0;
        if (points.length < 2) {
            return Double.POSITIVE_INFINITY;
        }
        Point[] byX = points.clone();
        Arrays.sort(byX, Comparator.comparingDouble((Point p) -> p.x).thenComparingDouble(p -> p.y));
        Point[] buffer = new Point[byX.length];
        return solve(byX, buffer, 0, byX.length - 1, 1);
    }

    private double solve(Point[] p, Point[] buffer, int lo, int hi, int depth) {
        maxDepth = Math.max(maxDepth, depth);

        if (hi - lo + 1 <= BRUTE_FORCE_LIMIT) {
            double best = Double.POSITIVE_INFINITY;
            for (int i = lo; i <= hi; i++) {
                for (int j = i + 1; j <= hi; j++) {
                    best = Math.min(best, dist(p[i], p[j]));
                }
            }
            sortByY(p, lo, hi);
            return best;
        }

        int mid = (lo + hi) >>> 1;
        double midX = p[mid].x;          // saved before the halves get reordered by y

        double d = Math.min(
                solve(p, buffer, lo, mid, depth + 1),
                solve(p, buffer, mid + 1, hi, depth + 1));

        mergeByY(p, buffer, lo, mid, hi);

        // Strip: points closer than d to the dividing line, already sorted by y
        int size = 0;
        for (int i = lo; i <= hi; i++) {
            if (Math.abs(p[i].x - midX) < d) {
                buffer[size++] = p[i];
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size && buffer[j].y - buffer[i].y < d; j++) {
                d = Math.min(d, dist(buffer[i], buffer[j]));
            }
        }
        return d;
    }

    private void mergeByY(Point[] p, Point[] buffer, int lo, int mid, int hi) {
        System.arraycopy(p, lo, buffer, lo, hi - lo + 1);
        int i = lo, j = mid + 1, k = lo;
        while (i <= mid && j <= hi) {
            p[k++] = (buffer[i].y <= buffer[j].y) ? buffer[i++] : buffer[j++];
        }
        while (i <= mid) {
            p[k++] = buffer[i++];
        }
        while (j <= hi) {
            p[k++] = buffer[j++];
        }
    }

    private static void sortByY(Point[] p, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++) {
            Point key = p[i];
            int j = i - 1;
            while (j >= lo && p[j].y > key.y) {
                p[j + 1] = p[j];
                j--;
            }
            p[j + 1] = key;
        }
    }

    private double dist(Point a, Point b) {
        distanceChecks++;
        return a.distanceTo(b);
    }

    /** O(n^2) reference solution, used for testing and comparison. */
    public static double bruteForce(Point[] points) {
        double best = Double.POSITIVE_INFINITY;
        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                best = Math.min(best, points[i].distanceTo(points[j]));
            }
        }
        return best;
    }

    public long getDistanceChecks() {
        return distanceChecks;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
