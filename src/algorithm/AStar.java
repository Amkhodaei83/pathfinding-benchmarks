package algorithm;

import model.GridMap;
import model.Node;

import java.util.*;

/**
 * A* (A-Star) Algorithm Implementation.
 * Uses a PriorityQueue for the Open Set and a HashMap for the Closed Set/G-Scores.
 */
public class AStar extends PathFinder {

    @Override
    public List<Node> findPath(GridMap map, Heuristic heuristic) {
        return solve(map, heuristic).path;
    }

    /**
     * Executes the A* search and returns a detailed SolverResult.
     */
    public SolverResult solve(GridMap map, Heuristic heuristic) {
        // Use nanoTime() for better precision (microsecond-level instead of millisecond-level)
        long startTime = System.nanoTime();

        // Open Set: Min-Heap based on F-cost
        PriorityQueue<Node> openSet = new PriorityQueue<>();

        // G-Score Map: Key="x,y", Value=Best Cost
        Map<String, Double> gScores = new HashMap<>();

        // Metrics
        int nodesExpanded = 0;
        int maxMemory = 0;

        Node start = map.getStartNode();
        Node goal = map.getGoalNode();

        // Initialize Start
        start.g = 0;
        start.h = heuristic.compute(start, goal, map);
        start.f = start.g + start.h;
        openSet.add(start);
        gScores.put(key(start), 0.0);

        while (!openSet.isEmpty()) {
            // Memory Tracking
            maxMemory = Math.max(maxMemory, openSet.size() + gScores.size());

            Node current = openSet.poll();

            // Lazy Deletion: If we found a better path to this node already, skip
            if (current.g > gScores.getOrDefault(key(current), Double.POSITIVE_INFINITY)) {
                continue;
            }

            // Goal Check
            if (current.equals(goal)) {
                List<Node> path = reconstructPath(current);
                // Convert nanoseconds to milliseconds for SolverResult
                long elapsedMs = (System.nanoTime() - startTime) / 1_000_000L;
                return new SolverResult(
                        path,
                        current.g,
                        nodesExpanded,
                        0,
                        maxMemory,
                        true,
                        elapsedMs
                );
            }

            nodesExpanded++;

            // Expansion
            for (Node neighbor : getNeighbors(current, map)) {
                double tentativeG = current.g + map.getCost(neighbor.x, neighbor.y);
                String neighborKey = key(neighbor);

                // If this path to neighbor is better than any previous one
                if (tentativeG < gScores.getOrDefault(neighborKey, Double.POSITIVE_INFINITY)) {
                    neighbor.g = tentativeG;
                    neighbor.h = heuristic.compute(neighbor, goal, map);
                    neighbor.f = neighbor.g + neighbor.h;
                    neighbor.parent = current;

                    gScores.put(neighborKey, tentativeG);

                    // Add to Open Set (Duplicates allowed, filtered by G-score check above)
                    openSet.add(neighbor);
                }
            }
        }

        // Convert nanoseconds to milliseconds for SolverResult
        long elapsedMs = (System.nanoTime() - startTime) / 1_000_000L;
        return SolverResult.failure(nodesExpanded, 0, maxMemory, elapsedMs);
    }

    /**
     * Helper to generate map key as per spec.
     */
    private String key(Node n) {
        return n.x + "," + n.y;
    }
}