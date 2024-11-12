package com.pdstat.gqlextractor.repo;

import com.pdstat.gqlextractor.extractor.GqlStringsExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@Repository
public class GqlStringsRepository {

    private static final Logger logger = LoggerFactory.getLogger(GqlStringsRepository.class);
    private static final String JS_EXTENSION = ".js";

    private final GqlStringsExtractor gqlStringsExtractor;
    private final Set<String> gqlStrings = new HashSet<>();

    public GqlStringsRepository(GqlStringsExtractor gqlStringsExtractor) {
        this.gqlStringsExtractor = gqlStringsExtractor;
    }

    public Set<String> getGqlStrings(String scanDirectory) {
        logger.info("Scanning directory for GraphQL Strings");
        try {
            Files.walk(Paths.get(scanDirectory))
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> processFile(filePath.toString()));
        } catch (IOException e) {
            logger.error("Error reading directory: {}", scanDirectory, e);
        }
        return gqlStrings;
    }

    private void processFile(String filePath) {
        if (filePath.endsWith(JS_EXTENSION)) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(filePath)));
                gqlStrings.addAll(gqlStringsExtractor.extract(content));
            } catch (IOException e) {
                logger.error("Error reading file: {}", filePath, e);
            }
        }
    }
}