package com.pdstat.gqlextractor.model;

import java.util.HashMap;
import java.util.Map;

public class GqlRequest {

    private String operationName;
    private final Map<String, Object> variables = new HashMap<>();
    private String query;

    public String getQuery() {
        return query;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariable(String key, Object value) {
        variables.put(key, value);
    }
}
