package io;

import model.CellType;
import model.GridMap;
import model.Node;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class TraceLogger {
    private final StringBuilder buffer;
    private int stepCounter;

    public TraceLogger() {
        this.buffer = new StringBuilder(100000);
        this.stepCounter = 0;
    }

    /**
     * Visualizes the map state.
     * Base Map: B (Wall), M (Mud), H (High Traffic), C (Shortcut), S (Start), G (Goal), ' ' (Road).
     * Overlays: @ (Robot), ? (Frontier), . (Visited/Memory).
     */
    public void logMapState(GridMap map, Node current, Collection<Node> openSet, Collection<Node> memory) {
        stepCounter++;
        buffer.append("\n=== STEP ").append(stepCounter).append(" ===\n");
        buffer.append(String.format("Current Focus: (%d, %d) | F: %.2f | G: %.2f\n",
                current.x, current.y, current.f, current.g));

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                char symbol;

                // 1. Determine base terrain symbol
                CellType type = map.getCellType(x, y);
                switch (type) {
                    case WALL: symbol = 'B'; break;
                    case MAINTENANCE: symbol = 'M'; break;
                    case HIGH_TRAFFIC: symbol = 'H'; break;
                    case SHORTCUT: symbol = 'C'; break;
                    case START: symbol = 'S'; break;
                    case GOAL: symbol = 'G'; break;
                    case ROAD: default: symbol = ' '; break; // Replace R with space for clarity
                }

                // 2. Overlay Algorithm State
                // Priority: Robot (@) > Frontier (?) > Visited (.) > Terrain

                // Check if visited/in memory (Background history)
                if (containsCoord(memory, x, y)) {
                    symbol = '.';
                }

                // Check if in Open Set (Immediate Frontier)
                if (containsCoord(openSet, x, y)) {
                    symbol = '?';
                }

                // Check if Current Node (The Robot) - Always on top
                if (current.x == x && current.y == y) {
                    symbol = '@';
                }

                buffer.append("[").append(symbol).append("]");
            }
            buffer.append("\n");
        }
        buffer.append("--------------------------------------------------\n");
    }

    /**
     * Appends the final detailed summary block.
     */
    public void writeSummary(boolean success, double cost, int expanded, String heuristicName,
                             String complexityTime, String complexitySpace, String algoName) {
        buffer.append("\n==========================================\n");
        buffer.append("            FINAL RESULT SUMMARY          \n");
        buffer.append("==========================================\n");
        buffer.append("1) Goal Found      : ").append(success ? "YES" : "NO").append("\n");
        buffer.append("2) Total Cost      : ").append(String.format("%.2f", cost)).append("\n");
        buffer.append("3) Nodes Expanded  : ").append(expanded).append("\n");
        buffer.append("4) Heuristic Used  : ").append(heuristicName).append("\n");
        buffer.append("5) Time Complexity : ").append(complexityTime).append(" (Steps/Nodes)\n");
        buffer.append("6) Space Complexity: ").append(complexitySpace).append(" (Max Nodes in Mem)\n");
        buffer.append("7) Algorithm       : ").append(algoName).append("\n");
        buffer.append("==========================================\n");
    }

    public void logInfo(String message) {
        buffer.append("[INFO] ").append(message).append("\n");
    }

    public void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(buffer.toString());
            System.out.println("Trace log saved to: " + filename);
        } catch (IOException e) {
            System.err.println("Failed to write trace log: " + e.getMessage());
        }
    }

    private boolean containsCoord(Collection<Node> nodes, int x, int y) {
        if (nodes == null) return false;
        for (Node n : nodes) {
            if (n.x == x && n.y == y) return true;
        }
        return false;
    }
}