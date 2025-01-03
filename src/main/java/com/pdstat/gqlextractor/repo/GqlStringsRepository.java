package com.pdstat.gqlextractor.repo;

import com.pdstat.gqlextractor.Constants;
import com.pdstat.gqlextractor.extractor.GqlStringsExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@Repository
public class GqlStringsRepository {

    private static final Logger logger = LoggerFactory.getLogger(GqlStringsRepository.class);
    private static final String JS_EXTENSION = ".js";
    private final GqlStringsExtractor gqlStringsExtractor;
    private final ApplicationArguments appArgs;
    private final Set<String> gqlStrings = new HashSet<>();

    public GqlStringsRepository(GqlStringsExtractor gqlStringsExtractor, ApplicationArguments appArgs) {
        this.gqlStringsExtractor = gqlStringsExtractor;
        this.appArgs = appArgs;
    }

    public Set<String> getGqlStrings() {
        logger.info("Scanning for GraphQL Strings");
        if (appArgs.containsOption(Constants.Arguments.INPUT_DIRECTORY)) {
            String scanDirectory = appArgs.getOptionValues(Constants.Arguments.INPUT_DIRECTORY).get(0);
            try {
                Files.walk(Paths.get(scanDirectory))
                        .filter(Files::isRegularFile)
                        .forEach(filePath -> processFile(filePath.toString()));
            } catch (IOException e) {
                logger.error("Error reading directory: {}", scanDirectory);
            }
        }

        if (appArgs.containsOption(Constants.Arguments.INPUT_URLS)) {
            String inputUrls = appArgs.getOptionValues(Constants.Arguments.INPUT_URLS).get(0);
            // For each URL in the inputUrls text file, get the content and extract the GQL strings using the rest template and the gqlStringsExtractor
            try {
                Files.lines(Paths.get(inputUrls))
                        .forEach(url -> processUrl(url));
            } catch (IOException e) {
                logger.error("Error reading input urls: {}", inputUrls);
            }

        }
        return gqlStrings;
    }

    private void processUrl(String url) {
        try {
            logger.info("Processing URL: {}", url);
            WebClient client = WebClient.builder().codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(20 * 1024 * 1024)).baseUrl(url).build();
            String content = client.get().retrieve().bodyToMono(String.class).block();
            gqlStrings.addAll(gqlStringsExtractor.extract(content));
        } catch (Exception e) {
            logger.error("Error reading URL: {}", url);
        }
    }

    private void processFile(String filePath) {
        if (filePath.endsWith(JS_EXTENSION)) {
            Path jsFilePath = Paths.get(filePath);
            logger.info("Processing file: {}", jsFilePath.getFileName());
            try {
                String content = new String(Files.readAllBytes(jsFilePath));
                gqlStrings.addAll(gqlStringsExtractor.extract(content));
            } catch (IOException e) {
                logger.error("Error reading file: {}", jsFilePath.getFileName());
            }
        }
    }
}