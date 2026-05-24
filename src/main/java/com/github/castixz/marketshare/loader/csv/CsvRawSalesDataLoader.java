package com.github.castixz.marketshare.loader.csv;

import com.github.castixz.marketshare.constants.Vendor;
import com.github.castixz.marketshare.loader.RawSalesData;
import com.github.castixz.marketshare.loader.RawSalesDataLoader;
import com.github.castixz.marketshare.loader.RawSalesDataQuery;
import com.github.castixz.marketshare.utils.country.Country;
import com.github.castixz.marketshare.utils.time.Quarter;
import com.github.castixz.marketshare.utils.time.YearQuarter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvRawSalesDataLoader implements RawSalesDataLoader {

    private static final List<String> EXPECTED_HEADERS = List.of("Country", "Timescale", "Vendor", "Units");
    private static final Pattern YEAR_QUARTER_PATTERN = Pattern.compile("(\\d{4})\\s+Q(\\d)");

    private final Path path;

    public CsvRawSalesDataLoader(Path path) {
        Objects.requireNonNull(path, "path cannot be null");
        this.path = path;
    }

    @Override
    public RawSalesData doLoad(RawSalesDataQuery query) {
        Objects.requireNonNull(query, "query cannot be null");
        if (!Files.exists(path)) {
            throw new IllegalStateException("CSV file does not exist: %s".formatted(path));
        }

        try (var lines = Files.lines(path)) {
            var iterator = lines.iterator();

            if (!iterator.hasNext()) {
                throw new IllegalStateException("CSV file is empty: %s".formatted(path));
            }

            validateHeaders(iterator.next());

            List<RawSalesData.Item> items = new ArrayList<>();
            while (iterator.hasNext()) {
                RawSalesData.Item item = parseLine(iterator.next());
                if (!doesRowMatchQuery(query, item)) {
                    continue;
                }
                items.add(item);
            }

            return new RawSalesData(items);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load CSV file: %s".formatted(path), e);
        }
    }

    private void validateHeaders(String header) {
        List<String> headers = List.of(header.split(","));
        if (!headers.equals(EXPECTED_HEADERS)) {
            throw new IllegalStateException(
                    "Invalid CSV headers. Expected: %s, got: %s".formatted(EXPECTED_HEADERS, headers)
            );
        }
    }

    private static boolean doesRowMatchQuery(RawSalesDataQuery query, RawSalesData.Item item) {
        return item.yearQuarter().equals(query.yearQuarter()) && item.country().equals(query.country());
    }

    private RawSalesData.Item parseLine(String line) {
        try {
            String[] parts = line.split(",");
            if (parts.length != 4) {
                throw new IllegalStateException("Invalid CSV line format: %s".formatted(line));
            }
            Country country = parseCountry(parts[0].trim());
            YearQuarter timescale = parseYearQuarter(parts[1].trim());
            Vendor vendor = parseVendor(parts);
            BigDecimal units = new BigDecimal(parts[3].trim());

            return new RawSalesData.Item(country, timescale, vendor, units);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse CSV line: %s".formatted(line), e);
        }
    }

    private static Country parseCountry(String rawCountryString) {
        return Country.valueOf(
                rawCountryString.toUpperCase(Locale.ENGLISH).replace(" ", "_")
        );
    }

    private static YearQuarter parseYearQuarter(String rawYearQuarterString) {
        Matcher matcher = YEAR_QUARTER_PATTERN.matcher(rawYearQuarterString);
        if (!matcher.matches()) {
            throw new IllegalStateException("Invalid year-quarter format at row %s".formatted(rawYearQuarterString));
        }
        int year = Integer.parseInt(matcher.group(1));
        Quarter quarter = Quarter.fromNumber(Integer.parseInt(matcher.group(2)));

        return new YearQuarter(year, quarter);
    }

    private static Vendor parseVendor(String[] parts) {
        String vendorRaw = parts[2].trim();
        final String vendorNormalized = vendorRaw.replace("-", " ")
                .replace(" ", "_")
                .toUpperCase(Locale.ENGLISH);

        return Vendor.valueOf(vendorNormalized);
    }

}
