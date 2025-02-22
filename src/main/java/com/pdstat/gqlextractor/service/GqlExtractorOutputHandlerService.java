package com.pdstat.gqlextractor.service;

import com.pdstat.gqlextractor.Constants;
import com.pdstat.gqlextractor.model.OutputMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GqlExtractorOutputHandlerService {

    private static final Logger logger = LoggerFactory.getLogger(GqlExtractorOutputHandlerService.class);

    private static final int DEFAULT_MAX_DEPTH = 10;

    private final GqlFieldWordListWriterService gqlFieldWordListWriterService;
    private final GqlOperationFilesWriterService gqlOperationFilesWriterService;
    private final GqlJsonRequestFileWriterService gqlJsonRequestFileWriterService;
    private final GqlSchemaFieldPathWriterService gqlSchemaFieldPathWriterService;
    private final GqlFieldPathWriterService gqlFieldPathWriterService;
    private final ApplicationArguments appArgs;

    public GqlExtractorOutputHandlerService(GqlFieldWordListWriterService gqlFieldWordListWriterService,
                                            GqlOperationFilesWriterService gqlOperationFilesWriterService,
                                            GqlJsonRequestFileWriterService gqlJsonRequestFileWriterService,
                                            GqlSchemaFieldPathWriterService gqlSchemaFieldPathWriterService,
                                            GqlFieldPathWriterService gqlFieldPathWriterService,
                                            ApplicationArguments appArgs) {
        this.gqlFieldWordListWriterService = gqlFieldWordListWriterService;
        this.gqlOperationFilesWriterService = gqlOperationFilesWriterService;
        this.gqlJsonRequestFileWriterService = gqlJsonRequestFileWriterService;
        this.gqlSchemaFieldPathWriterService = gqlSchemaFieldPathWriterService;
        this.gqlFieldPathWriterService = gqlFieldPathWriterService;
        this.appArgs = appArgs;
    }

    public void handleGQLExtractorOutput() {
        String outputDirectory = appArgs.getOptionValues(Constants.Arguments.OUTPUT_DIRECTORY).get(0);

        if (schemaInputProvided()) {
            handleSchemaFieldNameSearch(outputDirectory);
        } else {
            List<OutputMode> outputModes = getOutputModes();

            if (outputModes.contains(OutputMode.ALL)) {
                gqlFieldWordListWriterService.writeFieldsFile(outputDirectory);
                gqlOperationFilesWriterService.writeOperationFiles(outputDirectory);
                gqlJsonRequestFileWriterService.writeJsonRequestFiles(outputDirectory);
                handleOperationsFieldSearch(outputDirectory);
            } else {
                if (outputModes.contains(OutputMode.FIELDS)) {
                    gqlFieldWordListWriterService.writeFieldsFile(outputDirectory);
                }

                if (outputModes.contains(OutputMode.OPERATIONS)) {
                    gqlOperationFilesWriterService.writeOperationFiles(outputDirectory);
                }

                if (outputModes.contains(OutputMode.REQUESTS)) {
                    gqlJsonRequestFileWriterService.writeJsonRequestFiles(outputDirectory);
                }

                if (outputModes.contains(OutputMode.PATHS)) {
                    handleOperationsFieldSearch(outputDirectory);
                }
            }
        }

    }

    private void handleOperationsFieldSearch(String outputDirectory) {
        checkSearchFieldProvided();
        String searchField = appArgs.getOptionValues(Constants.Arguments.SEARCH_FIELD).get(0);
        gqlFieldPathWriterService.writeFieldsReport(outputDirectory, searchField);
    }

    private void handleSchemaFieldNameSearch(String outputDirectory) {
        checkSearchFieldProvided();
        int maxDepth = DEFAULT_MAX_DEPTH;
        if (appArgs.containsOption(Constants.Arguments.DEPTH)) {
            try {
                maxDepth = Integer.parseInt(appArgs.getOptionValues(Constants.Arguments.DEPTH).get(0));
            } catch (NumberFormatException e) {
                logger.warn("Invalid depth value provided, using default value of {}", DEFAULT_MAX_DEPTH);
            }
        }

        String inputSchema = appArgs.getOptionValues(Constants.Arguments.INPUT_SCHEMA).get(0);
        String searchField = appArgs.getOptionValues(Constants.Arguments.SEARCH_FIELD).get(0);
        gqlSchemaFieldPathWriterService.writeSchemaFieldPaths(outputDirectory, inputSchema, searchField, maxDepth);
    }

    private void checkSearchFieldProvided() {
        if (!appArgs.containsOption(Constants.Arguments.SEARCH_FIELD)) {
            logger.error("--search-field not provided. Exiting application. Use the --help option for more information.");
            System.exit(1);
        }
    }

    private boolean schemaInputProvided() {
        return appArgs.containsOption(Constants.Arguments.INPUT_SCHEMA);
    }

    private List<OutputMode> getOutputModes() {
        List<OutputMode> outputModes = new ArrayList<>();
        List<String> selectedOutputModes = appArgs.getOptionValues(Constants.Arguments.OUTPUT_MODE);
        for (String selectedOutputMode : selectedOutputModes) {
            outputModes.add(OutputMode.fromMode(selectedOutputMode));
        }
        return outputModes;
    }

}
