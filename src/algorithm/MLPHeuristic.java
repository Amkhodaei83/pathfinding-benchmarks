package algorithm;

import model.GridMap;
import model.Node;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Multi-Layer Perceptron (MLP) Neural Network Heuristic.
 * Implements a 3-layer neural network: 6 inputs -> 8 hidden -> 4 hidden -> 1 output.
 */
public class MLPHeuristic implements Heuristic {

    private static final String CONFIG_FILE = "ml_weights_mlp.properties";
    
    // Network architecture
    private int inputSize = 6;
    private int hidden1Size = 8;
    private int hidden2Size = 4;
    private int outputSize = 1;
    
    // Layer 1 weights and biases (Input -> Hidden1)
    private double[][] layer1Weights;  // [8][6]
    private double[] layer1Biases;     // [8]
    
    // Layer 2 weights and biases (Hidden1 -> Hidden2)
    private double[][] layer2Weights;  // [4][8]
    private double[] layer2Biases;     // [4]
    
    // Layer 3 weights and biases (Hidden2 -> Output)
    private double[] layer3Weights;    // [4]
    private double layer3Bias;         // scalar
    
    private boolean modelLoaded = false;
    
    // Cache for map-level features
    private GridMap cachedMap = null;
    private double cachedPctMaintenance = 0.0;
    private double cachedPctTraffic = 0.0;
    private double cachedPctShortcut = 0.0;
    private double cachedPctWall = 0.0;

    public MLPHeuristic() {
        loadModel();
    }

    private void loadModel() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);

            // Load architecture
            inputSize = Integer.parseInt(props.getProperty("INPUT_SIZE", "6"));
            hidden1Size = Integer.parseInt(props.getProperty("HIDDEN1_SIZE", "8"));
            hidden2Size = Integer.parseInt(props.getProperty("HIDDEN2_SIZE", "4"));
            outputSize = Integer.parseInt(props.getProperty("OUTPUT_SIZE", "1"));

            // Allocate arrays
            layer1Weights = new double[hidden1Size][inputSize];
            layer1Biases = new double[hidden1Size];
            layer2Weights = new double[hidden2Size][hidden1Size];
            layer2Biases = new double[hidden2Size];
            layer3Weights = new double[hidden2Size];
            layer3Bias = 0.0;

            // Load Layer 1 (Input -> Hidden1)
            for (int j = 0; j < hidden1Size; j++) {
                for (int i = 0; i < inputSize; i++) {
                    layer1Weights[j][i] = Double.parseDouble(
                        props.getProperty("L1_W_" + j + "_" + i, "0.0"));
                }
            }
            for (int j = 0; j < hidden1Size; j++) {
                layer1Biases[j] = Double.parseDouble(
                    props.getProperty("L1_B_" + j, "0.0"));
            }

            // Load Layer 2 (Hidden1 -> Hidden2)
            for (int j = 0; j < hidden2Size; j++) {
                for (int i = 0; i < hidden1Size; i++) {
                    layer2Weights[j][i] = Double.parseDouble(
                        props.getProperty("L2_W_" + j + "_" + i, "0.0"));
                }
            }
            for (int j = 0; j < hidden2Size; j++) {
                layer2Biases[j] = Double.parseDouble(
                    props.getProperty("L2_B_" + j, "0.0"));
            }

            // Load Layer 3 (Hidden2 -> Output)
            for (int i = 0; i < hidden2Size; i++) {
                layer3Weights[i] = Double.parseDouble(
                    props.getProperty("L3_W_0_" + i, "0.0"));
            }
            layer3Bias = Double.parseDouble(props.getProperty("L3_B_0", "0.0"));

            modelLoaded = true;
            System.out.println("✅ [MLP Heuristic] Neural network loaded from " + CONFIG_FILE);
            System.out.println("   Architecture: " + inputSize + " -> " + hidden1Size + " -> " + hidden2Size + " -> " + outputSize);

        } catch (IOException e) {
            System.err.println("⚠️ [MLP Heuristic] Could not find '" + CONFIG_FILE + "'. Defaulting to Manhattan.");
            modelLoaded = false;
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("⚠️ [MLP Heuristic] Error loading MLP weights: " + e.getMessage());
            modelLoaded = false;
        }
    }

    /**
     * ReLU activation function: max(0, x)
     */
    private double relu(double x) {
        return Math.max(0, x);
    }

    /**
     * Updates cached map-level features.
     */
    private void updateMapEnvironment(GridMap map) {
        if (map == cachedMap) return;

        this.cachedPctMaintenance = map.getPercentMaintenance();
        this.cachedPctTraffic = map.getPercentHighTraffic();
        this.cachedPctShortcut = map.getPercentShortcut();
        this.cachedPctWall = map.getPercentWall();

        this.cachedMap = map;
    }

    @Override
    public double compute(Node current, Node goal, GridMap map) {
        if (!modelLoaded) {
            // Fallback to simple Manhattan distance
            return Math.abs(current.x - goal.x) + Math.abs(current.y - goal.y);
        }

        // 1. Update Map Context
        updateMapEnvironment(map);

        // 2. Prepare input features (same order as training)
        double[] input = new double[inputSize];
        double dx = Math.abs(current.x - goal.x);
        double dy = Math.abs(current.y - goal.y);
        input[0] = dx + dy;  // manhattan_dist
        input[1] = Math.sqrt(dx * dx + dy * dy);  // euclidean_dist
        input[2] = cachedPctMaintenance;  // pct_maintenance
        input[3] = cachedPctTraffic;  // pct_hightraffic
        input[4] = cachedPctShortcut;  // pct_shortcut
        input[5] = cachedPctWall;  // pct_wall

        // 3. Forward pass through the neural network

        // Layer 1: Input -> Hidden1 (with ReLU activation)
        double[] hidden1 = new double[hidden1Size];
        for (int j = 0; j < hidden1Size; j++) {
            double sum = layer1Biases[j];
            for (int i = 0; i < inputSize; i++) {
                sum += input[i] * layer1Weights[j][i];
            }
            hidden1[j] = relu(sum);
        }

        // Layer 2: Hidden1 -> Hidden2 (with ReLU activation)
        double[] hidden2 = new double[hidden2Size];
        for (int j = 0; j < hidden2Size; j++) {
            double sum = layer2Biases[j];
            for (int i = 0; i < hidden1Size; i++) {
                sum += hidden1[i] * layer2Weights[j][i];
            }
            hidden2[j] = relu(sum);
        }

        // Layer 3: Hidden2 -> Output (linear, no activation)
        double output = layer3Bias;
        for (int i = 0; i < hidden2Size; i++) {
            output += hidden2[i] * layer3Weights[i];
        }

        // 4. Safety checks - heuristics cannot be negative
        return Math.max(0, output);
    }
}

