package com.pdstat.gqlextractor.service;

import graphql.language.*;
import graphql.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GqlMergerServiceTest {

    private GqlMergerService gqlMergerService;

    @BeforeEach
    void setUp() {
        gqlMergerService = new GqlMergerService();
    }

    @Test
    void testMergeGraphQLDocumentsWithOverlappingFields() {
        String query1 = "query GetSupply($input: SupplyInput) { supply(input: $input) { " +
                "stations { stationId location { lat lng } bikesAvailable } } }";
        String query2 = "query GetSupply($input: SupplyInput) { supply(input: $input) { " +
                "stations { stationId stationName location { lat } bikeDocksAvailable } } }";
        Document doc1 = new Parser().parseDocument(query1);
        Document doc2 = new Parser().parseDocument(query2);

        Document merged = gqlMergerService.mergeGraphQLDocuments(doc1, doc2);

        String mergedAst = AstPrinter.printAst(merged);
        Assertions.assertTrue(mergedAst.contains("stationId"));
        Assertions.assertTrue(mergedAst.contains("stationName"));
        Assertions.assertTrue(mergedAst.contains("bikesAvailable"));
        Assertions.assertTrue(mergedAst.contains("bikeDocksAvailable"));
        Assertions.assertTrue(mergedAst.contains("lat"));
        Assertions.assertTrue(mergedAst.contains("lng"));
    }

    @Test
    void testMergeGraphQLDocumentsWithDisjointFields() {
        String query1 = "query GetData { user { name email } }";
        String query2 = "query GetData { user { age phone } }";
        Document doc1 = new Parser().parseDocument(query1);
        Document doc2 = new Parser().parseDocument(query2);

        Document merged = gqlMergerService.mergeGraphQLDocuments(doc1, doc2);

        String mergedAst = AstPrinter.printAst(merged);
        Assertions.assertTrue(mergedAst.contains("name"));
        Assertions.assertTrue(mergedAst.contains("email"));
        Assertions.assertTrue(mergedAst.contains("age"));
        Assertions.assertTrue(mergedAst.contains("phone"));
    }

    @Test
    void testMergeGraphQLDocumentsWithIdenticalDocuments() {
        String query = "query GetUser { user { id name } }";
        Document doc1 = new Parser().parseDocument(query);
        Document doc2 = new Parser().parseDocument(query);

        Document merged = gqlMergerService.mergeGraphQLDocuments(doc1, doc2);

        String mergedAst = AstPrinter.printAst(merged);
        Assertions.assertTrue(mergedAst.contains("id"));
        Assertions.assertTrue(mergedAst.contains("name"));
    }

    @Test
    void testMergeGraphQLDocumentsWithVariableDefinitions() {
        String query1 = "query GetUser($id: ID!) { user(id: $id) { name } }";
        String query2 = "query GetUser($id: ID!, $limit: Int) { user(id: $id) { name email } }";
        Document doc1 = new Parser().parseDocument(query1);
        Document doc2 = new Parser().parseDocument(query2);

        Document merged = gqlMergerService.mergeGraphQLDocuments(doc1, doc2);

        OperationDefinition op = (OperationDefinition) merged.getDefinitions().get(0);
        Assertions.assertEquals(2, op.getVariableDefinitions().size());
        List<String> varNames = op.getVariableDefinitions().stream()
                .map(VariableDefinition::getName)
                .toList();
        Assertions.assertTrue(varNames.contains("id"));
        Assertions.assertTrue(varNames.contains("limit"));
    }

    @Test
    void testMergeGraphQLDocumentsWithFragments() {
        String query1 = "query GetUser { user { ...UserFields } } fragment UserFields on User { name email }";
        String query2 = "query GetUser { user { ...UserFields } } fragment UserFields on User { name phone }";
        Document doc1 = new Parser().parseDocument(query1);
        Document doc2 = new Parser().parseDocument(query2);

        Document merged = gqlMergerService.mergeGraphQLDocuments(doc1, doc2);

        String mergedAst = AstPrinter.printAst(merged);
        Assertions.assertTrue(mergedAst.contains("name"));
        Assertions.assertTrue(mergedAst.contains("email"));
        Assertions.assertTrue(mergedAst.contains("phone"));
    }

    @Test
    void testMergeGraphQLDocumentsWithNullSelectionSets() {
        String query1 = "query GetUser { user { name } }";
        String query2 = "query GetUser { user { email } }";
        Document doc1 = new Parser().parseDocument(query1);
        Document doc2 = new Parser().parseDocument(query2);

        Document merged = gqlMergerService.mergeGraphQLDocuments(doc1, doc2);

        String mergedAst = AstPrinter.printAst(merged);
        Assertions.assertTrue(mergedAst.contains("name"));
        Assertions.assertTrue(mergedAst.contains("email"));
    }

    @Test
    void testMergeGraphQLDocumentsMutation() {
        String mutation1 = "mutation CreateUser($input: CreateUserInput!) { createUser(input: $input) { id name } }";
        String mutation2 = "mutation CreateUser($input: CreateUserInput!) { createUser(input: $input) { id email } }";
        Document doc1 = new Parser().parseDocument(mutation1);
        Document doc2 = new Parser().parseDocument(mutation2);

        Document merged = gqlMergerService.mergeGraphQLDocuments(doc1, doc2);

        OperationDefinition op = (OperationDefinition) merged.getDefinitions().get(0);
        Assertions.assertEquals(OperationDefinition.Operation.MUTATION, op.getOperation());
        String mergedAst = AstPrinter.printAst(merged);
        Assertions.assertTrue(mergedAst.contains("id"));
        Assertions.assertTrue(mergedAst.contains("name"));
        Assertions.assertTrue(mergedAst.contains("email"));
    }

    @Test
    void testMergeGraphQLDocumentsWithFragmentSpreads() {
        String query1 = "query GetUser { user { ...UserBasic ...UserContact } } " +
                "fragment UserBasic on User { id name } " +
                "fragment UserContact on User { email }";
        String query2 = "query GetUser { user { ...UserBasic ...UserAddress } } " +
                "fragment UserBasic on User { id name } " +
                "fragment UserAddress on User { city }";
        Document doc1 = new Parser().parseDocument(query1);
        Document doc2 = new Parser().parseDocument(query2);

        Document merged = gqlMergerService.mergeGraphQLDocuments(doc1, doc2);

        String mergedAst = AstPrinter.printAst(merged);
        Assertions.assertTrue(mergedAst.contains("UserBasic"));
        Assertions.assertTrue(mergedAst.contains("UserContact"));
        Assertions.assertTrue(mergedAst.contains("UserAddress"));
    }

    @Test
    void testMergeGraphQLDocumentsDeeplyNested() {
        String query1 = "query GetData { level1 { level2 { level3 { fieldA } } } }";
        String query2 = "query GetData { level1 { level2 { level3 { fieldB } } } }";
        Document doc1 = new Parser().parseDocument(query1);
        Document doc2 = new Parser().parseDocument(query2);

        Document merged = gqlMergerService.mergeGraphQLDocuments(doc1, doc2);

        String mergedAst = AstPrinter.printAst(merged);
        Assertions.assertTrue(mergedAst.contains("fieldA"));
        Assertions.assertTrue(mergedAst.contains("fieldB"));
    }

    @Test
    void testMergeGraphQLDocumentsPreservesFieldArguments() {
        String query1 = "query GetUser { user(id: \"123\") { name } }";
        String query2 = "query GetUser { user(id: \"123\") { email } }";
        Document doc1 = new Parser().parseDocument(query1);
        Document doc2 = new Parser().parseDocument(query2);

        Document merged = gqlMergerService.mergeGraphQLDocuments(doc1, doc2);

        String mergedAst = AstPrinter.printAst(merged);
        Assertions.assertTrue(mergedAst.contains("id"));
        Assertions.assertTrue(mergedAst.contains("name"));
        Assertions.assertTrue(mergedAst.contains("email"));
    }

    @Test
    void testMergeGraphQLDocumentsWithInlineFragments() {
        String query1 = "query GetNode { node(id: \"1\") { ... on User { name } } }";
        String query2 = "query GetNode { node(id: \"1\") { ... on Post { title } } }";
        Document doc1 = new Parser().parseDocument(query1);
        Document doc2 = new Parser().parseDocument(query2);

        Document merged = gqlMergerService.mergeGraphQLDocuments(doc1, doc2);

        String mergedAst = AstPrinter.printAst(merged);
        Assertions.assertTrue(mergedAst.contains("User"));
        Assertions.assertTrue(mergedAst.contains("name"));
        Assertions.assertTrue(mergedAst.contains("Post"));
        Assertions.assertTrue(mergedAst.contains("title"));
    }

    @Test
    void testMergeGraphQLDocumentsFullQuery() {
        String fullQuery = "query GetSupply($input: SupplyInput!) { supply(input: $input) { stations { stationId stationName" +
                " location { lat lng } bikesAvailable bikeDocksAvailable ebikesAvailable scootersAvailable totalBikesAvailable" +
                " totalRideablesAvailable isValet isOffline isLightweight notices { ...NoticeFields } siteId ebikes { rideableName" +
                " batteryStatus { distanceRemaining { value unit } percent } } scooters { rideableName batteryStatus { " +
                "distanceRemaining { value unit } percent } } lastUpdatedMs } rideables { rideableId location { lat lng } " +
                "rideableType photoUrl batteryStatus { distanceRemaining { value unit } percent } } notices { ...NoticeFields " +
                "} requestErrors { ...NoticeFields } } }";
        Document fullDocument = new Parser().parseDocument(fullQuery);

        String query1 = "query GetSupply($input: SupplyInput) { supply(input: $input) { stations { stationId stationName" +
                " location { lat lng } bikesAvailable ebikesAvailable scootersAvailable totalBikesAvailable" +
                " totalRideablesAvailable isValet isOffline isLightweight notices { ...NoticeFields } siteId ebikes { rideableName" +
                " batteryStatus { distanceRemaining { value unit } percent } } scooters { rideableName batteryStatus { " +
                "distanceRemaining { value unit } percent } } lastUpdatedMs } rideables { rideableId location { lat lng } " +
                "rideableType photoUrl batteryStatus { distanceRemaining { value unit } percent } } notices { ...NoticeFields " +
                "} requestErrors { ...NoticeFields } } }";
        Document document1 = new Parser().parseDocument(query1);

        String query2 = "query GetSupply($input: SupplyInput) { supply(input: $input) { stations { stationId stationName" +
                " location { lat lng } bikeDocksAvailable ebikesAvailable scootersAvailable " +
                " totalRideablesAvailable isValet isOffline isLightweight notices { ...NoticeFields } siteId ebikes { rideableName" +
                " batteryStatus { percent } } scooters { rideableName batteryStatus { " +
                "distanceRemaining { value unit } percent } } lastUpdatedMs } rideables { rideableId location { lng } " +
                "rideableType photoUrl batteryStatus { distanceRemaining { value unit } percent } } notices { ...NoticeFields " +
                "} } }";
        Document document2 = new Parser().parseDocument(query2);

        Document mergedDocument = gqlMergerService.mergeGraphQLDocuments(document1, document2);

        String mergedAst = AstPrinter.printAst(mergedDocument);
        String fullAst = AstPrinter.printAst(fullDocument);
        // The merged document should contain all fields from both queries
        Assertions.assertTrue(mergedAst.contains("bikesAvailable"));
        Assertions.assertTrue(mergedAst.contains("bikeDocksAvailable"));
        Assertions.assertTrue(mergedAst.contains("totalBikesAvailable"));
        Assertions.assertTrue(mergedAst.contains("requestErrors"));
    }
}
