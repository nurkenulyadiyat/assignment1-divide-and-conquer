# Assignment 1 — Divide-and-Conquer Algorithm Analysis

## How to run

```bash
mvn test                                  # run JUnit 5 tests
mvn package -DskipTests                   # compile
java -cp target/classes daa.Main          # demo + experiments -> results/results.csv
python docs/plots/plot_results.py         # build plots (needs matplotlib)
```

---

## A. Project Overview

The goal is to implement four divide-and-conquer algorithms, analyse them with recurrences
and compare the theory with real measurements.

Implemented algorithms: **MergeSort**, **randomized QuickSort**, **Deterministic Select (Median-of-Medians)**,
**Closest Pair of Points**.

Measured metrics: execution time (`System.nanoTime()`, average of 7 runs after warm-up),
maximum recursion depth, number of comparisons (additional metric, saved in `results/results.csv`).
Input sizes: 1,000 – 1,000,000. Input types: random, sorted, reverse-sorted, duplicate-heavy.

---

## B. Algorithm Analysis

### MergeSort
Split the array in half, sort both halves recursively, merge them in linear time.
One auxiliary buffer is reused for all merges; parts of size ≤ 16 are sorted with Insertion Sort.

* Recurrence: T(n) = 2T(n/2) + Θ(n). Master Theorem, a = 2, b = 2, f(n) = Θ(n) = Θ(n^(log_b a)) → case 2.
* Time: **Θ(n log n)**. Space: Θ(n) buffer + O(log n) stack.

### QuickSort
Pick a random pivot, partition in place into `< pivot`, `= pivot`, `> pivot`,
recurse into the smaller part and continue with a loop on the larger part.

* Average: T(n) = 2T(n/2) + Θ(n) → **Θ(n log n)**. Even an uneven split like T(n/10) + T(9n/10) + Θ(n)
  gives Θ(n log n) by Akra–Bazzi intuition (1/10 + 9/10 = 1, so p = 1).
* Worst case: T(n) = T(n − 1) + Θ(n) → **O(n²)** (very unlikely with a random pivot).
* Space: O(log n) stack, because the recursive call always gets at most half of the elements.

### Deterministic Select (Median-of-Medians)
Split into groups of 5, take the median of each group, recursively find the median of these medians and use it
as the pivot. Partition in place and continue only in the part that contains the k-th element.

* The pivot has at least ≈ 3n/10 elements on each side, so the remaining part has ≤ 7n/10 elements.
* Recurrence: T(n) ≤ T(n/5) + T(7n/10) + Θ(n). Master Theorem does not apply directly; by Akra–Bazzi intuition
  1/5 + 7/10 = 0.9 < 1, so the Θ(n) term dominates.
* Time: **Θ(n)** in the worst case. Space: O(log n) stack.

### Closest Pair of Points
Sort points by x. Split in the middle, solve both halves, d = min(dLeft, dRight).
Merge the halves by y, take the strip of points with |x − x_mid| < d and compare each point
only with the next points whose y-difference is < d (at most 7 of them).

* Recurrence: T(n) = 2T(n/2) + Θ(n) (merge + strip). Master Theorem case 2.
* Time: **Θ(n log n)** (plus the initial Θ(n log n) sort). Space: Θ(n).

---

## C. Experimental Results

Measured on my laptop (Windows, run from IntelliJ IDEA). Full data: `results/results.csv`.

### Execution time, ms (random input)

| n | MergeSort | QuickSort | DeterministicSelect | ClosestPair |
|---:|---:|---:|---:|---:|
| 1,000 | 0.033 | 0.060 | 0.026 | 0.216 |
| 2,000 | 0.081 | 0.131 | 0.056 | 0.541 |
| 5,000 | 0.251 | 0.335 | 0.140 | 1.462 |
| 10,000 | 0.553 | 0.721 | 0.303 | 3.929 |
| 20,000 | 1.134 | 1.541 | 0.612 | 6.091 |
| 50,000 | 3.008 | 4.014 | 1.433 | 18.4 |
| 100,000 | 6.666 | 8.762 | 2.840 | 36.5 |
| 200,000 | 14.0 | 19.2 | 5.730 | 77.8 |
| 500,000 | 38.0 | 49.4 | 14.1 | 262.2 |
| 1,000,000 | 78.8 | 103.1 | 30.3 | 569.2 |

### Recursion depth (random input)

| n | log2(n) | MergeSort | QuickSort | DeterministicSelect | ClosestPair |
|---:|---:|---:|---:|---:|---:|
| 1,000 | 10.0 | 7 | 8 | 5 | 10 |
| 2,000 | 11.0 | 8 | 9 | 5 | 11 |
| 5,000 | 12.3 | 10 | 10 | 6 | 12 |
| 10,000 | 13.3 | 11 | 10 | 6 | 13 |
| 20,000 | 14.3 | 12 | 11 | 7 | 14 |
| 50,000 | 15.6 | 13 | 12 | 7 | 16 |
| 100,000 | 16.6 | 14 | 13 | 8 | 17 |
| 200,000 | 17.6 | 15 | 13 | 8 | 18 |
| 500,000 | 18.9 | 16 | 14 | 9 | 19 |
| 1,000,000 | 19.9 | 17 | 15 | 9 | 20 |

