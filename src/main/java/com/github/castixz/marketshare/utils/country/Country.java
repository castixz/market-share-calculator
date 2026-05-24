package com.github.castixz.marketshare.utils.country;

public enum Country {

    CZECH_REPUBLIC("CZ"),
    SLOVAKIA("SK");

    private final String isoCode;

    Country(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getIsoCode() {
        return isoCode;
    }

}
