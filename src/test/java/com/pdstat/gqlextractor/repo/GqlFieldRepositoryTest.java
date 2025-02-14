package com.pdstat.gqlextractor.repo;

import graphql.language.Document;
import graphql.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class GqlFieldRepositoryTest {

    @Mock
    private GqlDocumentRepository gqlDocumentRepository;

    @InjectMocks
    private GqlFieldRepository gqlFieldRepository;

    @Test
    void testInitGqlFields() {
        String getSupplyQuery = "query GetSupply($input: SupplyInput) { supply(input: $input) { stations { stationId " +
                "stationName location { lat lng } bikesAvailable bikeDocksAvailable ebikesAvailable scootersAvailable " +
                "totalBikesAvailable totalRideablesAvailable isValet isOffline isLightweight notices { ...NoticeFields }" +
                "siteId ebikes { rideableName batteryStatus { distanceRemaining { value unit } percent } } scooters { " +
                "rideableName batteryStatus { distanceRemaining { value unit } percent } } lastUpdatedMs } rideables { " +
                "rideableId location { lat lng } rideableType photoUrl batteryStatus { distanceRemaining { value unit } " +
                "percent } } notices { ...NoticeFields } requestErrors { ...NoticeFields } } }";
        String initMapQuery = "query InitMap { config { map { mapDataRegionCodes } } serviceAreas { polygons { holes polyline " +
                "} overrides { color type polygons { holes polyline } } type } currentMarket { singleRide { unlockPriceLabel " +
                "minutePriceLabel } } }";
        Document getSupplyDocument = new Parser().parseDocument(getSupplyQuery);
        Document initMapDocument = new Parser().parseDocument(initMapQuery);


        Mockito.when(gqlDocumentRepository.getGqlDocuments()).thenReturn(List.of(getSupplyDocument, initMapDocument));

        gqlFieldRepository.initGqlFields();

        List<String> expectedFields = getExpectedFields();

        List<String> gqlFields = gqlFieldRepository.getGqlFields();
        Assertions.assertFalse(gqlFields.isEmpty());
        Assertions.assertEquals(expectedFields.size(), gqlFields.size());
        Assertions.assertTrue(gqlFields.containsAll(expectedFields));
        Assertions.assertEquals(expectedFields, gqlFields);
    }

    private static List<String> getExpectedFields() {
        String fieldsArray = "batteryStatus,bikeDocksAvailable,bikesAvailable,color,config,currentMarket," +
                "distanceRemaining,ebikes,ebikesAvailable,holes,isLightweight,isOffline,isValet,lastUpdatedMs,lat,lng," +
                "location,map,mapDataRegionCodes,minutePriceLabel,notices,overrides,percent,photoUrl,polygons,polyline," +
                "requestErrors,rideableId,rideableName,rideableType,rideables,scooters,scootersAvailable,serviceAreas," +
                "singleRide,siteId,stationId,stationName,stations,supply,totalBikesAvailable,totalRideablesAvailable," +
                "type,unit,unlockPriceLabel,value";
        String[] fields = fieldsArray.split(",");
        return List.of(fields);
    }
}
