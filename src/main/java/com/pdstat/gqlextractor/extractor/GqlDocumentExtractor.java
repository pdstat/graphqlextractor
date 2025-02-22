package com.pdstat.gqlextractor.extractor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdstat.gqlextractor.graal.GraalAcornWalker;
import com.pdstat.gqlextractor.service.ResourceService;
import graphql.language.Document;
import graphql.parser.InvalidSyntaxException;
import graphql.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class GqlDocumentExtractor {

    private static final Logger logger = LoggerFactory.getLogger(GqlDocumentExtractor.class);
    private static final String JSON_STRING_ARRAY_PREFIX = "[\"";

    private final Resource gqlDocsVisitorResource;
    private final Resource gqlStringsVisitorResource;
    private final GraalAcornWalker walker;
    private final ResourceService resourceService;
    private final ObjectMapper mapper;

    public GqlDocumentExtractor(@Value("classpath:gql-docs-visitor.js") Resource gqlDocsVisitorResource,
                                @Value("classpath:gql-strings-visitor.js") Resource gqlStringsVisitorResource,
                                GraalAcornWalker walker, ResourceService resourceService, ObjectMapper mapper) {
        this.gqlDocsVisitorResource = gqlDocsVisitorResource;
        this.gqlStringsVisitorResource = gqlStringsVisitorResource;
        this.walker = walker;
        this.resourceService = resourceService;
        this.mapper = mapper;
    }

    public List<Document> extract(String javascript) {
        List<Document> documents = new ArrayList<>();
        try {
            documents.addAll(extractDocumentsWithVisitor(javascript, documents,
                    resourceService.readResourceFileContent(gqlDocsVisitorResource)));
            documents.addAll(extractDocumentsWithVisitor(javascript, documents,
                    resourceService.readResourceFileContent(gqlStringsVisitorResource)));
        } catch (IOException e) {
            logger.error("Error extracting documents from javascript", e);
        }

        return documents;
    }

    private List<Document> extractDocumentsWithVisitor(String javascript, List<Document> documents,
                                                       String visitorScript) throws IOException {
        String matchedDocumentStrings = walker.extractMatchedObjects(javascript, visitorScript);
        return parseDocuments(matchedDocumentStrings);
    }

    private List<Document> parseDocuments(String matchedDocumentStrings) throws IOException {
        List<Document> docs = new ArrayList<>();
        if (jsonStringArray(matchedDocumentStrings)) {
            List<String> documentStrings = mapper.readValue(matchedDocumentStrings, new TypeReference<List<String>>() {});
            if (gqlLanguageString(matchedDocumentStrings)) {
                extractGqlLanguageStrings(documentStrings, docs);
            } else {
                for (String doc : documentStrings) {
                    docs.add(mapper.readValue(doc, Document.class));
                }
            }
        } else {
            docs = mapper.readValue(matchedDocumentStrings, new TypeReference<List<Document>>() {});
        }
        return docs;
    }

    private static void extractGqlLanguageStrings(List<String> documentStrings, List<Document> docs) {
        Parser gqlParser = new Parser();
        for (String doc : documentStrings) {
            try {
                docs.add(gqlParser.parseDocument(doc));
            } catch (InvalidSyntaxException e) {
                logger.error("Error parsing document: {}", doc, e);
            }
        }
    }

    private boolean gqlLanguageString(String matchedDocumentStrings) {
        return matchedDocumentStrings.contains("query ") || matchedDocumentStrings.contains("mutation ") ||
                matchedDocumentStrings.contains("subscription ") || matchedDocumentStrings.contains("fragment ");
    }

    private boolean jsonStringArray(String matchedDocumentStrings) {
        return matchedDocumentStrings.startsWith(JSON_STRING_ARRAY_PREFIX);
    }


}
