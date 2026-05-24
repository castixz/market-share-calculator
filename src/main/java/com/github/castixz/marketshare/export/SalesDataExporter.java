package com.github.castixz.marketshare.export;

import com.github.castixz.marketshare.calculator.SalesDataShareCalculationResult;

import java.nio.file.Path;

public interface SalesDataExporter {

    SalesDataExportFileMetadata export(SalesDataShareCalculationResult result, Path path);
}
