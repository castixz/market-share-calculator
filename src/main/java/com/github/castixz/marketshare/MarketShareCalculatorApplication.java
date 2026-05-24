package com.github.castixz.marketshare;

import com.github.castixz.marketshare.calculator.SalesDataShareCalculationResult;
import com.github.castixz.marketshare.calculator.SalesDataShareCalculator;
import com.github.castixz.marketshare.constants.Vendor;
import com.github.castixz.marketshare.loader.RawSalesData;
import com.github.castixz.marketshare.loader.RawSalesDataQuery;
import com.github.castixz.marketshare.loader.csv.CsvRawSalesDataLoader;
import com.github.castixz.marketshare.utils.country.Country;
import com.github.castixz.marketshare.utils.time.Quarter;
import com.github.castixz.marketshare.utils.time.YearQuarter;

import java.nio.file.Path;
import java.util.logging.Logger;

public class MarketShareCalculatorApplication {

    private static final Logger LOG = Logger.getLogger(MarketShareCalculatorApplication.class.getName());

    private static final Path INPUT_FILE_PATH = Path.of(".data", "input", "data.csv");

    static void main(String[] args) {
        var query = new RawSalesDataQuery(
                new YearQuarter(2010, Quarter.Q3),
                Country.CZECH_REPUBLIC
        );
        LOG.info("Loading sales data from %s with query %s".formatted(INPUT_FILE_PATH, query));
        RawSalesData rawSalesData = new CsvRawSalesDataLoader(INPUT_FILE_PATH).doLoad(query);

        if (rawSalesData.isEmpty()) {
            throw new IllegalStateException("No data found for query: %s".formatted(query));
        }
        LOG.info("Loaded %d raw sales data items".formatted(rawSalesData.items().size()));

        Vendor[] vendors = {
                Vendor.ASUS,
                Vendor.ACER
        };
        SalesDataShareCalculationResult result = new SalesDataShareCalculator().calculate(rawSalesData, vendors);
        LOG.info("Calculating shares done");
    }

}
