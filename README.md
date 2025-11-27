# About - GraphQL Extractor

This is a command line tool which extracts information about GraphQL queries, mutations and subscriptions from schema introspection, remote javascript files and local javascript files.

The functionality of this tool is to extract the following information:
- GraphQL operations (queries, mutations and subscriptions) from javascript files.
- GraphQL requests in json format from javascript files.
- GraphQL unique operation field names.
- GraphQL field paths from introspection schema. Inspired by lupins 'GraphQL is the new PHP' talk (https://www.youtube.com/watch?v=tIo_t5uUK50&t=696s).
- GraphQL field paths from operations found in javascript files.

## Build and installation

Build is based upon GraalVM native-image.

### Prerequisites

- Oracle GraalVM 17.0.14+8.1 (https://www.oracle.com/java/technologies/downloads/#graalvmjava17)
- GraalVM js language installation
- Microsoft Visual Studio with C++ build tools (https://visualstudio.microsoft.com/visual-cpp-build-tools/)

#### GraalVM installation

- Download and extract the GraalVM JDK 17 archive. Into a directory of your choice.
- Install JS language feature for GraalVM by running the following command:
- Set the JAVA_HOME environment variable to the GraalVM JDK 17 directory.

**GraalVM Setup**
```shell
sudo tar -xzf graalvm-jdk-17.0.14_linux-x64_bin.tar.gz
cd /usr/lib/jdk/graalvm-jdk-17.0.14+8.1/bin
./gu install js
```

**Environment vars setup**
```shell
vim ~/.bashrc
# Add the following JAVA_HOME/PATH setup to bottom of your .bashrc e.g.
#export JAVA_HOME=/usr/lib/jdk/graalvm-jdk-17.0.14+8.1
#export PATH=$JAVA_HOME/bin:$PATH
source ~/.bashrc
```

#### Maven build

- Clone the repository and navigate to the root directory.
- Build the native image.

```shell
git clone https://github.com/pdstat/graphqlextractor.git
cd graphqlextractor
./mvnw -Pnative native:compile
```

- Copy the built binary executable in the `/target` directory to a directory in your PATH (e.g. `/usr/bin`) and start using the tool :).

## Usage

| Arg                | Description                                                                                                                                                                                                                                   |
|--------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| --help             | Outputs usage information                                                                                                                                                                                                                     |
| --input-directory  | The directory containing the javascript files with embedded GQL strings.                                                                                                                                                                      |
| --input-urls       | The path to a wordlist of urls to scan.                                                                                                                                                                                                       |
| --input-schema     | URL to a graphQL endpoint with introspection enabled or the path to a file containing the json response of an introspection query.                                                                                                            |
| --input-operations | The directory containing previously extracted .graphql operations, this avoids resource intensive Javascript AST parsing.                                                                                                                     |
| --request-header   | Request header key/value to set in introspection requests e.g. --request-header="Api-Key1: XXXX" --request-header="Api-Key2: YYYY".                                                                                                           |
| --search-field     | The field name paths to search for in the schema/operations.                                                                                                                                                                                  |
| --depth            | Depth of the field path search, defaults to 10 if not specified.                                                                                                                                                                              |
| --default-params   | The path to a json file of default parameter values. For use with 'requests' output mode                                                                                                                                                      |
| --output-directory | The directory where the generated files will be saved.                                                                                                                                                                                        |
| --output-mode      | The output mode for the generated files. Possible values are 'requests', 'operations', 'fields', 'paths' and 'all'. The default value is 'requests'. Multiple output modes are supported e.g. --output-mode=requests --output-mode=operations |

## Examples

### Schema field search

To search for the possible paths to a field in the schema, use the following command:

```shell
gqlextractor --input-schema=https://rickandmortyapi.com/graphql --search-field=name --output-directory=D:\hacking\recon\rickmorty
```

Example screenshot of the output:

![Schema field search](images/schema-fields.png)

To specify the depth of the search, use the `--depth` argument:

```shell
gqlextractor --input-schema=https://rickandmortyapi.com/graphql --search-field=name --output-directory=D:\hacking\recon\rickmorty --depth=5
```

Example screenshot of the output:

![Schema field search](images/schema-fields-depth.png)

Paths are also saved to a text file based on the name of the field being searched for. e.g.

![Schema field file](images/schema-field-file.png)


### Javascript AST processing

Don't have a schema? No problem this tool also works by processing the AST of javascript files (both locally and remotely) to extract GraphQL operations.

Here are some examples of graphql operation formats that are supported

Template string literals:
```javascript
const query = gql`
  query {
    user {
      id
      name
    }
  }
`;
```

String literals:
```javascript
const query = 'query { user { id name } }';
```

Escaped GraphQL document strings:
```javascript
const n=JSON.parse("{\"kind\":\"Document\",\"definitions\":[{\"kind\":\"FragmentDefinition\",\"name\":{\"kind\":\"Name\",
\"value\":\"StoccUser\"},\"typeCondition\":{\"kind\":\"NamedType\",\"name\":{\"kind\":\"Name\",\"value\":\"StoccUser\"}},
\"directives\":[],\"selectionSet\":{\"kind\":\"SelectionSet\",\"selections\":[{\"kind\":\"Field\",
\"name\":{\"kind\":\"Name\",\"value\":\"__typename\"},\"arguments\":[],\"directives\":[]},{\"kind\":\"Field\",
\"name\":{\"kind\":\"Name\",\"value\":\"id\"},\"arguments\":[],\"directives\":[]},{\"kind\":\"Field\",
\"name\":{\"kind\":\"Name\",\"value\":\"email\"},\"arguments\":[],\"directives\":[]},{\"kind\":\"Field\",
\"name\":{\"kind\":\"Name\",\"value\":\"firstName\"},\"arguments\":[],\"directives\":[]},{\"kind\":\"Field\",
\"name\":{\"kind\":\"Name\",\"value\":\"lastName\"},\"arguments\":[],\"directives\":[]},{\"kind\":\"Field\",
\"name\":{\"kind\":\"Name\",\"value\":\"country\"},\"arguments\":[],\"directives\":[]},{\"kind\":\"Field\",
\"name\":{\"kind\":\"Name\",\"value\":\"fullName\"},\"arguments\":[],\"directives\":[]}]}}],
\"definitionId\":\"aaafc494405155bf9a3e5c174a025db9f2db077e9a4931e58ea5d65c7a6da60c\"}")
```

GraphQL documents in javascript objects:
```javascript
i8={kind:"Document",definitions:[{kind:"OperationDefinition",operation:"mutation",name:{kind:"Name",value:"createInstance"},
variableDefinitions:[{kind:"VariableDefinition",variable:{kind:"Variable",name:{kind:"Name",value:"input"}},
type:{kind:"NonNullType",type:{kind:"NamedType",name:{kind:"Name",value:"CreateInstanceInput"}}}}],
selectionSet:{kind:"SelectionSet",selections:[{kind:"Field",name:{kind:"Name",value:"createInstance"},
arguments:[{kind:"Argument",name:{kind:"Name",value:"input"},value:{kind:"Variable",name:{kind:"Name",value:"input"}}}],
selectionSet:{kind:"SelectionSet",selections:[{kind:"Field",name:{kind:"Name",value:"instance"},selectionSet:{kind:"SelectionSet",
selections:[{kind:"FragmentSpread",name:{kind:"Name",value:"instanceFull"}}]}}]}}]}},{kind:"FragmentDefinition",
name:{kind:"Name",value:"instanceFull"},typeCondition:{kind:"NamedType",name:{kind:"Name",value:"Instance"}},
selectionSet:{kind:"SelectionSet",selections:[{kind:"Field",name:{kind:"Name",value:"id"}},{kind:"Field",
name:{kind:"Name",value:"name"}},{kind:"Field",name:{kind:"Name",value:"clientId"}},{kind:"Field",
name:{kind:"Name",value:"createdAt"}}]}}]}
```

See below for an example of how these GraphQL documents are successfully reconstructed into a GraphQL operation:

```graphql
mutation createInstance($input: CreateInstanceInput!) {
  createInstance(input: $input) {
    instance {
      ...instanceFull
    }
  }
}

fragment instanceFull on Instance {
  id
  name
  clientId
  createdAt
}
```

#### Extracting operations from javascript files

This mode will extract GraphQL operations (queries, mutations and subscriptions) from all of the above formats in javascript files.

```shell
gqlextractor --input-urls=D:\hacking\recon\caido\input-urls.txt --output-directory=D:\hacking\recon\caido\graphql --output-mode=operations 
```

Operations files will be created in an `operations` directory within the output directory.

![Operation files](images/operations-list.png)
![Operation output](images/operation-output.png)

### Extracting unique fields from operations

**NOTE:** The remaining examples will assume operations have already been generated using the previous example. This is to avoid the resource intensive javascript AST parsing. It is however possible to use the `--input-directory` and `--input-urls` arguments to reprocess javascript files.

This will extract unique fields across all operations. Useful for wordlist generation. (e.g. for fuzzing via clairvoyance)

```shell
gqlextractor --input-operations=D:\hacking\recon\caido\graphql\operations --output-directory=D:\hacking\recon\caido\graphql --output-mode=fields 
```

A unique fields file will be created in a `wordlist` directory within the output directory.

![Unique fields](images/unique-field-output.png)

### Extracting json requests from operations

This will extract the GraphQL requests in json format from the operations. Useful for replaying requests. The collection of requests can be used with Burp via Intruder for example.

```shell
gqlextractor --input-operations=D:\hacking\recon\caido\graphql\operations --output-directory=D:\hacking\recon\caido\graphql --output-mode=requests
```

Operations files will be created in an `requests` directory within the output directory.

![Requests list](images/requests-list.png)
![Request output](images/request-output.png)

#### Using default parameters

If you have a set of default parameters that you would like to use with the requests, you can specify a json file containing the default parameters.

This can be useful for example if you have known values of parameters and you're wanting to test IDOR's for example. (Or any other vuln type)

```shell
gqlextractor --input-operations=D:\hacking\recon\caido\graphql\operations --default-params=D:\hacking\recon\caido\default-params.json --output-directory=D:\hacking\recon\caido\graphql --output-mode=requests
```

The default parameters file should be in JSON format and contain the parameters you would like to use. For example:

```json
{
  "input": {
    "id": "1",
    "name": "TestInstance"
  }
}
```

![Default param output](images/default-param-output.png)

### Extracting field paths from operations

Similar to the schema field search, this will extract the field paths from the operations. This can be useful for understanding the structure of the data returned by the operations.

```shell
gqlextractor --input-operations=D:\hacking\recon\caido\graphql\operations --output-directory=D:\hacking\recon\caido\graphql --search-field=email --output-mode=paths
```

The field paths file will be created in a `field-paths` directory within the output directory.

![Field paths output](images/field-paths.png)