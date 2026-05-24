package com.github.castixz.marketshare.calculator;

import com.github.castixz.marketshare.constants.Vendor;
import com.github.castixz.marketshare.utils.Sort;
import com.github.castixz.marketshare.utils.country.Country;
import com.github.castixz.marketshare.utils.time.YearQuarter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record SalesDataShareCalculationResult(
        List<VendorWithCalculatedShare> vendorsWithShare,
        BigDecimal totalUnits,
        BigDecimal otherVendorsShare,
        BigDecimal otherVendorsUnits,
        Country country,
        YearQuarter yearQuarter
) {

    public SalesDataShareCalculationResult {
        vendorsWithShare = List.copyOf(vendorsWithShare);
    }

    @Override
    public List<VendorWithCalculatedShare> vendorsWithShare() {
        return List.copyOf(vendorsWithShare);
    }

    public VendorWithCalculatedShare getVendorWithUnitsAndCalculatedShare(Vendor vendor) {
        return vendorsWithShare.stream()
                .filter(shareWithGivenVendorCandidate -> shareWithGivenVendorCandidate.vendor() == vendor)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Vendor %s not found in results".formatted(vendor)));
    }

    public SalesDataShareCalculationResult sortByUnits(Sort sort) {
        return sortBy(Comparator.comparing(VendorWithCalculatedShare::units), sort);
    }

    public SalesDataShareCalculationResult sortByVendor(Sort sort) {
        return sortBy(Comparator.comparing(vendorWithShare -> vendorWithShare.vendor().name()), sort);
    }

    public int getRowNumberWithGivenVendor(Vendor vendor) {
        VendorWithCalculatedShare vendorWithCalculatedShare = getVendorWithUnitsAndCalculatedShare(vendor);

        return vendorsWithShare.indexOf(vendorWithCalculatedShare) + 1;
    }

    private SalesDataShareCalculationResult sortBy(Comparator<VendorWithCalculatedShare> comparator, Sort sort) {
        if (sort == Sort.DESC) {
            comparator = comparator.reversed();
        }
        var vendorsWithShareSorted = new ArrayList<>(vendorsWithShare);
        vendorsWithShareSorted.sort(comparator);

        return new SalesDataShareCalculationResult(
                vendorsWithShareSorted,
                totalUnits,
                otherVendorsShare,
                otherVendorsUnits,
                country,
                yearQuarter
        );
    }

    public record VendorWithCalculatedShare(Vendor vendor, BigDecimal units, BigDecimal share) {
    }

}
