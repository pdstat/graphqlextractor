package com.pdstat.gqlextractor.model;

import com.pdstat.gqlextractor.Constants;
import com.pdstat.gqlextractor.repo.DefaultParamsRepository;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GqlRequest {

    private String operationName;
    private final Map<String, Object> variables = new HashMap<>();
    private String query;

    private final DefaultParamsRepository defaultParamsRepository;

    public GqlRequest(String operationName, String gqlString, DefaultParamsRepository defaultParamsRepository) {
        this.defaultParamsRepository = defaultParamsRepository;
        initRequest(operationName, gqlString);
    }

    private void initRequest(String operationName, String gqlString) {
        this.operationName = operationName;

        String[] lines = gqlString.split("\n");
        for (String line : lines) {
            if ((line.contains(Constants.Gql.QUERY) || line.contains(Constants.Gql.MUTATION) ||
                    line.contains(Constants.Gql.SUBSCRIPTION)) && line.contains("(")) {
                String operationArgs = gqlString.substring(gqlString.indexOf("(") + 1, gqlString.indexOf(")"));
                String[] args = operationArgs.split("\\$");
                for (String arg : args) {
                    arg = arg.strip();
                    if (!StringUtils.isEmpty(arg)) {
                        String[] argParts = arg.split(":");
                        String argName = argParts[0].strip().replaceAll("\\$", "");
                        String argType = argParts[1].strip().replaceAll(",", "");
                        if (argType.contains("[")) {
                            argType = argType.substring(argType.indexOf("[") + 1, argType.indexOf("]"));
                            setVariable(argName, argType, true);
                        } else {
                            setVariable(argName, argType, false);
                        }
                    }
                }
            }
        }

        gqlString = gqlString.replaceAll("\n", "\\\n");
        this.query = gqlString;
    }

    private void setVariable(String argName, String argType, boolean isList) {
        Object defaultValue = defaultParamsRepository.getDefaultParam(argName);
        if (defaultValue == null) {
            argType = getArgType(argType);
            defaultValue = getDefaultValue(argType, isList);
        }
        variables.put(argName, defaultValue);
    }

    private Object getDefaultValue(String argType, boolean isList) {
        Object defaultValue;
        switch (argType) {
            case "Int":
                defaultValue = isList ? Arrays.asList(0) : 0;
                break;
            case "Float":
                defaultValue = isList ? Arrays.asList(0.0) : 0.0;
                break;
            case "Long":
                defaultValue = isList ? Arrays.asList(0L) : 0L;
                break;
            case "Boolean":
                defaultValue = isList ? Arrays.asList(false) : false;
                break;
            case "String":
                defaultValue = isList ? Arrays.asList("") : "";
                break;
            default:
                defaultValue = isList ? Arrays.asList(new HashMap<String, Object>()) : new HashMap<String, Object>();
                break;
        }
        return defaultValue;
    }
    private String getArgType(String argType) {
        if (argType.contains("!")) {
            argType = argType.substring(0, argType.indexOf("!"));
        }
        return argType;
    }

    public String getQuery() {
        return query;
    }

    public String getOperationName() {
        return operationName;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }
}
