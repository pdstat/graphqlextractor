package com.pdstat.gqlextractor.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GqlRequestTest {

    @Test
    void testSetAndGetOperationName() {
        GqlRequest request = new GqlRequest();
        request.setOperationName("GetUser");
        Assertions.assertEquals("GetUser", request.getOperationName());
    }

    @Test
    void testSetAndGetQuery() {
        GqlRequest request = new GqlRequest();
        String query = "query GetUser { user { id name } }";
        request.setQuery(query);
        Assertions.assertEquals(query, request.getQuery());
    }

    @Test
    void testSetVariable() {
        GqlRequest request = new GqlRequest();
        request.setVariable("id", "123");
        Assertions.assertEquals(1, request.getVariables().size());
        Assertions.assertEquals("123", request.getVariables().get("id"));
    }

    @Test
    void testSetMultipleVariables() {
        GqlRequest request = new GqlRequest();
        request.setVariable("id", "123");
        request.setVariable("name", "test");
        request.setVariable("active", true);
        Assertions.assertEquals(3, request.getVariables().size());
        Assertions.assertEquals("123", request.getVariables().get("id"));
        Assertions.assertEquals("test", request.getVariables().get("name"));
        Assertions.assertEquals(true, request.getVariables().get("active"));
    }

    @Test
    void testVariablesInitiallyEmpty() {
        GqlRequest request = new GqlRequest();
        Assertions.assertNotNull(request.getVariables());
        Assertions.assertTrue(request.getVariables().isEmpty());
    }

    @Test
    void testSetVariableOverwritesPreviousValue() {
        GqlRequest request = new GqlRequest();
        request.setVariable("id", "123");
        request.setVariable("id", "456");
        Assertions.assertEquals(1, request.getVariables().size());
        Assertions.assertEquals("456", request.getVariables().get("id"));
    }

    @Test
    void testOperationNameInitiallyNull() {
        GqlRequest request = new GqlRequest();
        Assertions.assertNull(request.getOperationName());
    }

    @Test
    void testQueryInitiallyNull() {
        GqlRequest request = new GqlRequest();
        Assertions.assertNull(request.getQuery());
    }

    @Test
    void testSetVariableWithNullValue() {
        GqlRequest request = new GqlRequest();
        request.setVariable("filter", null);
        Assertions.assertEquals(1, request.getVariables().size());
        Assertions.assertNull(request.getVariables().get("filter"));
    }

    @Test
    void testSetVariableWithComplexObject() {
        GqlRequest request = new GqlRequest();
        java.util.Map<String, Object> complexInput = new java.util.HashMap<>();
        complexInput.put("field1", "value1");
        complexInput.put("field2", 42);
        request.setVariable("input", complexInput);
        Assertions.assertInstanceOf(java.util.Map.class, request.getVariables().get("input"));
    }
}
