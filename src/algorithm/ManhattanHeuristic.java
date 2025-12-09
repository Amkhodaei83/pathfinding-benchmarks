package algorithm;

import model.GridMap;
import model.Node;

/**
 * Standard Manhattan distance.
 * Suitable for 4-directional grid movement.
 */
public class ManhattanHeuristic implements Heuristic {

    @Override
    public double compute(Node a, Node b, GridMap map) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }
}