package com.pdstat.gqlextractor.service;


import graphql.language.AstPrinter;
import graphql.language.Document;
import graphql.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GqlMergerServiceTest {

//    @Test
//    void testMergeGraphQLDocuments() {
//        String fullQuery = "query GetSupply($input: SupplyInput!) { supply(input: $input) { stations { stationId stationName" +
//                " location { lat lng } bikesAvailable bikeDocksAvailable ebikesAvailable scootersAvailable totalBikesAvailable" +
//                " totalRideablesAvailable isValet isOffline isLightweight notices { ...NoticeFields } siteId ebikes { rideableName" +
//                " batteryStatus { distanceRemaining { value unit } percent } } scooters { rideableName batteryStatus { " +
//                "distanceRemaining { value unit } percent } } lastUpdatedMs } rideables { rideableId location { lat lng } " +
//                "rideableType photoUrl batteryStatus { distanceRemaining { value unit } percent } } notices { ...NoticeFields " +
//                "} requestErrors { ...NoticeFields } } }";
//        Document fullDocument = new Parser().parseDocument(fullQuery);
//
//        String query1 = "query GetSupply($input: SupplyInput) { supply(input: $input) { stations { stationId stationName" +
//                " location { lat lng } bikesAvailable ebikesAvailable scootersAvailable totalBikesAvailable" +
//                " totalRideablesAvailable isValet isOffline isLightweight notices { ...NoticeFields } siteId ebikes { rideableName" +
//                " batteryStatus { distanceRemaining { value unit } percent } } scooters { rideableName batteryStatus { " +
//                "distanceRemaining { value unit } percent } } lastUpdatedMs } rideables { rideableId location { lat lng } " +
//                "rideableType photoUrl batteryStatus { distanceRemaining { value unit } percent } } notices { ...NoticeFields " +
//                "} requestErrors { ...NoticeFields } } }";
//        Document document1 = new Parser().parseDocument(query1);
//
//        String query2 = "query GetSupply($input: SupplyInput) { supply(input: $input) { stations { stationId stationName" +
//                " location { lat lng } bikeDocksAvailable ebikesAvailable scootersAvailable " +
//                " totalRideablesAvailable isValet isOffline isLightweight notices { ...NoticeFields } siteId ebikes { rideableName" +
//                " batteryStatus { percent } } scooters { rideableName batteryStatus { " +
//                "distanceRemaining { value unit } percent } } lastUpdatedMs } rideables { rideableId location { lng } " +
//                "rideableType photoUrl batteryStatus { distanceRemaining { value unit } percent } } notices { ...NoticeFields " +
//                "} } }";
//        Document document2 = new Parser().parseDocument(query2);
//
//        GqlMergerService gqlMergerService = new GqlMergerService();
//        Document mergedDocument = gqlMergerService.mergeGraphQLDocuments(document1, document2);
//
//        Assertions.assertEquals(AstPrinter.printAst(fullDocument).length(), AstPrinter.printAst(mergedDocument).length());
//
//    }
}
