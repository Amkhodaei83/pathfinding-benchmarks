package model;

import java.util.Objects;

/**
 * Represents a specific coordinate in the search space.
 * Implements Comparable for PriorityQueue ordering (Min-Heap).
 */
public class Node implements Comparable<Node> {
    public final int x;
    public final int y;

    // Pathfinding Costs
    public double g = Double.POSITIVE_INFINITY; // Cost from Start
    public double h = 0;                        // Heuristic cost to Goal
    public double f = Double.POSITIVE_INFINITY; // g + h

    // Graph reconstruction
    public Node parent = null;

    // Visualization flag
    public boolean isInPath = false;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Primary sorting key: F-cost.
     * Secondary tie-breaker: H-cost (Greedy preference towards goal).
     */
    @Override
    public int compareTo(Node other) {
        int comparison = Double.compare(this.f, other.f);
        if (comparison == 0) {
            // Tie-breaking: Choose the node closer to the goal (lower h)
            return Double.compare(this.h, other.h);
        }
        return comparison;
    }

    /**
     * Equality check based strictly on coordinates.
     * Vital for Contains checks in Open/Closed sets.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return x == node.x && y == node.y;
    }

    /**
     * HashCode based strictly on coordinates.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("[%d,%d | F:%.2f]", x, y, f);
    }
}