package com.pdstat.gqlextractor.model;

import java.util.HashMap;
import java.util.Map;

public enum OutputMode {

    GQL("graphql"),
    ALL("all"),
    JSON("json");

    private static final Map<String, OutputMode> BY_MODE = new HashMap<>();

    static {
        BY_MODE.put(GQL.mode, GQL);
        BY_MODE.put(ALL.mode, ALL);
        BY_MODE.put(JSON.mode, JSON);
    }

    private final String mode;

    OutputMode(String mode) {
        this.mode = mode;
    }

    public static final OutputMode fromMode(String mode) {
        OutputMode outputMode = BY_MODE.get(mode);
        if (outputMode == null) {
            return JSON;
        }
        return outputMode;
    }

}
