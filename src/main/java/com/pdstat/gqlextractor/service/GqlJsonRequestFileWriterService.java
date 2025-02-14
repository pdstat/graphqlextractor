package com.pdstat.gqlextractor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdstat.gqlextractor.model.GqlRequest;
import com.pdstat.gqlextractor.repo.GqlOperationsRepository;
import graphql.language.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class GqlJsonRequestFileWriterService {

    private static final Logger logger = LoggerFactory.getLogger(GqlJsonRequestFileWriterService.class);

    private final GqlOperationsRepository gqlOperationsRepository;
    private final GqlRequestFactoryService gqlRequestFactoryService;
    private final ObjectMapper mapper;

    public GqlJsonRequestFileWriterService(GqlOperationsRepository gqlOperationsRepository,
                                           GqlRequestFactoryService gqlRequestFactoryService,
                                           ObjectMapper mapper) {
        this.gqlOperationsRepository = gqlOperationsRepository;
        this.gqlRequestFactoryService = gqlRequestFactoryService;
        this.mapper = mapper;
    }

    public void writeJsonRequestFiles(String outputDirectory)  {
        for (Document document : gqlOperationsRepository.getGqlOperations().values()) {
            GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
            String jsonFileName = outputDirectory + "/" + gqlRequest.getOperationName() + ".json";
            Path jsonFilePath = Paths.get(jsonFileName);
            logger.info("Writing json request file: {}", jsonFilePath.getFileName());
            try {
                Files.write(jsonFilePath, mapper.writeValueAsString(gqlRequest).getBytes());
            } catch (IOException e) {
                logger.error("Error writing json request file: {}", jsonFilePath.getFileName());
            }
        }
    }
}
