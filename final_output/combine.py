import os
import re

# Configuration: Input filenames
FILES = {
    "dashboard": "index.html",
    "report": "ml_report.html",
    "style": "style.css",
    "script_main": "script.js",
    "script_ml": "ml_script.js",
    "data": "benchmark_results.csv"
}

# Configuration: Output filenames
OUTPUT_DASHBOARD = "Pathfinding_Analytics_Full.html"
OUTPUT_REPORT = "AI_Report_Full.html"

def read_file(filename):
    if not os.path.exists(filename):
        print(f"Warning: {filename} not found.")
        return None
    with open(filename, 'r', encoding='utf-8') as f:
        return f.read()

def inject_csv_logic(js_content):
    """
    Injects a check at the start of loadCSV function.
    If 'embedded-csv' element exists, use it. Otherwise, do normal load.
    """
    
    # The injection code to place at the start of loadCSV
    shim_code = """
    loadCSV: function() {
        var embedded = document.getElementById('embedded-csv');
        if (embedded) {
            console.log("Loading embedded CSV data...");
            var res = Papa.parse(embedded.textContent.trim(), {
                header: true, 
                dynamicTyping: true, 
                skipEmptyLines: true
            });
            this.handleDataLoad(res);
            return; 
        }
    """
    
    # Replace "loadCSV: function() {" with our shim code
    # This works for both script.js and ml_script.js
    if "loadCSV: function() {" in js_content:
        return js_content.replace("loadCSV: function() {", shim_code)
    else:
        print("  [!] Warning: Could not find 'loadCSV: function() {' in JS. CSV embedding might fail.")
        return js_content

def bundle_html(html_content, css_content, js_content, csv_content, js_filename_ref, is_main_dashboard):
    print(f"  - Processing CSS and JS...")
    
    # 1. Embed CSS
    css_tag = f"<style>\n{css_content}\n</style>"
    html_content = re.sub(
        r'<link rel="stylesheet" href="style.css">', 
        lambda match: css_tag, 
        html_content
    )

    # 2. Patch JS to support embedded CSV
    js_content = inject_csv_logic(js_content)

    # 3. Embed JS
    js_tag = f"<script>\n{js_content}\n</script>"
    pattern = r'<script src="' + re.escape(js_filename_ref) + r'"></script>'
    html_content = re.sub(pattern, lambda match: js_tag, html_content)

    # 4. Embed CSV Data (Hidden)
    if csv_content:
        print("  - Embedding CSV data...")
        # We put the CSV in a special script tag that browser won't execute, but JS can read
        csv_tag = f'<script id="embedded-csv" type="text/plain">\n{csv_content}\n</script>'
        # Inject before closing body tag
        html_content = html_content.replace('</body>', f'{csv_tag}\n</body>')

    # 5. Fix Navigation Links (Point to the new standalone filenames)
    if is_main_dashboard:
        html_content = html_content.replace('href="ml_report.html"', f'href="{OUTPUT_REPORT}"')
    else:
        html_content = html_content.replace('href="index.html"', f'href="{OUTPUT_DASHBOARD}"')

    return html_content

def main():
    print("--- Pathfinding Analytics Bundler (With Data) ---")
    
    # Read all assets
    css = read_file(FILES["style"])
    js_main = read_file(FILES["script_main"])
    js_ml = read_file(FILES["script_ml"])
    html_dash = read_file(FILES["dashboard"])
    html_rep = read_file(FILES["report"])
    csv_data = read_file(FILES["data"])

    if not all([css, js_main, js_ml, html_dash, html_rep]):
        print("Aborting: Critical source files missing.")
        return

    if not csv_data:
        print("Warning: Data.csv missing. Files will be generated but will try to load external CSV.")

    # 1. Bundle Main Dashboard
    print(f"\nBuilding {OUTPUT_DASHBOARD}...")
    final_dash = bundle_html(html_dash, css, js_main, csv_data, FILES["script_main"], True)
    with open(OUTPUT_DASHBOARD, 'w', encoding='utf-8') as f:
        f.write(final_dash)

    # 2. Bundle AI Report
    print(f"\nBuilding {OUTPUT_REPORT}...")
    final_rep = bundle_html(html_rep, css, js_ml, csv_data, FILES["script_ml"], False)
    with open(OUTPUT_REPORT, 'w', encoding='utf-8') as f:
        f.write(final_rep)

    print("\n-------------------------------------------------------")
    print("SUCCESS! Generated standalone files:")
    print(f"1. {OUTPUT_DASHBOARD}")
    print(f"2. {OUTPUT_REPORT}")
    print("-------------------------------------------------------")
    print("You can now send these 2 files to anyone. They contain")
    print("the HTML, CSS, JS, AND the Data.csv internally.")

if __name__ == "__main__":
    main()