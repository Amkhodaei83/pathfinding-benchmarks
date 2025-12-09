package algorithm;

import model.GridMap;
import model.Node;

/**
 * centralized collection of all Heuristic strategies.
 * Implements the Heuristic interface directly.
 */
public enum HeuristicType implements Heuristic {

    // --- A. ADMISSIBLE (Guarantees Shortest Path) ---
    // Scaled by 0.5 because the lowest cost in the map is 0.5 (Shortcut).

    SCALED_MANHATTAN {
        @Override
        public double compute(Node a, Node b, GridMap map) {
            return 0.5 * (Math.abs(a.x - b.x) + Math.abs(a.y - b.y));
        }
    },

    SCALED_EUCLIDEAN {
        @Override
        public double compute(Node a, Node b, GridMap map) {
            return 0.5 * Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
        }
    },

    SCALED_CHEBYSHEV {
        @Override
        public double compute(Node a, Node b, GridMap map) {
            return 0.5 * Math.max(Math.abs(a.x - b.x), Math.abs(a.y - b.y));
        }
    },

    DIJKSTRA_ZERO {
        @Override
        public double compute(Node a, Node b, GridMap map) {
            return 0.0;
        }
    },

    // --- B. INADMISSIBLE (Aggressive/Fast, but maybe not optimal) ---

    UNSCALED_MANHATTAN {
        @Override
        public double compute(Node a, Node b, GridMap map) {
            // Assumes standard Road cost (1.0)
            return 1.0 * (Math.abs(a.x - b.x) + Math.abs(a.y - b.y));
        }
    },

    AVG_COST_MANHATTAN {
        @Override
        public double compute(Node a, Node b, GridMap map) {
            // Approx average of (1, 5, 10, 0.5) ~ 4.1
            return 4.1 * (Math.abs(a.x - b.x) + Math.abs(a.y - b.y));
        }
    },

    MANHATTAN_SQUARED {
        @Override
        public double compute(Node a, Node b, GridMap map) {
            double dist = Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
            return dist * dist;
        }
    },

    // --- C. TIE-BREAKING & ADVANCED ---

    CROSS_PRODUCT {
        @Override
        public double compute(Node current, Node goal, GridMap map) {
            Node start = map.getStartNode();
            double dx1 = current.x - goal.x;
            double dy1 = current.y - goal.y;
            double dx2 = start.x - goal.x;
            double dy2 = start.y - goal.y;
            double cross = Math.abs(dx1 * dy2 - dx2 * dy1);

            // Base: Scaled Manhattan
            double h = 0.5 * (Math.abs(current.x - goal.x) + Math.abs(current.y - goal.y));
            return h + (cross * 0.001);
        }
    },

    COST_AWARE {
        @Override
        public double compute(Node a, Node b, GridMap map) {
            double dist = 0.5 * (Math.abs(a.x - b.x) + Math.abs(a.y - b.y));
            // If target is a shortcut (Cost < 1.0), pull harder (reduce h slightly)
            // If target is Swamp (Cost >= 10), push away (increase h slightly) - heuristic manipulation
            double targetCost = map.getCost(b.x, b.y);

            if (targetCost < 1.0) return dist * 0.9; // Pull towards shortcuts
            if (targetCost >= 10.0) return dist * 1.1; // Avoid swamp checks if possible
            return dist;
        }
    };
}