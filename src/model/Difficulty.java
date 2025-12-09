package model;

/**
 * Configuration for procedural map generation.
 * Defines probability thresholds for different terrain types.
 */
public enum Difficulty {
    EASY(0.10, 0.05, 0.0, 0.0),      // Sparse obstacles
    MEDIUM(0.20, 0.10, 0.05, 0.05),  // Balanced mix
    HARD(0.35, 0.15, 0.10, 0.02);    // High density, maze-like components

    private final double wallChance;
    private final double mudChance;
    private final double highTrafficChance;
    private final double shortcutChance;

    Difficulty(double wallChance, double mudChance, double highTrafficChance, double shortcutChance) {
        this.wallChance = wallChance;
        this.mudChance = mudChance;
        this.highTrafficChance = highTrafficChance;
        this.shortcutChance = shortcutChance;
    }

    public double getWallChance() { return wallChance; }
    public double getMudChance() { return mudChance; }
    public double getHighTrafficChance() { return highTrafficChance; }
    public double getShortcutChance() { return shortcutChance; }
}