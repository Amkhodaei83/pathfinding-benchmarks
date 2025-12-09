# PowerShell Script to Run Benchmark Suite and Shutdown PC
# This version automates the Main menu interface
# -------------------------------------------------------

# 1. Setup Environment
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  PATHFINDING BENCHMARK AUTOMATION" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

# 2. Check Java
Write-Host "[1/4] Checking Java installation..." -ForegroundColor Yellow
if (Get-Command "java" -ErrorAction SilentlyContinue) {
    $javaVersion = java -version 2>&1 | Select-Object -First 1
    Write-Host "[OK] Java found: $javaVersion" -ForegroundColor Green
} else {
    Write-Host "[ERROR] Java not found! Please install Java JDK." -ForegroundColor Red
    exit 1
}

# 3. Define Paths
$srcPath = Join-Path $scriptDir "src"
$outPath = Join-Path $scriptDir "out\production\pathfinding"

# 4. Compile Java Files
Write-Host ""
Write-Host "[2/4] Compiling Java source files..." -ForegroundColor Yellow

# Create output directory
if (-not (Test-Path $outPath)) {
    New-Item -ItemType Directory -Path $outPath -Force | Out-Null
}

# Get source files
$sourceFiles = Get-ChildItem -Path $srcPath -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }

if (-not $sourceFiles) {
    Write-Host "[ERROR] No .java files found in $srcPath" -ForegroundColor Red
    exit 1
}

# Compile
$compileArgs = @("-d", "$outPath", "-sourcepath", "$srcPath") + $sourceFiles
& javac $compileArgs 2>&1 | Out-Null

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed!" -ForegroundColor Red
    exit 1
} else {
    Write-Host "[OK] Compilation successful" -ForegroundColor Green
}

# 5. Run BenchmarkRunner via Main
Write-Host ""
Write-Host "[3/4] Running BenchmarkRunner (Menu Option 1)..." -ForegroundColor Yellow
Write-Host "==========================================" -ForegroundColor Cyan
$benchmarkStart = Get-Date

# We pipe "1" (Run Benchmarks) then "0" (Exit) to the Main class
# `n is a new line
$inputStr = "1`n0"

$inputStr | java -cp "$outPath" Main
$benchmarkExitCode = $LASTEXITCODE

$benchmarkDuration = (Get-Date) - $benchmarkStart

if ($benchmarkExitCode -ne 0) {
    Write-Host ""
    Write-Host "[ERROR] Benchmark run failed." -ForegroundColor Red
    # We continue anyway because sometimes Java exits weirdly with piped input
} else {
    Write-Host ""
    Write-Host "[OK] Benchmark completed in $($benchmarkDuration.ToString('mm\:ss'))" -ForegroundColor Green
}

# 6. Run MLBenchmarkRunner via Main
Write-Host ""
Write-Host "[4/4] Running MLBenchmarkRunner (Menu Option 4)..." -ForegroundColor Yellow
Write-Host "==========================================" -ForegroundColor Cyan
$mlStart = Get-Date

# We pipe "4" (Run ML Benchmarks) then "0" (Exit) to the Main class
$inputStr = "4`n0"

$inputStr | java -cp "$outPath" Main
$mlExitCode = $LASTEXITCODE

$mlDuration = (Get-Date) - $mlStart

if ($mlExitCode -ne 0) {
    Write-Host ""
    Write-Host "[ERROR] ML Benchmark run failed." -ForegroundColor Red
} else {
    Write-Host ""
    Write-Host "[OK] ML Benchmark completed in $($mlDuration.ToString('mm\:ss'))" -ForegroundColor Green
}

# 7. Summary and Shutdown
Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  BENCHMARK SUITE COMPLETE" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Total time: $((($benchmarkDuration + $mlDuration).ToString('mm\:ss')))" -ForegroundColor White
Write-Host "Results saved to: final_output/benchmark_results.csv" -ForegroundColor Green
Write-Host ""

Write-Host "==========================================" -ForegroundColor Red
Write-Host "  WARNING: PC WILL SHUTDOWN IN 30 SECONDS" -ForegroundColor Red
Write-Host "==========================================" -ForegroundColor Red
Write-Host "Press Ctrl+C to cancel shutdown" -ForegroundColor Yellow

for ($i = 30; $i -gt 0; $i--) {
    Write-Host "`rShutting down in $i seconds... " -NoNewline -ForegroundColor Yellow
    Start-Sleep -Seconds 1
}

Write-Host "`nShutting down PC now..." -ForegroundColor Red
shutdown /s /t 0