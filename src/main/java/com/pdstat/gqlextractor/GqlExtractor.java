package com.pdstat.gqlextractor;

import com.pdstat.gqlextractor.service.GqlExtractorOutputHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GqlExtractor implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(GqlExtractor.class);

    @Autowired
    private ApplicationArguments appArgs;
    @Autowired
    private GqlExtractorOutputHandlerService gqlExtractorOutputHandlerService;

    public static void main(String[] args) {
        SpringApplication.run(GqlExtractor.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (appArgs.containsOption(Constants.Arguments.HELP)) {
            logger.info("GqlExtractor is an application that extracts .graphql and .json files from a directory of javascript files that contain GraphQL strings.");
            logger.info("The application requires the following arguments:");
            logger.info("  --input-directory=<input-directory> : The directory containing the javascript files with embedded GQL strings.");
            logger.info("  --input-urls=<input-urls> : The path to a wordlist of urls to scan.");
            logger.info("  --input-schema=<input-schema> : URL to a graphQL endpoint with introspection enabled or the path to a file containing the json response of an introspection query.");
            logger.info("  --input-operations=<input-operations-directory> : The directory containing previously extracted .graphql operations, this avoids resource intensive Javascript AST parsing.");
            logger.info("  --request-header=<header-key-value> : Request header key/value to set in introspection requests e.g. --request-header=\"Api-Key1: XXXX\" --request-header=\"Api-Key2: YYYY\".");
            logger.info("  --search-field=<field-name> : The field name paths to search for in the schema/operations.");
            logger.info("  --depth=<search-depth> : Depth of the field path search, defaults to 10 if not specified.");
            logger.info("  --default-params=<default-params> : The path to a json file of default parameter values.");
            logger.info("  --output-directory=<output-directory> : The directory where the generated files will be saved.");
            logger.info("  --output-mode=<output-mode> : The output mode for the generated files. Possible values are 'requests', 'operations', 'fields', 'paths' and 'all'. The default value is 'requests'.");
            System.exit(0);
        }

        if (!appArgs.containsOption(Constants.Arguments.INPUT_DIRECTORY) && !appArgs.containsOption(Constants.Arguments.INPUT_URLS)
                && !appArgs.containsOption(Constants.Arguments.INPUT_SCHEMA) && !appArgs.containsOption(Constants.Arguments.INPUT_OPERATIONS)) {
            logger.error("--input-directory or --input-urls or --input-schema or --input-operations not provided. Exiting application. Use the --help option for more information.");
            System.exit(1);
        }

        if (!appArgs.containsOption(Constants.Arguments.OUTPUT_DIRECTORY)) {
            logger.error("--output-directory not provided. Exiting application. Use the --help option for more information.");
            System.exit(1);
        }

        gqlExtractorOutputHandlerService.handleGQLExtractorOutput();
        System.exit(0);
    }
}
