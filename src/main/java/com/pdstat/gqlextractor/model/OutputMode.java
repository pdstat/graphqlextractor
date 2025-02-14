package com.pdstat.gqlextractor.model;

import java.util.HashMap;
import java.util.Map;

public enum OutputMode {

    GQL("graphql"),
    FIELDS("fields"),
    REPORT("report"),
    ALL("all"),
    JSON("json");

    private static final Map<String, OutputMode> BY_MODE = new HashMap<>();

    static {
        BY_MODE.put(GQL.mode, GQL);
        BY_MODE.put(ALL.mode, ALL);
        BY_MODE.put(JSON.mode, JSON);
        BY_MODE.put(FIELDS.mode, FIELDS);
        BY_MODE.put(REPORT.mode, REPORT);
    }

    private final String mode;

    OutputMode(String mode) {
        this.mode = mode;
    }

    public static OutputMode fromMode(String mode) {
        OutputMode outputMode = BY_MODE.get(mode);
        if (outputMode == null) {
            return JSON;
        }
        return outputMode;
    }

}
