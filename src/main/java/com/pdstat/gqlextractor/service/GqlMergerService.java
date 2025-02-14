package com.pdstat.gqlextractor.service;

import graphql.language.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GqlMergerService {

    /**
     * Merge two GraphQL documents into a single document.
     * @param doc1 The first document
     * @param doc2 The second document
     * @return The merged document
     */
    public Document mergeGraphQLDocuments(Document doc1, Document doc2) {
        List<Definition> mergedDefinitions = new ArrayList<>();

        // Merge operations dynamically
        Map<String, OperationDefinition> mergedOperations = new LinkedHashMap<>();
        Map<String, FragmentDefinition> mergedFragments = new LinkedHashMap<>();

        for (Document doc : Arrays.asList(doc1, doc2)) {
            for (Definition<?> definition : doc.getDefinitions()) {
                if (definition instanceof OperationDefinition opDef) {
                    String operationName = opDef.getName() != null ? opDef.getName() : "Anonymous";
                    mergedOperations.merge(operationName, opDef, this::mergeOperationDefinitions);
                } else if (definition instanceof FragmentDefinition fragDef) {
                    mergedFragments.merge(fragDef.getName(), fragDef, this::mergeFragmentDefinitions);
                } else {
                    mergedDefinitions.add(definition); // Keep any other definitions
                }
            }
        }

        mergedDefinitions.addAll(mergedOperations.values());
        mergedDefinitions.addAll(mergedFragments.values());

        return Document.newDocument().definitions(mergedDefinitions).build();
    }

    private OperationDefinition mergeOperationDefinitions(OperationDefinition op1, OperationDefinition op2) {
        SelectionSet mergedSelectionSet = mergeSelectionSets(op1.getSelectionSet(), op2.getSelectionSet());

        return OperationDefinition.newOperationDefinition()
                .name(op1.getName()) // Use the first operation name
                .operation(op1.getOperation())
                .variableDefinitions(mergeVariableDefinitions(op1.getVariableDefinitions(), op2.getVariableDefinitions()))
                .selectionSet(mergedSelectionSet)
                .build();
    }


    private FragmentDefinition mergeFragmentDefinitions(FragmentDefinition frag1, FragmentDefinition frag2) {
        SelectionSet mergedSelectionSet = mergeSelectionSets(frag1.getSelectionSet(), frag2.getSelectionSet());

        return FragmentDefinition.newFragmentDefinition()
                .name(frag1.getName())
                .typeCondition(frag1.getTypeCondition())
                .selectionSet(mergedSelectionSet)
                .build();
    }

    private SelectionSet mergeSelectionSets(SelectionSet set1, SelectionSet set2) {
        if (set1 == null) return set2;
        if (set2 == null) return set1;

        Map<String, Field> mergedFields = new LinkedHashMap<>();
        Map<String, FragmentSpread> mergedFragments = new LinkedHashMap<>();
        List<Selection<?>> mergedSelections = new ArrayList<>();

        // Process first set
        for (Selection<?> selection : set1.getSelections()) {
            if (selection instanceof Field field) {
                mergedFields.put(field.getName(), field);
            } else if (selection instanceof FragmentSpread fragment) {
                mergedFragments.put(fragment.getName(), fragment);
            } else {
                mergedSelections.add(selection);
            }
        }

        // Process second set
        for (Selection<?> selection : set2.getSelections()) {
            if (selection instanceof Field field) {
                mergedFields.merge(field.getName(), field, this::mergeFields);
            } else if (selection instanceof FragmentSpread fragment) {
                mergedFragments.putIfAbsent(fragment.getName(), fragment); // Ensure uniqueness
            } else if (!mergedSelections.contains(selection)) {
                mergedSelections.add(selection);
            }
        }

        mergedSelections.addAll(mergedFields.values());
        mergedSelections.addAll(mergedFragments.values()); // Add unique fragment spreads

        return SelectionSet.newSelectionSet().selections(mergedSelections).build();
    }

    private Field mergeFields(Field field1, Field field2) {
        SelectionSet mergedSelectionSet = mergeSelectionSets(field1.getSelectionSet(), field2.getSelectionSet());

        return Field.newField()
                .name(field1.getName())
                .alias(field1.getAlias())
                .arguments(field1.getArguments().isEmpty() ? field2.getArguments() : field1.getArguments()) // Fix here
                .selectionSet(mergedSelectionSet)
                .build();
    }


    private List<VariableDefinition> mergeVariableDefinitions(List<VariableDefinition> vars1, List<VariableDefinition> vars2) {
        Map<String, VariableDefinition> mergedVars = new LinkedHashMap<>();

        for (VariableDefinition var : vars1) {
            mergedVars.put(var.getName(), var);
        }

        for (VariableDefinition var : vars2) {
            mergedVars.putIfAbsent(var.getName(), var);
        }

        return new ArrayList<>(mergedVars.values());
    }
}

