package com.github.castixz.marketshare;

import com.github.castixz.marketshare.calculator.SalesDataShareCalculationResult;
import com.github.castixz.marketshare.calculator.SalesDataShareCalculationResult.VendorWithCalculatedShare;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SortByUnitsTest {

    private SalesDataShareCalculationResult calculationResult;

    @BeforeEach
    void setUp() {
        var query = new RawSalesDataQuery(new YearQuarter(2010, Quarter.Q3), Country.CZECH_REPUBLIC);
        RawSalesData data = new CsvRawSalesDataLoader(Path.of("src/test/resources/test-data.csv")).doLoad(query);
        calculationResult = new SalesDataShareCalculator().calculate(data, Vendor.DELL, Vendor.ACER, Vendor.HEWLETT_PACKARD, Vendor.ASUS);
    }

    @Test
    void shouldSortByUnitsAscending() {
        var sorted = calculationResult.sortByUnits(Sort.ASC);

        List<VendorWithCalculatedShare> vendors = sorted.vendorsWithShare();
        for (int i = 0; i < vendors.size() - 1; i++) {
            assertTrue(vendors.get(i).units().compareTo(vendors.get(i + 1).units()) <= 0);
        }
    }

    @Test
    void shouldSortByUnitsDescending() {
        var sorted = calculationResult.sortByUnits(Sort.DESC);

        List<VendorWithCalculatedShare> vendors = sorted.vendorsWithShare();
        for (int i = 0; i < vendors.size() - 1; i++) {
            assertTrue(vendors.get(i).units().compareTo(vendors.get(i + 1).units()) >= 0);
        }
    }

    @Test
    void shouldHaveHewlettPackardFirstWhenSortedDescending() {
        var sorted = calculationResult.sortByUnits(Sort.DESC);

        assertEquals(Vendor.HEWLETT_PACKARD, sorted.vendorsWithShare().getFirst().vendor());
    }

}
