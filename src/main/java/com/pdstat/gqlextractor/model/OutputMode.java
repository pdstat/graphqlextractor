package com.pdstat.gqlextractor.model;

public enum OutputMode {

    GQL("graphql"),
    ALL("all"),
    JSON("json");

    private final String mode;

    OutputMode(String mode) {
        this.mode = mode;
    }

}
