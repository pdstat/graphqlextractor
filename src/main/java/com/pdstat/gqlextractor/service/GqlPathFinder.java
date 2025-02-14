package com.pdstat.gqlextractor.service;

import java.util.*;
import graphql.language.*;
import graphql.parser.*;
import org.springframework.stereotype.Component;

@Component
public class GqlPathFinder {


    private Map<String, Object> graphqlToMap(String graphqlString) {
        Document document = new Parser().parseDocument(graphqlString);
        return graphqlToMap(document);
    }

    private Map<String, Object> graphqlToMap(Document document) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Definition<?> definition : document.getDefinitions()) {
            if (definition instanceof FragmentDefinition fragmentDefinition) {
                result.put(fragmentDefinition.getName(), nodeToMap(fragmentDefinition.getSelectionSet()));
            } else if (definition instanceof OperationDefinition operationDefinition) {
                result.put(operationDefinition.getName(), nodeToMap(operationDefinition.getSelectionSet()));
            }
        }

        return result;
    }

    private Map<String, Object> nodeToMap(SelectionSet selectionSet) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (selectionSet != null) {
            for (Selection<?> selection : selectionSet.getSelections()) {
                if (selection instanceof Field field) {
                    map.put(field.getName(), nodeToMap(field.getSelectionSet()));
                } else if (selection instanceof FragmentSpread fragmentSpread) {
                    map.put(fragmentSpread.getName(), null);
                } else if (selection instanceof InlineFragment inlineFragment) {
                    map.put(inlineFragment.getTypeCondition().getName(), nodeToMap(inlineFragment.getSelectionSet()));
                }
            }
        }
        return map;
    }

    // Recursively find paths to the target field.
    private List<String> findFieldPaths(Map<String, Object> definition, String targetField) {
        return findFieldPaths(definition, targetField, new ArrayList<>(), new ArrayList<>());
    }

    private List<String> findFieldPaths(Map<String, Object> definition, String targetField, List<String> currentPath, List<String> paths) {
        for (Map.Entry<String, Object> entry : definition.entrySet()) {
            List<String> newPath = new ArrayList<>(currentPath);
            newPath.add(entry.getKey());

            if (entry.getKey().equals(targetField)) {
                Collections.reverse(newPath);
                paths.add(String.join(" → ", newPath));
            }

            if (entry.getValue() instanceof Map) {
                findFieldPaths((Map<String, Object>) entry.getValue(), targetField, newPath, paths);
            }
        }
        return paths;
    }

    public List<String> findFieldPaths(String graphqlString, String targetField) {
        try {
            Map<String, Object> gqlStringMap = graphqlToMap(graphqlString);
            return findFieldPaths(gqlStringMap, targetField);
        } catch (InvalidSyntaxException ignored) {
            return new ArrayList<>();
        }
    }

    public List<String> findFieldPaths(Document graphqlDocument, String targetField) {
        try {
            Map<String, Object> gqlDocumentMap = graphqlToMap(graphqlDocument);
            return findFieldPaths(gqlDocumentMap, targetField);
        } catch (InvalidSyntaxException ignored) {
            return new ArrayList<>();
        }
    }

}
