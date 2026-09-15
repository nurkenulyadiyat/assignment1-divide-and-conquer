package daa;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;

/**
 * Entry point.
 * 1) Small demo of all four algorithms with correctness checks.
 * 2) Full experiment, results saved to results/results.csv.
 *
 * Usage: java -cp target/classes daa.Main [output.csv]
 */
public class Main {
    public static void main(String[] args) throws Exception {
        demo();

        Path csv = Path.of(args.length > 0 ? args[0] : "results/results.csv");
        System.out.println();
        System.out.println("=== Running experiments (this can take a minute or two) ===");
        new Experiment().runAll(csv);
        System.out.println();
        System.out.println("Results saved to " + csv.toAbsolutePath());
    }

    private static void demo() {
        System.out.println("=== Demo on small inputs ===");
        int[] data = {38, 27, 43, 3, 9, 82, 10, 3, 27, 55, 1, 0, 43, 12, 7, 99, 5, 3, 64, 21};
        int[] expected = data.clone();
        Arrays.sort(expected);
        System.out.println("Input:               " + Arrays.toString(data));

        MergeSorter merge = new MergeSorter();
        int[] a = data.clone();
        merge.sort(a);
        System.out.println("MergeSort:           " + Arrays.toString(a) + "  correct=" + Arrays.equals(a, expected));

        QuickSorter quick = new QuickSorter();
        int[] b = data.clone();
        quick.sort(b);
        System.out.println("QuickSort:           " + Arrays.toString(b) + "  correct=" + Arrays.equals(b, expected));

        DeterministicSelector selector = new DeterministicSelector();
        int k = data.length / 2;
        int kth = selector.select(data.clone(), k);
        System.out.println("Select k=" + k + ":         " + kth + "  correct=" + (kth == expected[k]));

        Experiment generator = new Experiment();
        Point[] points = generator.generatePoints(1_000);
        ClosestPairSolver solver = new ClosestPairSolver();
        double fast = solver.solve(points);
        double slow = ClosestPairSolver.bruteForce(points);
        System.out.printf(Locale.US, "ClosestPair n=1000:  %.6f (brute force %.6f)  correct=%b%n",
                fast, slow, fast == slow);
    }
}
