import json
import pandas as pd
import numpy as np
from sklearn.linear_model import LinearRegression, Ridge, Lasso, ElasticNet
from sklearn.preprocessing import PolynomialFeatures, StandardScaler
from sklearn.pipeline import Pipeline
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_squared_error, r2_score, mean_absolute_error
from sklearn.neural_network import MLPRegressor
import warnings
warnings.filterwarnings('ignore')

# ==========================================
# 1. LOAD DATA
# ==========================================
data_file = 'final_output/training_data.jsonl'
data = []

print(f"Loading {data_file}...")
try:
    with open(data_file, 'r') as f:
        for line in f:
            if line.strip():
                data.append(json.loads(line))
except FileNotFoundError:
    print("Error: training_data.jsonl not found.")
    exit(1)

if len(data) == 0:
    print("Error: No data found in training_data.jsonl")
    exit(1)

df = pd.DataFrame(data)
print(f"Loaded {len(df)} samples")

# ==========================================
# 2. DEFINE FEATURES (Exactly 6 items)
# ==========================================
# These must match the order in DataExporter.java
features = [
    'manhattan_dist',    # Index 0
    'euclidean_dist',    # Index 1
    'pct_maintenance',   # Index 2
    'pct_hightraffic',   # Index 3
    'pct_shortcut',      # Index 4
    'pct_wall'           # Index 5
]

# Check if columns exist (Safety check)
missing_cols = [col for col in features if col not in df.columns]
if missing_cols:
    print(f"Error: Your JSON is missing these columns: {missing_cols}")
    print("Available columns:", list(df.columns))
    print("Check if your Java Generator is actually saving them.")
    exit(1)

# Data validation
if 'optimal_cost' not in df.columns:
    print("Error: 'optimal_cost' column not found in data")
    exit(1)

# Check for invalid values
if df['optimal_cost'].isna().any() or (df['optimal_cost'] < 0).any():
    print("Warning: Found invalid optimal_cost values (NaN or negative)")
    df = df[(df['optimal_cost'].notna()) & (df['optimal_cost'] >= 0)]

print(f"Data summary:")
print(f"  Optimal cost range: [{df['optimal_cost'].min():.2f}, {df['optimal_cost'].max():.2f}]")
print(f"  Optimal cost mean: {df['optimal_cost'].mean():.2f}")
print(f"  Optimal cost std: {df['optimal_cost'].std():.2f}")

X = df[features]
y = df['optimal_cost']

# ==========================================
# 3. PREPARE DATA
# ==========================================
print(f"\nPreparing data for training...")
print(f"Total samples: {len(X)}")

# Split for evaluation
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
print(f"Training samples: {len(X_train)}, Test samples: {len(X_test)}")

# ==========================================
# 4. DEFINE MODELS
# ==========================================
models = {}

# 1. Linear Regression
models['LinearRegression'] = LinearRegression()

# 2. Ridge Regression (L2 regularization)
models['Ridge'] = Ridge(alpha=1.0, max_iter=1000)

# 3. Lasso Regression (L1 regularization)
models['Lasso'] = Lasso(alpha=0.1, max_iter=1000)

# 4. ElasticNet (L1 + L2 regularization)
models['ElasticNet'] = ElasticNet(alpha=0.1, l1_ratio=0.5, max_iter=1000)

# 5. Polynomial Regression (degree 2)
poly_features = PolynomialFeatures(degree=2, include_bias=False)
models['Polynomial2'] = Pipeline([
    ('poly', poly_features),
    ('reg', LinearRegression())
])

# 6. Small MLP (Multi-Layer Perceptron)
# Architecture: 6 inputs -> 8 hidden -> 4 hidden -> 1 output
models['MLP'] = MLPRegressor(
    hidden_layer_sizes=(8, 4),
    activation='relu',
    solver='adam',
    alpha=0.01,  # L2 regularization
    learning_rate='adaptive',
    max_iter=500,
    random_state=42,
    early_stopping=True,
    validation_fraction=0.1
)

# ==========================================
# 5. TRAIN ALL MODELS
# ==========================================
print(f"\n{'='*70}")
print(f"{'Training All Models':^70}")
print(f"{'='*70}\n")

# Track which models succeeded
trained_models = {}
failed_models = []

linear_models = ['LinearRegression', 'Ridge', 'Lasso', 'ElasticNet', 'Polynomial2']

