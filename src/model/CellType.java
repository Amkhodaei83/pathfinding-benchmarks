package model;

import java.util.Arrays;

/**
 * Enum representing the type of terrain in the grid.
 * Holds cost values and symbol representations.
 */
public enum CellType {
    ROAD('R', 1.0),
    HIGH_TRAFFIC('H', 5.0),
    MAINTENANCE('M', 10.0), // Swamp/Mud
    SHORTCUT('C', 0.5),     // Preferred route
    WALL('B', Double.POSITIVE_INFINITY),
    START('S', 0.0),
    GOAL('G', 0.0);

    private final char symbol;
    private final double cost;

    CellType(char symbol, double cost) {
        this.symbol = symbol;
        this.cost = cost;
    }

    public char getSymbol() {
        return symbol;
    }

    public double getCost() {
        return cost;
    }

    /**
     * @return true if the cell can be entered (not a wall).
     */
    public boolean isTraversable() {
        return this != WALL;
    }

    /**
     * Efficiently looks up CellType by character symbol.
     * @param c The character representation.
     * @return The corresponding CellType, or ROAD if unknown.
     */
    public static CellType fromChar(char c) {
        // Linear scan is acceptable here as Enum set is tiny.
        // For larger sets, a static Map<Character, CellType> would be optimized.
        for (CellType type : values()) {
            if (type.symbol == c) {
                return type;
            }
        }
        return ROAD; // Default fallback
    }
}