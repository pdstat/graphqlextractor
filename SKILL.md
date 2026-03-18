---
name: gqlextractor
description: Guide the user through using the gqlextractor CLI tool for extracting GraphQL operations, requests, fields, and field paths from JavaScript files and GraphQL schema introspection.
---

# gqlextractor Skill

You are an expert at using the `gqlextractor` command-line tool. This tool extracts GraphQL information from JavaScript source files (local and remote) and GraphQL schema introspection endpoints.

## What gqlextractor Does

gqlextractor parses JavaScript files to find embedded GraphQL documents by walking the JavaScript AST. It supports these embedded formats:
- Template string literals (e.g., ``gql`query { ... }` ``)
- String literals (e.g., `'query { user { id } }'`)
- Escaped JSON GraphQL document strings (e.g., `JSON.parse("{\"kind\":\"Document\",...}")`)
- GraphQL documents as JavaScript objects (e.g., `{kind:"Document",definitions:[...]}`)

It can also fetch and parse GraphQL schemas via introspection.

## Binary Location

The compiled native binary is at `target/gqlextractor` (Linux) or `target/gqlextractor.exe` (Windows) after building. Users may also have it installed on their PATH as `gqlextractor`.

## CLI Arguments

| Argument | Required | Description |
|---|---|---|
| `--help` | No | Show usage information |
| `--input-directory=<path>` | One input required | Directory of local JavaScript files to parse |
| `--input-urls=<path>` | One input required | Path to a text file containing URLs of remote JavaScript files to fetch and parse |
| `--input-schema=<url-or-path>` | One input required | URL to a GraphQL endpoint with introspection enabled, OR path to a local file containing the JSON introspection response |
| `--input-operations=<path>` | One input required | Directory of previously extracted `.graphql` operation files (skips JS AST parsing) |
| `--output-directory=<path>` | Yes | Directory where output files are written |
| `--output-mode=<mode>` | No (default: `requests`) | Output mode: `operations`, `requests`, `fields`, `paths`, or `all`. Can be specified multiple times for multiple modes |
| `--search-field=<name>` | Required for `paths` mode and schema input | Field name to search for in schema or operations |
| `--depth=<n>` | No (default: 10) | Maximum depth for schema field path search |
| `--request-header=<key: value>` | No | HTTP header for introspection requests. Can be specified multiple times |
| `--default-params=<path>` | No | Path to a JSON file of default parameter values for `requests` output mode |

**At least one input argument is required:** `--input-directory`, `--input-urls`, `--input-schema`, or `--input-operations`.

## Output Modes and Directory Structure

Each output mode writes to a subdirectory within `--output-directory`:

| Mode | Output Location | Description |
|---|---|---|
| `operations` | `operations/` | Individual `.graphql` files for each extracted operation |
| `requests` | `requests/` | JSON files containing GraphQL request bodies ready for replay |
| `fields` | `wordlist/unique-fields.txt` | Unique field names across all operations (useful for fuzzing/wordlists) |
| `paths` | `field-paths/` | Field path files showing paths to a searched field within operations (requires `--search-field`) |
| `all` | All of the above | Runs operations + requests + fields + paths |

When using `--input-schema`, output goes to `schema-field-paths/` instead.

## Common Usage Patterns

### 1. Extract operations from local JavaScript files
```shell
gqlextractor --input-directory=/path/to/js/files --output-directory=/path/to/output --output-mode=operations
```

### 2. Extract operations from remote JavaScript URLs
```shell
gqlextractor --input-urls=/path/to/urls.txt --output-directory=/path/to/output --output-mode=operations
```
The URL file is a plain text file with one URL per line.

### 3. Generate JSON requests from previously extracted operations
```shell
gqlextractor --input-operations=/path/to/output/operations --output-directory=/path/to/output --output-mode=requests
```
This avoids re-parsing JavaScript (which is resource intensive). Use `--input-operations` pointing to a directory of `.graphql` files from a previous `operations` extraction.

### 4. Generate JSON requests with default parameter values
```shell
gqlextractor --input-operations=/path/to/output/operations --default-params=/path/to/defaults.json --output-directory=/path/to/output --output-mode=requests
```
The defaults JSON file maps parameter names to values:
```json
{
  "input": {
    "id": "1",
    "name": "TestInstance"
  }
}
```

### 5. Extract unique field names for wordlist generation
```shell
gqlextractor --input-operations=/path/to/output/operations --output-directory=/path/to/output --output-mode=fields
```

### 6. Search for field paths in a GraphQL schema (introspection)
```shell
gqlextractor --input-schema=https://example.com/graphql --search-field=email --output-directory=/path/to/output
```
With custom depth and auth headers:
```shell
gqlextractor --input-schema=https://example.com/graphql --search-field=email --depth=5 --request-header="Authorization: Bearer TOKEN" --output-directory=/path/to/output
```
The schema input can also be a local file containing the introspection JSON response.

### 7. Search for field paths in extracted operations
```shell
gqlextractor --input-operations=/path/to/output/operations --search-field=email --output-directory=/path/to/output --output-mode=paths
```

### 8. Run all extraction modes at once
```shell
gqlextractor --input-directory=/path/to/js/files --search-field=email --output-directory=/path/to/output --output-mode=all
```

### 9. Combine multiple output modes
```shell
gqlextractor --input-urls=/path/to/urls.txt --output-directory=/path/to/output --output-mode=operations --output-mode=requests
```

## Recommended Workflow

The most efficient workflow is two-phase:

1. **Phase 1 — Extract operations** from JavaScript sources (this is the expensive step):
   ```shell
   gqlextractor --input-urls=urls.txt --output-directory=./output --output-mode=operations
   ```

2. **Phase 2 — Reuse extracted operations** for fast subsequent analysis:
   ```shell
   # Generate requests
   gqlextractor --input-operations=./output/operations --output-directory=./output --output-mode=requests
   # Generate field wordlist
   gqlextractor --input-operations=./output/operations --output-directory=./output --output-mode=fields
   # Search for specific field paths
   gqlextractor --input-operations=./output/operations --search-field=id --output-directory=./output --output-mode=paths
   ```

This avoids repeating the resource-intensive JavaScript AST parsing step.

## Important Notes

- When `--input-schema` is provided, the tool runs schema field path search mode exclusively (ignores `--output-mode`). The `--search-field` argument is required in this mode.
- When `--output-mode=paths` or `--output-mode=all` is used with JS/operation inputs, `--search-field` is also required.
- The default output mode is `requests` if `--output-mode` is not specified.
- The default search depth is 10 if `--depth` is not specified.
- `--request-header` can be specified multiple times for multiple headers on introspection requests.