for model_name, model in models.items():
    print(f"Training {model_name}...", end=" ")
    
    try:
        # Train the model
        model.fit(X_train, y_train)
        
        # Validate the model was trained successfully
        # Try to make predictions
        y_test_pred = model.predict(X_test[:10])  # Test on small sample
        
        # Check for NaN or infinite values
        if np.any(np.isnan(y_test_pred)) or np.any(np.isinf(y_test_pred)):
            raise ValueError("Model produced NaN or infinite predictions")
        
        # Evaluate full performance
        y_train_pred = model.predict(X_train)
        y_test_pred = model.predict(X_test)
        
        train_r2 = r2_score(y_train, y_train_pred)
        test_r2 = r2_score(y_test, y_test_pred)
        test_rmse = np.sqrt(mean_squared_error(y_test, y_test_pred))
        test_mae = mean_absolute_error(y_test, y_test_pred)
        
        # Store successfully trained model
        trained_models[model_name] = {
            'model': model,
            'train_r2': train_r2,
            'test_r2': test_r2,
            'test_rmse': test_rmse,
            'test_mae': test_mae
        }
        
        print(f"✓ SUCCESS - Test R²: {test_r2:.4f}, RMSE: {test_rmse:.2f}")
        
    except Exception as e:
        failed_models.append(model_name)
        print(f"✗ FAILED - {str(e)}")
        continue

# ==========================================
# 6. VALIDATION SUMMARY
# ==========================================
print(f"\n{'='*70}")
print(f"{'Training Summary':^70}")
print(f"{'='*70}")
print(f"Successfully trained: {len(trained_models)}/{len(models)} models")

if failed_models:
    print(f"Failed models: {', '.join(failed_models)}")
    print(f"⚠️  Warning: {len(failed_models)} model(s) failed to train!")
else:
    print("✅ All models trained successfully!")

print(f"{'='*70}\n")

# ==========================================
# 7. SAVE ALL LINEAR MODELS
# ==========================================
print("Saving model weights...\n")

saved_count = 0
failed_save = []

for model_name in linear_models:
    if model_name not in trained_models:
        failed_save.append(model_name)
        continue
    
    try:
        model = trained_models[model_name]['model']
        metrics = trained_models[model_name]
        
        # Extract coefficients
        if isinstance(model, Pipeline):
            # Polynomial model - extract original feature coefficients
            regressor = model.named_steps['reg']
            # For polynomial, we'll use the first 6 coefficients (original features)
            # This is an approximation since polynomial has more features
            poly_features = model.named_steps['poly']
            # Get feature names to find original features
            feature_names = poly_features.get_feature_names_out(features)
            # Find indices of original features (degree 1 only)
            original_indices = [i for i, name in enumerate(feature_names) 
                              if name.count(' ') == 0]  # Original features have no spaces (no interactions)
            
            if len(original_indices) >= 6:
                coef = regressor.coef_[original_indices[:6]]
            else:
                # Fallback: use first 6 coefficients
                coef = regressor.coef_[:6]
            intercept = regressor.intercept_
        else:
            # Standard linear model
            coef = model.coef_
            intercept = model.intercept_
        
        # Validate coefficients
        if len(coef) < 6:
            raise ValueError(f"Expected 6 coefficients, got {len(coef)}")
        
        # Save weights to file
        output_path = f"ml_weights_{model_name.lower()}.properties"
        
        with open(output_path, "w") as f:
            f.write("# ML Heuristic Weights\n")
            f.write(f"# Model Type: {model_name}\n")
            f.write(f"# Trained on {len(X)} samples\n")
            f.write(f"# Test R² Score: {metrics['test_r2']:.4f}, RMSE: {metrics['test_rmse']:.4f}, MAE: {metrics['test_mae']:.4f}\n")
            f.write(f"# Feature order: manhattan_dist, euclidean_dist, pct_maintenance, pct_hightraffic, pct_shortcut, pct_wall\n")
            f.write("\n")
            f.write(f"MODEL_TYPE={model_name}\n")
            f.write(f"W_MANHATTAN={coef[0]:.6f}\n")
            f.write(f"W_EUCLIDEAN={coef[1]:.6f}\n")
            f.write(f"W_MAINTENANCE={coef[2]:.6f}\n")
            f.write(f"W_TRAFFIC={coef[3]:.6f}\n")
            f.write(f"W_SHORTCUT={coef[4]:.6f}\n")
            f.write(f"W_WALL={coef[5]:.6f}\n")
            f.write(f"INTERCEPT={intercept:.6f}\n")
        
        print(f"  ✓ Saved {model_name} -> {output_path} (R²: {metrics['test_r2']:.4f})")
        saved_count += 1
        
        # Save the first linear model as the default (for backward compatibility)
        if model_name == 'LinearRegression':
            import shutil
            shutil.copy(output_path, "ml_weights.properties")
            print(f"  ✓ Also saved as ml_weights.properties (default)")
        
    except Exception as e:
        failed_save.append(model_name)
        print(f"  ✗ Failed to save {model_name}: {str(e)}")

