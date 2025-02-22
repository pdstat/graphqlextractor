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

    public interface Output {

        interface FILES {
            String FIELDS_FILE = "unique-fields.txt";
            String FIELD_PATHS = "-paths.txt";
        }

        interface DIRECTORIES {
            String REQUESTS = "requests";
            String OPERATIONS = "operations";
            String FIELD_PATHS = "field-paths";
            String SCHEMA_FIELD_PATHS = "schema-field-paths";
            String WORDLIST = "wordlist";
        }

    }

    private Constants() {
    }
}
