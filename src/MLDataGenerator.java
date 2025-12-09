import algorithm.AStar;
import algorithm.ManhattanHeuristic;
import algorithm.SolverResult;
import io.DataExporter;
import model.GridMap;

import java.util.UUID;

public class MLDataGenerator {

    /**
     * Generates a dataset with 20 distinct difficulty levels using Linear Interpolation.
     */
    public void generate(int startSize, int endSize, int stepSize, int samplesPerConfig) {
        System.out.println("=== Starting 20-Level ML Data Generation ===");

        DataExporter exporter = DataExporter.getInstance();
        AStar solver = new AStar();
        ManhattanHeuristic heuristic = new ManhattanHeuristic();

        int globalCounter = 0;
        int totalLevels = 20;

        // 1. Loop Sizes
        for (int size = startSize; size <= endSize; size += stepSize) {

            // 2. Loop Levels (1 to 20)
            for (int level = 1; level <= totalLevels; level++) {

                // --- MATH: Calculate Probabilities ---
                // t goes from 0.0 (Level 1) to 1.0 (Level 20)
                double t = (double) (level - 1) / (totalLevels - 1);

                // Wall: 10% -> 35%
                double wallProb = 0.10 + (t * 0.25);

                // Maintenance (Mud): 5% -> 25%
                double mudProb = 0.05 + (t * 0.20);

                // High Traffic: Constant 5%
                double trafficProb = 0.05;

                // Shortcut: 5% -> 1% (Shortcuts become rare in hard maps)
                double shortcutProb = 0.05 - (t * 0.04);

                System.out.printf("Processing: Size %d | Level %d (Wall: %.2f, Mud: %.2f)...\n",
                        size, level, wallProb, mudProb);

                // 3. Generate Samples
                int currentValidSamples = 0;
                while (currentValidSamples < samplesPerConfig) {

                    GridMap map = new GridMap();

                    // Call the NEW method
                    map.generateAdvanced(size, size, wallProb, mudProb, trafficProb, shortcutProb);

                    // Solve
                    SolverResult result = solver.solve(map, heuristic);

                    if (result.isSuccess) {
                        currentValidSamples++;
                        globalCounter++;

                        String mapId = UUID.randomUUID().toString().substring(0, 8);
                        exporter.appendMLData(
                                mapId,
                                map,
                                map.getStartNode(),
                                map.getGoalNode(),
                                result.totalCost
                        );
                    }
                }
            }
        }

        System.out.println("\n=== Generation Complete ===");
        System.out.println("Total Maps Created: " + globalCounter);
        System.out.println("File: final_output/training_data.jsonl");
    }
}