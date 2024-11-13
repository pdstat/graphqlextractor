package com.pdstat.gqlextractor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdstat.gqlextractor.model.GqlRequest;
import com.pdstat.gqlextractor.model.OutputMode;
import com.pdstat.gqlextractor.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

@Service
public class GqlFileWriterService {

    private static final Logger logger = LoggerFactory.getLogger(GqlFileWriterService.class);
    private final GqlStringsRepository gqlStringsRepository;
    private final GqlFragmentsRepository gqlFragmentsRepository;

    private final GqlMutationsRepository gqlMutationsRepository;

    private final GqlQueryRepository gqlQueryRepository;

    private final DefaultParamsRepository defaultParamsRepository;

    private final ObjectMapper mapper;

    public GqlFileWriterService(GqlStringsRepository gqlStringsRepository,
                                GqlFragmentsRepository gqlFragmentsRepository,
                                GqlQueryRepository gqlQueryRepository,
                                GqlMutationsRepository gqlMutationsRepository,
                                DefaultParamsRepository defaultParamsRepository,
                                ObjectMapper mapper) {
        this.gqlStringsRepository = gqlStringsRepository;
        this.gqlFragmentsRepository = gqlFragmentsRepository;
        this.gqlQueryRepository = gqlQueryRepository;
        this.gqlMutationsRepository = gqlMutationsRepository;
        this.defaultParamsRepository = defaultParamsRepository;
        this.mapper = mapper;
    }

    public void writeGqlFiles(String outputDirectory, OutputMode outputMode) {
        initGqlRepositories();
        writeGqlQueries(outputDirectory, outputMode);
        writeGqlMutations(outputDirectory, outputMode);
        logger.info("Finished extracting GQL files");
    }

    private void writeGqlQueries(String outputDirectory, OutputMode outputMode) {
        Map<String, String> gqlQueries = gqlQueryRepository.getGqlQueries();
        gqlQueries.forEach((queryName, query) -> writeFile(outputDirectory, queryName, query, outputMode));
    }
    private void writeGqlMutations(String outputDirectory, OutputMode outputMode) {
        Map<String, String> gqlMutations = gqlMutationsRepository.getGqlMutations();
        gqlMutations.forEach((mutationName, mutation) -> writeFile(outputDirectory, mutationName, mutation, outputMode));
    }

    private void writeFile(String outputDirectory, String name, String content, OutputMode outputMode) {
        try {
            if (outputMode == OutputMode.GQL) {
                writeGqlFile(name, content, outputDirectory);
            } else if (outputMode == OutputMode.JSON) {
                writeJsonFile(name, content, outputDirectory);
            } else {
                writeGqlFile(name, content, outputDirectory);
                writeJsonFile(name, content, outputDirectory);
            }
        } catch (IOException e) {
            logger.error("Error writing file: " + name, e);
        }
    }

    private void writeJsonFile(String name, String content, String outputDirectory) throws IOException {
        String jsonFileName = outputDirectory + "/" + name + ".json";
        Path jsonFilePath = Paths.get(jsonFileName);
        logger.info("Writing json file: {}", jsonFilePath.getFileName());
        Files.write(jsonFilePath, mapper.writeValueAsString(new GqlRequest(name, content, defaultParamsRepository)).getBytes());
    }

    private static void writeGqlFile(String name, String content, String outputDirectory) throws IOException {
        String gqlFileName = outputDirectory + "/" + name + ".graphql";
        Path gqlFilePath = Paths.get(gqlFileName);
        logger.info("Writing graphql file: {}", gqlFilePath.getFileName());
        Files.write(gqlFilePath, content.getBytes());
    }

    private void initGqlRepositories() {
        Set<String> gqlStrings = gqlStringsRepository.getGqlStrings();
        gqlStrings.forEach(gqlFragmentsRepository::addGqlFragment);
        gqlStrings.forEach(gqlQueryRepository::addGqlQuery);
        gqlStrings.forEach(gqlMutationsRepository::addGqlMutation);
    }

}
