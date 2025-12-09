/**
 * Pathfinding Edu-Analytics Engine
 * Author: AMIR HOSSEIN KHODAEI
 */

const translations = {
    en: {
        sidebar_title: "Control Panel",
        lbl_map_size: "Map Size (Grid)",
        lbl_difficulty: "Difficulty Level",
        lbl_algorithm: "Algorithm",
        lbl_heuristic_metric: "Chart Metric",
        opt_all: "All / Any",
        opt_nodes: "Nodes Expanded (Efficiency)",
        opt_time: "Time (Speed)",
        opt_cost: "Path Cost",
        btn_reset: "Reset Filters",
        btn_encyclopedia: "📖 Standard Heuristics Guide",
        
        edu_glossary_title: "📚 Mini Glossary",
        term_astar: "A* (A-Star)",
        def_astar: "Finds shortest path but eats memory.",
        term_sma: "SMA*",
        def_sma: "Memory-bounded version. Deletes old nodes when full.",
        term_ml: "ML Heuristic",
        def_ml: "Predicts cost using Linear Regression.",

        report_title: "Pathfinding Algorithm Analysis",
        report_subtitle: "A comparative study of A* vs SMA* under varying memory constraints.",
        
        kpi_total: "Total Runs",
        kpi_success: "Success Rate",
        kpi_time: "Avg Time",
        kpi_optimal: "Optimality",
        kpi_ml_reduction: "Avg Effort Reduction",
        kpi_ml_accuracy: "Cost Prediction Error",

        btn_manual_load: "Select benchmark_results.csv Manually",
        
        chart_1_title: "1. Heuristic Efficiency",
        chart_1_desc: "Comparing how smart different heuristics are. Lower is better.",
        note_title: "🎓 Educational Note:",
        note_1_text: "A heuristic acts like a compass. If the compass is accurate, the robot walks straight to the goal. If it ignores traffic, it explores too many wrong streets.",
        
        chart_2_title: "2. The Memory Wall (SMA*)",
        chart_2_desc: "Visualizing the 'Thrashing' phenomenon when memory runs out.",
        note_2_text: "When SMA* fills its memory, it must 'Prune' (forget) the worst path. If it needs it later, it must 'Regenerate' it. This is Thrashing.",

        chart_3_title: "3. Failure & Survival Analysis",
        chart_4_title: "4. Time vs. Space Tradeoff",
        note_4_text: "A* (Blue) pays with Space to be fast. SMA* (Orange) saves Space but pays with Time.",
        
        chart_5_title: "5. AI vs Standard (Bonus)",
        chart_5_desc: "Direct comparison: Does the Machine Learning model beat the Standard Math?",
        note_5_text: "The bar chart compares Nodes Expanded. If the Purple bar (AI) is lower than the Blue bar (Standard), the Machine Learning model successfully learned the map topology.",

        modal_title: "Standard Heuristics Guide",
        cat_admissible: "A. Admissible Heuristics (Guarantee Optimal Path)",
        cat_inadmissible: "B. Inadmissible Heuristics (Fast but Not Guaranteed Optimal)",
        cat_advanced: "C. Advanced & Tie-Breaking Heuristics",
        
        desc_scaled_manhattan: "Manhattan distance scaled by 0.5 (the minimum cost in the map). This is the ground truth heuristic used to establish optimal costs. It's admissible because it never overestimates the true cost.",
        desc_scaled_euclidean: "Straight-line distance (as the crow flies) scaled by 0.5. More accurate than Manhattan for diagonal movement, but computationally more expensive due to square root calculation.",
        desc_scaled_chebyshev: "Takes the maximum of horizontal and vertical distances. Useful for 8-directional movement (including diagonals). Always ≤ Manhattan distance.",
        desc_dijkstra: "Zero heuristic - essentially turns A* into Dijkstra's algorithm. Explores uniformly in all directions, guaranteeing optimality but with maximum exploration.",
        desc_unscaled_manhattan: "Standard Manhattan distance without scaling. Assumes all cells cost 1.0, which may overestimate when shortcuts (cost 0.5) exist. Can be faster but may miss optimal paths.",
        desc_avg_cost: "Uses average cost (≈4.1) of all terrain types. Aggressive heuristic that may overestimate, leading to faster but potentially suboptimal paths.",
        desc_manhattan_squared: "Squared Manhattan distance. Heavily penalizes longer distances, making the algorithm prefer shorter paths. Inadmissible for most cost functions.",
        desc_cross_product: "Adds a small tie-breaking term based on cross product of vectors. Helps break ties when multiple paths have the same f-cost, preferring paths that stay closer to the straight line from start to goal.",
        desc_cost_aware: "Adapts based on the target cell's cost. Reduces heuristic for shortcuts (pulls towards them) and increases for swamps (pushes away). Slightly inadmissible but often finds better paths.",
        desc_manhattan: "Standard grid distance: |x1-x2| + |y1-y2|.",
        desc_ml: "A Linear Regression model trained on map features. Adjusts estimated cost based on Mud and Traffic density."
    },
    fa: {
        sidebar_title: "پنل تنظیمات",
        lbl_map_size: "اندازه نقشه",
        lbl_difficulty: "درجه سختی",
        lbl_algorithm: "الگوریتم",
        lbl_heuristic_metric: "معیار نمودار",
        opt_all: "همه موارد",
        opt_nodes: "گره‌های باز شده (کارایی)",
        opt_time: "زمان اجرا (سرعت)",
        opt_cost: "هزینه مسیر",
        btn_reset: "بازنشانی فیلترها",
        btn_encyclopedia: "📖 راهنمای هیوریستیک‌های استاندارد",
        
        edu_glossary_title: "📚 واژه‌نامه کوچک",
        term_astar: "الگوریتم A*",
        def_astar: "کوتاه‌ترین مسیر را می‌یابد اما حافظه زیادی مصرف می‌کند.",
        term_sma: "الگوریتم SMA*",
        def_sma: "نسخه حافظه محدود. وقتی حافظه پر شود، گره‌ها را حذف می‌کند.",
        term_ml: "هیوریستیک یادگیری ماشین",
        def_ml: "تخمین هزینه با استفاده از رگرسیون خطی.",

        report_title: "تحلیل الگوریتم‌های مسیریابی",
        report_subtitle: "مقایسه A* و SMA* تحت محدودیت‌های مختلف حافظه.",
        
        kpi_total: "تعداد اجرا",
        kpi_success: "نرخ موفقیت",
        kpi_time: "میانگین زمان",
        kpi_optimal: "بهینگی",
        kpi_ml_reduction: "کاهش تلاش (بهبود)",
        kpi_ml_accuracy: "خطای تخمین هزینه",

        btn_manual_load: "انتخاب دستی فایل benchmark_results.csv",

        chart_1_title: "۱. کارایی هیوریستیک‌ها",
        chart_1_desc: "مقایسه هوشمندی توابع هیوریستیک. مقدار کمتر بهتر است.",
        note_title: "🎓 نکته آموزشی:",
        note_1_text: "هیوریستیک مانند قطب‌نما است. اگر دقیق باشد، ربات مستقیم به هدف می‌رسد. اگر ترافیک را نبیند، ربات مسیرهای اشتباه را چک می‌کند.",
        
        chart_2_title: "۲. دیوار حافظه در SMA*",
        chart_2_desc: "نمایش پدیده Thrashing زمانی که حافظه تمام می‌شود.",
        note_2_text: "وقتی حافظه SMA* پر شود، بدترین مسیر را حذف می‌کند. اگر دوباره به آن نیاز شود، باید دوباره محاسبه شود (Thrashing).",

        chart_3_title: "۳. تحلیل نرخ شکست",
        chart_4_title: "۴. مبادله زمان و فضا",
        note_4_text: "A* (آبی) از فضا خرج می‌کند تا سریع باشد. SMA* (نارنجی) فضا را ذخیره می‌کند اما زمان بیشتری می‌برد.",

        chart_5_title: "۵. هوش مصنوعی در برابر استاندارد (امتیازی)",
        chart_5_desc: "مقایسه مستقیم: آیا مدل یادگیری ماشین بهتر از ریاضیات استاندارد عمل کرد؟",
        note_5_text: "نمودار میله‌ای تعداد گره‌های باز شده را مقایسه می‌کند. اگر ستون بنفش (AI) پایین‌تر از آبی (استاندارد) باشد، یعنی مدل یادگیری ماشین با موفقیت توپولوژی نقشه را یاد گرفته است.",

        modal_title: "راهنمای هیوریستیک‌های استاندارد",
        cat_admissible: "الف. هیوریستیک‌های قابل قبول (ضمانت مسیر بهینه)",
        cat_inadmissible: "ب. هیوریستیک‌های غیرقابل قبول (سریع اما نه لزوماً بهینه)",
        cat_advanced: "ج. هیوریستیک‌های پیشرفته و شکستن تساوی",
        
        desc_scaled_manhattan: "فاصله منهتن ضرب در 0.5 (حداقل هزینه در نقشه). این هیوریستیک حقیقت پایه است که برای تعیین هزینه‌های بهینه استفاده می‌شود. قابل قبول است چون هرگز هزینه واقعی را بیش‌برآورد نمی‌کند.",
        desc_scaled_euclidean: "فاصله خط مستقیم (مستقیم) ضرب در 0.5. برای حرکت مورب دقیق‌تر از منهتن است، اما به دلیل محاسبه جذر محاسباتی گران‌تر است.",
        desc_scaled_chebyshev: "حداکثر فاصله افقی و عمودی را می‌گیرد. برای حرکت 8 جهته (شامل مورب) مفید است. همیشه ≤ فاصله منهتن است.",
        desc_dijkstra: "هیوریستیک صفر - اساساً A* را به الگوریتم دایکسترا تبدیل می‌کند. به طور یکنواخت در همه جهات کاوش می‌کند، بهینگی را تضمین می‌کند اما با حداکثر کاوش.",
        desc_unscaled_manhattan: "فاصله منهتن استاندارد بدون مقیاس. فرض می‌کند همه سلول‌ها هزینه 1.0 دارند، که ممکن است وقتی میانبرها (هزینه 0.5) وجود دارند بیش‌برآورد کند. می‌تواند سریع‌تر باشد اما ممکن است مسیرهای بهینه را از دست بدهد.",
        desc_avg_cost: "از میانگین هزینه (≈4.1) همه انواع زمین استفاده می‌کند. هیوریستیک تهاجمی که ممکن است بیش‌برآورد کند، منجر به مسیرهای سریع‌تر اما احتمالاً غیربهینه می‌شود.",
        desc_manhattan_squared: "فاصله منهتن به توان دو. فاصله‌های طولانی‌تر را به شدت جریمه می‌کند، باعث می‌شود الگوریتم مسیرهای کوتاه‌تر را ترجیح دهد. برای اکثر توابع هزینه غیرقابل قبول است.",
        desc_cross_product: "یک عبارت کوچک شکستن تساوی بر اساس ضرب خارجی بردارها اضافه می‌کند. به شکستن تساوی‌ها کمک می‌کند وقتی چندین مسیر f-cost یکسانی دارند، مسیرهایی را ترجیح می‌دهد که به خط مستقیم از شروع به هدف نزدیک‌تر بمانند.",
        desc_cost_aware: "بر اساس هزینه سلول هدف سازگار می‌شود. هیوریستیک را برای میانبرها کاهش می‌دهد (به سمت آن‌ها می‌کشد) و برای باتلاق‌ها افزایش می‌دهد (دور می‌کند). کمی غیرقابل قبول اما اغلب مسیرهای بهتری پیدا می‌کند.",
        desc_manhattan: "فاصله استاندارد شبکه: |x1-x2| + |y1-y2|.",
        desc_ml: "مدل رگرسیون خطی آموزش‌دیده. هزینه را بر اساس تراکم گل‌ولای و ترافیک تخمین می‌زند."
    }
};

