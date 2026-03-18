package com.pdstat.gqlextractor.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdstat.gqlextractor.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class DefaultParamsRepositoryTest {

    @Mock
    private ApplicationArguments appArgs;

    @Test
    void testGetDefaultParamReturnsNullWhenNoParamsConfigured() {
        Mockito.when(appArgs.containsOption(Constants.Arguments.DEFAULT_PARAMS)).thenReturn(false);

        DefaultParamsRepository repo = new DefaultParamsRepository(new ObjectMapper(), appArgs);
        repo.init();

        Assertions.assertNull(repo.getDefaultParam("anyParam"));
    }

    @Test
    void testGetDefaultParamReturnsValueFromFile(@TempDir Path tempDir) throws IOException {
        Path paramsFile = tempDir.resolve("params.json");
        Files.writeString(paramsFile, "{\"userId\": \"test-123\", \"limit\": 10}");

        Mockito.when(appArgs.containsOption(Constants.Arguments.DEFAULT_PARAMS)).thenReturn(true);
        Mockito.when(appArgs.getOptionValues(Constants.Arguments.DEFAULT_PARAMS))
                .thenReturn(List.of(paramsFile.toString()));

        DefaultParamsRepository repo = new DefaultParamsRepository(new ObjectMapper(), appArgs);
        repo.init();

        Assertions.assertEquals("test-123", repo.getDefaultParam("userId"));
        Assertions.assertEquals(10, repo.getDefaultParam("limit"));
    }

    @Test
    void testGetDefaultParamReturnsNullForMissingKey(@TempDir Path tempDir) throws IOException {
        Path paramsFile = tempDir.resolve("params.json");
        Files.writeString(paramsFile, "{\"userId\": \"test-123\"}");

        Mockito.when(appArgs.containsOption(Constants.Arguments.DEFAULT_PARAMS)).thenReturn(true);
        Mockito.when(appArgs.getOptionValues(Constants.Arguments.DEFAULT_PARAMS))
                .thenReturn(List.of(paramsFile.toString()));

        DefaultParamsRepository repo = new DefaultParamsRepository(new ObjectMapper(), appArgs);
        repo.init();

        Assertions.assertNull(repo.getDefaultParam("nonExistent"));
    }

    @Test
    void testInitHandlesInvalidJsonFile(@TempDir Path tempDir) throws IOException {
        Path paramsFile = tempDir.resolve("params.json");
        Files.writeString(paramsFile, "not valid json");

        Mockito.when(appArgs.containsOption(Constants.Arguments.DEFAULT_PARAMS)).thenReturn(true);
        Mockito.when(appArgs.getOptionValues(Constants.Arguments.DEFAULT_PARAMS))
                .thenReturn(List.of(paramsFile.toString()));

        DefaultParamsRepository repo = new DefaultParamsRepository(new ObjectMapper(), appArgs);
        // Should not throw, just log the error
        Assertions.assertDoesNotThrow(repo::init);
        Assertions.assertNull(repo.getDefaultParam("anyParam"));
    }

    @Test
    void testInitHandlesNonExistentFile() {
        Mockito.when(appArgs.containsOption(Constants.Arguments.DEFAULT_PARAMS)).thenReturn(true);
        Mockito.when(appArgs.getOptionValues(Constants.Arguments.DEFAULT_PARAMS))
                .thenReturn(List.of("/nonexistent/path/params.json"));

        DefaultParamsRepository repo = new DefaultParamsRepository(new ObjectMapper(), appArgs);
        Assertions.assertDoesNotThrow(repo::init);
        Assertions.assertNull(repo.getDefaultParam("anyParam"));
    }

    @Test
    void testGetDefaultParamWithNestedObject(@TempDir Path tempDir) throws IOException {
        Path paramsFile = tempDir.resolve("params.json");
        Files.writeString(paramsFile, "{\"input\": {\"name\": \"test\", \"value\": 42}}");

        Mockito.when(appArgs.containsOption(Constants.Arguments.DEFAULT_PARAMS)).thenReturn(true);
        Mockito.when(appArgs.getOptionValues(Constants.Arguments.DEFAULT_PARAMS))
                .thenReturn(List.of(paramsFile.toString()));

        DefaultParamsRepository repo = new DefaultParamsRepository(new ObjectMapper(), appArgs);
        repo.init();

        Object input = repo.getDefaultParam("input");
        Assertions.assertNotNull(input);
        Assertions.assertInstanceOf(java.util.Map.class, input);
    }
}
