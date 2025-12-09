/**
 * Machine Learning Heuristic Analysis Engine
 * File: ml_script.js
 * Fix: Automatically detects ALL heuristics from CSV
 */

const translations = {
    en: {
        sidebar_title: "AI Control Panel",
        lbl_map_size: "Map Size",
        lbl_difficulty: "Difficulty",
        lbl_compare_vs: "Compare AI Against",
        lbl_ml_model: "Select ML Model",
        opt_all: "All / Any",
        btn_reset: "Reset Filters",
        btn_ml_guide: "🤖 ML Heuristics Guide",
        
        edu_ml_title: "🤖 AI Insight",
        edu_ml_desc: "The ML model predicts path cost using features like obstacle density. This report compares its average performance against standard math.",

        report_title: "AI Heuristic Performance Report",
        report_subtitle: "Statistical comparison: Machine Learning vs Standard methods.",
        
        kpi_ml_dominance: "AI Efficiency (Avg)",
        kpi_node_reduction: "Avg Work Saved",
        kpi_time_overhead: "Avg Time Difference",
        kpi_accuracy: "Avg Cost Deviation",

        chart_1_title: "1. Search Efficiency (Avg Nodes)",
        chart_1_desc: "Lower bars = Less work performed.",
        note_title: "🎓 Analysis:",
        note_1_text: "If Purple (AI) is lower, it navigated the mazes more intelligently on average.",
        
        chart_2_title: "2. Performance Distribution",
        chart_2_desc: "Comparing the spread of Nodes Expanded. Lower/Left is better.",
        
        chart_3_title: "3. The Cost of Thinking (Time)",
        chart_3_desc: "Is the complex AI calculation actually slower?",
        note_3_text: "AI reduces steps (Nodes), but each step takes longer to calculate. We want positive savings here.",

        // --- NEW TRANSLATIONS ADDED HERE ---
        ml_modal_title: "🤖 Machine Learning Heuristics Guide",
        ml_intro_title: "What is a Machine Learning Heuristic?",
        ml_intro_desc: "Instead of using fixed mathematical formulas, ML heuristics learn from data. They analyze thousands of solved maps to discover patterns: 'Mud increases cost', 'Shortcuts are valuable', etc. The model then predicts path costs based on these learned patterns.",
        ml_linear_title: "Linear Models (Regression-Based)",
        ml_neural_title: "Neural Network Model",
        ml_features_title: "Input Features",
        ml_training_title: "💡 Training Process",
        ml_training_desc: "Models are trained on 1000+ solved maps. Each map provides features (terrain percentages, distances) and the label (optimal cost found by SCALED_MANHATTAN). The model learns to predict optimal costs for new maps based on these patterns.",
        
        desc_linear_regression: "Standard linear regression. Learns optimal weights for each feature through least squares minimization. Simple, interpretable, and fast. Good baseline for ML heuristics.",
        desc_ridge: "Linear regression with L2 regularization. Penalizes large weights to prevent overfitting. Useful when you have many features or multicollinearity issues.",
        desc_lasso: "Linear regression with L1 regularization. Can set some weights to exactly zero, performing automatic feature selection. Useful for identifying which features matter most.",
        desc_elasticnet: "Combines benefits of both Ridge and Lasso. Uses both L1 and L2 penalties. Good balance between feature selection and stability. Often performs best in practice.",
        desc_polynomial2: "Polynomial regression of degree 2. Captures non-linear relationships and feature interactions (e.g., 'Manhattan × Traffic density'). More expressive than linear models.",
        desc_mlp: "Deep learning model with multiple hidden layers. Can learn complex non-linear relationships and feature interactions automatically. Most expressive but also most complex.",
        
        feat_manhattan: "Grid distance to goal",
        feat_euclidean: "Straight-line distance to goal",
        feat_maintenance: "Percentage of map with maintenance zones (cost 5)",
        feat_traffic: "Percentage of map with high traffic (cost 10)",
        feat_shortcut: "Percentage of map with shortcuts (cost 0.5)",
        feat_wall: "Percentage of map with walls (impassable)"
    },
    fa: {
        sidebar_title: "پنل کنترل هوش مصنوعی",
        lbl_map_size: "اندازه نقشه",
        lbl_difficulty: "سختی",
        lbl_compare_vs: "مقایسه با",
        opt_all: "همه موارد",
        btn_reset: "بازنشانی فیلترها",
        btn_ml_guide: "🤖 راهنمای هیوریستیک‌های ML",
        lbl_ml_model: "انتخاب مدل ML",
        edu_ml_title: "🤖 بینش هوش مصنوعی",
        edu_ml_desc: "مدل یادگیری ماشین هزینه مسیر را پیش‌بینی می‌کند.",
        report_title: "گزارش عملکرد هیوریستیک AI",
        report_subtitle: "مقایسه آماری بین هوش مصنوعی و روش‌های استاندارد.",
        kpi_ml_dominance: "کارایی AI (میانگین)",
        kpi_node_reduction: "کاهش کار",
        kpi_time_overhead: "تفاوت زمانی",
        kpi_accuracy: "انحراف هزینه",
        chart_1_title: "۱. کارایی جستجو (میانگین)",
        chart_1_desc: "ستون کمتر = کار کمتر.",
        note_title: "🎓 تحلیل:",
        note_1_text: "اگر بنفش پایین‌تر است، AI مسیر بهتری یافته است.",
        chart_2_title: "۲. توزیع عملکرد",
        chart_2_desc: "مقایسه پراکندگی گره‌ها. پایین/چپ بهتر است.",
        chart_3_title: "۳. هزینه تفکر (زمان)",
        chart_3_desc: "آیا محاسبه AI کند است؟",
        note_3_text: "AI گام‌ها را کم می‌کند، اما هر گام محاسبات سنگین‌تری دارد.",

        // --- NEW TRANSLATIONS ADDED HERE ---
        ml_modal_title: "🤖 راهنمای هیوریستیک‌های یادگیری ماشین",
        ml_intro_title: "هیوریستیک یادگیری ماشین چیست؟",
        ml_intro_desc: "به جای استفاده از فرمول‌های ریاضی ثابت، هیوریستیک‌های ML از داده‌ها یاد می‌گیرند. آن‌ها هزاران نقشه حل‌شده را تحلیل می‌کنند تا الگوها را کشف کنند: «گل‌ولای هزینه را افزایش می‌دهد»، «میان‌برها ارزشمندند» و غیره. سپس مدل هزینه مسیر را بر اساس این الگوهای یادگرفته شده پیش‌بینی می‌کند.",
        
        ml_linear_title: "مدل‌های خطی (مبتنی بر رگرسیون)",
        ml_neural_title: "مدل شبکه عصبی",
        ml_features_title: "ویژگی‌های ورودی",
        ml_training_title: "💡 فرآیند آموزش",
        ml_training_desc: "مدل‌ها روی بیش از ۱۰۰۰ نقشه حل‌شده آموزش دیده‌اند. هر نقشه ویژگی‌ها (درصدهای زمین، فاصله‌ها) و برچسب (هزینه بهینه یافت شده توسط SCALED_MANHATTAN) را فراهم می‌کند. مدل یاد می‌گیرد تا هزینه‌های بهینه برای نقشه‌های جدید را بر اساس این الگوها پیش‌بینی کند.",

        desc_linear_regression: "رگرسیون خطی استاندارد. وزن‌های بهینه برای هر ویژگی را از طریق کمترین مربعات یاد می‌گیرد. ساده، قابل تفسیر و سریع. پایه خوبی برای هیوریستیک‌های ML.",
        desc_ridge: "رگرسیون خطی با منظم‌سازی L2. وزن‌های بزرگ را جریمه می‌کند تا از بیش‌برازش (Overfitting) جلوگیری کند. زمانی مفید است که ویژگی‌های زیادی دارید یا هم‌خطی وجود دارد.",
        desc_lasso: "رگرسیون خطی با منظم‌سازی L1. می‌تواند برخی وزن‌ها را دقیقاً صفر کند که منجر به انتخاب خودکار ویژگی می‌شود. برای شناسایی ویژگی‌های مهم مفید است.",
        desc_elasticnet: "مزایای Ridge و Lasso را ترکیب می‌کند. از هر دو جریمه L1 و L2 استفاده می‌کند. تعادل خوبی بین انتخاب ویژگی و پایداری. اغلب در عمل بهترین عملکرد را دارد.",
        desc_polynomial2: "رگرسیون چندجمله‌ای درجه ۲. روابط غیرخطی و تعاملات ویژگی‌ها (مثلاً «فاصله منهتن × تراکم ترافیک») را ثبت می‌کند. بیانگرتر از مدل‌های خطی است.",
        desc_mlp: "مدل یادگیری عمیق با چندین لایه پنهان. می‌تواند روابط پیچیده غیرخطی و تعاملات ویژگی‌ها را به صورت خودکار یاد بگیرد. قدرتمندترین اما پیچیده‌ترین مدل.",

        feat_manhattan: "فاصله شبکه‌ای (Manhattan) تا هدف",
        feat_euclidean: "فاصله مستقیم (Euclidean) تا هدف",
        feat_maintenance: "درصد نقشه با مناطق تعمیر و نگهداری (هزینه ۵)",
        feat_traffic: "درصد نقشه با ترافیک سنگین (هزینه ۱۰)",
        feat_shortcut: "درصد نقشه با میان‌برها (هزینه ۰.۵)",
        feat_wall: "درصد نقشه با دیوارها (غیرقابل عبور)"
    }
};

