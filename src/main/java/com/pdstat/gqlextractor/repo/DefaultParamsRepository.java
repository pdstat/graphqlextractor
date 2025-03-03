package com.pdstat.gqlextractor.repo;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdstat.gqlextractor.Constants;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Repository
public class DefaultParamsRepository {

    private static final Logger logger = LoggerFactory.getLogger(DefaultParamsRepository.class);
    private Map<String, Object> defaultParams;
    private final ObjectMapper mapper;
    private final ApplicationArguments appArgs;

    public DefaultParamsRepository(ObjectMapper mapper, ApplicationArguments appArgs) {
        this.mapper = mapper;
        this.appArgs = appArgs;
    }

    @PostConstruct
    void init() {
        if (appArgs.containsOption(Constants.Arguments.DEFAULT_PARAMS)) {
            logger.info("Initializing default params repository");
            String defaultParamsFile = appArgs.getOptionValues(Constants.Arguments.DEFAULT_PARAMS).get(0);
            Path defaultParamsPath = Paths.get(defaultParamsFile);
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
            };
            try {
                defaultParams = mapper.readValue(defaultParamsPath.toFile(), typeRef);
            } catch (IOException e) {
                logger.error("Error reading default params file, skipping", e);
            }
        }
    }

    public Object getDefaultParam(String paramName) {
        if (defaultParams == null) {
            return null;
        }
        return defaultParams.get(paramName);
    }
}
