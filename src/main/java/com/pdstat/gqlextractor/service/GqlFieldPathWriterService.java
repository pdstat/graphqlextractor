package com.pdstat.gqlextractor.service;

import com.pdstat.gqlextractor.repo.GqlOperationsRepository;
import graphql.language.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class GqlFieldPathWriterService {

    private static final Logger logger = LoggerFactory.getLogger(GqlFieldPathWriterService.class);

    private final GqlOperationsRepository gqlOperationsRepository;
    private final GqlPathFinder gqlPathFinder;

    public GqlFieldPathWriterService(GqlOperationsRepository gqlOperationsRepository,
                                     GqlPathFinder gqlPathFinder) {
        this.gqlOperationsRepository = gqlOperationsRepository;
        this.gqlPathFinder = gqlPathFinder;
    }

    public void writeFieldsReport(String outputDirectory, String searchField) {
        logger.info("Finding field paths for field: {}", searchField);
        String fieldsFileName = outputDirectory + "/graphql-field-paths-report.txt";
        Path fieldsFilePath = Paths.get(fieldsFileName);
        Set<String> fieldPaths = new HashSet<>();
        for (Document document: gqlOperationsRepository.getGqlOperations().values()) {
            fieldPaths.addAll(gqlPathFinder.findFieldPaths(document, searchField));
        }
        List<String> sortedFieldPaths = new ArrayList<>(fieldPaths);
        Collections.sort(sortedFieldPaths);

        try (BufferedWriter writer = Files.newBufferedWriter(fieldsFilePath)) {
            logger.info("Writing GQL operations fields report file");
            for (String field : sortedFieldPaths) {
                logger.info(field);
                writer.write(field);
                writer.newLine();
            }
        } catch (IOException e) {
            logger.error("Error writing fields file: {}", fieldsFileName, e);
        }

    }

}
