package com.pdstat.gqlextractor.service;

import com.pdstat.gqlextractor.repo.GqlSchemaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class GqlSchemaFieldPathWriterService {

    private static final Logger logger = LoggerFactory.getLogger(GqlSchemaFieldPathWriterService.class);

    private final GqlSchemaRepository gqlSchemaRepository;
    private final GqlSchemaPathFinder gqlSchemaPathFinder;

    public GqlSchemaFieldPathWriterService(GqlSchemaRepository gqlSchemaRepository, GqlSchemaPathFinder gqlSchemaPathFinder) {
        this.gqlSchemaRepository = gqlSchemaRepository;
        this.gqlSchemaPathFinder = gqlSchemaPathFinder;
    }

    public void writeSchemaFieldPaths(String outputDirectory, String inputSchema, String searchField, int maxDepth) {
        logger.info("Finding schema field paths for field: {}", searchField);
        String schemaFieldPathsFileName = outputDirectory + "/graphql-schema-field-paths.txt";
        Path schemaFieldPathsFilePath = Paths.get(schemaFieldPathsFileName);
        List<String> gqlSchemaFieldPaths =gqlSchemaPathFinder
                .findFieldPaths(gqlSchemaRepository.getGqlSchema(inputSchema), searchField, maxDepth);
        try (BufferedWriter writer = Files.newBufferedWriter(schemaFieldPathsFilePath)) {
            logger.info("Writing GQL schema fields report file");
            for (String fieldPath : gqlSchemaFieldPaths) {
                logger.info(fieldPath);
                writer.write(fieldPath);
                writer.newLine();
            }
        } catch (IOException e) {
            logger.error("Error writing schema field paths file: {}", schemaFieldPathsFileName, e);
        }

    }

}
