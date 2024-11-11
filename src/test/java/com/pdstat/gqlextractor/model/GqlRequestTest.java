package com.pdstat.gqlextractor.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GqlRequestTest {

    @Test
    void testGqlRequestNoParams() {
        GqlRequest gqlRequest = new GqlRequest("CountriesSupported", "fragment CountryFields on Country {\n" +
                "  code\n" +
                "  name\n" +
                "  mandatoryPostalCode\n" +
                "  regions {\n" +
                "    code\n" +
                "    name\n" +
                "  }\n" +
                "}\n" +
                "query CountriesSupported {\n" +
                "  bssConfiguration {\n" +
                "    addressFormMode\n" +
                "    shippingCountries: supportedCountries(context: \"shipping\") {\n" +
                "      ...CountryFields\n" +
                "    }\n" +
                "    billingCountries: supportedCountries(context: \"billing\") {\n" +
                "      ...CountryFields\n" +
                "    }\n" +
                "  }\n" +
                "}");
        assertEquals("fragment CountryFields on Country {\n  code\n  name\n  mandatoryPostalCode\n  regions {" +
                "\n    code\n    name\n  }\n}\nquery CountriesSupported {\n  bssConfiguration {\n    addressFormMode\n" +
                "    shippingCountries: supportedCountries(context: \"shipping\") {\n      ...CountryFields\n    }\n" +
                "    billingCountries: supportedCountries(context: \"billing\") {\n      ...CountryFields\n    }\n  }\n}",
                gqlRequest.getQuery());
        assertEquals("CountriesSupported", gqlRequest.getOperationName());
        assertEquals(0, gqlRequest.getVariables().size());
    }

    @Test
    void testGqlRequestOneParam() {
        GqlRequest gqlRequest = new GqlRequest("ConnectBssToLyft", "mutation ConnectBssToLyft($jwt: String!) {\n" +
                "  connectBssToLyft(jwt: $jwt) {\n" +
                "    id\n" +
                "    hasLinkedBssAccount\n" +
                "    migration {\n" +
                "      id\n" +
                "      needsToSeePrompt\n" +
                "    }\n" +
                "    termsToAgreeTo {\n" +
                "      termsId\n" +
                "    }\n" +
                "  }\n" +
                "}");
        assertEquals("mutation ConnectBssToLyft($jwt: String!) {\n  connectBssToLyft(jwt: $jwt) {\n    id\n" +
                        "    hasLinkedBssAccount\n    migration {\n      id\n      needsToSeePrompt\n    }\n" +
                        "    termsToAgreeTo {\n      termsId\n    }\n  }\n}",
                gqlRequest.getQuery());
        assertEquals("ConnectBssToLyft", gqlRequest.getOperationName());
        assertEquals(1, gqlRequest.getVariables().size());
        assertTrue(gqlRequest.getVariables().containsKey("jwt"));
        assertEquals("", gqlRequest.getVariables().get("jwt"));
    }

    @Test
    void testGqlRequestMultipleParams() {
        GqlRequest gqlRequest = new GqlRequest("BssPurchasePageCpaPayload", "query BssPurchasePageCpaPayload($params: SubscriptionTypesParams, $queryString: String, $autoRenew: Boolean, $memberId: Int) {\n" +
                "  currentMarket {\n" +
                "    subscriptionTypes(params: $params, memberId: $memberId) {\n" +
                "      id\n" +
                "      quotation(params: $params) {\n" +
                "        id\n" +
                "        payloadForCpaShield(\n" +
                "          purchasePageData: {autoRenew: $autoRenew, queryString: $queryString}\n" +
                "        )\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}");
        assertEquals("query BssPurchasePageCpaPayload($params: SubscriptionTypesParams, $queryString: String, " +
                        "$autoRenew: Boolean, $memberId: Int) {\n  currentMarket {\n    " +
                        "subscriptionTypes(params: $params, memberId: $memberId) {\n      id\n      " +
                        "quotation(params: $params) {\n        id\n        payloadForCpaShield(\n          " +
                        "purchasePageData: {autoRenew: $autoRenew, queryString: $queryString}\n        )\n      " +
                        "}\n    }\n  }\n}",
                gqlRequest.getQuery());
        assertEquals("BssPurchasePageCpaPayload", gqlRequest.getOperationName());
        assertEquals(4, gqlRequest.getVariables().size());
        assertTrue(gqlRequest.getVariables().containsKey("params"));
        assertTrue(gqlRequest.getVariables().get("params") instanceof Object);
        assertTrue(gqlRequest.getVariables().containsKey("queryString"));
        assertEquals("", gqlRequest.getVariables().get("queryString"));
        assertTrue(gqlRequest.getVariables().containsKey("autoRenew"));
        assertEquals(false, gqlRequest.getVariables().get("autoRenew"));
        assertTrue(gqlRequest.getVariables().containsKey("memberId"));
        assertEquals(0, gqlRequest.getVariables().get("memberId"));
    }

}
