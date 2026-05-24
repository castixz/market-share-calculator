package com.github.castixz.marketshare;

import com.github.castixz.marketshare.calculator.SalesDataShareCalculationResult;
import com.github.castixz.marketshare.calculator.SalesDataShareCalculator;
import com.github.castixz.marketshare.constants.Vendor;
import com.github.castixz.marketshare.loader.RawSalesData;
import com.github.castixz.marketshare.loader.RawSalesDataQuery;
import com.github.castixz.marketshare.loader.csv.CsvRawSalesDataLoader;
import com.github.castixz.marketshare.utils.Sort;
import com.github.castixz.marketshare.utils.country.Country;
import com.github.castixz.marketshare.utils.time.Quarter;
import com.github.castixz.marketshare.utils.time.YearQuarter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class AscertainVendorRowTest {

    private SalesDataShareCalculationResult result;

    @BeforeEach
    void setUp() {
        var query = new RawSalesDataQuery(new YearQuarter(2010, Quarter.Q3), Country.CZECH_REPUBLIC);
        RawSalesData data = new CsvRawSalesDataLoader(Path.of("src/test/resources/test-data.csv")).doLoad(query);
        result = new SalesDataShareCalculator().calculate(data, Vendor.DELL, Vendor.ACER, Vendor.HEWLETT_PACKARD);
    }

    @Test
    void shouldReturnCorrectRowAfterSortByVendor() {
        var sorted = result.sortByVendor(Sort.ASC);

        assertEquals(1, sorted.getRowNumberWithGivenVendor(Vendor.ACER));
        assertEquals(2, sorted.getRowNumberWithGivenVendor(Vendor.DELL));
        assertEquals(3, sorted.getRowNumberWithGivenVendor(Vendor.HEWLETT_PACKARD));
    }

    @Test
    void shouldThrowForVendorNotInResults() {
        assertThrows(IllegalArgumentException.class, () ->
                result.getRowNumberWithGivenVendor(Vendor.APPLE));
    }

}
