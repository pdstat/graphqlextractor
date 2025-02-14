package com.pdstat.gqlextractor.repo;

import static org.mockito.ArgumentMatchers.any;

import com.pdstat.gqlextractor.service.GqlMergerService;
import graphql.language.Document;
import graphql.language.FragmentDefinition;
import graphql.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class GqlFragmentDefinitionsRepositoryTest {

    @Mock
    private GqlDocumentRepository gqlDocumentRepository;

    @Mock
    private GqlMergerService gqlMergerService;

    @InjectMocks
    private GqlFragmentDefinitionsRepository gqlFragmentDefinitionsRepository;

    @Test
    void testInitFragmentDefinitionsNewFragment() {
        String noticeFieldsFragment = "fragment NoticeFields on Notice { localizedTitle localizedDescription url }";
        Document noticeFieldsFragmentDocument = new Parser().parseDocument(noticeFieldsFragment);

        Mockito.when(gqlDocumentRepository.getGqlDocuments()).thenReturn(List.of(noticeFieldsFragmentDocument));

        gqlFragmentDefinitionsRepository.initFragmentDefinitions();

        FragmentDefinition noticeFieldsFragmentDefinition = gqlFragmentDefinitionsRepository.getGqlFragmentDefinition("NoticeFields");
        Assertions.assertNotNull(noticeFieldsFragmentDefinition);
    }

    @Test
    void testInitFragmentDefinitionsExistingFragment() {
        String noticeFieldsFragment1 = "fragment NoticeFields on Notice { localizedTitle localizedDescription url }";
        Document noticeFieldsFragmentDocument1 = new Parser().parseDocument(noticeFieldsFragment1);
        String noticeFieldsFragment2 = "fragment NoticeFields on Notice { localizedTitle localizedDescription url anotherUrl }";
        Document noticeFieldsFragmentDocument2 = new Parser().parseDocument(noticeFieldsFragment2);

        Mockito.when(gqlDocumentRepository.getGqlDocuments()).thenReturn(List.of(noticeFieldsFragmentDocument1, noticeFieldsFragmentDocument2));
        Mockito.when(gqlMergerService.mergeGraphQLDocuments(any(), any())).thenReturn(noticeFieldsFragmentDocument2);

        gqlFragmentDefinitionsRepository.initFragmentDefinitions();

        FragmentDefinition noticeFieldsFragmentDefinition = gqlFragmentDefinitionsRepository.getGqlFragmentDefinition("NoticeFields");
        Assertions.assertNotNull(noticeFieldsFragmentDefinition);
        FragmentDefinition expectedFragmentDefinition = (FragmentDefinition) noticeFieldsFragmentDocument2.getDefinitions().get(0);
        Assertions.assertEquals(expectedFragmentDefinition.getName(), ((FragmentDefinition) noticeFieldsFragmentDefinition).getName());
        Assertions.assertEquals(expectedFragmentDefinition.getSelectionSet(), ((FragmentDefinition) noticeFieldsFragmentDefinition).getSelectionSet());
    }

}
