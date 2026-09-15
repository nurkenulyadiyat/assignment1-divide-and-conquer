package daa;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Runs all algorithms on several input sizes and input types,
 * measures time (System.nanoTime), max recursion depth and comparisons,
 * and saves the averaged results to a CSV file.
 */
public class Experiment {
    public static final int[] SIZES = {1_000, 2_000, 5_000, 10_000, 20_000, 50_000, 100_000, 200_000, 500_000, 1_000_000};
    public static final String[] INPUT_TYPES = {"random", "sorted", "reverse", "duplicates"};

    private static final int WARMUP_RUNS = 3;
    private static final int MEASURED_RUNS = 7;
    private static final int BRUTE_FORCE_MAX_N = 20_000;

    private final Random random = new Random(42);
    private final List<String> rows = new ArrayList<>();

    /** One measured result (averaged over MEASURED_RUNS). */
    private record Result(double timeMs, int maxDepth, long comparisons) { }

    public void runAll(Path csvFile) throws IOException {
        rows.clear();
        warmUpJvm();
        for (int n : SIZES) {
            for (String type : INPUT_TYPES) {
                int[] base = generateArray(n, type);
                record("MergeSort", type, n, measureMergeSort(base));
                record("QuickSort", type, n, measureQuickSort(base));
                record("DeterministicSelect", type, n, measureSelect(base));
            }
            Point[] points = generatePoints(n);
            record("ClosestPair", "random", n, measureClosestPair(points));
            if (n <= BRUTE_FORCE_MAX_N) {
                record("ClosestPairBruteForce", "random", n, measureBruteForce(points));
            }
        }
        save(csvFile);
    }

    /** Runs every algorithm a few times before measuring so that the JIT has compiled the hot code. */
    private void warmUpJvm() {
        MergeSorter merge = new MergeSorter();
        QuickSorter quick = new QuickSorter();
        DeterministicSelector selector = new DeterministicSelector();
        ClosestPairSolver solver = new ClosestPairSolver();
        for (int i = 0; i < 10; i++) {
            for (String type : INPUT_TYPES) {
                int[] a = generateArray(50_000, type);
                merge.sort(a.clone());
                quick.sort(a.clone());
                selector.select(a.clone(), a.length / 2);
            }
            solver.solve(generatePoints(50_000));
            ClosestPairSolver.bruteForce(generatePoints(2_000));
        }
    }

    private Result measureMergeSort(int[] base) {
        MergeSorter sorter = new MergeSorter();
        return measure(() -> {
            int[] a = base.clone();
            long start = System.nanoTime();
            sorter.sort(a);
            long time = System.nanoTime() - start;
            return new Result(time / 1e6, sorter.getMaxDepth(), sorter.getComparisons());
        }, MEASURED_RUNS);
    }

    private Result measureQuickSort(int[] base) {
        QuickSorter sorter = new QuickSorter();
        return measure(() -> {
            int[] a = base.clone();
            long start = System.nanoTime();
            sorter.sort(a);
            long time = System.nanoTime() - start;
            return new Result(time / 1e6, sorter.getMaxDepth(), sorter.getComparisons());
        }, MEASURED_RUNS);
    }

    private Result measureSelect(int[] base) {
        DeterministicSelector selector = new DeterministicSelector();
        int k = base.length / 2;                       // median
        return measure(() -> {
            int[] a = base.clone();
            long start = System.nanoTime();
            selector.select(a, k);
            long time = System.nanoTime() - start;
            return new Result(time / 1e6, selector.getMaxDepth(), selector.getComparisons());
        }, MEASURED_RUNS);
    }

    private Result measureClosestPair(Point[] points) {
        ClosestPairSolver solver = new ClosestPairSolver();
        return measure(() -> {
            long start = System.nanoTime();
            solver.solve(points);
            long time = System.nanoTime() - start;
            return new Result(time / 1e6, solver.getMaxDepth(), solver.getDistanceChecks());
        }, MEASURED_RUNS);
    }

    private Result measureBruteForce(Point[] points) {
        long n = points.length;
        return measure(() -> {
            long start = System.nanoTime();
            ClosestPairSolver.bruteForce(points);
            long time = System.nanoTime() - start;
            return new Result(time / 1e6, 0, n * (n - 1) / 2);
        }, 1);
    }

    /** Warm-up runs (not recorded) let the JIT compile the code, then the measured runs are averaged. */
    private Result measure(java.util.function.Supplier<Result> run, int measuredRuns) {
        for (int i = 0; i < WARMUP_RUNS; i++) {
            run.get();
        }
        double totalTime = 0;
        int depth = 0;
        long comparisons = 0;
        for (int i = 0; i < measuredRuns; i++) {
            Result r = run.get();
            totalTime += r.timeMs();
            depth = Math.max(depth, r.maxDepth());
            comparisons += r.comparisons();
        }
        return new Result(totalTime / measuredRuns, depth, comparisons / measuredRuns);
    }

    private void record(String algorithm, String type, int n, Result r) {
        String row = String.format(Locale.US, "%s,%s,%d,%s,%.4f,%d,%d",
                algorithm, type, n, sizeCategory(n), r.timeMs(), r.maxDepth(), r.comparisons());
        rows.add(row);
        System.out.printf(Locale.US, "%-22s %-11s n=%-7d time=%10.3f ms  depth=%3d  comparisons=%d%n",
                algorithm, type, n, r.timeMs(), r.maxDepth(), r.comparisons());
    }

    private void save(Path csvFile) throws IOException {
        if (csvFile.getParent() != null) {
            Files.createDirectories(csvFile.getParent());
        }
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(csvFile))) {
            out.println("algorithm,input_type,n,size_category,avg_time_ms,max_depth,comparisons");
            rows.forEach(out::println);
        }
    }

    static String sizeCategory(int n) {
        if (n <= 2_000) {
            return "small";
        }
        return n <= 20_000 ? "medium" : "large";
    }

    // ---------- input generators ----------

    public int[] generateArray(int n, String type) {
        int[] a = new int[n];
        switch (type) {
            case "random" -> {
                for (int i = 0; i < n; i++) {
                    a[i] = random.nextInt();
                }
            }
            case "sorted" -> {
                for (int i = 0; i < n; i++) {
                    a[i] = i;
                }
            }
            case "reverse" -> {
                for (int i = 0; i < n; i++) {
                    a[i] = n - i;
                }
            }
            case "duplicates" -> {
                for (int i = 0; i < n; i++) {
                    a[i] = random.nextInt(10);      // only 10 distinct values
                }
            }
            default -> throw new IllegalArgumentException("unknown input type: " + type);
        }
        return a;
    }

    public Point[] generatePoints(int n) {
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            points[i] = new Point(random.nextDouble() * 1_000_000, random.nextDouble() * 1_000_000);
        }
        return points;
    }

    /** Small helper used by Main for a quick sanity check. */
    public static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i - 1] > a[i]) {
                return false;
            }
        }
        return true;
    }

    static String preview(int[] a) {
        return a.length <= 20 ? Arrays.toString(a) : Arrays.toString(Arrays.copyOf(a, 20)) + "...";
    }
}
