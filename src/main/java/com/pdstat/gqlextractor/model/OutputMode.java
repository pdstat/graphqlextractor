package com.pdstat.gqlextractor.model;

import java.util.HashMap;
import java.util.Map;

public enum OutputMode {

    OPERATIONS("operations"),
    FIELDS("fields"),
    PATHS("paths"),
    ALL("all"),
    REQUESTS("requests");

    private static final Map<String, OutputMode> BY_MODE = new HashMap<>();

    static {
        BY_MODE.put(OPERATIONS.mode, OPERATIONS);
        BY_MODE.put(ALL.mode, ALL);
        BY_MODE.put(REQUESTS.mode, REQUESTS);
        BY_MODE.put(FIELDS.mode, FIELDS);
        BY_MODE.put(PATHS.mode, PATHS);
    }

    private final String mode;

    OutputMode(String mode) {
        this.mode = mode;
    }

    public static OutputMode fromMode(String mode) {
        OutputMode outputMode = BY_MODE.get(mode);
        if (outputMode == null) {
            return REQUESTS;
        }
        return outputMode;
    }

}