# Export MLP model (neural network)
if 'MLP' in trained_models:
    mlp_metrics = trained_models['MLP']
    mlp_model = trained_models['MLP']['model']
    print(f"\n  ℹ MLP model trained successfully (R²: {mlp_metrics['test_r2']:.4f})")
    print(f"    Exporting MLP weights and biases for Java...")
    
    try:
        # Get the MLPRegressor
        # Architecture: 6 inputs -> 8 hidden -> 4 hidden -> 1 output
        
        # Get weights and biases
        coefs = mlp_model.coefs_  # List of weight matrices
        intercepts = mlp_model.intercepts_  # List of bias vectors
        
        output_path = "ml_weights_mlp.properties"
        
        with open(output_path, "w") as f:
            f.write("# MLP (Multi-Layer Perceptron) Neural Network Weights\n")
            f.write(f"# Model Type: MLP\n")
            f.write(f"# Architecture: 6 -> 8 -> 4 -> 1\n")
            f.write(f"# Activation: relu\n")
            f.write(f"# Trained on {len(X)} samples\n")
            f.write(f"# Test R² Score: {mlp_metrics['test_r2']:.4f}, RMSE: {mlp_metrics['test_rmse']:.4f}\n")
            f.write(f"# Feature order: manhattan_dist, euclidean_dist, pct_maintenance, pct_hightraffic, pct_shortcut, pct_wall\n")
            f.write("\n")
            f.write("MODEL_TYPE=MLP\n")
            f.write(f"INPUT_SIZE=6\n")
            f.write(f"HIDDEN1_SIZE=8\n")
            f.write(f"HIDDEN2_SIZE=4\n")
            f.write(f"OUTPUT_SIZE=1\n")
            f.write("\n")
            
            # Layer 1: Input -> Hidden1 (6 -> 8)
            # Weights: 8x6 matrix, stored row by row
            f.write("# Layer 1 Weights (8x6): Hidden1 = relu(Input * W1 + b1)\n")
            for i in range(len(coefs[0][0])):  # 6 input neurons
                for j in range(len(coefs[0])):  # 8 hidden1 neurons
                    weight = coefs[0][j][i]  # Weight from input i to hidden1 j
                    f.write(f"L1_W_{j}_{i}={weight:.6f}\n")
            
            f.write("\n# Layer 1 Biases (8)\n")
            for j in range(len(intercepts[0])):
                bias = intercepts[0][j]
                f.write(f"L1_B_{j}={bias:.6f}\n")
            
            # Layer 2: Hidden1 -> Hidden2 (8 -> 4)
            f.write("\n# Layer 2 Weights (4x8): Hidden2 = relu(Hidden1 * W2 + b2)\n")
            for i in range(len(coefs[1][0])):  # 8 hidden1 neurons
                for j in range(len(coefs[1])):  # 4 hidden2 neurons
                    weight = coefs[1][j][i]  # Weight from hidden1 i to hidden2 j
                    f.write(f"L2_W_{j}_{i}={weight:.6f}\n")
            
            f.write("\n# Layer 2 Biases (4)\n")
            for j in range(len(intercepts[1])):
                bias = intercepts[1][j]
                f.write(f"L2_B_{j}={bias:.6f}\n")
            
            # Layer 3: Hidden2 -> Output (4 -> 1)
            f.write("\n# Layer 3 Weights (1x4): Output = Hidden2 * W3 + b3 (no activation)\n")
            for i in range(len(coefs[2][0])):  # 4 hidden2 neurons
                weight = coefs[2][0][i]  # Weight from hidden2 i to output
                f.write(f"L3_W_0_{i}={weight:.6f}\n")
            
            f.write("\n# Layer 3 Bias (1)\n")
            f.write(f"L3_B_0={intercepts[2][0]:.6f}\n")
        
        print(f"  ✓ Saved MLP model -> {output_path}")
        saved_count += 1
        
    except Exception as e:
        print(f"  ✗ Failed to export MLP: {str(e)}")
        failed_save.append('MLP')

print(f"\n{'='*70}")
print(f"Summary: {saved_count} model(s) saved successfully")
if failed_save:
    print(f"Failed to save: {', '.join(failed_save)}")
print(f"{'='*70}")
print("✅ Model training complete!")