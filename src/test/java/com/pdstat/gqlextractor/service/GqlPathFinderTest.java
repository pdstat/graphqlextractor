package com.pdstat.gqlextractor.service;

import graphql.language.Document;
import graphql.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GqlPathFinderTest {

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

        GqlPathFinder gqlPathFinder = new GqlPathFinder();
        List<String> fieldPaths = gqlPathFinder.findFieldPaths(getSupplyDocument, "distanceRemaining");
        Assertions.assertNotNull(fieldPaths);
        Assertions.assertFalse(fieldPaths.isEmpty());
        Assertions.assertEquals(3, fieldPaths.size());
        Assertions.assertTrue(fieldPaths.contains("distanceRemaining → batteryStatus → ebikes → stations → supply → GetSupply"));
        Assertions.assertTrue(fieldPaths.contains("distanceRemaining → batteryStatus → scooters → stations → supply → GetSupply"));
        Assertions.assertTrue(fieldPaths.contains("distanceRemaining → batteryStatus → rideables → supply → GetSupply"));
    }

}
