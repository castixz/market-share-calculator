package com.github.castixz.marketshare;

import com.github.castixz.marketshare.calculator.SalesDataShareCalculationResult;
import com.github.castixz.marketshare.calculator.SalesDataShareCalculationResult.VendorWithCalculatedShare;
import com.github.castixz.marketshare.calculator.SalesDataShareCalculator;
import com.github.castixz.marketshare.constants.Vendor;
import com.github.castixz.marketshare.loader.RawSalesData;
import com.github.castixz.marketshare.loader.RawSalesDataQuery;
import com.github.castixz.marketshare.loader.csv.CsvRawSalesDataLoader;
import com.github.castixz.marketshare.utils.country.Country;
import com.github.castixz.marketshare.utils.time.Quarter;
import com.github.castixz.marketshare.utils.time.YearQuarter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class AscertainUnitsAndShareTest {

    private SalesDataShareCalculationResult calculationResult;

    @BeforeEach
    void setUp() {
        var query = new RawSalesDataQuery(new YearQuarter(2010, Quarter.Q3), Country.CZECH_REPUBLIC);
        RawSalesData data = new CsvRawSalesDataLoader(Path.of("src/test/resources/test-data.csv")).doLoad(query);
        calculationResult = new SalesDataShareCalculator().calculate(data, Vendor.DELL, Vendor.ACER, Vendor.HEWLETT_PACKARD, Vendor.ASUS);
    }

    @Test
    void shouldReturnUnitsAndShareForDell() {
        VendorWithCalculatedShare dell = calculationResult.getVendorWithUnitsAndCalculatedShare(Vendor.DELL);

        assertEquals(0, new BigDecimal("8267.033639").compareTo(dell.units()));
        assertEquals(new BigDecimal("21.93"), dell.share());
    }

    @Test
    void shouldReturnUnitsAndShareForAcer() {
        VendorWithCalculatedShare acer = calculationResult.getVendorWithUnitsAndCalculatedShare(Vendor.ACER);

        assertEquals(0, new BigDecimal("9570.718105").compareTo(acer.units()));
        assertEquals(new BigDecimal("25.39"), acer.share());
    }

    @Test
    void shouldCalculateOthersWithPreciseValues() {
        assertEquals(0, new BigDecimal("7486.0725044").compareTo(calculationResult.otherVendorsUnits()));
        assertEquals(new BigDecimal("19.86"), calculationResult.otherVendorsShare());
    }

    @Test
    void shouldHaveTotalUnits() {
        assertEquals(0, new BigDecimal("37698.2318474").compareTo(calculationResult.totalUnits()));
    }

    @Test
    void shouldHaveSharesSumToHundred() {
        BigDecimal selectedSharesSum = calculationResult.vendorsWithShare().stream()
                .map(VendorWithCalculatedShare::share)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalShare = selectedSharesSum.add(calculationResult.otherVendorsShare());
        assertEquals(0, new BigDecimal("100.00").compareTo(totalShare));
    }

    @Test
    void shouldThrowForVendorNotInResults() {
        assertThrows(IllegalArgumentException.class, () ->
                calculationResult.getVendorWithUnitsAndCalculatedShare(Vendor.LENOVO));
    }
}
