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
        if (missingFragmentDefinitions(document)) {
            Set<String> documentFragmentSpreads = getDocumentFragmentSpreads(document);
            Document.Builder documentBuilder = Document.newDocument();
            for (Definition<?> definition : document.getDefinitions()) {
                documentBuilder.definition(definition);
            }
            for (String fragmentSpread : documentFragmentSpreads) {
                if (missingFragmentDefinition(document, fragmentSpread)) {
                    FragmentDefinition fragmentDefinition = gqlFragmentDefinitionsRepository.getGqlFragmentDefinition(fragmentSpread);
                    if (fragmentDefinition != null) {
                        documentBuilder.definition(fragmentDefinition);
                    }
                }
            }
            return documentBuilder.build();
        }
        return document;
    }

    private boolean missingFragmentDefinitions(Document document) {
        boolean containsFragmentSpread = document.toString().contains("FragmentSpread");
        boolean containsFragmentDefinitions = false;
        for (Definition<?> definition : document.getDefinitions()) {
            if (definition instanceof FragmentDefinition) {
                containsFragmentDefinitions = true;
                break;
            }
        }
        if (!containsFragmentDefinitions && containsFragmentSpread) {
            return true;
        }
        Set<String> documentFragmentSpreads = getDocumentFragmentSpreads(document);
        for (Definition<?> definition : document.getDefinitions()) {
            if (definition instanceof FragmentDefinition fragmentDefinition) {
                if (!documentFragmentSpreads.contains(fragmentDefinition.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean missingFragmentDefinition(Document document, String fragmentSpread) {
        for (Definition<?> definition : document.getDefinitions()) {
            if (definition instanceof FragmentDefinition fragmentDefinition) {
                if (fragmentDefinition.getName().equals(fragmentSpread)) {
                    return false;
                }
            }
        }
        return true;
    }

    private Set<String> getDocumentFragmentSpreads(Document document) {
        Set<String> fragmentSpreads = new HashSet<>();
        Pattern fragmentSpreadPattern = Pattern.compile("FragmentSpread\\{name='([^']+)'");
        Matcher matcher = fragmentSpreadPattern.matcher(document.toString());
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
