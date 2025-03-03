package com.pdstat.gqlextractor.model;

import com.pdstat.gqlextractor.repo.DefaultParamsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class GqlRequestTest {

    @Mock
    private DefaultParamsRepository defaultParamsRepository;

    @Test
    void testGqlRequestNoParams() {
//        GqlRequest gqlRequest = new GqlRequest("CountriesSupported", "fragment CountryFields on Country {\n" +
//                "  code\n" +
//                "  name\n" +
//                "  mandatoryPostalCode\n" +
//                "  regions {\n" +
//                "    code\n" +
//                "    name\n" +
//                "  }\n" +
//                "}\n" +
//                "query CountriesSupported {\n" +
//                "  bssConfiguration {\n" +
//                "    addressFormMode\n" +
//                "    shippingCountries: supportedCountries(context: \"shipping\") {\n" +
//                "      ...CountryFields\n" +
//                "    }\n" +
//                "    billingCountries: supportedCountries(context: \"billing\") {\n" +
//                "      ...CountryFields\n" +
//                "    }\n" +
//                "  }\n" +
//                "}", defaultParamsRepository);
//        assertEquals("fragment CountryFields on Country {\n  code\n  name\n  mandatoryPostalCode\n  regions {" +
//                "\n    code\n    name\n  }\n}\nquery CountriesSupported {\n  bssConfiguration {\n    addressFormMode\n" +
//                "    shippingCountries: supportedCountries(context: \"shipping\") {\n      ...CountryFields\n    }\n" +
//                "    billingCountries: supportedCountries(context: \"billing\") {\n      ...CountryFields\n    }\n  }\n}",
//                gqlRequest.getQuery());
//        assertEquals("CountriesSupported", gqlRequest.getOperationName());
//        assertEquals(0, gqlRequest.getVariables().size());
    }

    @Test
    void testGqlRequestOneParam() {
//        GqlRequest gqlRequest = new GqlRequest("ConnectBssToLyft", "mutation ConnectBssToLyft($jwt: String!) {\n" +
//                "  connectBssToLyft(jwt: $jwt) {\n" +
//                "    id\n" +
//                "    hasLinkedBssAccount\n" +
//                "    migration {\n" +
//                "      id\n" +
//                "      needsToSeePrompt\n" +
//                "    }\n" +
//                "    termsToAgreeTo {\n" +
//                "      termsId\n" +
//                "    }\n" +
//                "  }\n" +
//                "}", defaultParamsRepository);
//        assertEquals("mutation ConnectBssToLyft($jwt: String!) {\n  connectBssToLyft(jwt: $jwt) {\n    id\n" +
//                        "    hasLinkedBssAccount\n    migration {\n      id\n      needsToSeePrompt\n    }\n" +
//                        "    termsToAgreeTo {\n      termsId\n    }\n  }\n}",
//                gqlRequest.getQuery());
//        assertEquals("ConnectBssToLyft", gqlRequest.getOperationName());
//        assertEquals(1, gqlRequest.getVariables().size());
//        assertTrue(gqlRequest.getVariables().containsKey("jwt"));
//        assertEquals("", gqlRequest.getVariables().get("jwt"));
    }

    @Test
    void testGqlRequestMultipleParams() {
//        GqlRequest gqlRequest = new GqlRequest("BssPurchasePageCpaPayload", "query BssPurchasePageCpaPayload($params: SubscriptionTypesParams, $queryString: String, $autoRenew: Boolean, $memberId: Int) {\n" +
//                "  currentMarket {\n" +
//                "    subscriptionTypes(params: $params, memberId: $memberId) {\n" +
//                "      id\n" +
//                "      quotation(params: $params) {\n" +
//                "        id\n" +
//                "        payloadForCpaShield(\n" +
//                "          purchasePageData: {autoRenew: $autoRenew, queryString: $queryString}\n" +
//                "        )\n" +
//                "      }\n" +
//                "    }\n" +
//                "  }\n" +
//                "}", defaultParamsRepository);
//        assertEquals("query BssPurchasePageCpaPayload($params: SubscriptionTypesParams, $queryString: String, " +
//                        "$autoRenew: Boolean, $memberId: Int) {\n  currentMarket {\n    " +
//                        "subscriptionTypes(params: $params, memberId: $memberId) {\n      id\n      " +
//                        "quotation(params: $params) {\n        id\n        payloadForCpaShield(\n          " +
//                        "purchasePageData: {autoRenew: $autoRenew, queryString: $queryString}\n        )\n      " +
//                        "}\n    }\n  }\n}",
//                gqlRequest.getQuery());
//        assertEquals("BssPurchasePageCpaPayload", gqlRequest.getOperationName());
//        assertEquals(4, gqlRequest.getVariables().size());
//        assertTrue(gqlRequest.getVariables().containsKey("params"));
//        assertTrue(gqlRequest.getVariables().get("params") instanceof Object);
//        assertTrue(gqlRequest.getVariables().containsKey("queryString"));
//        assertEquals("", gqlRequest.getVariables().get("queryString"));
//        assertTrue(gqlRequest.getVariables().containsKey("autoRenew"));
//        assertEquals(false, gqlRequest.getVariables().get("autoRenew"));
//        assertTrue(gqlRequest.getVariables().containsKey("memberId"));
//        assertEquals(0, gqlRequest.getVariables().get("memberId"));
    }

    @Test
    void testGqlRequestParamsNoSpaces() {
//        GqlRequest gqlRequest = new GqlRequest("AccountReonboardingRetrieveQuery", "query AccountReonboardingRetrieveQuery($input:OpenapiGetV1AccountInput" +
//                "$v1Context:V1ContextInput!)@tag(id:\\\"gql\\\")@hash(id:\\\"aad7352242\\\"){account:v1Account(input:" +
//                "$input v1Context:$v1Context)@params(expand:[]includeOnly:[\\\"id\\\" \\\"merchant_reonboarded_from\\\" " +
//                "\\\"merchants_reonboarded_to\\\"]fragments:[]nodeLookupMap:[{nodes:[\\\"id\\\" \\\"merchant_reonboarded_from\\\" " +
//                "\\\"merchants_reonboarded_to\\\"]parentPath:\\\"ROOT\\\"}]predicates:{})@rest(type:\\\"OpenapiAccount\\\" " +
//                "method:\\\"GET\\\" pathBuilder:$pathBuilder runtimePath:\\\"/v1/account\\\" queryName:\\\"account\\\")" +
//                "{id merchant_reonboarded_from merchants_reonboarded_to}}", defaultParamsRepository);
//
//        assertEquals("AccountReonboardingRetrieveQuery", gqlRequest.getOperationName());
//        assertEquals(2, gqlRequest.getVariables().size());
//        assertTrue(gqlRequest.getVariables().containsKey("input"));
//        assertTrue(gqlRequest.getVariables().get("input") instanceof Object);
//        assertTrue(gqlRequest.getVariables().containsKey("v1Context"));
//        assertTrue(gqlRequest.getVariables().get("v1Context") instanceof Object);
    }

}
