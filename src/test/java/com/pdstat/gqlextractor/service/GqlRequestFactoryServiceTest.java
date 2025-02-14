package com.pdstat.gqlextractor.service;

import com.pdstat.gqlextractor.model.GqlRequest;
import com.pdstat.gqlextractor.repo.DefaultParamsRepository;
import graphql.language.Document;
import graphql.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class GqlRequestFactoryServiceTest {

    @Mock
    private DefaultParamsRepository defaultParamsRepository;

    @InjectMocks
    private GqlRequestFactoryService gqlRequestFactoryService;

    @Test
    void testCreateGqlRequestIntegerVariable() {
        // Create a GQL query string with an integer variable
        String gqlQuery = "query getPerson($id:Int!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals(gqlQuery, gqlRequest.getQuery());
        Assertions.assertEquals("getPerson", gqlRequest.getOperationName());
        Assertions.assertTrue(gqlRequest.getVariables().containsKey("id"));
        Assertions.assertEquals(0, gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestIntegerListVariable() {
        // Create a GQL query string with an integer variable
        String gqlQuery = "query getPerson($id:[Int]!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals(gqlQuery, gqlRequest.getQuery());
        Assertions.assertEquals("getPerson", gqlRequest.getOperationName());
        Assertions.assertTrue(gqlRequest.getVariables().containsKey("id"));
        Assertions.assertEquals(List.of(0), gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestFloatVariable() {
        // Create a GQL query string with an integer variable
        String gqlQuery = "query getPerson($id:Float!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals(gqlQuery, gqlRequest.getQuery());
        Assertions.assertEquals("getPerson", gqlRequest.getOperationName());
        Assertions.assertTrue(gqlRequest.getVariables().containsKey("id"));
        Assertions.assertEquals(0.0F, gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestFloatListVariable() {
        // Create a GQL query string with an integer variable
        String gqlQuery = "query getPerson($id:[Float]!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals(gqlQuery, gqlRequest.getQuery());
        Assertions.assertEquals("getPerson", gqlRequest.getOperationName());
        Assertions.assertTrue(gqlRequest.getVariables().containsKey("id"));
        Assertions.assertEquals(List.of(0.0F), gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestLongVariable() {
        // Create a GQL query string with an integer variable
        String gqlQuery = "query getPerson($id:Long!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals(gqlQuery, gqlRequest.getQuery());
        Assertions.assertEquals("getPerson", gqlRequest.getOperationName());
        Assertions.assertTrue(gqlRequest.getVariables().containsKey("id"));
        Assertions.assertEquals(0L, gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestLongListVariable() {
        // Create a GQL query string with an integer variable
        String gqlQuery = "query getPerson($id:[Long]!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals(gqlQuery, gqlRequest.getQuery());
        Assertions.assertEquals("getPerson", gqlRequest.getOperationName());
        Assertions.assertTrue(gqlRequest.getVariables().containsKey("id"));
        Assertions.assertEquals(List.of(0L), gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestBooleanVariable() {
        // Create a GQL query string with an integer variable
        String gqlQuery = "query getPerson($id:Boolean!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals(gqlQuery, gqlRequest.getQuery());
        Assertions.assertEquals("getPerson", gqlRequest.getOperationName());
        Assertions.assertTrue(gqlRequest.getVariables().containsKey("id"));
        Assertions.assertEquals(false, gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestBooleanListVariable() {
        // Create a GQL query string with an integer variable
        String gqlQuery = "query getPerson($id:[Boolean]!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals(gqlQuery, gqlRequest.getQuery());
        Assertions.assertEquals("getPerson", gqlRequest.getOperationName());
        Assertions.assertTrue(gqlRequest.getVariables().containsKey("id"));
        Assertions.assertEquals(List.of(false), gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestStringVariable() {
        // Create a GQL query string with an integer variable
        String gqlQuery = "query getPerson($id:String!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals(gqlQuery, gqlRequest.getQuery());
        Assertions.assertEquals("getPerson", gqlRequest.getOperationName());
        Assertions.assertTrue(gqlRequest.getVariables().containsKey("id"));
        Assertions.assertEquals("", gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestStringListVariable() {
        // Create a GQL query string with an integer variable
        String gqlQuery = "query getPerson($id:[String]!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals(gqlQuery, gqlRequest.getQuery());
        Assertions.assertEquals("getPerson", gqlRequest.getOperationName());
        Assertions.assertTrue(gqlRequest.getVariables().containsKey("id"));
        Assertions.assertEquals(List.of(""), gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestObjectVariable() {
        // Create a GQL query string with an integer variable
        String gqlQuery = "query getPerson($id:SomeObject!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals(gqlQuery, gqlRequest.getQuery());
        Assertions.assertEquals("getPerson", gqlRequest.getOperationName());
        Assertions.assertTrue(gqlRequest.getVariables().containsKey("id"));
        Assertions.assertInstanceOf(Map.class, gqlRequest.getVariables().get("id"));
    }
}
