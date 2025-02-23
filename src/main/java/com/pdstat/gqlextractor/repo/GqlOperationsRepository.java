package com.pdstat.gqlextractor.repo;

import com.pdstat.gqlextractor.service.GqlMergerService;
import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.FragmentDefinition;
import graphql.language.OperationDefinition;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class GqlOperationsRepository {

    private static final Pattern FRAGMENT_SPREAD_PATTERN = Pattern.compile("FragmentSpread\\{name='([^']+)'");

    private final GqlDocumentRepository gqlDocumentRepository;
    private final GqlFragmentDefinitionsRepository gqlFragmentDefinitionsRepository;
    private final GqlMergerService gqlMergerService;

    private final Map<String, Document> gqlOperations = new HashMap<>();

    public GqlOperationsRepository(GqlDocumentRepository gqlDocumentRepository,
                                   GqlFragmentDefinitionsRepository gqlFragmentDefinitionsRepository,
                                   GqlMergerService gqlMergerService) {
        this.gqlDocumentRepository = gqlDocumentRepository;
        this.gqlFragmentDefinitionsRepository = gqlFragmentDefinitionsRepository;
        this.gqlMergerService = gqlMergerService;
    }

    @PostConstruct
    void initGqlOperations() {
        for (Document document : gqlDocumentRepository.getGqlDocuments()) {
            for (Definition<?> definition : document.getDefinitions()) {
                if (definition instanceof OperationDefinition operationDefinition) {
                    String operationName = operationDefinition.getName();
                    if (gqlOperations.containsKey(operationName)) {
                        Document storedOperation = gqlOperations.get(operationName);

                        if (!documentsEqual(storedOperation, document)) {
                            document = gqlMergerService.mergeGraphQLDocuments(storedOperation, document);
                        }
                    }
                    gqlOperations.put(operationName, addMissingFragmentDefinitions(document));
                }
            }
        }
    }

    private boolean documentsEqual(Document doc1, Document doc2) {
        return doc1.toString().length() == doc2.toString().length();
    }

    private Document addMissingFragmentDefinitions(Document document) {
        Set<String> addedFragments = new HashSet<>();
        Document.Builder documentBuilder = Document.newDocument();

        // Copy existing definitions
        for (Definition<?> definition : document.getDefinitions()) {
            documentBuilder.definition(definition);
            if (definition instanceof FragmentDefinition) {
                addedFragments.add(((FragmentDefinition) definition).getName());
            }
        }

        // Recursively resolve missing fragment definitions
        resolveMissingFragments(documentBuilder, addedFragments, getDocumentFragmentSpreads(document));

        return documentBuilder.build();
    }

    private void resolveMissingFragments(Document.Builder documentBuilder, Set<String> addedFragments, Set<String> fragmentSpreads) {
        for (String fragmentSpread : fragmentSpreads) {
            if (!addedFragments.contains(fragmentSpread)) {
                FragmentDefinition fragmentDefinition = gqlFragmentDefinitionsRepository.getGqlFragmentDefinition(fragmentSpread);
                if (fragmentDefinition != null) {
                    documentBuilder.definition(fragmentDefinition);
                    addedFragments.add(fragmentSpread);

                    // Recursively resolve dependencies of the added fragment
                    resolveMissingFragments(documentBuilder, addedFragments, getFragmentDefinitionFragmentSpreads(fragmentDefinition));
                }
            }
        }
    }

    private Set<String> getFragmentDefinitionFragmentSpreads(FragmentDefinition fragmentDefinition) {
        Set<String> fragmentSpreads = new HashSet<>();
        Matcher matcher = FRAGMENT_SPREAD_PATTERN.matcher(fragmentDefinition.toString());
        while (matcher.find()) {
            fragmentSpreads.add(matcher.group(1));
        }
        return fragmentSpreads;
    }

    private Set<String> getDocumentFragmentSpreads(Document document) {
        Set<String> fragmentSpreads = new HashSet<>();
        Matcher matcher = FRAGMENT_SPREAD_PATTERN.matcher(document.toString());
        while (matcher.find()) {
            fragmentSpreads.add(matcher.group(1));
        }
        return fragmentSpreads;
    }

    public Document getGqlOperation(String operationName) {
        return gqlOperations.get(operationName);
    }

    public Map<String, Document> getGqlOperations() {
        return gqlOperations;
    }

}
