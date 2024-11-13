package com.pdstat.gqlextractor;

import com.pdstat.gqlextractor.model.OutputMode;
import com.pdstat.gqlextractor.service.GqlFileWriterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GqlApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(GqlApplication.class);

    @Autowired
    private ApplicationArguments appArgs;
    @Autowired
    private GqlFileWriterService gqlFileWriterService;

    public static void main(String[] args) {
        SpringApplication.run(GqlApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (appArgs.containsOption(Constants.Arguments.HELP)) {
            // Log information about the application and its arguments
            logger.info("GqlExtractor is an application that extracts .graphql and .json files from a directory of javascript files that contain GraphQL strings.");
            logger.info("The application requires the following arguments:");
            logger.info("  --input-directory=<input-directory> : The directory containing the javascript files with embedded GQL strings.");
            logger.info("  --input-urls=<input-urls> : The path to a wordlist of urls to scan.");
            logger.info("  --default-params=<default-params> : The path to a json file of default parameter values.");
            logger.info("  --output-directory=<output-directory> : The directory where the generated GraphQL files will be saved.");
            logger.info("  --output-mode=<output-mode> : The output mode for the generated files. Possible values are 'json', 'graphql' and 'all'. The default value is 'json'.");
            System.exit(0);
        }

        if (!appArgs.containsOption(Constants.Arguments.INPUT_DIRECTORY) && !appArgs.containsOption(Constants.Arguments.INPUT_URLS)) {
            logger.error("--input-directory and --input-urls not provided. Exiting application. Use the --help option for more information.");
            System.exit(1);
        }

        if (!appArgs.containsOption(Constants.Arguments.OUTPUT_DIRECTORY)) {
            logger.error("--output-directory not provided. Exiting application. Use the --help option for more information.");
            System.exit(1);
        }
        String outputDirectory = appArgs.getOptionValues(Constants.Arguments.OUTPUT_DIRECTORY).get(0);
        OutputMode outputMode = OutputMode.JSON;
        if (appArgs.containsOption(Constants.Arguments.OUTPUT_MODE)) {
            outputMode = OutputMode.fromMode(appArgs.getOptionValues(Constants.Arguments.OUTPUT_MODE).get(0).toLowerCase());
        }
        gqlFileWriterService.writeGqlFiles(outputDirectory, outputMode);
    }
}
