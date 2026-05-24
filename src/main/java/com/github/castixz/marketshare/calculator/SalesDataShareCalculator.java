package com.github.castixz.marketshare.calculator;

import com.github.castixz.marketshare.constants.Vendor;
import com.github.castixz.marketshare.loader.RawSalesData;
import com.github.castixz.marketshare.utils.Assert;
import com.github.castixz.marketshare.utils.country.Country;
import com.github.castixz.marketshare.utils.time.YearQuarter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SalesDataShareCalculator {

    public SalesDataShareCalculationResult calculate(RawSalesData rawSalesData, Vendor... vendors) {
        List<RawSalesData.Item> items = rawSalesData.items();
        Set<Vendor> selectedVendors = Set.of(vendors);

        Assert.requireNotEmpty(items);
        Assert.requireNotEmpty(selectedVendors);

        YearQuarter yearQuarter = items.getFirst().yearQuarter();
        requireAllItemsFromSameYearQuarter(items, yearQuarter);

        Country country = items.getFirst().country();
        requireAllItemsFromSameCountry(items, country);

        Map<Vendor, BigDecimal> unitsByVendor = sumUnitsByVendor(items);
        BigDecimal totalUnits = sumTotalUnits(unitsByVendor);

        List<SalesDataShareCalculationResult.VendorWithCalculatedShare> vendorsWithShare = calculateShareForSelectedVendors(unitsByVendor, selectedVendors, totalUnits);

        BigDecimal selectedUnits = vendorsWithShare.stream()
                .map(SalesDataShareCalculationResult.VendorWithCalculatedShare::units)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal otherVendorsUnits = totalUnits.subtract(selectedUnits);
        BigDecimal otherVendorsShare = calculateShare(otherVendorsUnits, totalUnits);

        return new SalesDataShareCalculationResult(
                vendorsWithShare,
                totalUnits,
                otherVendorsShare,
                otherVendorsUnits,
                country,
                yearQuarter
        );
    }

    private static void requireAllItemsFromSameCountry(List<RawSalesData.Item> items, Country country) {
        items.stream()
                .filter(Predicate.not(itemWithNotSelectedCountryCandidate -> itemWithNotSelectedCountryCandidate.country().equals(country)))
                .findAny()
                .ifPresent(_ -> {
                    throw new IllegalArgumentException("All items must be from the same country");
                });
    }

    private static void requireAllItemsFromSameYearQuarter(List<RawSalesData.Item> items, YearQuarter yearQuarter) {
        items.stream()
                .filter(Predicate.not(itemWithNotSelectedYearQuarterCandidate -> itemWithNotSelectedYearQuarterCandidate.yearQuarter().equals(yearQuarter)))
                .findAny()
                .ifPresent(_ -> {
                    throw new IllegalArgumentException("All items must be from the same year-quarter");
                });
    }

    private List<SalesDataShareCalculationResult.VendorWithCalculatedShare> calculateShareForSelectedVendors(
            Map<Vendor, BigDecimal> unitsByVendor,
            Set<Vendor> selectedVendors,
            BigDecimal totalUnits
    ) {
        return unitsByVendor.entrySet().stream()
                .filter(vendorToBeSelectedCandidate -> selectedVendors.contains(vendorToBeSelectedCandidate.getKey()))
                .map(selectedVendorToUnits -> new SalesDataShareCalculationResult.VendorWithCalculatedShare(
                        selectedVendorToUnits.getKey(),
                        selectedVendorToUnits.getValue(),
                        calculateShare(selectedVendorToUnits.getValue(), totalUnits)
                ))
                .toList();
    }

    private static BigDecimal sumTotalUnits(Map<Vendor, BigDecimal> unitsByVendor) {
        return unitsByVendor.values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static Map<Vendor, BigDecimal> sumUnitsByVendor(List<RawSalesData.Item> items) {
        return items.stream()
                .collect(Collectors.groupingBy(
                        RawSalesData.Item::vendor,
                        Collectors.reducing(BigDecimal.ZERO, RawSalesData.Item::units, BigDecimal::add)
                ));
    }

    private BigDecimal calculateShare(BigDecimal units, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Total units cannot be zero");
        }

        return units.multiply(new BigDecimal("100"))
                .divide(total, 2, RoundingMode.HALF_UP);
    }

}
