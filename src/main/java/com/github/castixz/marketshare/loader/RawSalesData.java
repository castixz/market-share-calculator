package com.github.castixz.marketshare.loader;

import com.github.castixz.marketshare.constants.Vendor;
import com.github.castixz.marketshare.utils.country.Country;
import com.github.castixz.marketshare.utils.time.YearQuarter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record RawSalesData(
        List<Item> items
) {

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public record Item(
            Country country,
            YearQuarter yearQuarter,
            Vendor vendor,
            BigDecimal units
    ) {

        public Item {
            Objects.requireNonNull(country, "Country cannot be null");
            Objects.requireNonNull(yearQuarter, "Timescale cannot be null");
            Objects.requireNonNull(vendor, "Vendor cannot be null");
            Objects.requireNonNull(units, "Units cannot be null");
            if (units.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Units must be greater than zero");
            }
        }
    }

    public RawSalesData(List<Item> items) {
        Objects.requireNonNull(items, "Items cannot be null");
        this.items = List.copyOf(items);
    }

}
