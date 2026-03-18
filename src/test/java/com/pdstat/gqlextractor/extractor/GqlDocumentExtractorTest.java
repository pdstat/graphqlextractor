package com.pdstat.gqlextractor.extractor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.pdstat.gqlextractor.deserialiser.DocumentDeserialiser;
import com.pdstat.gqlextractor.graal.GraalAcornWalker;
import com.pdstat.gqlextractor.service.ResourceService;
import graphql.language.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class GqlDocumentExtractorTest {

    private static final String DOCS_VISITOR = "docs-visitor-script";
    private static final String STRINGS_VISITOR = "strings-visitor-script";

    @Mock
    private Resource gqlDocsVisitorResource;

    @Mock
    private Resource gqlStringsVisitorResource;

    @Mock
    private GraalAcornWalker walker;

    @Mock
    private ResourceService resourceService;

    private GqlDocumentExtractor gqlDocumentExtractor;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Document.class, new DocumentDeserialiser());
        objectMapper.registerModule(module);
        gqlDocumentExtractor = new GqlDocumentExtractor(
                gqlDocsVisitorResource, gqlStringsVisitorResource,
                walker, resourceService, objectMapper);
    }

    private void mockBothVisitors() throws IOException {
        Mockito.when(resourceService.readResourceFileContent(gqlDocsVisitorResource)).thenReturn(DOCS_VISITOR);
        Mockito.when(resourceService.readResourceFileContent(gqlStringsVisitorResource)).thenReturn(STRINGS_VISITOR);
    }

    @Test
    void testExtractReturnsDocumentsFromJsonDocumentObjects() throws Exception {
        mockBothVisitors();
        String javascript = "var x = 'some javascript';";
        String documentJson = "[{\"kind\":\"Document\",\"definitions\":[{\"kind\":\"OperationDefinition\"," +
                "\"operation\":\"query\",\"name\":{\"kind\":\"Name\",\"value\":\"GetUser\"}," +
                "\"variableDefinitions\":[],\"selectionSet\":{\"kind\":\"SelectionSet\"," +
                "\"selections\":[{\"kind\":\"Field\",\"name\":{\"kind\":\"Name\",\"value\":\"user\"}," +
                "\"arguments\":[],\"selectionSet\":{\"kind\":\"SelectionSet\"," +
                "\"selections\":[{\"kind\":\"Field\",\"name\":{\"kind\":\"Name\",\"value\":\"id\"}," +
                "\"arguments\":[]}]}}]}}]}]";

        Mockito.when(walker.extractMatchedObjects(javascript, DOCS_VISITOR)).thenReturn(documentJson);
        Mockito.when(walker.extractMatchedObjects(javascript, STRINGS_VISITOR)).thenReturn(null);

        List<Document> documents = gqlDocumentExtractor.extract(javascript);
        Assertions.assertEquals(1, documents.size());
    }

    @Test
    void testExtractReturnsEmptyListWhenNoDocumentsFound() throws Exception {
        mockBothVisitors();
        String javascript = "var x = 'no graphql here';";

        Mockito.when(walker.extractMatchedObjects(javascript, DOCS_VISITOR)).thenReturn(null);
        Mockito.when(walker.extractMatchedObjects(javascript, STRINGS_VISITOR)).thenReturn(null);

        List<Document> documents = gqlDocumentExtractor.extract(javascript);
        Assertions.assertNotNull(documents);
        Assertions.assertTrue(documents.isEmpty());
    }

    @Test
    void testExtractHandlesGqlLanguageStrings() throws Exception {
        mockBothVisitors();
        String javascript = "var x = 'some js';";
        String documentJson = "[\"query GetUser { user { id name } }\"," +
                "\"mutation CreateUser($input: CreateUserInput!) { createUser(input: $input) { id } }\"]";

        Mockito.when(walker.extractMatchedObjects(javascript, DOCS_VISITOR)).thenReturn(null);
        Mockito.when(walker.extractMatchedObjects(javascript, STRINGS_VISITOR)).thenReturn(documentJson);

        List<Document> documents = gqlDocumentExtractor.extract(javascript);
        Assertions.assertEquals(2, documents.size());
    }

    @Test
    void testExtractHandlesGqlLanguageStringsWithLeadingText() throws Exception {
        mockBothVisitors();
        String javascript = "var x = 'some js';";
        String documentJson = "[\"some preamble text query GetUser { user { id } }\"]";

        Mockito.when(walker.extractMatchedObjects(javascript, DOCS_VISITOR)).thenReturn(null);
        Mockito.when(walker.extractMatchedObjects(javascript, STRINGS_VISITOR)).thenReturn(documentJson);

        List<Document> documents = gqlDocumentExtractor.extract(javascript);
        Assertions.assertEquals(1, documents.size());
    }

    @Test
    void testExtractSkipsStringsWithNoGqlKeyword() throws Exception {
        mockBothVisitors();
        String javascript = "var x = 'some js';";
        String documentJson = "[\"query ValidQuery { user { id } }\"," +
                "\"this string has no valid graphql keyword at all xyz\"]";

        Mockito.when(walker.extractMatchedObjects(javascript, DOCS_VISITOR)).thenReturn(null);
        Mockito.when(walker.extractMatchedObjects(javascript, STRINGS_VISITOR)).thenReturn(documentJson);

        List<Document> documents = gqlDocumentExtractor.extract(javascript);
        Assertions.assertEquals(1, documents.size());
    }

    @Test
    void testExtractHandlesIOException() throws Exception {
        Mockito.when(resourceService.readResourceFileContent(gqlDocsVisitorResource))
                .thenThrow(new IOException("Resource not found"));

        String javascript = "var x = 'some js';";
        List<Document> documents = gqlDocumentExtractor.extract(javascript);
        Assertions.assertNotNull(documents);
        Assertions.assertTrue(documents.isEmpty());
    }

    @Test
    void testExtractWithMutationString() throws Exception {
        mockBothVisitors();
        String javascript = "var x = 'some js';";
        String documentJson = "[\"mutation CreatePost($title: String!) { createPost(title: $title) { id title } }\"]";

        Mockito.when(walker.extractMatchedObjects(javascript, DOCS_VISITOR)).thenReturn(null);
        Mockito.when(walker.extractMatchedObjects(javascript, STRINGS_VISITOR)).thenReturn(documentJson);

        List<Document> documents = gqlDocumentExtractor.extract(javascript);
        Assertions.assertEquals(1, documents.size());
    }

    @Test
    void testExtractWithSubscriptionString() throws Exception {
        mockBothVisitors();
        String javascript = "var x = 'some js';";
        String documentJson = "[\"subscription OnMessage { messageAdded { id content } }\"]";

        Mockito.when(walker.extractMatchedObjects(javascript, DOCS_VISITOR)).thenReturn(null);
        Mockito.when(walker.extractMatchedObjects(javascript, STRINGS_VISITOR)).thenReturn(documentJson);

        List<Document> documents = gqlDocumentExtractor.extract(javascript);
        Assertions.assertEquals(1, documents.size());
    }

    @Test
    void testExtractWithFragmentString() throws Exception {
        mockBothVisitors();
        String javascript = "var x = 'some js';";
        String documentJson = "[\"fragment UserFields on User { id name email }\"]";

        Mockito.when(walker.extractMatchedObjects(javascript, DOCS_VISITOR)).thenReturn(null);
        Mockito.when(walker.extractMatchedObjects(javascript, STRINGS_VISITOR)).thenReturn(documentJson);

        List<Document> documents = gqlDocumentExtractor.extract(javascript);
        Assertions.assertEquals(1, documents.size());
    }

    @Test
    void testExtractCombinesBothVisitorResults() throws Exception {
        mockBothVisitors();
        String javascript = "var x = 'some js';";

        String docsResult = "[{\"kind\":\"Document\",\"definitions\":[{\"kind\":\"OperationDefinition\"," +
                "\"operation\":\"query\",\"name\":{\"kind\":\"Name\",\"value\":\"GetUser\"}," +
                "\"variableDefinitions\":[],\"selectionSet\":{\"kind\":\"SelectionSet\"," +
                "\"selections\":[{\"kind\":\"Field\",\"name\":{\"kind\":\"Name\",\"value\":\"user\"}," +
                "\"arguments\":[]}]}}]}]";
        String stringsResult = "[\"query GetPosts { posts { id title } }\"]";

        Mockito.when(walker.extractMatchedObjects(javascript, DOCS_VISITOR)).thenReturn(docsResult);
        Mockito.when(walker.extractMatchedObjects(javascript, STRINGS_VISITOR)).thenReturn(stringsResult);

        List<Document> documents = gqlDocumentExtractor.extract(javascript);
        Assertions.assertEquals(2, documents.size());
    }

    @Test
    void testExtractWithJsonStringDocuments() throws Exception {
        mockBothVisitors();
        String javascript = "some js code";

        String documentJson = "[\"{\\\"kind\\\":\\\"Document\\\",\\\"definitions\\\":[{\\\"kind\\\":" +
                "\\\"OperationDefinition\\\",\\\"operation\\\":\\\"query\\\",\\\"name\\\":{\\\"kind\\\":" +
                "\\\"Name\\\",\\\"value\\\":\\\"TestQuery\\\"},\\\"variableDefinitions\\\":[]," +
                "\\\"selectionSet\\\":{\\\"kind\\\":\\\"SelectionSet\\\",\\\"selections\\\":[{\\\"kind\\\":" +
                "\\\"Field\\\",\\\"name\\\":{\\\"kind\\\":\\\"Name\\\",\\\"value\\\":\\\"test\\\"}," +
                "\\\"arguments\\\":[]}]}}]}\"]";

        Mockito.when(walker.extractMatchedObjects(javascript, DOCS_VISITOR)).thenReturn(documentJson);
        Mockito.when(walker.extractMatchedObjects(javascript, STRINGS_VISITOR)).thenReturn(null);

        List<Document> documents = gqlDocumentExtractor.extract(javascript);
        Assertions.assertEquals(1, documents.size());
    }
}
