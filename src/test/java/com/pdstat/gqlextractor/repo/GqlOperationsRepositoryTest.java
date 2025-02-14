package com.pdstat.gqlextractor.repo;

import com.pdstat.gqlextractor.service.GqlMergerService;
import graphql.language.Document;
import graphql.language.FragmentDefinition;
import graphql.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class GqlOperationsRepositoryTest {

    @Mock
    private GqlDocumentRepository gqlDocumentRepository;

    @Mock
    private GqlFragmentDefinitionsRepository gqlFragmentDefinitionsRepository;

    @Mock
    private GqlMergerService gqlMergerService;

    @InjectMocks
    private GqlOperationsRepository gqlOperationsRepository;

    @Test
    void testInitGqlOperationsMissingFragment() {
        String getSupplyQuery = "query GetSupply($input: SupplyInput) { supply(input: $input) { stations { stationId " +
                "stationName location { lat lng } bikesAvailable bikeDocksAvailable ebikesAvailable scootersAvailable " +
                "totalBikesAvailable totalRideablesAvailable isValet isOffline isLightweight notices { ...NoticeFields }" +
                "siteId ebikes { rideableName batteryStatus { distanceRemaining { value unit } percent } } scooters { " +
                "rideableName batteryStatus { distanceRemaining { value unit } percent } } lastUpdatedMs } rideables { " +
                "rideableId location { lat lng } rideableType photoUrl batteryStatus { distanceRemaining { value unit } " +
                "percent } } notices { ...NoticeFields } requestErrors { ...NoticeFields } } }";
        String noticeFieldsFragment = "fragment NoticeFields on Notice { localizedTitle localizedDescription url }";
        Document getSupplyDocument = new Parser().parseDocument(getSupplyQuery);
        Document noticeFieldsFragmentDocument = new Parser().parseDocument(noticeFieldsFragment);

        Mockito.when(gqlDocumentRepository.getGqlDocuments()).thenReturn(List.of(getSupplyDocument));
        Mockito.when(gqlFragmentDefinitionsRepository.getGqlFragmentDefinition("NoticeFields"))
                .thenReturn((FragmentDefinition) noticeFieldsFragmentDocument.getDefinitions().get(0));

        gqlOperationsRepository.initGqlOperations();

        Document storedGetSupplyDoc = gqlOperationsRepository.getGqlOperation("GetSupply");
        Assertions.assertNotNull(storedGetSupplyDoc);
        Assertions.assertTrue(storedGetSupplyDoc.getDefinitions().size() > 1);
    }

    @Test
    void testInitGqlOperationsMergeDocuments() {
        String getSupplyQuery1 = "query GetSupply($input: SupplyInput) { supply(input: $input) { stations { stationId " +
                "location { lat lng } bikesAvailable ebikesAvailable scootersAvailable " +
                "totalBikesAvailable totalRideablesAvailable isValet isOffline isLightweight notices { ...NoticeFields }" +
                "siteId ebikes { batteryStatus { distanceRemaining { unit } percent } } scooters { " +
                "batteryStatus { distanceRemaining { value unit } percent } } lastUpdatedMs } rideables { " +
                "location { lat } rideableType batteryStatus { distanceRemaining { value } " +
                "percent } } notices { ...NoticeFields } requestErrors { ...NoticeFields } } } fragment NoticeFields on" +
                " Notice { localizedTitle localizedDescription url }";
        String getSupplyQuery2 = "query GetSupply($input: SupplyInput) { supply(input: $input) { stations { stationId " +
                "stationName location { lat lng } bikesAvailable bikeDocksAvailable ebikesAvailable " +
                "totalBikesAvailable totalRideablesAvailable isValet isOffline isLightweight notices { ...NoticeFields }" +
                "siteId ebikes { rideableName batteryStatus { distanceRemaining { value unit } percent } } scooters { " +
                "rideableName batteryStatus { distanceRemaining { value unit } percent } } lastUpdatedMs } rideables { " +
                "rideableId location { lat lng } rideableType photoUrl batteryStatus { distanceRemaining { value unit } " +
                "percent } } notices { ...NoticeFields } requestErrors { ...NoticeFields } } } fragment NoticeFields on" +
                " Notice { localizedTitle localizedDescription url }";
        Document getSupplyDocument1 = new Parser().parseDocument(getSupplyQuery1);
        Document getSupplyDocument2 = new Parser().parseDocument(getSupplyQuery2);

        Mockito.when(gqlDocumentRepository.getGqlDocuments()).thenReturn(List.of(getSupplyDocument1, getSupplyDocument2));
        Mockito.when(gqlMergerService.mergeGraphQLDocuments(getSupplyDocument1, getSupplyDocument2))
                .thenReturn(getSupplyDocument2);

        gqlOperationsRepository.initGqlOperations();
    }

    @Test
    void testInitGqlOperationsNoMergeDocuments() {
        String getSupplyQuery1 = "query GetSupply($input: SupplyInput) { supply(input: $input) { stations { stationId " +
                "stationName location { lat lng } bikesAvailable bikeDocksAvailable ebikesAvailable " +
                "totalBikesAvailable totalRideablesAvailable isValet isOffline isLightweight notices { ...NoticeFields }" +
                "siteId ebikes { rideableName batteryStatus { distanceRemaining { value unit } percent } } scooters { " +
                "rideableName batteryStatus { distanceRemaining { value unit } percent } } lastUpdatedMs } rideables { " +
                "rideableId location { lat lng } rideableType photoUrl batteryStatus { distanceRemaining { value unit } " +
                "percent } } notices { ...NoticeFields } requestErrors { ...NoticeFields } } } fragment NoticeFields on" +
                " Notice { localizedTitle localizedDescription url }";
        String getSupplyQuery2 = "query GetSupply($input: SupplyInput) { supply(input: $input) { stations { stationId " +
                "stationName location { lat lng } bikesAvailable bikeDocksAvailable ebikesAvailable " +
                "totalBikesAvailable totalRideablesAvailable isValet isOffline isLightweight notices { ...NoticeFields }" +
                "siteId ebikes { rideableName batteryStatus { distanceRemaining { value unit } percent } } scooters { " +
                "rideableName batteryStatus { distanceRemaining { value unit } percent } } lastUpdatedMs } rideables { " +
                "rideableId location { lat lng } rideableType photoUrl batteryStatus { distanceRemaining { value unit } " +
                "percent } } notices { ...NoticeFields } requestErrors { ...NoticeFields } } } fragment NoticeFields on" +
                " Notice { localizedTitle localizedDescription url }";
        Document getSupplyDocument1 = new Parser().parseDocument(getSupplyQuery1);
        Document getSupplyDocument2 = new Parser().parseDocument(getSupplyQuery2);

        Mockito.when(gqlDocumentRepository.getGqlDocuments()).thenReturn(List.of(getSupplyDocument1, getSupplyDocument2));

        gqlOperationsRepository.initGqlOperations();

        Mockito.verify(gqlMergerService, Mockito.never()).mergeGraphQLDocuments(getSupplyDocument1, getSupplyDocument2);
    }

}