const app = {
    data: { raw: [], processed: [] },
    config: { csvFile: 'benchmark_results.csv' },
    state: { lang: 'en', sidebarOpen: false },

    init: function() {
        console.log("App Initializing...");
        
        const fileInput = document.getElementById('fallbackInput');
        if(fileInput) {
            fileInput.addEventListener('change', (e) => {
                const file = e.target.files[0];
                if(file) {
                    this.setLoadingState("Parsing CSV file...");
                    Papa.parse(file, { 
                        header: true, 
                        dynamicTyping: true, 
                        skipEmptyLines: true, 
                        complete: (res) => this.handleDataLoad(res),
                        error: (err) => console.error("Manual parse error:", err)
                    });
                }
            });
        }

        this.loadCSV();

        window.addEventListener('resize', () => {
             document.querySelectorAll('.js-plotly-plot').forEach(el => Plotly.Plots.resize(el));
        });
    },

    toggleTheme: function() {
        document.body.classList.toggle('light-mode');
        document.getElementById('themeBtn').innerText = document.body.classList.contains('light-mode') ? '☾' : '☀';
        this.runAnalysis();
    },

    toggleLang: function() {
        this.state.lang = this.state.lang === 'en' ? 'fa' : 'en';
        document.documentElement.dir = this.state.lang === 'fa' ? 'rtl' : 'ltr';
        document.getElementById('langBtn').innerText = this.state.lang === 'en' ? 'FA' : 'EN';
        this.updateTexts();
        this.runAnalysis(); 
    },

    toggleSidebar: function() {
        this.state.sidebarOpen = !this.state.sidebarOpen;
        const el = document.getElementById('mainSidebar');
        const overlay = document.querySelector('.sidebar-overlay');
        this.state.sidebarOpen ? el.classList.add('active') : el.classList.remove('active');
        overlay.style.display = this.state.sidebarOpen ? 'block' : 'none';
    },

    toggleModal: function(id) {
        const modal = document.getElementById(id);
        if (modal) {
            if (modal.classList.contains('open')) {
                modal.classList.remove('open');
                setTimeout(() => modal.style.display = 'none', 300);
            } else {
                modal.style.display = 'flex';
                setTimeout(() => {
                    modal.classList.add('open');
                    this.updateTexts(); // Update translations when modal opens
                }, 10);
            }
        }
    },

    updateTexts: function() {
        const t = translations[this.state.lang];
        document.querySelectorAll('[data-i18n]').forEach(el => {
            const key = el.getAttribute('data-i18n');
            if(t[key]) el.innerText = t[key];
        });
    },

    setLoadingState: function(msg) {
        const overlay = document.getElementById('statusOverlay');
        const text = document.getElementById('loadingStatus');
        if(text) text.innerText = msg;
        overlay.style.display = 'block';
    },

    loadCSV: function() {
        setTimeout(() => {
            if (this.data.raw.length === 0) {
                this.showManualUpload();
            }
        }, 1500);

        const tryLoad = (filename) => {
            Papa.parse(filename, {
                download: true, header: true, dynamicTyping: true, skipEmptyLines: true, worker: false,
                complete: (res) => {
                    if (res.data && res.data.length > 0) {
                        this.handleDataLoad(res);
                    } else {
                        // Try alternative filename
                        if (filename === 'benchmark_results.csv') {
                            tryLoad('Data.csv');
                        } else {
                            this.showManualUpload();
                        }
                    }
                },
                error: () => {
                    // Try alternative filename
                    if (filename === 'benchmark_results.csv') {
                        tryLoad('Data.csv');
                    } else {
                        console.error("CSV load failed. Please select file manually.");
                        this.showManualUpload();
                    }
                }
            });
        };
        tryLoad(this.config.csvFile);
    },

    showManualUpload: function() {
        const overlay = document.getElementById('statusOverlay');
        const label = document.getElementById('manualLabel');
        const text = document.getElementById('loadingStatus');
        const loader = document.querySelector('.loader');
        
        if(loader) loader.style.display = 'none';
        if(text) text.innerHTML = "Auto-load blocked by browser.<br><b>Please select Data.csv manually:</b>";
        if(label) label.style.display = 'inline-block';
        overlay.style.display = 'block';
    },

    handleDataLoad: function(results) {
        if (!results.data || results.data.length === 0) {
            this.showManualUpload();
            return;
        }
        
        this.data.raw = results.data;
        this.processData();
        
        if (this.data.processed.length === 0) {
            alert("No valid 'AStar' data found.");
            return;
        }

        this.initFilters();
        this.updateTexts();
        document.getElementById('statusOverlay').style.display = 'none';
        this.runAnalysis();
    },

    processData: function() {
        this.data.processed = this.data.raw
            .filter(d => d.Algorithm && d.Algorithm.trim() === 'AStar')
            .map(row => ({
                ...row,
                CostNum: (row.Cost === 'Infinity' || row.Cost === Infinity) ? 999999 : parseFloat(row.Cost),
                TimeMs: (row.Time_ns || 0) / 1_000_000,
                SuccessBool: String(row.Success).toLowerCase() === 'true',
                Nodes: row.NodesExpanded,
                Heuristic: row.Heuristic ? row.Heuristic.trim() : 'Unknown'
            }));
    },
initFilters: function() {
        // 1. Map Sizes and Difficulties
        const sizes = [...new Set(this.data.processed.map(d => d.MapSize))].sort((a,b)=>a-b);
        const diffs = [...new Set(this.data.processed.map(d => d.Difficulty))].sort();
        
        const appendOpts = (id, arr) => {
            const sel = document.getElementById(id);
            if(!sel) return;
            sel.innerHTML = '<option value="ALL" data-i18n="opt_all">All</option>'; 
            arr.forEach(v => sel.innerHTML += `<option value="${v}">${v}</option>`);
        };
        appendOpts('filterSize', sizes);
        appendOpts('filterDiff', diffs);

        // 2. Identify ML Heuristics vs Standard Heuristics
        const allHeuristics = [...new Set(this.data.processed.map(d => d.Heuristic))];
        
        // ML heuristics: MLP, LinearRegression, Ridge, Lasso, ElasticNet, Polynomial2, MachineLearned
        const mlHeuristicNames = ['MLP', 'LinearRegression', 'Ridge', 'Lasso', 'ElasticNet', 'Polynomial2', 'MachineLearned'];
        const mlHeuristics = allHeuristics.filter(h => mlHeuristicNames.includes(h)).sort();
        const stdHeuristics = allHeuristics.filter(h => !mlHeuristicNames.includes(h)).sort();

        // 3. ML Model Selector
        const mlSel = document.getElementById('filterMLModel');
        if(mlSel) {
            mlSel.innerHTML = '';
            if(mlHeuristics.length > 0) {
                mlHeuristics.forEach(h => {
                    const isSelected = h === 'MLP' || h === 'LinearRegression' ? 'selected' : '';
                    mlSel.innerHTML += `<option value="${h}" ${isSelected}>${h}</option>`;
                });
            } else {
                mlSel.innerHTML = '<option value="ALL">No ML Models Found</option>';
            }
        }

        // 4. Standard Heuristic Comparison Selector
        const compSel = document.getElementById('filterCompare');
        if(compSel) {
            compSel.innerHTML = '';
            // Default to SCALED_MANHATTAN if available, otherwise first standard heuristic
            const defaultHeuristic = stdHeuristics.find(h => h === 'SCALED_MANHATTAN') || stdHeuristics[0] || 'SCALED_MANHATTAN';
            stdHeuristics.forEach(h => {
                const isSelected = h === defaultHeuristic ? 'selected' : '';
                compSel.innerHTML += `<option value="${h}" ${isSelected}>${h}</option>`;
            });
        }
    },

    getFilteredData: function() {
        const size = document.getElementById('filterSize').value;
        const diff = document.getElementById('filterDiff').value;
        return this.data.processed.filter(d => {
            if (size !== 'ALL' && String(d.MapSize) !== size) return false;
            if (diff !== 'ALL' && d.Difficulty !== diff) return false;
            return true;
        });
    },

    resetFilters: function() {
        document.getElementById('filterSize').value = 'ALL';
        document.getElementById('filterDiff').value = 'ALL';
        const mlSel = document.getElementById('filterMLModel');
        if(mlSel) mlSel.value = mlSel.options[0]?.value || 'ALL';
        this.runAnalysis();
    },

runAnalysis: function() {
        // 1. Get ALL data for Win Rate
        const allData = this.getFilteredData();
        
        // Get selected ML model and standard heuristic
        const mlModelSel = document.getElementById('filterMLModel');
        const mlHeuristic = mlModelSel ? mlModelSel.value : 'MLP';
        const targetHeuristic = document.getElementById('filterCompare').value;

        // If "ALL" selected for ML, aggregate all ML models
        const mlHeuristicNames = ['MLP', 'LinearRegression', 'Ridge', 'Lasso', 'ElasticNet', 'Polynomial2', 'MachineLearned'];
        const mlDataAll = mlHeuristic === 'ALL' 
            ? allData.filter(d => mlHeuristicNames.includes(d.Heuristic))
            : allData.filter(d => d.Heuristic === mlHeuristic);
        const stdDataAll = allData.filter(d => d.Heuristic === targetHeuristic);

        if (mlDataAll.length === 0 || stdDataAll.length === 0) {
            console.warn("Insufficient data for comparison.");
            return;
        }

        // 2. CREATE A CLEAN DATASET (Success Only) for Efficiency Charts
        const mlDataSuccess = mlDataAll.filter(d => d.SuccessBool === true);
        const stdDataSuccess = stdDataAll.filter(d => d.SuccessBool === true);

        // --- PART A: KPIs (Win Rate uses ALL data) ---
        const mlWins = mlDataSuccess.length; 
        const mlSuccessRate = (mlWins / mlDataAll.length) * 100;
        document.getElementById('kpiWinRate').innerText = mlSuccessRate.toFixed(1) + '%';

        // --- PART B: Grouping (Uses SUCCESS data) ---
        const groups = {};

        const processGroup = (dataset, type) => {
            dataset.forEach(d => {
                const key = `${d.MapSize}-${d.Difficulty}`;
                if(!groups[key]) groups[key] = { mlNodes:[], stdNodes:[], mlTime:[], stdTime:[], mlCost:[], stdCost:[] };
                
                if(type === 'ML') {
                    groups[key].mlNodes.push(d.Nodes);
                    groups[key].mlTime.push(d.TimeMs);
                    groups[key].mlCost.push(d.CostNum);
                } else {
                    groups[key].stdNodes.push(d.Nodes);
                    groups[key].stdTime.push(d.TimeMs);
                    groups[key].stdCost.push(d.CostNum);
                }
            });
        };

        processGroup(mlDataSuccess, 'ML');
        processGroup(stdDataSuccess, 'STD');

        // Create Virtual Pairs
        const virtualPairs = [];
        Object.keys(groups).forEach(key => {
            const g = groups[key];
            // Only compare if BOTH solved this category
            if (g.mlNodes.length > 0 && g.stdNodes.length > 0) {
                virtualPairs.push({
                    label: key,
                    mlNodes: _.mean(g.mlNodes),
                    stdNodes: _.mean(g.stdNodes),
                    mlTime: _.mean(g.mlTime),
                    stdTime: _.mean(g.stdTime),
                    mlCost: _.mean(g.mlCost),
                    stdCost: _.mean(g.stdCost)
                });
            }
        });

        if (virtualPairs.length === 0) {
            // Optional: Handle empty intersection gracefully
            return;
        }

        this.updateKPIs(virtualPairs);
        this.updateEfficiencyChart(virtualPairs, targetHeuristic, mlHeuristic);
        this.updateBoxPlot(mlDataSuccess, stdDataSuccess, targetHeuristic, mlHeuristic);
        this.updateTimeChart(virtualPairs, targetHeuristic, mlHeuristic);
    },

    updateKPIs: function(pairs) {
        const wins = pairs.filter(p => p.mlNodes < p.stdNodes).length;
        const rate = (wins / pairs.length) * 100;
        document.getElementById('kpiWinRate').innerText = rate.toFixed(1) + '%';

        const totalStd = _.sumBy(pairs, 'stdNodes');
        const totalMl = _.sumBy(pairs, 'mlNodes');
        const reduction = totalStd > 0 ? ((totalStd - totalMl) / totalStd) * 100 : 0;
        const elRed = document.getElementById('kpiReduction');
        elRed.innerText = (reduction > 0 ? '↓ ' : '↑ ') + Math.abs(reduction).toFixed(1) + '%';
        elRed.style.color = reduction > 0 ? 'var(--success)' : 'var(--danger)';

        const totalStdTime = _.sumBy(pairs, 'stdTime');
        const totalMlTime = _.sumBy(pairs, 'mlTime');
        const avgDiff = (totalMlTime - totalStdTime) / pairs.length;
        const elTime = document.getElementById('kpiTimeOverhead');
        elTime.innerText = (avgDiff > 0 ? '+' : '') + avgDiff.toFixed(2) + ' ms';
        elTime.style.color = avgDiff < 0 ? 'var(--success)' : 'var(--danger)';
        
        const costDiff = _.meanBy(pairs, p => Math.abs(p.mlCost - p.stdCost));
        document.getElementById('kpiAccuracy').innerText = costDiff < 0.5 ? "Optimal" : `+${costDiff.toFixed(2)}`;
    },

    getCommonLayout: function() {
        const isDark = !document.body.classList.contains('light-mode');
        const color = isDark ? '#94a3b8' : '#475569';
        return {
            paper_bgcolor: 'rgba(0,0,0,0)', plot_bgcolor: 'rgba(0,0,0,0)',
            font: { color: color, family: 'Vazirmatn' },
            xaxis: { gridcolor: isDark ? '#334155' : '#e2e8f0' },
            yaxis: { gridcolor: isDark ? '#334155' : '#e2e8f0' },
            margin: { t: 40, b: 40, l: 60, r: 20 },
            showlegend: true, legend: { orientation: 'h', y: -0.2 }
        };
    },

    updateEfficiencyChart: function(pairs, stdName, mlName) {
        const xLabels = pairs.map(p => p.label);
        const mlLabel = mlName === 'ALL' ? 'AI (All Models)' : `AI (${mlName})`;
        
        const trace1 = { x: xLabels, y: pairs.map(p => p.stdNodes), name: stdName, type: 'bar', marker: { color: '#3b82f6' } };
        const trace2 = { x: xLabels, y: pairs.map(p => p.mlNodes), name: mlLabel, type: 'bar', marker: { color: '#8b5cf6' } };

        const layout = this.getCommonLayout();
        layout.barmode = 'group';
        layout.yaxis.title = "Avg Nodes Expanded";

        Plotly.newPlot('chartEfficiency', [trace1, trace2], layout);
    },

    updateBoxPlot: function(mlData, stdData, stdName, mlName) {
        const mlLabel = mlName === 'ALL' ? 'AI (All Models)' : `AI (${mlName})`;
        
        const trace1 = {
            y: stdData.map(d => d.Nodes),
            type: 'box',
            name: stdName,
            marker: { color: '#3b82f6' }
        };
        
        const trace2 = {
            y: mlData.map(d => d.Nodes),
            type: 'box',
            name: mlLabel,
            marker: { color: '#8b5cf6' }
        };

        const layout = this.getCommonLayout();
        layout.yaxis.title = "Nodes Expanded (Log Scale)";
        layout.yaxis.type = 'log'; 

        Plotly.newPlot('chartScatter', [trace1, trace2], layout);
    },

    updateTimeChart: function(pairs, stdName, mlName) {
         const xLabels = pairs.map(p => p.label);
         const mlLabel = mlName === 'ALL' ? 'AI (All Models)' : `AI (${mlName})`;
 
         const trace1 = { x: xLabels, y: pairs.map(p => p.stdTime), name: stdName, type: 'bar', marker: { color: '#3b82f6' } };
         const trace2 = { x: xLabels, y: pairs.map(p => p.mlTime), name: mlLabel, type: 'bar', marker: { color: '#f59e0b' } };
 
         const layout = this.getCommonLayout();
         layout.barmode = 'group';
         layout.yaxis.title = "Avg Time (ms)";
 
         Plotly.newPlot('chartTime', [trace1, trace2], layout);
    }
};

document.addEventListener('DOMContentLoaded', () => app.init());