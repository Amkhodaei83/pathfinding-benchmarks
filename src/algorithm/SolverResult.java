package algorithm;

import model.Node;
import java.util.Collections;
import java.util.List;

/**
 * Data Transfer Object (DTO) containing the results of a pathfinding execution.
 */
public class SolverResult {
    public final List<Node> path;
    public final double totalCost;
    public final int nodesExpanded;
    public final int prunedNodes;
    public final int memoryUsed;
    public final boolean isSuccess;
    public final long timeElapsedMs; // Optional but useful for internal tracking

    public SolverResult(List<Node> path, double totalCost, int nodesExpanded,
                        int prunedNodes, int memoryUsed, boolean isSuccess, long timeElapsedMs) {
        this.path = path != null ? path : Collections.emptyList();
        this.totalCost = totalCost;
        this.nodesExpanded = nodesExpanded;
        this.prunedNodes = prunedNodes;
        this.memoryUsed = memoryUsed;
        this.isSuccess = isSuccess;
        this.timeElapsedMs = timeElapsedMs;
    }

    public static SolverResult failure(int nodesExpanded, int prunedNodes, int memoryUsed, long timeMs) {
        return new SolverResult(null, 0.0, nodesExpanded, prunedNodes, memoryUsed, false, timeMs);
    }
}