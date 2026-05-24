package com.github.castixz.marketshare.export.xlsx;

import com.github.castixz.marketshare.calculator.SalesDataShareCalculationResult;
import com.github.castixz.marketshare.export.SalesDataExportFileMetadata;
import com.github.castixz.marketshare.export.SalesDataExporter;

import java.nio.file.Path;

/**
 * Exports {@link SalesDataShareCalculationResult} to an Excel (.xlsx) file.
 *
 * <p>The implementation would use the Apache POI library ({@code org.apache.poi.xssf}) to create
 * an Excel workbook with a single sheet containing the market share table. The sheet would include
 * a header row (Vendor, Units, Share) with bold styling, followed by data rows for each selected
 * vendor, an "Others" row, and a highlighted "Total" row.</p>
 */
public class XlsxSalesDataExporter implements SalesDataExporter {

    @Override
    public SalesDataExportFileMetadata export(SalesDataShareCalculationResult result, Path path) {
        throw new UnsupportedOperationException("Excel export is not yet implemented");
    }
}
