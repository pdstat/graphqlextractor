# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

GraphQL Extractor is a Spring Boot CLI tool that extracts GraphQL operations, requests, fields, and field paths from JavaScript source files (local/remote) and GraphQL schema introspection. It uses GraalVM's Polyglot API to parse JavaScript ASTs via the Acorn parser to find embedded GraphQL documents.

## Build Commands

**Prerequisites:** Oracle GraalVM JDK 17 with JS language feature, Maven wrapper included.

```shell
# Build native image (primary build target)
./mvnw -Pnative native:compile

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=GqlMergerServiceTest

# Run a single test method
./mvnw test -Dtest=GqlMergerServiceTest#testMerge

# Standard JAR build (without native image)
./mvnw package
```

The native binary is output to `target/`.

## Architecture

**Entry point:** `GqlExtractor` implements Spring Boot `CommandLineRunner`. CLI args are parsed via Spring's `ApplicationArguments`. The main orchestrator is `GqlExtractorOutputHandlerService`, which delegates to writer services based on the selected `OutputMode` (operations, requests, fields, paths, all).

**Core pipeline:**
1. **Input loading** — `GqlDocumentRepository` reads JS files from disk (parallel via ThreadPoolExecutor), fetches remote URLs, or loads pre-extracted `.graphql` operations
2. **JS AST extraction** — `GqlDocumentExtractor` uses `GraalAcornWalker` to execute Acorn JS parser via GraalVM Polyglot, walking the AST with visitor scripts (`gql-docs-visitor.js`, `gql-strings-visitor.js`) to find GraphQL documents
3. **Document processing** — `GqlOperationsRepository` merges operations using `GqlMergerService` (recursive selection/variable merging) and resolves fragment definitions from `GqlFragmentDefinitionsRepository`
4. **Schema introspection** — `GqlSchemaRepository` fetches and parses GraphQL schemas; `GqlSchemaPathFinder` searches field paths in schemas
5. **Output** — Writer services generate files: operations (`.graphql`), JSON requests, field wordlists, field paths

**JavaScript resources** in `src/main/resources/`: `acorn.js` and `walk.js` (Acorn parser), `parseDocs.js`, `gql-docs-visitor.js`, `gql-strings-visitor.js` (custom AST visitors).

**Custom deserialization:** `DocumentDeserialiser` handles JSON-to-GraphQL `Document` conversion, registered via `JacksonConfig`.

## Key Dependencies

- **graphql-java 22.3** — GraphQL document parsing and schema handling
- **GraalVM Polyglot 23.1.6** — JavaScript execution engine for AST parsing
- **Spring Boot 3.3.5** — DI, WebFlux (HTTP client for remote URLs/introspection), JSON serialization
- **JUnit 5 + Mockito** — Testing

## Testing

Tests are in `src/test/java/` using JUnit 5 with `@ExtendWith(MockitoExtension.class)`. Test resources (sample JS files, GraphQL documents) are in `src/test/resources/`. Tests cover repositories, services, and models but do not require a running application context — they use Mockito mocks.

## Native Image

GraalVM native-image configuration files are in `src/main/resources/META-INF/native-image/` for reflection, resources, and serialization needed at compile time.
