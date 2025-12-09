package algorithm;

import model.GridMap;
import model.Node;

/**
 * A weighted heuristic that considers the terrain cost of the target.
 * Logic: If the target is on a 'Shortcut' (low cost) terrain,
 * reduce the heuristic estimate to encourage the algorithm to explore that direction.
 */
public class CostAwareHeuristic implements Heuristic {

    @Override
    public double compute(Node a, Node b, GridMap map) {
        double dist = Math.abs(a.x - b.x) + Math.abs(a.y - b.y);

        // As per prompt logic: if target cell cost is < 1.0 (Shortcut), reduce estimate by 20%.
        // Note: In standard A*, 'b' is usually the Goal. This heuristic implies
        // a specific usage or an experimental weighting logic provided by the spec.
        double multiplier = (map.getCost(b.x, b.y) < 1.0) ? 0.8 : 1.0;

        return dist * multiplier;
    }
}