package com.github.castixz.marketshare.loader;

import com.github.castixz.marketshare.utils.country.Country;
import com.github.castixz.marketshare.utils.time.YearQuarter;

public record RawSalesDataQuery(YearQuarter yearQuarter, Country country) {
}
