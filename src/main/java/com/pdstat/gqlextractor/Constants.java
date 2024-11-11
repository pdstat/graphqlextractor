package com.pdstat.gqlextractor;

public class Constants {

    public interface Gql {
        String FRAGMENT = "fragment ";
        String QUERY = "query ";
        String MUTATION = "mutation ";
        String SUBSCRIPTION = "subscription ";
    }
    public interface Arguments {
        String HELP = "help";
        String INPUT_DIRECTORY = "input-directory";
        String OUTPUT_DIRECTORY = "output-directory";
        String OUTPUT_MODE = "output-mode";
    }
    private Constants() {
    }
}