### Different input sizes and types: time, ms / max depth

| Size | n | Input type | MergeSort | QuickSort | DeterministicSelect |
|---|---:|---|---:|---:|---:|
| small | 1,000 | random | 0.033 / 7 | 0.060 / 8 | 0.026 / 5 |
| small | 1,000 | sorted | 0.009 / 7 | 0.043 / 8 | 0.016 / 5 |
| small | 1,000 | reverse | 0.019 / 7 | 0.045 / 7 | 0.017 / 5 |
| small | 1,000 | duplicates | 0.019 / 7 | 0.015 / 4 | 0.008 / 5 |
| medium | 20,000 | random | 1.134 / 12 | 1.541 / 11 | 0.612 / 7 |
| medium | 20,000 | sorted | 0.201 / 12 | 1.035 / 12 | 0.361 / 7 |
| medium | 20,000 | reverse | 0.404 / 12 | 1.070 / 11 | 0.385 / 7 |
| medium | 20,000 | duplicates | 0.653 / 12 | 0.303 / 4 | 0.538 / 7 |
| large | 1,000,000 | random | 78.8 / 17 | 103.1 / 15 | 30.3 / 9 |
| large | 1,000,000 | sorted | 14.4 / 17 | 68.2 / 14 | 15.8 / 9 |
| large | 1,000,000 | reverse | 32.3 / 17 | 67.4 / 15 | 17.6 / 9 |
| large | 1,000,000 | duplicates | 40.5 / 17 | 12.1 / 4 | 12.1 / 9 |

### Plots

![Time vs n](docs/plots/time_vs_n.png)

![Recursion depth vs n](docs/plots/depth_vs_n.png)

---

## D. Discussion

**Do the results match theoretical complexity?**
Yes. On random input, the number of comparisons stays ≈ 1.0 · n log2 n for MergeSort, ≈ 1.9 · n log2 n for QuickSort
and ≈ 10 · n for Select for all sizes from 1,000 to 1,000,000 (see `results.csv`), which matches Θ(n log n) and Θ(n).
On the time plot the lines grow almost linearly on the log–log scale. The recursion depth grows like log n.

**How does input structure affect performance?**
MergeSort is fastest on sorted input (14.4 ms vs 78.8 ms on random, n = 1,000,000) because merges stop early and
branches are predictable. QuickSort with a random pivot does not become O(n²) on sorted input. On duplicate-heavy input
the 3-way partition removes all equal elements at once, so QuickSort is much faster (12.1 ms vs 103.1 ms) and its depth is only 4.

**Why does smaller-first recursion help QuickSort?**
The recursive call always gets the smaller part (≤ n/2 elements), so the stack depth is at most log2 n + 1 for any
pivots. The larger part is handled by a loop and uses no extra stack. In the experiments the depth was 15 for
n = 1,000,000.

**Why does Median-of-Medians guarantee O(n)?**
The pivot always removes at least ≈ 30% of the elements. The two recursive sizes add up to 0.9n < n, so the work is a
geometric series n + 0.9n + 0.81n + … ≤ 10n.

**Why is divide-and-conquer Closest Pair faster than O(n²) for large inputs?**
Brute force compares all pairs: ≈ 200 million distance checks for n = 20,000 (≈ 265 ms). The divide-and-conquer
version compares only neighbours in the strip, ≈ 26 thousand checks (≈ 6 ms). The gap grows with n.

**What practical factors affect performance?**
JIT warm-up (first runs are slow, so warm-up runs are excluded), garbage collection pauses, CPU cache
(`int[]` is faster than `Point[]` of objects), branch prediction, and timer noise for very small inputs.

---

## E. Reflection

I learned that asymptotic complexity describes growth, but constants and input structure strongly affect real speed:
Select is linear but has a large constant, and MergeSort and QuickSort behave differently on sorted and
duplicate-heavy data even though both are Θ(n log n). Counting comparisons was the easiest way to check the theory,
because it does not depend on the JVM.

The main challenges were handling duplicates (solved with a 3-way partition), getting the indices right when moving
group medians in Median-of-Medians, and saving the middle x-coordinate in Closest Pair before the halves are reordered
by y. The first time measurements for small n were noisy until I added a JVM warm-up.

---

## F. Screenshots

Program output:

![Program output part 1](docs/screenshots/program_output_1.png)
![Program output part 2](docs/screenshots/program_output_2.png)

Test results:

![Test results](docs/screenshots/test_results.png)

Results and plots:

![Results CSV](docs/screenshots/results_csv.png)
![Plots](docs/screenshots/plots_overview.png)
