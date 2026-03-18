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
        String gqlQuery = "query getPerson($id:Float!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals(0.0F, gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestFloatListVariable() {
        String gqlQuery = "query getPerson($id:[Float]!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals(List.of(0.0F), gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestLongVariable() {
        String gqlQuery = "query getPerson($id:Long!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals(0L, gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestLongListVariable() {
        String gqlQuery = "query getPerson($id:[Long]!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals(List.of(0L), gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestBooleanVariable() {
        String gqlQuery = "query getPerson($id:Boolean!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals(false, gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestBooleanListVariable() {
        String gqlQuery = "query getPerson($id:[Boolean]!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals(List.of(false), gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestStringVariable() {
        String gqlQuery = "query getPerson($id:String!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals("", gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestStringListVariable() {
        String gqlQuery = "query getPerson($id:[String]!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals(List.of(""), gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestObjectVariable() {
        String gqlQuery = "query getPerson($id:SomeObject!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertInstanceOf(Map.class, gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestIDVariable() {
        String gqlQuery = "query getPerson($id:ID!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals("", gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestWithDefaultParam() {
        String gqlQuery = "query getPerson($id:String!){person(id:$id){name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn("custom-value");

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals("custom-value", gqlRequest.getVariables().get("id"));
    }

    @Test
    void testCreateGqlRequestNoVariables() {
        String gqlQuery = "query getUsers{users{id name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals("getUsers", gqlRequest.getOperationName());
        Assertions.assertTrue(gqlRequest.getVariables().isEmpty());
    }

    @Test
    void testCreateGqlRequestMultipleVariables() {
        String gqlQuery = "query getPerson($id:ID!,$name:String,$active:Boolean){person(id:$id,name:$name,active:$active){id}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("id")).thenReturn(null);
        Mockito.when(defaultParamsRepository.getDefaultParam("name")).thenReturn(null);
        Mockito.when(defaultParamsRepository.getDefaultParam("active")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals(3, gqlRequest.getVariables().size());
        Assertions.assertEquals("", gqlRequest.getVariables().get("id"));
        Assertions.assertEquals("", gqlRequest.getVariables().get("name"));
        Assertions.assertEquals(false, gqlRequest.getVariables().get("active"));
    }

    @Test
    void testCreateGqlRequestObjectListVariable() {
        String gqlQuery = "query getUsers($filters:[FilterInput]!){users(filters:$filters){id}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("filters")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Object filters = gqlRequest.getVariables().get("filters");
        Assertions.assertInstanceOf(List.class, filters);
    }

    @Test
    void testCreateGqlRequestMutation() {
        String gqlQuery = "mutation createUser($input:CreateUserInput!){createUser(input:$input){id name}}";
        Document document = new Parser().parseDocument(gqlQuery);

        Mockito.when(defaultParamsRepository.getDefaultParam("input")).thenReturn(null);

        GqlRequest gqlRequest = gqlRequestFactoryService.createGqlRequest(document);
        Assertions.assertEquals("createUser", gqlRequest.getOperationName());
        Assertions.assertInstanceOf(Map.class, gqlRequest.getVariables().get("input"));
    }
}
