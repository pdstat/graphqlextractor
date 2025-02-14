package com.pdstat.gqlextractor;

public class Constants {

    public interface Gql {

        interface Scalar {
            String STRING = "String";
            String INT = "Int";
            String FLOAT = "Float";
            String BOOLEAN = "Boolean";
            String LONG = "Long";
            String ID = "ID";
        }

    }
    public interface Arguments {
        String HELP = "help";
        String INPUT_URLS = "input-urls";
        String DEFAULT_PARAMS = "default-params";
        String INPUT_DIRECTORY = "input-directory";
        String INPUT_SCHEMA = "input-schema";
        String INPUT_OPERATIONS = "input-operations";
        String REQUEST_HEADER = "request-header";
        String OUTPUT_DIRECTORY = "output-directory";
        String SEARCH_FIELD = "search-field";
        String DEPTH = "depth";
        String OUTPUT_MODE = "output-mode";
    }
    private Constants() {
    }
}
