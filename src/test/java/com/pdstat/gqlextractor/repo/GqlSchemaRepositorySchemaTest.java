package com.pdstat.gqlextractor.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdstat.gqlextractor.Constants;
import graphql.language.AstPrinter;
import graphql.language.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class GqlSchemaRepositorySchemaTest {

    @Spy
    private ObjectMapper mapper = new ObjectMapper();

    @Mock
    private ApplicationArguments appArgs;

    @InjectMocks
    private GqlSchemaRepository gqlSchemaRepository;

//    @Test
//    void testGetGqlSchema() {
//        Mockito.when(appArgs.containsOption(Constants.Arguments.REQUEST_HEADER)).thenReturn(false);
//        Mockito.when(appArgs.getOptionValues(Constants.Arguments.REQUEST_HEADER))
//                .thenReturn(List.of("X-Airbnb-Api-Key: d306zoyjsyarp7ifhu67rjxn52tv0t20"));
//        Document schema = gqlSchemaRepository.getGqlSchema("https://rickandmortyapi.com/graphql");
//        System.out.println(AstPrinter.printAst(schema));
//    }
}
