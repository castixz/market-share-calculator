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

public class SortByVendorTest {

    private SalesDataShareCalculationResult calculationResult;

    @BeforeEach
    void setUp() {
        var query = new RawSalesDataQuery(new YearQuarter(2010, Quarter.Q3), Country.CZECH_REPUBLIC);
        RawSalesData data = new CsvRawSalesDataLoader(Path.of("src/test/resources/test-data.csv")).doLoad(query);
        calculationResult = new SalesDataShareCalculator().calculate(data, Vendor.DELL, Vendor.ACER, Vendor.HEWLETT_PACKARD, Vendor.ASUS);
    }

    @Test
    void shouldSortByVendorAscending() {
        var sorted = calculationResult.sortByVendor(Sort.ASC);

        List<Vendor> vendors = sorted.vendorsWithShare().stream()
                .map(VendorWithCalculatedShare::vendor)
                .toList();

        assertEquals(Vendor.ACER, vendors.get(0));
        assertEquals(Vendor.ASUS, vendors.get(1));
        assertEquals(Vendor.DELL, vendors.get(2));
        assertEquals(Vendor.HEWLETT_PACKARD, vendors.get(3));
    }

    @Test
    void shouldSortByVendorDescending() {
        var sorted = calculationResult.sortByVendor(Sort.DESC);

        List<Vendor> vendors = sorted.vendorsWithShare().stream()
                .map(VendorWithCalculatedShare::vendor)
                .toList();

        assertEquals(Vendor.HEWLETT_PACKARD, vendors.get(0));
        assertEquals(Vendor.DELL, vendors.get(1));
        assertEquals(Vendor.ASUS, vendors.get(2));
        assertEquals(Vendor.ACER, vendors.get(3));
    }

}
