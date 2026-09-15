package daa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Test;

/** Deterministic Select is compared with Arrays.sort(a)[k]. */
class DeterministicSelectorTest {
    private final DeterministicSelector selector = new DeterministicSelector();

    private int reference(int[] a, int k) {
        int[] copy = a.clone();
        Arrays.sort(copy);
        return copy[k];
    }

    @Test
    void hundredRandomTests() {
        Random random = new Random(2024);
        for (int t = 0; t < 200; t++) {
            int n = 1 + random.nextInt(2_000);
            int[] a = new int[n];
            boolean manyDuplicates = t % 2 == 0;
            for (int i = 0; i < n; i++) {
                a[i] = manyDuplicates ? random.nextInt(20) : random.nextInt();
            }
            int k = random.nextInt(n);
            assertEquals(reference(a, k), selector.select(a.clone(), k), "test " + t + ", n=" + n + ", k=" + k);
        }
    }

    @Test
    void everyKOnSmallArray() {
        int[] a = {9, 1, 8, 2, 7, 3, 6, 4, 5, 5, 0, 11, 10};
        for (int k = 0; k < a.length; k++) {
            assertEquals(reference(a, k), selector.select(a.clone(), k));
        }
    }

    @Test
    void allInputTypes() {
        Experiment gen = new Experiment();
        for (String type : Experiment.INPUT_TYPES) {
            int[] a = gen.generateArray(10_000, type);
            for (int k : new int[]{0, 1, 5_000, 9_998, 9_999}) {
                assertEquals(reference(a, k), selector.select(a.clone(), k), type + " k=" + k);
            }
        }
    }

    @Test
    void singleElement() {
        assertEquals(7, selector.select(new int[]{7}, 0));
    }
}