const app = {
    data: { raw: [], processed: [] },
    config: { csvFile: 'benchmark_results.csv', maxPoints: 3000 },
    state: { lang: 'en', sidebarOpen: false },

    init: function() {
        this.loadCSV();
        document.getElementById('fallbackInput').addEventListener('change', (e) => {
            const file = e.target.files[0];
            if(file) Papa.parse(file, { header: true, dynamicTyping: true, skipEmptyLines: true, complete: (res) => this.handleDataLoad(res) });
        });
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
        const el = document.getElementById(id);
        if (el) {
            if(el.classList.contains('open')) {
                el.classList.remove('open');
                setTimeout(() => el.style.display = 'none', 300);
            } else {
                el.style.display = 'flex';
                setTimeout(() => {
                    el.classList.add('open');
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

    loadCSV: function() {
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
                        document.getElementById('statusMsg').innerText = "Load Failed. Please select file manually.";
                        document.getElementById('fallbackLabel').style.display = "inline-block";
                    }
                }
            });
        };
        tryLoad(this.config.csvFile);
    },

    handleDataLoad: function(results) {
        this.data.raw = results.data;
        this.processData();
        this.initFilters();
        this.updateTexts(); 
        document.getElementById('statusOverlay').style.display = 'none';
        this.runAnalysis();
    },

    processData: function() {
        this.data.processed = this.data.raw.map(row => ({
            ...row,
            CostNum: (row.Cost === 'Infinity' || row.Cost === Infinity) ? null : parseFloat(row.Cost),
            TimeMs: (row.Time_ns || 0) / 1_000_000,
            SuccessBool: String(row.Success).toLowerCase() === 'true',
            MemLimitNum: (row.MemoryLimit === -1 || row.MemoryLimit === '-1') ? 1000000000 : row.MemoryLimit,
            MemLimitLabel: row.MemoryLimit === -1 ? 'Unlimited' : row.MemoryLimit
        })).filter(d => d.Algorithm);
    },

    initFilters: function() {
        const sizes = [...new Set(this.data.processed.map(d => d.MapSize))].sort((a,b)=>a-b);
        const diffs = [...new Set(this.data.processed.map(d => d.Difficulty))].sort();
        const appendOpts = (id, arr) => {
            const sel = document.getElementById(id);
            const first = sel.options[0];
            sel.innerHTML = ''; sel.appendChild(first);
            arr.forEach(v => sel.innerHTML += `<option value="${v}">${v}</option>`);
        };
        appendOpts('filterSize', sizes);
        appendOpts('filterDiff', diffs);
    },

    getFilteredData: function() {
        const size = document.getElementById('filterSize').value;
        const diff = document.getElementById('filterDiff').value;
        const algo = document.getElementById('filterAlgo').value;
        return this.data.processed.filter(d => {
            if (size !== 'ALL' && String(d.MapSize) !== size) return false;
            if (diff !== 'ALL' && d.Difficulty !== diff) return false;
            if (algo !== 'ALL' && d.Algorithm !== algo) return false;
            return true;
        });
    },

    resetFilters: function() {
        ['filterSize', 'filterDiff', 'filterAlgo'].forEach(id => document.getElementById(id).value = 'ALL');
        this.runAnalysis();
    },

    runAnalysis: function() {
        const data = this.getFilteredData();
        if(data.length === 0) return;
        this.updateKPIs(data);
        this.updateHeuristicChart(data);
        this.updateMemoryChart(data);
        this.updateFailureChart(data);
        this.updateTradeoffChart(data);
        
        // --- NEW BONUS SECTION ---
        // Check if we have ML data in the FULL dataset (not just filtered)
        this.updateMLComparison(this.data.processed); 
    },

    updateKPIs: function(data) {
        document.getElementById('kpiTotal').innerText = data.length.toLocaleString();
        const success = data.filter(d => d.SuccessBool);
        const rate = (success.length / data.length) * 100;
        const elSucc = document.getElementById('kpiSuccess');
        elSucc.innerText = rate.toFixed(1) + '%';
        elSucc.style.color = rate > 90 ? 'var(--success)' : 'var(--danger)';
        const avgTime = _.meanBy(success, 'TimeMs') || 0;
        document.getElementById('kpiTime').innerText = avgTime.toFixed(2) + ' ms';
        const nonOptimal = success.filter(d => (d.CostNum - d.OptimalCost) > 0.1);
        const elOpt = document.getElementById('kpiOptimal');
        elOpt.innerText = nonOptimal.length === 0 ? "✓ 100%" : `⚠ ${nonOptimal.length} Sub-opt`;
        elOpt.style.color = nonOptimal.length === 0 ? 'var(--success)' : 'var(--danger)';
    },

    getCommonLayout: function() {
        const isDark = !document.body.classList.contains('light-mode');
        const color = isDark ? '#94a3b8' : '#64748b';
        return {
            paper_bgcolor: 'rgba(0,0,0,0)', plot_bgcolor: 'rgba(0,0,0,0)',
            font: { color: color, family: 'Vazirmatn' },
            xaxis: { gridcolor: isDark ? '#334155' : '#cbd5e1' },
            yaxis: { gridcolor: isDark ? '#334155' : '#cbd5e1' },
            margin: { t: 30, b: 40, l: 60, r: 20 },
            showlegend: true, legend: { bgcolor: 'rgba(0,0,0,0)', orientation: 'h', y: -0.2 }
        };
    },

    updateHeuristicChart: function(passedData) {
        const data = passedData || this.getFilteredData();
        const success = data.filter(d => d.SuccessBool);
        const metric = document.getElementById('metricHeuristic').value;
        const heuristics = [...new Set(success.map(d => d.Heuristic))];
        const traces = heuristics.map(h => ({ y: success.filter(d => d.Heuristic === h).map(d => d[metric]), type: 'box', name: h, boxpoints: false }));
        const layout = this.getCommonLayout(); layout.yaxis.title = metric; 
        Plotly.newPlot('chartHeuristic', traces, layout);
    },

    updateMemoryChart: function(data) {
        const sma = data.filter(d => d.Algorithm === 'SMAStar');
        const plotData = sma.length > this.config.maxPoints ? _.sampleSize(sma, this.config.maxPoints) : sma;
        const heuristics = [...new Set(plotData.map(d => d.Heuristic))];
        const traces = heuristics.map(h => ({
            x: plotData.filter(d => d.Heuristic === h).map(d => d.MemLimitNum),
            y: plotData.filter(d => d.Heuristic === h).map(d => d.PrunedNodes),
            mode: 'markers', type: 'scatter', name: h, marker: { size: 6, opacity: 0.7 }
        }));
        const layout = this.getCommonLayout(); layout.xaxis.autorange = 'reversed'; layout.xaxis.title = "Memory Limit"; layout.yaxis.title = "Pruned Nodes";
        Plotly.newPlot('chartMemory', traces, layout);
    },

    updateFailureChart: function(data) {
        const sma = data.filter(d => d.Algorithm === 'SMAStar');
        const groups = _.groupBy(sma, 'MemLimitLabel');
        let x=[], y=[];
        Object.keys(groups).forEach(limit => {
            if(limit === 'Unlimited') return;
            const group = groups[limit];
            const rate = (group.filter(d => !d.SuccessBool).length / group.length) * 100;
            x.push(parseInt(limit)); y.push(rate);
        });
        const combined = x.map((v,i)=>({x:v, y:y[i]})).sort((a,b)=>a.x-b.x);
        const trace = { x: combined.map(d=>d.x), y: combined.map(d=>d.y), type: 'bar', marker: { color: combined.map(d=>d.y>50?'#ef4444':'#f59e0b') } };
        const layout = this.getCommonLayout(); layout.yaxis.title = "Failure Rate %";
        Plotly.newPlot('chartFailure', [trace], layout);
    },

    updateTradeoffChart: function(data) {
        const success = data.filter(d => d.SuccessBool);
        const plotData = success.length > this.config.maxPoints ? _.sampleSize(success, this.config.maxPoints) : success;
        const traces = ['AStar', 'SMAStar'].map(algo => ({
            x: plotData.filter(d => d.Algorithm === algo).map(d => d.MemoryUsed),
            y: plotData.filter(d => d.Algorithm === algo).map(d => d.TimeMs),
            mode: 'markers', type: 'scatter', name: algo, marker: { size: 8, opacity: 0.6 }
        }));
        const layout = this.getCommonLayout(); layout.xaxis.type = 'log'; layout.xaxis.title = "Memory (Log Scale)"; layout.yaxis.title = "Time (ms)";
        Plotly.newPlot('chartTradeoff', traces, layout);
    },

    // ===============================================
    // NEW: ML COMPARISON LOGIC (Bonus Section)
    // ===============================================
    // IN SCRIPT.JS (Main File)

    updateMLComparison: function(allData) {
        // 1. Identify ML heuristics
        const mlHeuristicNames = ['MLP', 'LinearRegression', 'Ridge', 'Lasso', 'ElasticNet', 'Polynomial2', 'MachineLearned'];
        
        // 2. Filter for SUCCESSFUL ML runs only (Crucial for fairness)
        const mlData = allData.filter(d => 
            mlHeuristicNames.includes(d.Heuristic) && 
            d.Algorithm === 'AStar' && 
            d.SuccessBool === true
        );

        const section = document.getElementById('mlSection');
        if (mlData.length === 0) {
            console.warn("No Successful ML data found.");
            section.style.display = 'none';
            return;
        }
        section.style.display = 'block';

        // 3. Find the best "Standard" to compare against (SCALED_MANHATTAN as ground truth)
        const standardCandidates = allData.filter(d => 
            !mlHeuristicNames.includes(d.Heuristic) && 
            d.Algorithm === 'AStar' && 
            d.SuccessBool === true
        );
        
        // Default to SCALED_MANHATTAN (ground truth), or take the first available one
        let targetHeuristic = 'SCALED_MANHATTAN';
        const hasScaledManhattan = standardCandidates.some(d => d.Heuristic === 'SCALED_MANHATTAN');
        if (!hasScaledManhattan && standardCandidates.length > 0) {
            targetHeuristic = standardCandidates[0].Heuristic;
        }

        const stdData = standardCandidates.filter(d => d.Heuristic === targetHeuristic);

        if (stdData.length === 0) {
            console.warn("No Standard data found to compare.");
            return;
        }

        // 4. Group by Difficulty (The Safe Way)
        const difficulties = ['EASY', 'MEDIUM', 'HARD'];
        const yML = [];
        const yStd = [];
        const labels = [];

        difficulties.forEach(diff => {
            const mlGroup = mlData.filter(d => d.Difficulty === diff);
            const stdGroup = stdData.filter(d => d.Difficulty === diff);

            // Only show bars if we have data for BOTH in this difficulty category
            if (mlGroup.length > 0 && stdGroup.length > 0) {
                labels.push(diff);
                yML.push(_.meanBy(mlGroup, 'NodesExpanded'));
                yStd.push(_.meanBy(stdGroup, 'NodesExpanded'));
            }
        });

        // 5. Build the Chart
        const traceStd = {
            x: labels,
            y: yStd,
            name: `Standard (${targetHeuristic})`,
            type: 'bar',
            marker: { color: '#3b82f6' }
        };
        const traceML = {
            x: labels,
            y: yML,
            name: 'AI (All ML Models)',
            type: 'bar',
            marker: { color: '#8b5cf6' }
        };

        const layout = this.getCommonLayout();
        layout.title = "Nodes Expanded (Success Only)";
        layout.barmode = 'group';

        Plotly.newPlot('chartMLComparison', [traceStd, traceML], layout);

        // 6. Update Stats Text
        const avgML = _.mean(yML) || 0;
        const avgStd = _.mean(yStd) || 0;
        
        const elRed = document.getElementById('kpiMLReduction');
        const elAcc = document.getElementById('kpiMLAccuracy');

        if (avgStd > 0) {
            const reduction = ((avgStd - avgML) / avgStd) * 100;
            elRed.innerText = (reduction > 0 ? '↓ ' : '↑ ') + Math.abs(reduction).toFixed(1) + '%';
            elRed.style.color = reduction > 0 ? 'var(--success)' : 'var(--danger)';
        } else {
            elRed.innerText = "N/A";
        }

        // Calculate Cost Accuracy (Only for ML)
        const mlCostDiff = _.meanBy(mlData, d => Math.abs(d.CostNum - d.OptimalCost));
        elAcc.innerText = (mlCostDiff < 0.5) ? "Optimal" : `+${mlCostDiff.toFixed(1)}`;
    }
};

document.addEventListener('DOMContentLoaded', () => app.init());