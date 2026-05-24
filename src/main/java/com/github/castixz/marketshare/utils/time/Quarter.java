package com.github.castixz.marketshare.utils.time;

import java.util.Arrays;

public enum Quarter {

    Q1(1),
    Q2(2),
    Q3(3),
    Q4(4);

    private final int value;

    Quarter(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Quarter fromNumber(int value) {
        return Arrays.stream(values())
                .filter(candidate -> candidate.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid quarter: %d".formatted(value)));
    }
}
