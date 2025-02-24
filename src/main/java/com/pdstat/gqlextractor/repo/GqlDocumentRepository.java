package com.pdstat.gqlextractor.repo;

import com.pdstat.gqlextractor.Constants;
import com.pdstat.gqlextractor.extractor.GqlDocumentExtractor;
import graphql.language.Document;
import graphql.parser.InvalidSyntaxException;
import graphql.parser.Parser;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Repository
public class GqlDocumentRepository {

    private static final Logger logger = LoggerFactory.getLogger(GqlDocumentRepository.class);

    private static final String JS_EXTENSION = ".js";
    private static final String GQL_EXTENSION = ".graphql";

    private final ApplicationArguments appArgs;
    private final GqlDocumentExtractor gqlDocumentExtractor;
    private final List<Document> gqlDocuments = new ArrayList<>();

    public GqlDocumentRepository(ApplicationArguments appArgs, GqlDocumentExtractor gqlDocumentExtractor) {
        this.appArgs = appArgs;
        this.gqlDocumentExtractor = gqlDocumentExtractor;
    }

    @PostConstruct
    void initGqlDocuments() {
        if (gqlDocuments.isEmpty()) {
            if (appArgs.containsOption(Constants.Arguments.INPUT_DIRECTORY) ||
                    appArgs.containsOption(Constants.Arguments.INPUT_URLS)) {
                logger.info("Scanning for GraphQL Documents");
                if (appArgs.containsOption(Constants.Arguments.INPUT_DIRECTORY)) {
                    String scanDirectory = appArgs.getOptionValues(Constants.Arguments.INPUT_DIRECTORY).get(0);
                    try (Stream<Path> paths = Files.walk(Paths.get(scanDirectory))) {
                        paths.filter(Files::isRegularFile)
                                .forEach(filePath -> processJavascriptFile(filePath.toString()));
                    } catch (IOException e) {
                        logger.error("Error reading directory: {}", scanDirectory, e);
                    }
                }

                if (appArgs.containsOption(Constants.Arguments.INPUT_URLS)) {
                    String inputUrls = appArgs.getOptionValues(Constants.Arguments.INPUT_URLS).get(0);
                    try (Stream<String> urls = Files.lines(Paths.get(inputUrls))) {
                        urls.forEach(this::processUrl);
                    } catch (IOException e) {
                        logger.error("Error reading input urls: {}", inputUrls, e);
                    }
                }
            } else if (appArgs.containsOption(Constants.Arguments.INPUT_OPERATIONS)) {
                String scanDirectory = appArgs.getOptionValues(Constants.Arguments.INPUT_OPERATIONS).get(0);
                try (Stream<Path> paths = Files.walk(Paths.get(scanDirectory))) {
                    paths.filter(Files::isRegularFile)
                            .forEach(filePath -> processGqlOperationsFile(filePath.toString()));
                } catch (IOException e) {
                    logger.error("Error reading directory: {}", scanDirectory, e);
                }
            }
        }
    }

    public List<Document> getGqlDocuments() {
        return gqlDocuments;
    }

    private void processUrl(String url) {
        try {
            logger.info("Processing URL: {}", url);
            WebClient client = WebClient.builder().codecs(configurer -> configurer.defaultCodecs()
                    .maxInMemorySize(20 * 1024 * 1024)).baseUrl(url).build();
            String content = client.get().retrieve().bodyToMono(String.class).block();
            gqlDocuments.addAll(gqlDocumentExtractor.extract(content));
        } catch (Exception e) {
            logger.error("Error reading URL: {}", url, e);
        }
    }

    private void processGqlOperationsFile(String filePath) {
        if (filePath.endsWith(GQL_EXTENSION)) {
            Path gqlFilePath = Paths.get(filePath);
            logger.info("Processing graphql operation file: {}", gqlFilePath.getFileName());
            try {
                String content = Files.readString(gqlFilePath);
                gqlDocuments.add(Parser.parse(content));
            } catch (IOException | InvalidSyntaxException e) {
                logger.error("Error reading graphql operation file: {}", gqlFilePath.getFileName(), e);
            }

        }
    }

    private void processJavascriptFile(String filePath) {
        if (filePath.endsWith(JS_EXTENSION)) {
            Path jsFilePath = Paths.get(filePath);
            logger.info("Processing javascript file: {}", jsFilePath.getFileName());
            try {
                String content = Files.readString(jsFilePath);
                gqlDocuments.addAll(gqlDocumentExtractor.extract(content));
            } catch (IOException e) {
                logger.error("Error reading javascript file: {}", jsFilePath.getFileName(), e);
            }
        }
    }

}
