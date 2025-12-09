import algorithm.*;
import io.DataExporter;
import model.*;
import model.SharedMapConfig;
import model.SharedMapConfig.MapConfig;
import algorithm.MachineLearnedHeuristic;
import algorithm.HeuristicType;

public class MLBenchmarkRunner {

    // Config: Same settings as main benchmark for fair comparison
    private static final int SIZE_START = 20;
    private static final int SIZE_END = 60;
    private static final int SIZE_STEP = 10;
    private static final int ITERATIONS_PER_CONFIG = 5;

    public void runBenchmarks() {
        System.out.println("=================================================");
        System.out.println("   ML HEURISTIC VALIDATION BENCHMARK");
        System.out.println("   Comparing ML Heuristic vs Ground Truth");
        System.out.println("=================================================");
        
        // Load maps from disk (if they exist from a previous BenchmarkRunner run)
        SharedMapConfig.loadFromFile();
        
        // Check if maps have been pre-generated
        if (!SharedMapConfig.areMapsGenerated()) {
            System.out.println("\n⚠️  WARNING: No pre-generated maps found!");
            System.out.println("   Please run BenchmarkRunner first to generate shared maps.");
            System.out.println("   Falling back to random map generation...\n");
        } else {
            System.out.println("✓ Using " + SharedMapConfig.getMapCount() + " pre-generated maps from BenchmarkRunner");
        }

        warmUp();
        DataExporter exporter = DataExporter.getInstance();

        // 1. Discover and load all available ML models
        java.util.List<java.util.Map.Entry<String, Heuristic>> mlHeuristics = new java.util.ArrayList<>();
        
        // Try to load MLP first
        try {
            mlHeuristics.add(new java.util.AbstractMap.SimpleEntry<>("MLP", new algorithm.MLPHeuristic()));
            System.out.println("✓ Found MLP (Neural Network) model");
        } catch (Exception e) {
            System.out.println("  ℹ MLP model not available: " + e.getMessage());
        }
        
        // Try to load all linear models
        String[] linearModels = {"LinearRegression", "Ridge", "Lasso", "ElasticNet", "Polynomial2"};
        for (String modelName : linearModels) {
            String configFile = "ml_weights_" + modelName.toLowerCase() + ".properties";
            try {
                java.io.File f = new java.io.File(configFile);
                if (f.exists()) {
                    mlHeuristics.add(new java.util.AbstractMap.SimpleEntry<>(modelName, 
                        new MachineLearnedHeuristic(configFile)));
                    System.out.println("✓ Found " + modelName + " model");
                }
            } catch (Exception e) {
                // Skip if file doesn't exist or can't load
            }
        }
        
        // Try default linear model as fallback
        if (mlHeuristics.isEmpty()) {
            try {
                mlHeuristics.add(new java.util.AbstractMap.SimpleEntry<>("MachineLearned", 
                    new MachineLearnedHeuristic()));
                System.out.println("✓ Using default MachineLearned model");
            } catch (Exception e) {
                System.err.println("⚠️  WARNING: No ML models found! Cannot run ML benchmarks.");
                return;
            }
        }
        
        System.out.println("\n📊 Testing " + mlHeuristics.size() + " ML heuristic(s) on shared maps");
        System.out.print("   Models: ");
        for (int i = 0; i < mlHeuristics.size(); i++) {
            if (i > 0) System.out.print(", ");
            System.out.print(mlHeuristics.get(i).getKey());
        }
        System.out.println("\n");

        // 2. Load Standard Heuristic (for Ground Truth/Baseline)
        // CRITICAL: Use SCALED_MANHATTAN to match BenchmarkRunner's ground truth
        Heuristic standardHeuristic = HeuristicType.SCALED_MANHATTAN;
        
        // Track statistics
        int totalMaps = 0;
        java.util.Map<String, Integer> successCount = new java.util.HashMap<>();

        for (int size = SIZE_START; size <= SIZE_END; size += SIZE_STEP) {
            for (Difficulty diff : Difficulty.values()) {

                System.out.printf("\n>>> CONFIG: Size=%d | Difficulty=%s\n", size, diff.name());

                for (int i = 1; i <= ITERATIONS_PER_CONFIG; i++) {
                    System.out.printf("   [Map %d/%d] ", i, ITERATIONS_PER_CONFIG);

                    // --- STEP 1: Use Pre-generated Map or Generate New One ---
                    MapConfig mapConfig = SharedMapConfig.getMapConfig(size, diff, i);
                    GridMap map;
                    String mapId;
                    double trueOptimalCost;
                    SolverResult groundTruth = null;
                    
                    if (mapConfig != null) {
                        // Use stored map configuration
                        map = SharedMapConfig.generateMap(mapConfig);
                        mapId = mapConfig.mapId;
                        trueOptimalCost = mapConfig.optimalCost;
                        System.out.print("(Using stored map) ");
                    } else {
                        // Fallback: Generate new map if not stored
                        System.out.print("(Generating new map) ");
                        boolean validMap = false;
                        map = null;

                        while (!validMap) {
                            map = new GridMap();
                            map.generateRandom(size, size, diff);

                            AStar validator = new AStar();
                            groundTruth = validator.solve(map, standardHeuristic);

                            if (groundTruth.isSuccess) {
                                validMap = true;
                            }
                        }
                        
                        mapId = java.util.UUID.randomUUID().toString().substring(0, 8);
                        trueOptimalCost = groundTruth.totalCost;
                    }
                    
                    // Safety check
                    if (map == null) {
                        System.out.println("ERROR: Failed to generate map. Skipping...");
                        continue;
                    }

                    // Note: We are not saving training data here, just benchmarking results.

                    // Track maps processed
                    totalMaps++;
                    
                    // --- STEP 2: Test ALL ML Heuristics on this map ---
                    for (java.util.Map.Entry<String, Heuristic> mlEntry : mlHeuristics) {
                        String heuristicType = mlEntry.getKey();
                        Heuristic mlHeuristic = mlEntry.getValue();
                        
                        // Get baseline for comparison (only if we generated a new map)
                        int baselineExpanded = 0;
                        if (mapConfig == null && mlEntry == mlHeuristics.get(0)) {
                            // Only calculate baseline once per map
                            // GC control: Reduce variance from garbage collection
                            System.gc();
                            try { Thread.sleep(50); } catch (InterruptedException e) {}
                            
                            AStar baselineSolver = new AStar();
                            SolverResult baseline = baselineSolver.solve(map, standardHeuristic);
                            baselineExpanded = baseline.nodesExpanded;
                        }
                        
                        // Run A* with this ML Heuristic
                        // GC control: Reduce variance from garbage collection
                        System.gc();
                        try { Thread.sleep(50); } catch (InterruptedException e) {}
                        
                        AStar aStar = new AStar();
                        SolverResult mlResult = aStar.solve(map, mlHeuristic);
                        
                        // Track success statistics
                        successCount.put(heuristicType, successCount.getOrDefault(heuristicType, 0) + (mlResult.isSuccess ? 1 : 0));
                        
                        // Determine actual status for A*
                        String aStarStatus = mlResult.isSuccess ? "SUCCESS" : "FAILED";
                        
                        // Note: Maps from SharedMapConfig are already validated as solvable in BenchmarkRunner.
                        // If ML heuristic fails on a stored map, it's because the heuristic is suboptimal/inadmissible,
                        // not because the map is invalid.
                        
                        // Only print output for first heuristic to avoid clutter
                        if (mlEntry == mlHeuristics.get(0)) {
                            System.out.printf("GT Cost: %.1f | ML Cost: %.1f | Expanded: %d", 
                                    trueOptimalCost, mlResult.totalCost, mlResult.nodesExpanded);
                            if (baselineExpanded > 0) {
                                System.out.printf(" (Standard was %d)", baselineExpanded);
                            }
                            System.out.println();
                        }

                        exporter.writeBenchmarkRecord(
                                mapId,
                                "AStar",
                                heuristicType, // ML model name
                                String.valueOf(size),
                                diff.name(),
                                -1, // No memory limit for A*
                                mlResult.isSuccess,
                                aStarStatus, // Use actual status
                                mlResult.totalCost,
                                trueOptimalCost, // Key comparison column
                                mlResult.nodesExpanded,
                                0,
                                mlResult.timeElapsedMs * 1_000_000L, // Convert ms to ns (consistent with BenchmarkRunner)
                                mlResult.memoryUsed
                        );

                        // --- STEP 3: Run SMA* with this ML Heuristic ---
                        // We test if ML helps SMA* find paths with less memory churn
                        
                        // FIXED: Use size-based timeout instead of heuristic-dependent timeout
                        // This ensures fair comparison across all heuristics
                        // Formula: size² * 10ms, with min 2s and max 30s
                        long smaTimeout = Math.max(2000, Math.min(30000, size * size * 10L));
                        int minPath = (mlResult.path != null) ? mlResult.path.size() : 0;

                        // FIXED: Use size-based memory limit instead of A*-dependent limit
                        // This ensures fair comparison across all heuristics
                        // Test at 50% of map area (size² * 0.5), with minimum of path length + 5
                        int memLimit = Math.max(minPath + 5, (int)(size * size * 0.5));

                        // GC control: Reduce variance from garbage collection
                        System.gc();
                        try { Thread.sleep(50); } catch (InterruptedException e) {}
                        
                        SMAStar sma = new SMAStar(memLimit);
                        SolverResult smaRes = sma.solve(map, mlHeuristic, smaTimeout);

                        // Determine actual status for SMA*
                        String smaStatus;
                        if (smaRes.isSuccess) {
                            smaStatus = "SUCCESS";
                        } else if (smaRes.timeElapsedMs >= smaTimeout) {
                            smaStatus = "TIMEOUT";
                        } else {
                            smaStatus = "EXHAUSTED";
                        }

                        exporter.writeBenchmarkRecord(
                                mapId,
                                "SMAStar",
                                heuristicType, // ML model name
                                String.valueOf(size),
                                diff.name(),
                                memLimit,
                                smaRes.isSuccess,
                                smaStatus,
                                smaRes.totalCost,
                                trueOptimalCost,
                                smaRes.nodesExpanded,
                                smaRes.prunedNodes,
                                smaRes.timeElapsedMs * 1_000_000L, // Convert ms to ns (consistent with BenchmarkRunner)
                                smaRes.memoryUsed
                        );
                    }
                }
            }
        }
        
        // Final summary
        System.out.println("\n" + "=".repeat(70));
        System.out.println("   ML BENCHMARK SUMMARY");
        System.out.println("=".repeat(70));
        System.out.println("Total maps tested: " + totalMaps);
        System.out.println("Models tested: " + mlHeuristics.size());
        System.out.println("\nSuccess rates per model (A* runs):");
        for (java.util.Map.Entry<String, Heuristic> mlEntry : mlHeuristics) {
            String modelName = mlEntry.getKey();
            int successes = successCount.getOrDefault(modelName, 0);
            double successRate = totalMaps > 0 ? (100.0 * successes / totalMaps) : 0;
            System.out.printf("  %-20s: %d/%d (%.1f%%)\n", modelName, successes, totalMaps, successRate);
        }
        System.out.println("=".repeat(70));
        System.out.println("✅ ML Benchmark Complete. Check final_output/benchmark_results.csv for detailed results.");
    }

    private void warmUp() {
        System.out.print("Warming up JVM (50 iterations for JIT compilation)...");
        // Run multiple iterations to allow JIT compiler to optimize hot code paths
        for (int i = 0; i < 50; i++) {
            GridMap map = new GridMap();
            map.generateRandom(20, 20, Difficulty.EASY);
            new AStar().solve(map, HeuristicType.SCALED_MANHATTAN);
        }
        System.out.println(" Done.");
    }
}