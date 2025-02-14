package com.pdstat.gqlextractor.service;

import com.pdstat.gqlextractor.Constants;
import com.pdstat.gqlextractor.model.GqlRequest;
import com.pdstat.gqlextractor.repo.DefaultParamsRepository;
import graphql.language.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class GqlRequestFactoryService {

    private final DefaultParamsRepository defaultParamsRepository;

    public GqlRequestFactoryService(DefaultParamsRepository defaultParamsRepository) {
        this.defaultParamsRepository = defaultParamsRepository;
    }

    public GqlRequest createGqlRequest(Document document) {
        GqlRequest gqlRequest = new GqlRequest();
        gqlRequest.setQuery(AstPrinter.printAstCompact(document));
        setVariables(document, gqlRequest);
        return gqlRequest;
    }

    private void setVariables(Document document, GqlRequest gqlRequest) {
        document.getDefinitions().forEach(definition -> {
            if (definition instanceof graphql.language.OperationDefinition) {
                gqlRequest.setOperationName(((graphql.language.OperationDefinition) definition).getName());
                ((graphql.language.OperationDefinition) definition).getVariableDefinitions().forEach(variableDefinition -> {
                    String argName = variableDefinition.getName();
                    Type<?> argType = variableDefinition.getType();
                    Object defaultValue = defaultParamsRepository.getDefaultParam(argName);
                    if (defaultValue == null) {
                        defaultValue = getDefaultValue(argType);
                    }
                    gqlRequest.setVariable(argName, defaultValue);
                });
            }
        });

    }

    private Object getDefaultValue(Type<?> type) {
        boolean isList = false;
        String typeName = null;

        // Unwrap NonNullType if present
        while (type instanceof NonNullType) {
            type = ((NonNullType) type).getType();
        }

        // Check if it's a ListType and unwrap it
        if (type instanceof ListType) {
            isList = true;
            type = ((ListType) type).getType();

            // Unwrap NonNullType inside the list if present
            while (type instanceof NonNullType) {
                type = ((NonNullType) type).getType();
            }
        }

        if (type instanceof TypeName) {
            typeName = ((TypeName) type).getName();
            return switch (typeName) {
                case Constants.Gql.Scalar.INT -> isList ? List.of(0) : 0;
                case Constants.Gql.Scalar.FLOAT -> isList ? List.of(0.0F) : 0.0F;
                case Constants.Gql.Scalar.LONG -> isList ? List.of(0L) : 0L;
                case Constants.Gql.Scalar.BOOLEAN -> isList ? List.of(false) : false;
                case Constants.Gql.Scalar.STRING, Constants.Gql.Scalar.ID -> isList ? List.of("") : "";
                default -> isList ? List.of(new HashMap<String, Object>()) : new HashMap<String, Object>();
            };
        }

        throw new IllegalArgumentException("Unexpected GraphQL Type structure");
    }
}
