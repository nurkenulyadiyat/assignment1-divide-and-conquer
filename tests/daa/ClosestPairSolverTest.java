package daa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.Test;

/** Divide-and-conquer result is compared with the O(n^2) brute force for n <= 2000. */
class ClosestPairSolverTest {
    private final ClosestPairSolver solver = new ClosestPairSolver();

    @Test
    void matchesBruteForceOnSmallInputs() {
        Random random = new Random(99);
        for (int t = 0; t < 100; t++) {
            int n = 2 + random.nextInt(1_999);                 // 2..2000
            Point[] points = new Point[n];
            for (int i = 0; i < n; i++) {
                points[i] = new Point(random.nextDouble() * 1000, random.nextDouble() * 1000);
            }
            assertEquals(ClosestPairSolver.bruteForce(points), solver.solve(points), 1e-9, "test " + t);
        }
    }

    @Test
    void duplicatePointsGiveZero() {
        Point[] points = {new Point(1, 1), new Point(5, 5), new Point(1, 1), new Point(9, 0)};
        assertEquals(0.0, solver.solve(points), 1e-12);
    }

    @Test
    void sameXCoordinate() {
        Point[] points = new Point[500];
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point(3, i * 2.5);
        }
        assertEquals(2.5, solver.solve(points), 1e-9);
    }

    @Test
    void integerGridWithManyTies() {
        Random random = new Random(5);
        Point[] points = new Point[1_500];
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point(random.nextInt(60), random.nextInt(60));
        }
        assertEquals(ClosestPairSolver.bruteForce(points), solver.solve(points), 1e-9);
    }

    @Test
    void fewerThanTwoPoints() {
        assertEquals(Double.POSITIVE_INFINITY, solver.solve(new Point[0]));
        assertEquals(Double.POSITIVE_INFINITY, solver.solve(new Point[]{new Point(0, 0)}));
        assertEquals(Double.POSITIVE_INFINITY, solver.solve(null));
    }

    @Test
    void twoPoints() {
        assertEquals(5.0, solver.solve(new Point[]{new Point(0, 0), new Point(3, 4)}), 1e-12);
    }

    @Test
    void largeInputUsesFastVersionOnly() {
        Point[] points = new Experiment().generatePoints(200_000);
        double d = solver.solve(points);
        assertTrue(d >= 0 && d < Double.POSITIVE_INFINITY);
        assertTrue(solver.getMaxDepth() <= 20);               // about log2(n)
    }
}
