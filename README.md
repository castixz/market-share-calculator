# Market Share Calculator

Loads PC vendor sales data from CSV, calculates market shares for selected vendors, and exports results to HTML.

## Requirements

- Java 25
- Maven

## Build & Test

```bash
mvn clean test
```

## Run

```bash
mvn compile exec:java -Dexec.mainClass="com.github.castixz.marketshare.MarketShareCalculatorApplication"
```

Input CSV is read from `.data/input/data.csv`, output HTML is written to `.data/output/export.html`. For demonstration purposes example input and output files are commited in this repo.

## How It Works

1. **Load** -- `CsvRawSalesDataLoader` reads CSV, validates headers, filters by query (country + quarter)
2. **Calculate** -- `SalesDataShareCalculator` sums units by vendor, computes percentage shares, aggregates non-selected vendors into "Others"
3. **Export** -- `HtmlSalesDataExporter` writes an HTML table with vendor names, units, and share percentages
