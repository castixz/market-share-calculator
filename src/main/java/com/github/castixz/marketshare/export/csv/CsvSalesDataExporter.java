package com.github.castixz.marketshare.export.csv;

import com.github.castixz.marketshare.calculator.SalesDataShareCalculationResult;
import com.github.castixz.marketshare.export.SalesDataExportFileMetadata;
import com.github.castixz.marketshare.export.SalesDataExporter;

import java.nio.file.Path;

/**
 * Exports {@link SalesDataShareCalculationResult} to a CSV file.
 *
 * <p>The implementation would write a comma-separated file with columns: Vendor, Units, Share.
 * Each selected vendor would be written as a row, followed by an "Others" row aggregating
 * remaining vendors, and a "Total" row with summed units and 100% share.</p>
 */
public class CsvSalesDataExporter implements SalesDataExporter {

    @Override
    public SalesDataExportFileMetadata export(SalesDataShareCalculationResult result, Path path) {
        throw new UnsupportedOperationException("CSV export is not yet implemented");
    }

}
