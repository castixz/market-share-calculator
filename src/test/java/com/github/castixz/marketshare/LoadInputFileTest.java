package com.github.castixz.marketshare;

import com.github.castixz.marketshare.constants.Vendor;
import com.github.castixz.marketshare.loader.RawSalesData;
import com.github.castixz.marketshare.loader.RawSalesDataQuery;
import com.github.castixz.marketshare.loader.csv.CsvRawSalesDataLoader;
import com.github.castixz.marketshare.utils.country.Country;
import com.github.castixz.marketshare.utils.time.Quarter;
import com.github.castixz.marketshare.utils.time.YearQuarter;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class LoadInputFileTest {

    private static final Path TEST_CSV = Path.of("src/test/resources/test-data.csv");
    private static final Path INVALID_HEADERS_CSV = Path.of("src/test/resources/invalid-headers.csv");

    @Test
    void shouldLoadAndFilterByCzechRepublicQ3() {
        var query = new RawSalesDataQuery(new YearQuarter(2010, Quarter.Q3), Country.CZECH_REPUBLIC);
        RawSalesData data = new CsvRawSalesDataLoader(TEST_CSV).doLoad(query);

        assertFalse(data.isEmpty());
        assertEquals(7, data.items().size());
        assertTrue(data.items().stream().allMatch(item -> item.country() == Country.CZECH_REPUBLIC));
        assertTrue(data.items().stream().allMatch(item -> item.yearQuarter().equals(new YearQuarter(2010, Quarter.Q3))));
    }

    @Test
    void shouldLoadAndFilterBySlovakiaQ4() {
        var query = new RawSalesDataQuery(new YearQuarter(2010, Quarter.Q4), Country.SLOVAKIA);
        RawSalesData data = new CsvRawSalesDataLoader(TEST_CSV).doLoad(query);

        assertFalse(data.isEmpty());
        assertEquals(7, data.items().size());
        assertTrue(data.items().stream().allMatch(item -> item.country() == Country.SLOVAKIA));
    }

    @Test
    void shouldReturnEmptyForNonExistentQuarter() {
        var query = new RawSalesDataQuery(new YearQuarter(2020, Quarter.Q1), Country.CZECH_REPUBLIC);
        RawSalesData data = new CsvRawSalesDataLoader(TEST_CSV).doLoad(query);

        assertTrue(data.isEmpty());
    }

    @Test
    void shouldParseAllVendorsCorrectly() {
        var query = new RawSalesDataQuery(new YearQuarter(2010, Quarter.Q3), Country.CZECH_REPUBLIC);
        RawSalesData data = new CsvRawSalesDataLoader(TEST_CSV).doLoad(query);

        var vendors = data.items().stream().map(RawSalesData.Item::vendor).toList();
        assertTrue(vendors.contains(Vendor.DELL));
        assertTrue(vendors.contains(Vendor.HEWLETT_PACKARD));
        assertTrue(vendors.contains(Vendor.FUJITSU_SIEMENS));
        assertTrue(vendors.contains(Vendor.ACER));
        assertTrue(vendors.contains(Vendor.ASUS));
        assertTrue(vendors.contains(Vendor.APPLE));
        assertTrue(vendors.contains(Vendor.LENOVO));
    }

    @Test
    void shouldThrowIfHeadersAreInvalid() {
        var query = new RawSalesDataQuery(new YearQuarter(2010, Quarter.Q3), Country.CZECH_REPUBLIC);

        assertThrows(IllegalStateException.class, () ->
                new CsvRawSalesDataLoader(INVALID_HEADERS_CSV).doLoad(query));
    }

    @Test
    void shouldThrowIfFileDoesNotExist() {
        var query = new RawSalesDataQuery(new YearQuarter(2010, Quarter.Q3), Country.CZECH_REPUBLIC);
        assertThrows(IllegalStateException.class, () ->
                new CsvRawSalesDataLoader(Path.of("nonexistent.csv")).doLoad(query));
    }

}
