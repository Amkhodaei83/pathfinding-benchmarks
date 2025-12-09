import algorithm.*;
import io.TraceLogger;
import model.*;

import java.util.*;

public class Main {

    // A complex map used specifically for Mode 2 (Visualization)
    private static final String DEMO_MAP =
            "....................\n" +
                    ".S..HH...MM..CC.....\n" +
                    "...HH..BBM...C..HH..\n" +
                    ".......BBM......HM..\n" +
                    "..CC........HHH.....\n" +
                    "..C..MM.....HMH.....\n" +
                    "....................\n" +
                    "......HHH....MMM....\n" +
                    "......HMH...........\n" +
                    "......HHH.CCCC......\n" +
                    "..........C........G\n" +
                    "...BB.....C..BB.....\n" +
                    "...BB.....C..BB...M.\n" +
                    "..................M.\n" +
                    "HHHHH..MMMM.........\n" +
                    "....H.....MMM...HHH.\n" +
                    "....H...........HMH.\n" +
                    "......CCC...........\n" +
                    "..M...C.C.....M.....\n" +
                    "....................";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Main Application Loop
        while (true) {
            System.out.println("\n==========================================");
            System.out.println("    PATHFINDING BENCHMARK SYSTEM v3.0     ");
            System.out.println("==========================================");
            System.out.println("Select Mode:");
            System.out.println("  [1] Standard Benchmark (A* vs SMA*)");
            System.out.println("  [2] Visualize Trace (Single Map Audit)");
            System.out.println("  [3] Generate Training Data (For Python)");
            System.out.println("  [4] Test Machine Learned Heuristic (Bonus)"); // NEW OPTION
            System.out.println("  [0] Exit");
            System.out.print(">> ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    // Run the standard mass benchmark
                    new BenchmarkRunner().runBenchmarks();
                    break;
                case "2":
                    // Run the single map visualization
                    runVisualDemo();
                    break;
                case "3":
                    // Run the structured data generation
                    handleMLGeneration(scanner);
                    break;
                case "4":
                    // Run the specific ML Benchmark
                    new MLBenchmarkRunner().runBenchmarks();
                    break;
                case "0":
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid selection. Please try again.");
            }
        }
    }

    /**
     * Handles user input for the ML Data Generator configuration.
     */
    private static void handleMLGeneration(Scanner scanner) {
        try {
            System.out.println("\n--- ML Data Generation Configuration ---");

            System.out.print("Enter Start Map Size (e.g., 20): ");
            int startSize = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter End Map Size   (e.g., 60): ");
            int endSize = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter Step Size      (e.g., 10): ");
            int stepSize = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Samples per Config   (e.g., 50): ");
            int samples = Integer.parseInt(scanner.nextLine().trim());

            // Launch the generator
            new MLDataGenerator().generate(startSize, endSize, stepSize, samples);

        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid input. Please enter integers only.");
        }
    }

    /**
     * Runs detailed traces of A* and SMA* on the DEMO_MAP.
     * Generates .txt files for analysis.
     */
    private static void runVisualDemo() {
        System.out.println("\n=== MODE: VISUAL AUDIT ===");

        GridMap map = new GridMap();
        map.setFromText(DEMO_MAP);
        System.out.println("Map Loaded (" + map.getWidth() + "x" + map.getHeight() + ")");

        // --- Run 1: Standard A* ---
        System.out.println("1) Running A* Visual Trace...");
        TraceLogger loggerA = new TraceLogger();
        loggerA.logInfo("Map Layout:\n" + DEMO_MAP);

        AuditableAStar aStar = new AuditableAStar(loggerA);
        aStar.solve(map, new ManhattanHeuristic());
        loggerA.saveToFile("trace_astar.txt");

        // --- Run 2: SMA* ---
        // Using a tight memory limit to force pruning behavior for demonstration
        int auditMemLimit = 15;
        System.out.println("2) Running SMA* Visual Trace (MaxMem=" + auditMemLimit + ")...");
        TraceLogger loggerSMA = new TraceLogger();
        loggerSMA.logInfo("Map Layout:\n" + DEMO_MAP);

        AuditableSMAStar smaStar = new AuditableSMAStar(loggerSMA, auditMemLimit);
        smaStar.solve(map, new ManhattanHeuristic());
        loggerSMA.saveToFile("trace_smastar.txt");

        System.out.println("Done. Check 'trace_astar.txt' and 'trace_smastar.txt'.");
    }

    // ==========================================
    // Auditable A* Implementation (For Visualization Only)
    // ==========================================
    private static class AuditableAStar extends PathFinder {
        private final TraceLogger logger;

        public AuditableAStar(TraceLogger logger) { this.logger = logger; }

        @Override
        public List<Node> findPath(GridMap map, Heuristic h) { return solve(map, h).path; }

        public SolverResult solve(GridMap map, Heuristic heuristic) {
            PriorityQueue<Node> openSet = new PriorityQueue<>();
            Map<String, Double> gScores = new HashMap<>();
            List<Node> visitedForVis = new ArrayList<>();

            Node start = map.getStartNode();
            Node goal = map.getGoalNode();

            start.g = 0;
            start.h = heuristic.compute(start, goal, map);
            start.f = start.g + start.h;

            openSet.add(start);
            gScores.put(key(start), 0.0);
            visitedForVis.add(start);

            int expanded = 0;
            int maxMem = 0;

            while (!openSet.isEmpty()) {
                maxMem = Math.max(maxMem, openSet.size() + gScores.size());
                Node current = openSet.poll();

                logger.logMapState(map, current, openSet, visitedForVis);

                if (current.equals(goal)) {
                    logger.writeSummary(true, current.g, expanded,
                            heuristic.getClass().getSimpleName(),
                            "O(b^d)", String.valueOf(maxMem), "A* (A-Star)");
                    return new SolverResult(reconstructPath(current), current.g, expanded, 0, maxMem, true, 0);
                }

                expanded++;
                List<Node> neighbors = getNeighbors(current, map);

                for (Node neighbor : neighbors) {
                    double tentativeG = current.g + map.getCost(neighbor.x, neighbor.y);
                    if (tentativeG < gScores.getOrDefault(key(neighbor), Double.POSITIVE_INFINITY)) {
                        neighbor.g = tentativeG;
                        neighbor.h = heuristic.compute(neighbor, goal, map);
                        neighbor.f = neighbor.g + neighbor.h;
                        neighbor.parent = current;

                        gScores.put(key(neighbor), tentativeG);
                        openSet.add(neighbor);
                        visitedForVis.add(neighbor);
                    }
                }
            }

            logger.writeSummary(false, 0, expanded, heuristic.getClass().getSimpleName(), "O(b^d)", String.valueOf(maxMem), "A*");
            return SolverResult.failure(expanded, 0, maxMem, 0);
        }
        private String key(Node n) { return n.x + "," + n.y; }
    }

    // ==========================================
    // Auditable SMA* Implementation (For Visualization Only)
    // ==========================================
    private static class AuditableSMAStar extends PathFinder {
        private final TraceLogger logger;
        private final int maxMemory;
        private static final int MAX_STEPS_SAFETY = 5000;

        public AuditableSMAStar(TraceLogger logger, int maxMemory) {
            this.logger = logger;
            this.maxMemory = maxMemory;
        }

        @Override
        public List<Node> findPath(GridMap map, Heuristic h) { return solve(map, h).path; }

        public SolverResult solve(GridMap map, Heuristic heuristic) {
            List<Node> openSet = new ArrayList<>(maxMemory + 1);

            Node start = map.getStartNode();
            Node goal = map.getGoalNode();

            start.g = 0;
            start.h = heuristic.compute(start, goal, map);
            start.f = start.g + start.h;
            openSet.add(start);

            int expanded = 0;
            int pruned = 0;
            int stepCount = 0;

            while (!openSet.isEmpty()) {
                if (stepCount++ > MAX_STEPS_SAFETY) {
                    logger.logInfo("!!! TERMINATED: Hit Safety Step Limit (" + MAX_STEPS_SAFETY + ") !!!");
                    logger.writeSummary(false, 0, expanded, heuristic.getClass().getSimpleName(),
                            "Infinite (Thrashing)", maxMemory + " (Fixed)", "SMA*");
                    return SolverResult.failure(expanded, pruned, maxMemory, 0);
                }

                Collections.sort(openSet);
                Node current = openSet.get(0);

                logger.logMapState(map, current, openSet, openSet);

                if (current.equals(goal)) {
                    logger.writeSummary(true, current.g, expanded,
                            heuristic.getClass().getSimpleName(),
                            "O(b^d) - Pruned: " + pruned,
                            maxMemory + " (Fixed Limit)", "SMA*");
                    return new SolverResult(reconstructPath(current), current.g, expanded, pruned, maxMemory, true, 0);
                }

                openSet.remove(0);
                expanded++;

                List<Node> neighbors = getNeighbors(current, map);
                for (Node successor : neighbors) {
                    successor.g = current.g + map.getCost(successor.x, successor.y);
                    successor.h = heuristic.compute(successor, goal, map);
                    successor.f = successor.g + successor.h;
                    successor.parent = current;

                    boolean alreadyInMemory = false;
                    for(Node n : openSet) {
                        if(n.equals(successor)) {
                            alreadyInMemory = true;
                            if(successor.g < n.g) {
                                n.g = successor.g;
                                n.f = successor.f;
                                n.parent = successor.parent;
                            }
                            break;
                        }
                    }

                    if (!alreadyInMemory) {
                        openSet.add(successor);
                    }
                }

                while (openSet.size() > maxMemory) {
                    Collections.sort(openSet);
                    Node worst = openSet.get(openSet.size() - 1);
                    logger.logInfo("Memory Full ("+ openSet.size() +")! Pruning Worst: (" + worst.x + "," + worst.y + ") F:" + String.format("%.2f", worst.f));
                    if (worst.parent != null) worst.parent.f = worst.f;
                    openSet.remove(openSet.size() - 1);
                    pruned++;
                }
            }
            logger.writeSummary(false, 0, expanded, heuristic.getClass().getSimpleName(), "O(b^d)", String.valueOf(maxMemory), "SMA*");
            return SolverResult.failure(expanded, pruned, maxMemory, 0);
        }
    }
}