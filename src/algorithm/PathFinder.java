package algorithm;

import model.GridMap;
import model.Node;
import model.CellType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for pathfinding algorithms.
 * Handles common tasks like neighbor generation and path reconstruction.
 */
public abstract class PathFinder {

    // 4-Directional Movement (Up, Down, Left, Right)
    private static final int[] DX = {0, 0, -1, 1};
    private static final int[] DY = {-1, 1, 0, 0};

    /**
     * Main entry point for calculation.
     * @param map The grid to search.
     * @param heuristic The heuristic strategy to use.
     * @return A list of nodes representing the path (Start -> Goal), or empty if no path.
     */
    public abstract List<Node> findPath(GridMap map, Heuristic heuristic);

    /**
     * Generates valid neighboring nodes for the current node.
     * Checks map bounds and traversability (walls).
     * @param current The node to expand.
     * @param map The grid map.
     * @return List of traversable neighbor nodes.
     */
    protected List<Node> getNeighbors(Node current, GridMap map) {
        List<Node> neighbors = new ArrayList<>(4);

        for (int i = 0; i < 4; i++) {
            int newX = current.x + DX[i];
            int newY = current.y + DY[i];

            // 1. Check Bounds
            if (!map.isValid(newX, newY)) {
                continue;
            }

            // 2. Check Traversability (Walls)
            CellType type = map.getCellType(newX, newY);
            if (!type.isTraversable()) {
                continue;
            }

            // Create a fresh node for the search tree.
            // Note: g, h, f are initialized to defaults and set by the specific algorithm.
            neighbors.add(new Node(newX, newY));
        }

        return neighbors;
    }

    /**
     * Backtracks from the end node to the start node using parent references.
     * @param end The goal node reached by the algorithm.
     * @return The ordered path from Start to End.
     */
    protected List<Node> reconstructPath(Node end) {
        List<Node> path = new ArrayList<>();
        Node current = end;

        // Backtrack
        while (current != null) {
            current.isInPath = true; // Mark for visualization/debugging
            path.add(current);
            current = current.parent;
        }

        // Reverse to get Start -> End order
        Collections.reverse(path);
        return path;
    }
}