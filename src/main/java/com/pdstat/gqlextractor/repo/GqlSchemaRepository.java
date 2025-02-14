package com.pdstat.gqlextractor.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdstat.gqlextractor.Constants;
import graphql.introspection.IntrospectionResultToSchema;
import graphql.language.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GqlSchemaRepository {

    private static final Logger logger = LoggerFactory.getLogger(GqlSchemaRepository.class);

    private static final String INTROSPECTION_QUERY = "query IntrospectionQuery { __schema { queryType { name } " +
            "mutationType { name } subscriptionType { name } types { ...FullType } directives { name description " +
            "locations args { ...InputValue } } } } fragment FullType on __Type { kind name description " +
            "fields(includeDeprecated: true) { name description args { ...InputValue } type { ...TypeRef } " +
            "isDeprecated deprecationReason } inputFields { ...InputValue } interfaces { ...TypeRef } " +
            "enumValues(includeDeprecated: true) { name description isDeprecated deprecationReason } possibleTypes " +
            "{ ...TypeRef }} fragment InputValue on __InputValue { name  description type { ...TypeRef }  defaultValue}" +
            " fragment TypeRef on __Type { kind  name  ofType { kind name ofType { kind name ofType { kind name ofType " +
            "{ kind name ofType { kind name ofType { kind name ofType { kind name } } } } } } }}";

    private Document gqlSchema;

    private final ObjectMapper mapper;
    private final ApplicationArguments appArgs;

    public GqlSchemaRepository(ObjectMapper mapper, ApplicationArguments appArgs) {
        this.mapper = mapper;
        this.appArgs = appArgs;
    }

    public Document getGqlSchema(String inputSchema) {
        if (gqlSchema == null) {
            try {
                handleRemoteIntrospectionSchema(inputSchema);
            } catch (MalformedURLException e) {
                handleLocalIntrospectionSchema(inputSchema);
            }
        }
        return gqlSchema;
    }

    private void handleLocalIntrospectionSchema(String inputSchema) {
        Path schemaFilePath = Path.of(inputSchema);
        if (schemaFilePath.toFile().exists()) {
            try {
                String schema = Files.readAllBytes(schemaFilePath).toString();
                Map<String, Object> responseMap = mapper.readValue(schema, new TypeReference<HashMap<String, Object>>() {
                });
                if (responseMap.containsKey("data") && responseMap.get("data") != null) {
                    initGqlSchemaDocument(responseMap);
                } else {
                    logger.error("Invalid schema file: {}", inputSchema);
                    System.exit(1);
                }
            } catch (Exception e) {
                logger.error("Error reading schema file: {}", e.getMessage());
                System.exit(1);
            }
        } else {
            logger.error("Schema file does not exist: {}", inputSchema);
            System.exit(1);
        }
    }

    private void handleRemoteIntrospectionSchema(String inputSchema) throws MalformedURLException {
        new URL(inputSchema);
        WebClient client = WebClient.builder().codecs(configurer -> configurer.defaultCodecs()
                .maxInMemorySize(20 * 1024 * 1024))
                .baseUrl(inputSchema).build();
        ResponseEntity<String> response = client.post()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> initialiseRequestHeaders().forEach(httpHeaders::add))
                .bodyValue("{\"query\":\"" + INTROSPECTION_QUERY + "\"}")
                .retrieve().toEntity(String.class).block();
        if (response != null && response.getStatusCode().is2xxSuccessful()) {
            try {
                TypeReference<HashMap<String, Object>> typeRef
                        = new TypeReference<HashMap<String, Object>>() {
                };
                Map<String, Object> responseMap = mapper.readValue(response.getBody(), typeRef);
                if (responseMap.containsKey("errors")) {
                    List<Map<String, Object>> errors = (List<Map<String, Object>>) responseMap.get("errors");
                    if (errors.isEmpty()) {
                        logger.error("Introspection query failed");
                    } else {
                        Map<String, Object> error = errors.get(0);
                        logger.error("Introspection query failed: {}", mapper.writeValueAsString(error));
                    }
                    System.exit(1);
                } else if (responseMap.containsKey("data")) {
                    initGqlSchemaDocument(responseMap);
                } else {
                    logger.error("Introspection query failed");
                    System.exit(1);
                }
            } catch (JsonProcessingException e) {
                logger.error("Introspection query failed: {}", e.getMessage());
                System.exit(1);
            }
        } else {
            logger.error("Introspection query failed, server responded with status code: {}",
                    response != null ? response.getStatusCode() : "null");
            System.exit(1);
        }
    }

    private void initGqlSchemaDocument(Map<String, Object> responseMap) {
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        IntrospectionResultToSchema introspectionResultToSchema = new IntrospectionResultToSchema();
        gqlSchema = introspectionResultToSchema.createSchemaDefinition(data);
    }

    private Map<String, String> initialiseRequestHeaders() {
        Map<String, String> headers = new HashMap<>();
        if (appArgs.containsOption(Constants.Arguments.REQUEST_HEADER)) {
            List<String> requestHeaders = appArgs.getOptionValues(Constants.Arguments.REQUEST_HEADER);
            for (String requestHeader : requestHeaders) {
                String[] header = requestHeader.split(":");
                if (header.length == 2) {
                    headers.put(header[0].trim(), header[1].trim());
                }
            }
        }
        return headers;
    }
}
