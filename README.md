# GraphQL Extractor

Ever come across javascript files with embedded GraphQL queries and wish you could extract them into a separate file? This tool does just that.

For example, given the following javascript file:

```javascript
  const N = o.Ps`
    fragment AdditionalUserInformationFragment on Query {
  member(id: $memberId) {
    id
    dateOfBirth
    pronoun
    gender
    hasSubmittedDemographicData
    subscriptions {
      id
    }
  }
  config {
    purchase {
      postCollectingFields
    }
  }
}
    `,
          S = o.Ps`
    fragment DnaPaymentsPayloadFragment on DnaPaymentsPayload {
  invoiceId
  amount
  currency
  description
  terminalId
  sdkConfiguration {
    authentication
    configurations
    transactionType
  }
}
    `,
```

This tool will extract the GraphQL queries into separate files:

```graphql
query AddAccountMember {
  config {
    comembers {
      enabled
    }
    signup {
      minAgeToSignUp
    }
  }
  currentMarket {
    memberTermsAndConditions {
      termsId
      agreements {
        display
        url
      }
    }
  }
}
```

And

```json
{
  "operationName" : "AddAccountMember",
  "variables" : { },
  "query" : "query AddAccountMember {\n  config {\n    comembers {\n      enabled\n    }\n    signup {\n      minAgeToSignUp\n    }\n  }\n  currentMarket {\n    memberTermsAndConditions {\n      termsId\n      agreements {\n        display\n        url\n      }\n    }\n  }\n}"
}
```

The collection of JSON files can then be used as wordlists for fuzzing GraphQL endpoints.

Example usage:

```bash
 ./run.sh
Usage: run.sh -i <input_directory> -o <output_directory> [-m <output_mode> (json, graphql or all)]
```