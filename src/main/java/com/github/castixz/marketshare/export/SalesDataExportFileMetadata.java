package com.github.castixz.marketshare.export;

import java.nio.file.Path;
import java.time.Instant;

public record SalesDataExportFileMetadata(
        Path path,
        Instant finished,
        SalesDataExportFormat format
) {
}
