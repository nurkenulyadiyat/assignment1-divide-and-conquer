"""Builds the plots for README.md from results/results.csv.
Usage (from the repository root):  python docs/plots/plot_results.py
Requires: matplotlib
"""
import csv
import math
from collections import defaultdict
from pathlib import Path

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

ROOT = Path(__file__).resolve().parents[2]
CSV = ROOT / "results" / "results.csv"
OUT = ROOT / "docs" / "plots"

rows = list(csv.DictReader(open(CSV)))
data = defaultdict(list)            # (algorithm, input_type) -> [(n, time, depth, comparisons)]
for r in rows:
    data[(r["algorithm"], r["input_type"])].append(
        (int(r["n"]), float(r["avg_time_ms"]), int(r["max_depth"]), int(r["comparisons"])))
for v in data.values():
    v.sort()

MAIN = ["MergeSort", "QuickSort", "DeterministicSelect", "ClosestPair"]
MARK = {"MergeSort": "o", "QuickSort": "s", "DeterministicSelect": "^", "ClosestPair": "D",
        "ClosestPairBruteForce": "x"}


def col(series, i):
    return [p[i] for p in series]


# 1. Time vs n (random input)
plt.figure(figsize=(8, 5))
for alg in MAIN:
    s = data[(alg, "random")]
    plt.plot(col(s, 0), col(s, 1), marker=MARK[alg], label=alg)
plt.xscale("log"); plt.yscale("log")
plt.xlabel("n (log scale)"); plt.ylabel("average time, ms (log scale)")
plt.title("Execution time vs n (random input)")
plt.grid(True, which="both", alpha=0.3); plt.legend(); plt.tight_layout()
plt.savefig(OUT / "time_vs_n.png", dpi=130); plt.close()

# 2. Recursion depth vs n (random input) with log2(n) reference
plt.figure(figsize=(8, 5))
for alg in MAIN:
    s = data[(alg, "random")]
    plt.plot(col(s, 0), col(s, 2), marker=MARK[alg], label=alg)
ns = col(data[("MergeSort", "random")], 0)
plt.plot(ns, [math.log2(n) for n in ns], "k--", label="log2(n)")
plt.xscale("log")
plt.xlabel("n (log scale)"); plt.ylabel("max recursion depth")
plt.title("Recursion depth vs n (random input)")
plt.grid(True, which="both", alpha=0.3); plt.legend(); plt.tight_layout()
plt.savefig(OUT / "depth_vs_n.png", dpi=130); plt.close()

print("plots saved to", OUT)
