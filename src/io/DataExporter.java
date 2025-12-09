package io;

import model.CellType;
import model.GridMap;
import model.Node;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataExporter {

    private static final String OUTPUT_DIR = "final_output";
    private static final String CSV_FILE = OUTPUT_DIR + File.separator + "benchmark_results.csv";
    private static final String JSONL_FILE = OUTPUT_DIR + File.separator + "training_data.jsonl";

    private static final DataExporter INSTANCE = new DataExporter();

    private DataExporter() {
        initializeFiles();
    }

    public static DataExporter getInstance() {
        return INSTANCE;
    }

    private void initializeFiles() {
        // Create output directory if it doesn't exist
        File outputDir = new File(OUTPUT_DIR);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        // Init CSV Header
        File csv = new File(CSV_FILE);
        if (!csv.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(csv))) {
                writer.write("MapID,Algorithm,Heuristic,MapSize,Difficulty,MemoryLimit,Success,Status,Cost,OptimalCost,NodesExpanded,PrunedNodes,Time_ns,MemoryUsed\n");
            } catch (IOException e) { 
                System.err.println("Error creating CSV file: " + e.getMessage());
                e.printStackTrace(); 
            }
        }

        // Init JSONL (We append to it, so no header needed here)
        // File will be created automatically on first write
    }

    // --- CSV Benchmark Method (Unchanged) ---
    public synchronized void writeBenchmarkRecord(String mapId, String algo, String heuristic,
                                                  String mapSize, String difficulty, int memoryLimit,
                                                  boolean success, String status,
                                                  double cost, double optimalCost,
                                                  int expanded, int pruned, long timeNs, int memoryUsed) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE, true))) {
            String line = String.format("%s,%s,%s,%s,%s,%d,%b,%s,%.2f,%.2f,%d,%d,%d,%d\n",
                    mapId, algo, heuristic, mapSize, difficulty, memoryLimit,
                    success, status, cost, optimalCost, expanded, pruned, timeNs, memoryUsed);
            writer.write(line);
        } catch (IOException e) {
            System.err.println("Error CSV: " + e.getMessage());
        }
    }

    // --- ML Data Method (Updated for your CellType) ---
    public synchronized void appendMLData(String mapId, GridMap map, Node start, Node goal, double optimalCost) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(JSONL_FILE, true))) {

            // 1. Calculate Geometry
            double manhattan = Math.abs(start.x - goal.x) + Math.abs(start.y - goal.y);
            double euclidean = Math.sqrt(Math.pow(start.x - goal.x, 2) + Math.pow(start.y - goal.y, 2));

            // 2. Calculate Terrain Stats using your Specific Enums
            MapStats stats = calculateMapStats(map);

            // 3. Build JSON
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"map_id\": \"").append(mapId).append("\", ");
            json.append("\"width\": ").append(map.getWidth()).append(", ");
            json.append("\"height\": ").append(map.getHeight()).append(", ");

            // Layout (Optional now, but good to keep for debugging)
            json.append("\"layout\": \"").append(map.getFlattenedLayout()).append("\", ");

            // Features for ML
            // IMPORTANT: Order must match ML.py features list:
            // [manhattan_dist, euclidean_dist, pct_maintenance, pct_hightraffic, pct_shortcut, pct_wall]
            json.append("\"manhattan_dist\": ").append(manhattan).append(", ");
            json.append("\"euclidean_dist\": ").append(String.format("%.4f", euclidean)).append(", ");
            json.append("\"pct_maintenance\": ").append(String.format("%.4f", stats.maintenancePct)).append(", ");
            json.append("\"pct_hightraffic\": ").append(String.format("%.4f", stats.highTrafficPct)).append(", ");
            json.append("\"pct_shortcut\": ").append(String.format("%.4f", stats.shortcutPct)).append(", ");
            json.append("\"pct_wall\": ").append(String.format("%.4f", stats.wallPct)).append(", ");

            // The Label
            json.append("\"optimal_cost\": ").append(String.format("%.2f", optimalCost));
            json.append("}\n");

            writer.write(json.toString());
        } catch (IOException e) {
            System.err.println("Error JSONL: " + e.getMessage());
        }
    }

    private static class MapStats {
        double wallPct, maintenancePct, highTrafficPct, shortcutPct;
    }

    private MapStats calculateMapStats(GridMap map) {
        int total = map.getWidth() * map.getHeight();
        int walls = 0;
        int maintenance = 0;
        int highTraffic = 0;
        int shortcuts = 0;

        for(int y=0; y<map.getHeight(); y++) {
            for(int x=0; x<map.getWidth(); x++) {
                CellType type = map.getCellType(x, y);

                // MAPPING YOUR ENUMS HERE
                if (type == CellType.WALL) {
                    walls++;
                } else if (type == CellType.MAINTENANCE) {
                    maintenance++;
                } else if (type == CellType.HIGH_TRAFFIC) {
                    highTraffic++;
                } else if (type == CellType.SHORTCUT) {
                    shortcuts++;
                }
            }
        }

        MapStats stats = new MapStats();
        stats.wallPct = (double) walls / total;
        stats.maintenancePct = (double) maintenance / total;
        stats.highTrafficPct = (double) highTraffic / total;
        stats.shortcutPct = (double) shortcuts / total;
        return stats;
    }
}