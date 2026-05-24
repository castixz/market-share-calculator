package com.github.castixz.marketshare.utils;

import java.util.Collection;
import java.util.Objects;

public final class Assert {

    private Assert() {
    }

    public static void requireNotEmpty(Collection<?> collection) {
        Objects.requireNonNull(collection, "Collection cannot be null");
        if (collection.isEmpty()) {
            throw new IllegalArgumentException("Collection must not be empty");
        }
    }

}
