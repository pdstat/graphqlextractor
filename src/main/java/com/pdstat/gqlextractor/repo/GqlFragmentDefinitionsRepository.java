package com.pdstat.gqlextractor.repo;

import com.pdstat.gqlextractor.service.GqlMergerService;
import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.FragmentDefinition;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.HashMap;

@Repository
public class GqlFragmentDefinitionsRepository {

    private final GqlDocumentRepository gqlDocumentRepository;
    private final GqlMergerService gqlMergerService;

    private final Map<String, FragmentDefinition> gqlFragmentDefinitions = new HashMap<>();

    public GqlFragmentDefinitionsRepository(GqlDocumentRepository gqlDocumentRepository, GqlMergerService gqlMergerService) {
        this.gqlDocumentRepository = gqlDocumentRepository;
        this.gqlMergerService = gqlMergerService;
    }

    @PostConstruct
    void initFragmentDefinitions() {
        for (Document document : gqlDocumentRepository.getGqlDocuments()) {
            for (Definition<?> definition : document.getDefinitions()) {
                if (definition instanceof FragmentDefinition fragmentDefinition) {
                    String fragmentDefinitionName = fragmentDefinition.getName();
                    if (gqlFragmentDefinitions.containsKey(fragmentDefinitionName)) {
                        FragmentDefinition storedFragmentDefinition = gqlFragmentDefinitions.get(fragmentDefinitionName);

                        Document storedFragmentDocument = buildDocumentFromFragmentDefinition(storedFragmentDefinition);
                        Document fragmentDocument = buildDocumentFromFragmentDefinition(fragmentDefinition);

                        if (!documentsEqual(storedFragmentDocument, fragmentDocument)) {
                            Document mergedDocument = gqlMergerService.mergeGraphQLDocuments(storedFragmentDocument, fragmentDocument);
                            FragmentDefinition mergedFragmentDefinition = (FragmentDefinition) mergedDocument.getDefinitions().get(0);

                            gqlFragmentDefinitions.put(fragmentDefinition.getName(), mergedFragmentDefinition);
                        }

                    } else {
                        gqlFragmentDefinitions.put(fragmentDefinition.getName(), fragmentDefinition);
                    }
                }
            }
        }
    }

    private Document buildDocumentFromFragmentDefinition(FragmentDefinition fragmentDefinition) {
        return Document.newDocument().definition(fragmentDefinition).build();
    }

    private boolean documentsEqual(Document doc1, Document doc2) {
        return doc1.toString().length() == doc2.toString().length();
    }

    public FragmentDefinition getGqlFragmentDefinition(String fragmentName) {
        return gqlFragmentDefinitions.get(fragmentName);
    }

}
