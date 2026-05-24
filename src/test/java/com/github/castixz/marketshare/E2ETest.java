package com.github.castixz.marketshare;

import com.github.castixz.marketshare.calculator.SalesDataShareCalculationResult;
import com.github.castixz.marketshare.calculator.SalesDataShareCalculator;
import com.github.castixz.marketshare.constants.Vendor;
import com.github.castixz.marketshare.export.html.HtmlSalesDataExporter;
import com.github.castixz.marketshare.loader.RawSalesData;
import com.github.castixz.marketshare.loader.RawSalesDataQuery;
import com.github.castixz.marketshare.loader.csv.CsvRawSalesDataLoader;
import com.github.castixz.marketshare.utils.Sort;
import com.github.castixz.marketshare.utils.country.Country;
import com.github.castixz.marketshare.utils.time.Quarter;
import com.github.castixz.marketshare.utils.time.YearQuarter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class E2ETest {

    private static final Path TEST_CSV = Path.of("src/test/resources/test-data.csv");
    private static final Path EXPECTED_HTML = Path.of("src/test/resources/expected-export.html");
    private static final Path ACTUAL_HTML = Path.of("target/test-output/export.html");

    @Test
    void shouldProduceHtmlMatchingExpectedSnapshot() throws IOException {
        var query = new RawSalesDataQuery(new YearQuarter(2010, Quarter.Q3), Country.CZECH_REPUBLIC);
        RawSalesData data = new CsvRawSalesDataLoader(TEST_CSV).doLoad(query);

        SalesDataShareCalculationResult result = new SalesDataShareCalculator()
                .calculate(data, Vendor.ASUS, Vendor.ACER);
        var sorted = result.sortByVendor(Sort.ASC);

        new HtmlSalesDataExporter().export(sorted, ACTUAL_HTML);

        String expected = Files.readString(EXPECTED_HTML);
        String actual = Files.readString(ACTUAL_HTML);

        assertEquals(expected, actual);
    }
}
