package model;

import java.util.Random;

public class GridMap {
    private int width;
    private int height;
    private CellType[][] grid;
    private Node startNode;
    private Node goalNode;

    // Cached flatten string for export performance
    private String cachedLayout = null;

    public GridMap() {
        // Empty constructor
    }

    /**
     * EXISTING METHOD (Preserved)
     * Other classes (like your Main.java) still use this.
     * It simply extracts the numbers from the Enum and calls the internal logic.
     */
    public void generateRandom(int width, int height, Difficulty difficulty) {
        generateAdvanced(
                width,
                height,
                difficulty.getWallChance(),
                difficulty.getMudChance(),
                difficulty.getHighTrafficChance(),
                difficulty.getShortcutChance()
        );
    }

    /**
     * Seed-based version for deterministic map generation.
     */
    public void generateRandom(int width, int height, Difficulty difficulty, long seed) {
        generateAdvanced(
                width,
                height,
                difficulty.getWallChance(),
                difficulty.getMudChance(),
                difficulty.getHighTrafficChance(),
                difficulty.getShortcutChance(),
                seed
        );
    }

    /**
     * NEW METHOD for ML Generator.
     * Allows passing raw probabilities for 20 custom difficulty levels.
     */
    public void generateAdvanced(int width, int height, double wallProb, double mudProb, double trafficProb, double shortcutProb) {
        // Use random seed
        generateAdvanced(width, height, wallProb, mudProb, trafficProb, shortcutProb, System.currentTimeMillis());
    }

    /**
     * Seed-based version for deterministic map generation.
     */
    public void generateAdvanced(int width, int height, double wallProb, double mudProb, double trafficProb, double shortcutProb, long seed) {
        this.width = width;
        this.height = height;
        this.grid = new CellType[height][width];
        this.cachedLayout = null;
        Random random = new Random(seed);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double roll = random.nextDouble();

                // Determine cell type based on cumulative probability
                if (roll < wallProb) {
                    grid[y][x] = CellType.WALL;
                } else if (roll < wallProb + mudProb) {
                    grid[y][x] = CellType.MAINTENANCE;
                } else if (roll < wallProb + mudProb + trafficProb) {
                    grid[y][x] = CellType.HIGH_TRAFFIC;
                } else if (roll < wallProb + mudProb + trafficProb + shortcutProb) {
                    grid[y][x] = CellType.SHORTCUT;
                } else {
                    grid[y][x] = CellType.ROAD;
                }
            }
        }

        // Enforce Start (Top-Left) and Goal (Bottom-Right)
        setStartAndGoal(0, 0, width - 1, height - 1);
    }

    /**
     * Explicitly sets start and goal, overriding whatever terrain was there.
     */
    private void setStartAndGoal(int startX, int startY, int goalX, int goalY) {
        grid[startY][startX] = CellType.START;
        grid[goalY][goalX] = CellType.GOAL;
        this.startNode = new Node(startX, startY);
        this.goalNode = new Node(goalX, goalY);
    }

    // --- ALL EXISTING METHODS BELOW ARE UNTOUCHED ---

    public void setFromText(String layoutBlock) {
        String[] lines = layoutBlock.trim().split("\\r?\\n");
        this.height = lines.length;
        this.width = lines.length > 0 ? lines[0].trim().length() : 0;
        this.grid = new CellType[height][width];
        this.cachedLayout = null;

        for (int y = 0; y < height; y++) {
            String line = lines[y].trim();
            for (int x = 0; x < width; x++) {
                char symbol = line.charAt(x);
                CellType type = CellType.fromChar(symbol);
                grid[y][x] = type;

                if (type == CellType.START) {
                    this.startNode = new Node(x, y);
                } else if (type == CellType.GOAL) {
                    this.goalNode = new Node(x, y);
                }
            }
        }
    }

    public double getCost(int x, int y) {
        if (!isValid(x, y)) {
            return Double.POSITIVE_INFINITY;
        }
        return grid[y][x].getCost();
    }

    public CellType getCellType(int x, int y) {
        if (!isValid(x, y)) return CellType.WALL;
        return grid[y][x];
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public String getFlattenedLayout() {
        if (cachedLayout != null) {
            return cachedLayout;
        }
        StringBuilder sb = new StringBuilder(width * height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sb.append(grid[y][x].getSymbol());
            }
        }
        cachedLayout = sb.toString();
        return cachedLayout;
    }
    public double getPercentMaintenance() {
        return (double) countCells(CellType.MAINTENANCE) / (width * height);
    }

    public double getPercentHighTraffic() {
        return (double) countCells(CellType.HIGH_TRAFFIC) / (width * height);
    }

    public double getPercentShortcut() {
        return (double) countCells(CellType.SHORTCUT) / (width * height);
    }

    public double getPercentWall() {
        return (double) countCells(CellType.WALL) / (width * height);
    }

    /**
     * Correctly iterates over the 2D grid to count specific cell types.
     */
    private int countCells(CellType targetType) {
        int count = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Check if the cell at [y][x] matches the type we want
                if (grid[y][x] == targetType) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Node getStartNode() { return startNode; }
    public Node getGoalNode() { return goalNode; }
}