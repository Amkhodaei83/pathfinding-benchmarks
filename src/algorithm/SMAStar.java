package algorithm;

import model.GridMap;
import model.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SMAStar extends PathFinder {

    private final int maxMemory;

    public SMAStar(int maxMemory) {
        this.maxMemory = maxMemory;
    }

    @Override
    public List<Node> findPath(GridMap map, Heuristic heuristic) {
        // Default timeout of 60 seconds if not specified
        return solve(map, heuristic, 60000).path;
    }

    /**
     * Solves the pathfinding problem with a strict memory limit and time limit.
     * @param timeoutMs Maximum execution time in milliseconds.
     */
    public SolverResult solve(GridMap map, Heuristic heuristic, long timeoutMs) {
        // Use nanoTime() for better precision (microsecond-level instead of millisecond-level)
        long startTime = System.nanoTime();
        long timeoutNs = timeoutMs * 1_000_000L; // Convert timeout to nanoseconds

        List<Node> openSet = new ArrayList<>(maxMemory + 1);

        int nodesExpanded = 0;
        int prunedNodes = 0;

        Node start = map.getStartNode();
        Node goal = map.getGoalNode();

        start.g = 0;
        start.h = heuristic.compute(start, goal, map);
        start.f = start.g + start.h;
        openSet.add(start);

        while (!openSet.isEmpty()) {
            // --- TIMEOUT CHECK ---
            long elapsedNs = System.nanoTime() - startTime;
            if (elapsedNs > timeoutNs) {
                // Return partial statistics with Failure status
                long elapsedMs = elapsedNs / 1_000_000L; // Convert to ms for SolverResult
                return new SolverResult(
                        null,                  // No path found
                        Double.POSITIVE_INFINITY,
                        nodesExpanded,
                        prunedNodes,
                        maxMemory,
                        false,                 // Success = False
                        elapsedMs              // Record that we hit the limit
                );
            }

            // Standard SMA* Logic
            Collections.sort(openSet);
            Node current = openSet.get(0);

            if (current.equals(goal)) {
                List<Node> path = reconstructPath(current);
                // Convert nanoseconds to milliseconds for SolverResult
                long elapsedMs = (System.nanoTime() - startTime) / 1_000_000L;
                return new SolverResult(
                        path, current.g, nodesExpanded, prunedNodes, openSet.size(),
                        true, elapsedMs
                );
            }

            openSet.remove(0);
            nodesExpanded++;

            List<Node> neighbors = getNeighbors(current, map);
            for (Node successor : neighbors) {
                successor.g = current.g + map.getCost(successor.x, successor.y);
                successor.h = heuristic.compute(successor, goal, map);
                successor.f = successor.g + successor.h;
                successor.parent = current;

                // Update if exists (simplified for list structure)
                boolean inMemory = false;
                for (Node n : openSet) {
                    if (n.equals(successor)) {
                        inMemory = true;
                        if (successor.g < n.g) {
                            n.g = successor.g;
                            n.f = successor.f;
                            n.parent = successor.parent;
                        }
                        break;
                    }
                }

                if (!inMemory) {
                    openSet.add(successor);
                }
            }

            // Pruning Logic
            while (openSet.size() > maxMemory) {
                Collections.sort(openSet);
                Node worstNode = openSet.get(openSet.size() - 1);

                if (worstNode.parent != null) {
                    worstNode.parent.f = worstNode.f;
                }

                openSet.remove(openSet.size() - 1);
                prunedNodes++;
            }
        }

        // Exhausted search space without finding goal
        // Convert nanoseconds to milliseconds for SolverResult
        long elapsedMs = (System.nanoTime() - startTime) / 1_000_000L;
        return SolverResult.failure(nodesExpanded, prunedNodes, maxMemory, elapsedMs);
    }
}