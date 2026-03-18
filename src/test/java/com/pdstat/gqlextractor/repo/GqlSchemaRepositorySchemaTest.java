package com.pdstat.gqlextractor.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import graphql.language.Document;

@ExtendWith(MockitoExtension.class)
public class GqlSchemaRepositorySchemaTest {

    @Spy
    private ObjectMapper mapper = new ObjectMapper();

    @Mock
    private ApplicationArguments appArgs;

    private static final String INTROSPECTION_JSON = """
            {
              "data": {
                "__schema": {
                  "queryType": { "name": "Query" },
                  "types": [
                    {
                      "kind": "OBJECT",
                      "name": "Query",
                      "fields": [
                        {
                          "name": "user",
                          "args": [
                            {
                              "name": "id",
                              "type": {
                                "kind": "NON_NULL",
                                "name": null,
                                "ofType": { "kind": "SCALAR", "name": "ID", "ofType": null }
                              }
                            }
                          ],
                          "type": { "kind": "OBJECT", "name": "User", "ofType": null }
                        }
                      ]
                    },
                    {
                      "kind": "OBJECT",
                      "name": "User",
                      "fields": [
                        {
                          "name": "id",
                          "args": [],
                          "type": { "kind": "SCALAR", "name": "ID", "ofType": null }
                        },
                        {
                          "name": "name",
                          "args": [],
                          "type": { "kind": "SCALAR", "name": "String", "ofType": null }
                        }
                      ]
                    },
                    {
                      "kind": "SCALAR",
                      "name": "ID",
                      "fields": null
                    },
                    {
                      "kind": "SCALAR",
                      "name": "String",
                      "fields": null
                    }
                  ]
                }
              }
            }
            """;

    @Test
    void testGetGqlSchemaFromLocalIntrospectionJson(@TempDir Path tempDir) throws IOException {
        Path schemaFile = tempDir.resolve("introspection.json");
        Files.writeString(schemaFile, INTROSPECTION_JSON);

        GqlSchemaRepository repo = new GqlSchemaRepository(mapper, appArgs);
        Document result = repo.getGqlSchema(schemaFile.toString());

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getDefinitions().isEmpty());
    }

    @Test
    void testGetGqlSchemaReturnsCachedSchema(@TempDir Path tempDir) throws IOException {
        Path schemaFile = tempDir.resolve("schema.json");
        Files.writeString(schemaFile, INTROSPECTION_JSON);

        GqlSchemaRepository repo = new GqlSchemaRepository(mapper, appArgs);
        Document result1 = repo.getGqlSchema(schemaFile.toString());
        Document result2 = repo.getGqlSchema(schemaFile.toString());

        Assertions.assertSame(result1, result2);
    }
}
