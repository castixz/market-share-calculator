package com.github.castixz.marketshare.export;

import com.github.castixz.marketshare.export.csv.CsvSalesDataExporter;
import com.github.castixz.marketshare.export.html.HtmlSalesDataExporter;
import com.github.castixz.marketshare.export.xlsx.XlsxSalesDataExporter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SalesDataExporterRegistry {

    private final Map<SalesDataExportFormat, SalesDataExporter> exporters = new HashMap<>();

    public SalesDataExporterRegistry() {
        exporters.put(SalesDataExportFormat.HTML, new HtmlSalesDataExporter());
        exporters.put(SalesDataExportFormat.XLSX, new XlsxSalesDataExporter());
        exporters.put(SalesDataExportFormat.CSV, new CsvSalesDataExporter());
    }

    public SalesDataExporter getExporter(SalesDataExportFormat format) {
        Objects.requireNonNull(format, "format cannot be null");
        if (!exporters.containsKey(format)) {
            throw new IllegalArgumentException("Unsupported format: %s".formatted(format));
        }

        return exporters.get(format);
    }
}
