package com.github.castixz.marketshare.loader;

public interface RawSalesDataLoader {

    RawSalesData doLoad(RawSalesDataQuery query);
}
