package com.github.castixz.marketshare.export.html;

import com.github.castixz.marketshare.calculator.SalesDataShareCalculationResult;
import com.github.castixz.marketshare.calculator.SalesDataShareCalculationResult.VendorWithCalculatedShare;
import com.github.castixz.marketshare.export.SalesDataExportFileMetadata;
import com.github.castixz.marketshare.export.SalesDataExportFormat;
import com.github.castixz.marketshare.export.SalesDataExporter;
import com.github.castixz.marketshare.utils.time.YearQuarter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.util.Locale;

public class HtmlSalesDataExporter implements SalesDataExporter {

    private static final DecimalFormat UNITS_FORMAT = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    private static final DecimalFormat SHARE_FORMAT = new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

    @Override
    public SalesDataExportFileMetadata export(SalesDataShareCalculationResult result, Path path) {
        String html = prepareRawHtml(result);

        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(path, html);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write HTML file: %s".formatted(path), e);
        }

        return new SalesDataExportFileMetadata(
                path,
                Instant.now(),
                SalesDataExportFormat.HTML
        );
    }

    private String prepareRawHtml(SalesDataShareCalculationResult result) {
        String title = "Table 1, PC Quarterly Market Share, %s, %s".formatted(
                Locale.of("", result.country().getIsoCode()).getDisplayCountry(Locale.ENGLISH),
                formatYearQuarter(result.yearQuarter())
        );

        var sb = new StringBuilder();

        sb.append("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        table { border-collapse: collapse; text-align: center; }
                        th, td { border: 1px solid black; padding: 8px 32px; }
                        th { background-color: #d3d3d3; }
                        .total { background-color: #F5DEB3; font-weight: bold; }
                        caption { font-weight: bold; margin-bottom: 8px; }
                    </style>
                </head>
                <body>
                <table>
                """);
        sb.append("    <caption>").append(title).append("</caption>\n");
        sb.append("""
                    <thead>
                        <tr>
                            <th>Vendor</th>
                            <th>Units</th>
                            <th>Share</th>
                        </tr>
                    </thead>
                    <tbody>
                """);

        for (VendorWithCalculatedShare item : result.vendorsWithShare()) {
            sb.append(buildRow(item.vendor().name(), item.units(), item.share(), ""));
        }

        sb.append(buildRow("Others", result.otherVendorsUnits(), result.otherVendorsShare(), ""));
        sb.append(buildRow("Total", result.totalUnits(), new BigDecimal("100.0"), " class=\"total\""));

        sb.append("""
                    </tbody>
                </table>
                </body>
                </html>
                """);

        return sb.toString();
    }

    private String buildRow(String label, BigDecimal units, BigDecimal share, String attributes) {
        return "        <tr%s><td>%s</td><td>%s</td><td>%s%%</td></tr>".formatted(
                attributes,
                label,
                UNITS_FORMAT.format(units),
                SHARE_FORMAT.format(share)
        ) + "\n";
    }

    private String formatYearQuarter(YearQuarter yearQuarter) {
        return "%dQ%02d".formatted(
                yearQuarter.quarter().getValue(),
                yearQuarter.year() % 100
        );
    }

}
