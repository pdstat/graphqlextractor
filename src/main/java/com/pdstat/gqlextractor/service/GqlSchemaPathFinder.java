package com.pdstat.gqlextractor.service;

import graphql.language.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GqlSchemaPathFinder {

    private final Map<String, Map<String, String>> fieldTypes = new HashMap<>();
    private final Set<String> visitedPaths = new HashSet<>();

    private void buildGraph(Document document) {
        for (Definition<?> definition : document.getDefinitions()) {
            if (definition instanceof ObjectTypeDefinition typeDef) {
                fieldTypes.putIfAbsent(typeDef.getName(), new HashMap<>());

                for (FieldDefinition field : typeDef.getFieldDefinitions()) {
                    fieldTypes.get(typeDef.getName()).put(field.getName(), extractTypeName(field.getType()));
                }
            }
        }
    }

    private String extractTypeName(Type<?> type) {
        if (type instanceof TypeName) {
            return ((TypeName) type).getName();
        } else if (type instanceof NonNullType) {
            return extractTypeName(((NonNullType) type).getType());
        } else if (type instanceof ListType) {
            return extractTypeName(((ListType) type).getType());
        }
        return null;
    }

    /**
     * Find all paths to a target field in the GraphQL schema
     * @param document GraphQL schema document
     * @param targetField Field name to search for
     * @param maxDepth Maximum depth to search
     * @return List of paths to the target field
     */
    public List<String> findFieldPaths(Document document, String targetField, int maxDepth) {
        buildGraph(document);
        List<String> paths = new ArrayList<>();
        Queue<List<String>> queue = new LinkedList<>();

        List<String> rootTypes = Arrays.asList("Query", "Mutation", "Subscription");
        for (String rootType : rootTypes) {
            if (fieldTypes.containsKey(rootType)) {
                queue.add(Collections.singletonList(rootType));
            }
        }

        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            if (path.size() > maxDepth) continue;

            String currentType = path.get(path.size() - 1);
            if (!fieldTypes.containsKey(currentType)) continue;

            for (Map.Entry<String, String> entry : fieldTypes.get(currentType).entrySet()) {
                String fieldName = entry.getKey();
                String nextType = entry.getValue();

                List<String> newPath = new ArrayList<>(path);
                newPath.add(fieldName);

                // Generate a unique key for path tracking to avoid cycles
                String pathKey = String.join(" -> ", newPath);
                if (visitedPaths.contains(pathKey)) continue;
                visitedPaths.add(pathKey);

                if (fieldName.equals(targetField)) {
                    Collections.reverse(newPath);
                    paths.add(String.join(" -> ", newPath));
                } else if (fieldTypes.containsKey(nextType)) {
                    List<String> nextPath = new ArrayList<>(newPath);
                    nextPath.add(nextType);
                    queue.add(nextPath);
                }
            }
        }
        return paths;
    }
}
