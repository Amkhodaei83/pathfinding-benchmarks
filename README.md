# Pathfinding Benchmarks & Visualizer 🚀

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![Plotly](https://img.shields.io/badge/Plotly-3F4F75?style=for-the-badge&logo=plotly&logoColor=white)

A high-performance benchmarking laboratory designed to analyze, visualize, and compare pathfinding algorithms (**A*** and **SMA***). 

Unlike standard visualizers, this project includes a **Machine Learning module** to generate and test custom heuristics, allowing the AI to "learn" the map topology (mud, traffic, walls) and predict costs better than standard Manhattan distance.

## 🌟 Key Features

### 1. The Algorithms
*   **A* (A-Star):** Standard infinite-memory implementation for optimal pathfinding.
*   **SMA* (Simplified Memory-Bounded A*):** Constrained memory implementation. Demonstrates node pruning and "thrashing" when memory limits are hit.

### 2. The Analytics Dashboard
The project generates detailed CSV logs which are fed into a modern, responsive HTML/JS dashboard (`ml_report.html` & `index.html`).
*   **Efficiency Charts:** Compare Nodes Expanded vs. Path Optimality.
*   **Interactive Graphs:** Built with Plotly.js for zooming and filtering.
*   **ML Insights:** Visualize how the AI heuristic compares to standard mathematical heuristics.
  
   # 🚀 [CLICK HERE TO PLAY WITH THE LIVE DEMO](https://amkhodaei83.github.io/pathfinding-benchmarks/final_output/)

### 3. Machine Learning Integration
*   **Data Generation:** The Java backend generates training datasets from solved maps.
*   **Heuristic Training:** Supports testing Linear Regression and Neural Network heuristics against standard Manhattan/Euclidean formulas.

## 🛠️ Installation & Usage

### Prerequisites
*   Java JDK 11+
*   Modern Web Browser (Chrome/Firefox)

### Running the Java Engine
Compile and run the `Main.java` file. You will see the interactive CLI:

```text
==========================================
    PATHFINDING BENCHMARK SYSTEM v3.0     
==========================================
Select Mode:
  [1] Standard Benchmark (A* vs SMA*)
  [2] Visualize Trace (Single Map Audit)
  [3] Generate Training Data (For Python)
  [4] Test Machine Learned Heuristic (Bonus)
  [0] Exit
```

1.  **Option 1:** Runs massive benchmarks and exports `benchmark_results.csv`.
2.  **Option 2:** visualizes a single complex map (Standard vs SMA* pruning).
3.  **Option 4:** Runs the AI/ML specific benchmarks.

### Viewing the Results
1.  After running a benchmark, open `index.html` or `ml_report.html` in your browser.
2.  If the CSV doesn't load automatically (due to browser security), use the **"📂 Select Data.csv"** button in the dashboard to load your generated results.

## 📂 Project Structure

```bash
├── src/
│   ├── algorithm/     # A* and SMA* implementations
│   ├── model/         # GridMap, Node, Heuristic interfaces
│   ├── io/            # TraceLogger and CSV exporters
│   └── Main.java      # CLI Entry point
├── web/
│   ├── index.html     # Main Analytics Dashboard
│   ├── ml_report.html # AI/ML specific Report
│   ├── style.css      # Dashboard styling
│   └── script.js      # Plotly logic
└── README.md
```

## 🧠 Educational Insight: SMA* Thrashing
This project demonstrates the **Memory Wall** problem. When SMA* hits its memory limit (e.g., 15 nodes in the demo), it must delete the "worst" node to make room for a new one. If it needs that deleted node later, it must regenerate it. This cycle is called "Thrashing," and you can observe it directly in the **Visual Audit** mode.
