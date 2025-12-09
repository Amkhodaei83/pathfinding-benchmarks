

import algorithm.*;
import io.DataExporter;
import model.*;
import model.SharedMapConfig;

import java.util.Random;

public class BenchmarkRunner {

    // Config
    private static final int SIZE_START = 20;
    private static final int SIZE_END = 60;
    private static final int SIZE_STEP = 10;
    private static final int ITERATIONS_PER_CONFIG = 5;
    
    // Seed for generating deterministic map seeds
    private static final long MASTER_SEED = 42L;

    public void runBenchmarks() {
        System.out.println("=== Starting Comprehensive Heuristic Benchmark Suite ===");
        System.out.println("Heuristics loaded: " + HeuristicType.values().length);
        
        // Clear any previously stored maps
        SharedMapConfig.clear();

        warmUp();
        DataExporter exporter = DataExporter.getInstance();
        
        // Seed generator for deterministic map seed generation
        Random seedGenerator = new Random(MASTER_SEED);

        for (int size = SIZE_START; size <= SIZE_END; size += SIZE_STEP) {
            for (Difficulty diff : Difficulty.values()) {

                System.out.printf("\n>>> CONFIG: Size=%d | Difficulty=%s\n", size, diff.name());

                for (int i = 1; i <= ITERATIONS_PER_CONFIG; i++) {
                    System.out.printf("   [Map %d/%d] Generating... ", i, ITERATIONS_PER_CONFIG);

                    // --- STEP 1: Generate Map & Establish Ground Truth ---
                    GridMap map = null;
                    SolverResult groundTruth = null;
                    boolean validMap = false;
                    int attempts = 0;
                    long mapSeed = 0;
                    String mapId = null;

                    while (!validMap && attempts < 500) {
                        attempts++;
                        // Generate a deterministic seed based on map parameters
                        mapSeed = seedGenerator.nextLong();
                        map = new GridMap();
                        map.generateRandom(size, size, diff, mapSeed);

                        AStar validator = new AStar();
                        // Use SCALED_MANHATTAN for the definitive 'True Cost'
                        groundTruth = validator.solve(map, HeuristicType.SCALED_MANHATTAN);

                        if (groundTruth.isSuccess) {
                            validMap = true;
                            // Generate deterministic mapId based on seed
                            mapId = String.format("%08x", mapSeed).substring(0, 8);
                        }
                    }

                    if (!validMap) {
                        System.out.println("SKIPPED (No valid map).");
                        continue;
                    }

                    double trueOptimalCost = groundTruth.totalCost;
                    System.out.println("GT Cost: " + (int)trueOptimalCost + " (Seed: " + mapSeed + ")");
                    
                    // --- STORE MAP CONFIGURATION FOR SHARED USE ---
                    SharedMapConfig.storeMapConfig(size, diff, i, mapSeed, mapId, trueOptimalCost);

                    // --- FIX IS HERE: Use appendMLData instead of writeTrainingData ---
                    // This calculates Density, Euclidean, etc., automatically
                    exporter.appendMLData(
                            mapId,
                            map,
                            map.getStartNode(),
                            map.getGoalNode(),
                            trueOptimalCost
                    );

                    // --- STEP 2: Heuristic Loop ---
                    for (HeuristicType hType : HeuristicType.values()) {

                        // A. Run A* with this Heuristic
                        // GC control: Reduce variance from garbage collection
                        System.gc();
                        try { Thread.sleep(50); } catch (InterruptedException e) {}
                        
                        AStar aStar = new AStar();
                        SolverResult aStarRes = aStar.solve(map, hType);

                        // Determine actual status for A*
                        String aStarStatus = aStarRes.isSuccess ? "SUCCESS" : "FAILED";

                        exporter.writeBenchmarkRecord(
                                mapId, "AStar", hType.name(),
                                String.valueOf(size), diff.name(), -1,
                                aStarRes.isSuccess, aStarStatus, // Use actual status
                                aStarRes.totalCost, trueOptimalCost,
                                aStarRes.nodesExpanded, 0,
                                aStarRes.timeElapsedMs * 1_000_000L, // Convert ms to ns
                                aStarRes.memoryUsed
                        );

                        // B. Run SMA* Curve for this Heuristic
                        // FIXED: Use size-based timeout instead of heuristic-dependent timeout
                        // This ensures fair comparison across all heuristics
                        // Formula: size² * 10ms, with min 2s and max 30s
                        long smaTimeout = Math.max(2000, Math.min(30000, size * size * 10L));

                        int startNodes = Math.max(1, aStarRes.nodesExpanded);
                        int minPath = (aStarRes.path != null) ? aStarRes.path.size() : 0;

                        // Memory Loop (10% -> 100%)
                        for (int percent = 10; percent <= 100; percent += 10) {
                            double fraction = percent / 100.0;
                            int memLimit = (int) (startNodes * fraction);
                            if (memLimit < minPath + 5) memLimit = minPath + 5;

                            // GC control: Reduce variance from garbage collection
                            System.gc();
                            try { Thread.sleep(50); } catch (InterruptedException e) {}
                            
                            SMAStar sma = new SMAStar(memLimit);
                            SolverResult smaRes = sma.solve(map, hType, smaTimeout);

                            // Determine actual status
                            String status;
                            if (smaRes.isSuccess) {
                                status = "SUCCESS";
                            } else if (smaRes.timeElapsedMs >= smaTimeout) {
                                status = "TIMEOUT";
                            } else {
                                status = "EXHAUSTED";
                            }

                            exporter.writeBenchmarkRecord(
                                    mapId, "SMAStar", hType.name(),
                                    String.valueOf(size), diff.name(), memLimit,
                                    smaRes.isSuccess, status,
                                    smaRes.totalCost, trueOptimalCost,
                                    smaRes.nodesExpanded, smaRes.prunedNodes,
                                    smaRes.timeElapsedMs * 1_000_000L, // Convert ms to ns
                                    smaRes.memoryUsed
                            );

                            if (smaRes.isSuccess && smaRes.prunedNodes == 0) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        // Mark that maps have been generated and ensure they're saved to disk
        SharedMapConfig.setMapsGenerated(true);
        SharedMapConfig.saveToFile(); // Explicitly save to ensure persistence
        int storedCount = SharedMapConfig.getMapCount();
        System.out.println("\n=== Heuristic Benchmark Complete ===");
        System.out.println("✅ Stored " + storedCount + " map configurations for shared use.");
        System.out.println("✅ Map configurations saved to: final_output/map_configs.txt");
        System.out.println("✅ Results saved to: final_output/benchmark_results.csv");
        System.out.println("✅ Training data saved to: final_output/training_data.jsonl");
        
        if (storedCount == 0) {
            System.err.println("⚠️  WARNING: No maps were stored! MLBenchmarkRunner may not work correctly.");
        } else {
            System.out.println("✅ You can now run MLBenchmarkRunner (Option 4) to use these maps.");
        }
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