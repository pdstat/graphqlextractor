package com.pdstat.gqlextractor.service;

import graphql.language.Document;
import graphql.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GqlSchemaPathFinderTest {

    private GqlSchemaPathFinder finder;

    private static final String SCHEMA = """
            type Query {
              character(id: ID!): Character
              characters(page: Int, filter: FilterCharacter): Characters
              charactersByIds(ids: [ID!]!): [Character]
              location(id: ID!): Location
              locations(page: Int, filter: FilterLocation): Locations
              locationsByIds(ids: [ID!]!): [Location]
              episode(id: ID!): Episode
              episodes(page: Int, filter: FilterEpisode): Episodes
              episodesByIds(ids: [ID!]!): [Episode]
            }

            type Character {
              id: ID
              name: String
              status: String
              species: String
              type: String
              gender: String
              origin: Location
              location: Location
              image: String
              episode: [Episode]!
              created: String
            }

            type Location {
              id: ID
              name: String
              type: String
              dimension: String
              residents: [Character]!
              created: String
            }

            type Episode {
              id: ID
              name: String
              air_date: String
              episode: String
              characters: [Character]!
              created: String
            }

            type Characters {
              info: Info
              results: [Character]
            }

            type Info {
              count: Int
              pages: Int
              next: Int
              prev: Int
            }

            type Locations {
              info: Info
              results: [Location]
            }

            type Episodes {
              info: Info
              results: [Episode]
            }

            input FilterCharacter {
              name: String
              status: String
              species: String
              type: String
              gender: String
            }

            input FilterLocation {
              name: String
              type: String
              dimension: String
            }

            input FilterEpisode {
              name: String
              episode: String
            }
            """;

    @BeforeEach
    void setUp() {
        finder = new GqlSchemaPathFinder();
    }

    @Test
    void testFindFieldPaths() {
        Document document = new Parser().parseDocument(SCHEMA);

        List<String> paths = finder.findFieldPaths(document, "name", 3);
        List<String> expectedPaths = List.of(
                "name -> Character -> character -> Query",
                "name -> Location -> locationsByIds -> Query",
                "name -> Location -> location -> Query",
                "name -> Episode -> episode -> Query",
                "name -> Episode -> episodesByIds -> Query",
                "name -> Character -> charactersByIds -> Query"
        );
        Assertions.assertEquals(expectedPaths, paths);
    }

    @Test
    void testFindFieldPathsFieldNotFound() {
        Document document = new Parser().parseDocument(SCHEMA);

        List<String> paths = finder.findFieldPaths(document, "nonExistentField", 5);
        Assertions.assertNotNull(paths);
        Assertions.assertTrue(paths.isEmpty());
    }

    @Test
    void testFindFieldPathsMaxDepthLimitsResults() {
        Document document = new Parser().parseDocument(SCHEMA);

        // Depth 1 should only find direct fields on Query
        List<String> paths = finder.findFieldPaths(document, "name", 1);
        Assertions.assertNotNull(paths);
        Assertions.assertTrue(paths.isEmpty());
    }

    @Test
    void testFindFieldPathsDirectFieldOnQuery() {
        Document document = new Parser().parseDocument(SCHEMA);

        List<String> paths = finder.findFieldPaths(document, "character", 2);
        Assertions.assertFalse(paths.isEmpty());
        Assertions.assertTrue(paths.contains("character -> Query"));
    }

    @Test
    void testFindFieldPathsReusableAcrossCalls() {
        Document document = new Parser().parseDocument(SCHEMA);

        // First call
        List<String> paths1 = finder.findFieldPaths(document, "name", 3);
        Assertions.assertFalse(paths1.isEmpty());

        // Second call with same finder instance should produce correct results
        // (regression test for stale state bug)
        List<String> paths2 = finder.findFieldPaths(document, "dimension", 3);
        Assertions.assertFalse(paths2.isEmpty());
        Assertions.assertTrue(paths2.stream().anyMatch(p -> p.contains("dimension")));
    }

    @Test
    void testFindFieldPathsWithDifferentSchemas() {
        Document document1 = new Parser().parseDocument(SCHEMA);

        // First call with full schema
        List<String> paths1 = finder.findFieldPaths(document1, "name", 3);
        Assertions.assertFalse(paths1.isEmpty());

        // Second call with a different, simpler schema
        String simpleSchema = """
                type Query {
                  user(id: ID!): User
                }
                type User {
                  id: ID
                  username: String
                }
                """;
        Document document2 = new Parser().parseDocument(simpleSchema);
        List<String> paths2 = finder.findFieldPaths(document2, "username", 3);
        Assertions.assertFalse(paths2.isEmpty());
        Assertions.assertEquals(1, paths2.size());
        Assertions.assertTrue(paths2.contains("username -> User -> user -> Query"));
    }

    @Test
    void testFindFieldPathsEmptySchema() {
        String emptySchema = "type Query { placeholder: String }";
        Document document = new Parser().parseDocument(emptySchema);

        List<String> paths = finder.findFieldPaths(document, "nonExistent", 5);
        Assertions.assertNotNull(paths);
        Assertions.assertTrue(paths.isEmpty());
    }

    @Test
    void testFindFieldPathsWithMutationType() {
        String schema = """
                type Query {
                  user(id: ID!): User
                }
                type Mutation {
                  createUser(name: String!): User
                }
                type User {
                  id: ID
                  name: String
                }
                """;
        Document document = new Parser().parseDocument(schema);

        List<String> paths = finder.findFieldPaths(document, "name", 3);
        Assertions.assertFalse(paths.isEmpty());
        // Should find paths from both Query and Mutation root types
        boolean hasQueryPath = paths.stream().anyMatch(p -> p.contains("Query"));
        boolean hasMutationPath = paths.stream().anyMatch(p -> p.contains("Mutation"));
        Assertions.assertTrue(hasQueryPath);
        Assertions.assertTrue(hasMutationPath);
    }
}
