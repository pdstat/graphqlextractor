package com.pdstat.gqlextractor.repo;

import graphql.language.Definition;
import graphql.language.Field;
import graphql.language.SelectionSet;
import graphql.language.SelectionSetContainer;
import graphql.language.Document;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

@Repository
public class GqlFieldRepository {

    private final Set<String> gqlFields = new HashSet<>();

    private final GqlDocumentRepository gqlDocumentRepository;

    public GqlFieldRepository(GqlDocumentRepository gqlDocumentRepository) {
        this.gqlDocumentRepository = gqlDocumentRepository;
    }

    @PostConstruct
    void initGqlFields() {
        for (Document document : gqlDocumentRepository.getGqlDocuments()) {
            for (Definition<?> definition : document.getDefinitions()) {
                if (definition instanceof SelectionSetContainer<?> selectionSetContainer) {
                    gqlFields.addAll(collectSelectionSetFields(selectionSetContainer));
                }
            }
        }
    }

    public List<String> getGqlFields() {
        List<String> sortedGqlFields = new ArrayList<>(gqlFields);
        Collections.sort(sortedGqlFields);
        return sortedGqlFields;
    }

    private Set<String> collectSelectionSetFields(SelectionSetContainer<?> selectionSetContainer) {
        Set<String> fields = new HashSet<>();
        SelectionSet selectionSet = selectionSetContainer.getSelectionSet();
        if (selectionSet != null) {
            selectionSet.getSelections().forEach(selection -> {
                if (selection instanceof Field field) {
                    fields.add(field.getName());
                    fields.addAll(collectSelectionSetFields(field));
                }
            });
        }

        return fields;
    }

}
