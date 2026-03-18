package com.pdstat.gqlextractor.service;

import graphql.language.Document;
import graphql.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GqlPathFinderTest {

    private GqlPathFinder gqlPathFinder;

    @BeforeEach
    void setUp() {
        gqlPathFinder = new GqlPathFinder();
    }

    @Test
    void testFindFieldPaths() {
        String getSupplyQuery = "query GetSupply($input: SupplyInput) { supply(input: $input) { stations { stationId " +
                "stationName location { lat lng } bikesAvailable bikeDocksAvailable ebikesAvailable scootersAvailable " +
                "totalBikesAvailable totalRideablesAvailable isValet isOffline isLightweight notices { ...NoticeFields }" +
                "siteId ebikes { rideableName batteryStatus { distanceRemaining { value unit } percent } } scooters { " +
                "rideableName batteryStatus { distanceRemaining { value unit } percent } } lastUpdatedMs } rideables { " +
                "rideableId location { lat lng } rideableType photoUrl batteryStatus { distanceRemaining { value unit } " +
                "percent } } notices { ...NoticeFields } requestErrors { ...NoticeFields } } } fragment NoticeFields on " +
                "Notice { localizedTitle localizedDescription url }";
        Document getSupplyDocument = new Parser().parseDocument(getSupplyQuery);

        List<String> fieldPaths = gqlPathFinder.findFieldPaths(getSupplyDocument, "distanceRemaining");
        Assertions.assertNotNull(fieldPaths);
        Assertions.assertFalse(fieldPaths.isEmpty());
        Assertions.assertEquals(3, fieldPaths.size());
        Assertions.assertTrue(fieldPaths.contains("distanceRemaining -> batteryStatus -> ebikes -> stations -> supply -> GetSupply"));
        Assertions.assertTrue(fieldPaths.contains("distanceRemaining -> batteryStatus -> scooters -> stations -> supply -> GetSupply"));
        Assertions.assertTrue(fieldPaths.contains("distanceRemaining -> batteryStatus -> rideables -> supply -> GetSupply"));
    }

    @Test
    void testFindFieldPathsFieldNotFound() {
        String query = "query GetUser { user { id name email } }";
        Document document = new Parser().parseDocument(query);

        List<String> paths = gqlPathFinder.findFieldPaths(document, "nonExistentField");
        Assertions.assertNotNull(paths);
        Assertions.assertTrue(paths.isEmpty());
    }

    @Test
    void testFindFieldPathsTopLevelField() {
        String query = "query GetUser { user { id name } }";
        Document document = new Parser().parseDocument(query);

        List<String> paths = gqlPathFinder.findFieldPaths(document, "user");
        Assertions.assertNotNull(paths);
        Assertions.assertEquals(1, paths.size());
        Assertions.assertEquals("user -> GetUser", paths.get(0));
    }

    @Test
    void testFindFieldPathsLeafField() {
        String query = "query GetUser { user { id name } }";
        Document document = new Parser().parseDocument(query);

        List<String> paths = gqlPathFinder.findFieldPaths(document, "name");
        Assertions.assertEquals(1, paths.size());
        Assertions.assertEquals("name -> user -> GetUser", paths.get(0));
    }

    @Test
    void testFindFieldPathsMultipleOperations() {
        String query = "query GetUser { user { id name } } query GetPost { post { id title } }";
        Document document = new Parser().parseDocument(query);

        List<String> paths = gqlPathFinder.findFieldPaths(document, "id");
        Assertions.assertEquals(2, paths.size());
        Assertions.assertTrue(paths.contains("id -> user -> GetUser"));
        Assertions.assertTrue(paths.contains("id -> post -> GetPost"));
    }

    @Test
    void testFindFieldPathsWithFragmentDefinition() {
        String query = "query GetUser { user { ...UserFields } } fragment UserFields on User { id name email }";
        Document document = new Parser().parseDocument(query);

        List<String> paths = gqlPathFinder.findFieldPaths(document, "name");
        Assertions.assertFalse(paths.isEmpty());
        // Should find path within the fragment definition
        Assertions.assertTrue(paths.stream().anyMatch(p -> p.contains("UserFields")));
    }

    @Test
    void testFindFieldPathsWithInlineFragment() {
        String query = "query GetNode { node { ... on User { name } ... on Post { title } } }";
        Document document = new Parser().parseDocument(query);

        List<String> paths = gqlPathFinder.findFieldPaths(document, "name");
        Assertions.assertFalse(paths.isEmpty());
    }

    @Test
    void testFindFieldPathsDeeplyNested() {
        String query = "query GetData { a { b { c { d { target } } } } }";
        Document document = new Parser().parseDocument(query);

        List<String> paths = gqlPathFinder.findFieldPaths(document, "target");
        Assertions.assertEquals(1, paths.size());
        Assertions.assertEquals("target -> d -> c -> b -> a -> GetData", paths.get(0));
    }

    @Test
    void testFindFieldPathsEmptyDocument() {
        String query = "query EmptyQuery { __typename }";
        Document document = new Parser().parseDocument(query);

        List<String> paths = gqlPathFinder.findFieldPaths(document, "nonExistent");
        Assertions.assertTrue(paths.isEmpty());
    }
}
