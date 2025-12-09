package algorithm;

import model.GridMap;
import model.Node;

/**
 * Strategy interface for calculating the heuristic value (h-score).
 */
public interface Heuristic {
    /**
     * Computes the estimated cost from node 'a' to node 'b'.
     * @param a The start node (current).
     * @param b The target node (goal).
     * @param map The map reference (for cost-aware calculations).
     * @return The estimated cost.
     */
    double compute(Node a, Node b, GridMap map);
}