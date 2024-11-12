# GraphQL Extractor

Ever come across javascript files with embedded GraphQL queries and wish you could extract them into a separate file? This tool does just that.

For example, given the following javascript file:

```javascript
        function zt(e) {
          const t = u(u({}, f), e);
          return i.aM(Xt, t);
        }
        const en = o.Ps`
    query Profile($isAccountHolder: Boolean!, $memberId: String) {
  ...MemberAlertsFragment
  ...MemberStatsFragment
  ...CurrentBalanceWidgetFragment
  ...YourInformationDataFragment
  ...ComembersDataFragment @include(if: $isAccountHolder)
  ...MemberSubscriptionsFragment
  ...MemberKeyfobDataFragment
  ...EassistPreferenceDataFragment
  ...NotificationPreferencesDataFragment
  ...AdsContainerDataFragment
  ...AdditionalUserInformationFragment @include(if: $isAccountHolder)
  member(id: $memberId) @include(if: $isAccountHolder) {
    firstName
    migration {
      status
    }
  }
  config {
    profile {
      showCurrentBalance
    }
    migration @include(if: $isAccountHolder) {
      supportsMigration
    }
    system {
      usesSecurityQuestions
    }
    membershipHistory {
      enabled
    }
  }
}
    ${$}
${V}
${v}
${j}
${k}
${F}
${x}
${q}
${W}
${B}
${N}`;
```

This tool will extract the GraphQL queries into separate files:

```graphql
fragment MemberAlertsFragment on Query {
  member(id: $memberId) {
    id
    subscriptions {
      id
      endDate
    }
    alerts {
      id
      name
      title
      body
      color
      action {
        text
        href
      }
    }
  }
  config {
    system {
      giftRedemptionHelpUrl
    }
  }
}
fragment MemberStatsFragment on Query {
  member(id: $memberId) {
    id
    stats {
      numberOfRides
    }
  }
}
fragment CurrentBalanceWidgetFragment on Query {
  member(id: $memberId) {
    id
    currentBalance {
      balance {
        amount
        formatted
      }
      nextBillingDateMs
    }
  }
}
fragment YourInformationDataFragment on Query {
  member(id: $memberId) {
    id
    firstName
    lastName
    email
    phoneNumber
    phoneNumberIsVerified
    emailIsVerified
    shippingAddress {
      addressLine1
      addressLine2
      addressLine3
      city
      country
      region
      postalCode
    }
    memberCanAccessAccount
  }
  config {
    backend
    profile {
      validatePhoneNumber
    }
  }
}
fragment ComembersDataFragment on Query {
  comembers {
    id
    firstName
    lastName
    keyFob {
      id
    }
  }
  config {
    comembers {
      enabled
    }
  }
}
fragment MemberSubscriptionsFragment on Query {
  member(id: $memberId) {
    id
    subscriptions {
      id
      packageId
      chargeAccountId
      cancellationSurvey {
        title
        question
        questionDetails
        buttonText
        possibleAnswers {
          id
          text
          detailsPlaceholderText {
            accessibilityLabel
            text
          }
        }
      }
      title
      subtitle
      status
      offerCategory
      canCancel
      canPause
      canReactivate
      isRenewable
      subscriptionBenefits
      renewalType
      businessSubscriptionOptInDetails {
        status
        confirmationStartDate
        confirmationDaysLeft
      }
      priceLabel
      renewalDate
      endDate
      corporateEmailConfirmed
      confirmationByEmail
      actions {
        actionType
        isPermitted
        detailText
      }
      statusSummary {
        title
        body
      }
      promotions {
        buttonText
        fallbackUrl
        title
      }
      renewsTo {
        id
        title
        priceLabel
      }
      cancelMessagingDetails {
        benefitsTitles
      }
      legalText
      canBeRenewedWithDiscount
    }
  }
  config {
    system {
      cancelInsteadOfRenewal
    }
  }
}
fragment MemberKeyfobDataFragment on Query {
  member(id: $memberId) {
    id
    nfc {
      id
      number
    }
    keyFob {
      id
      number
    }
    canAddKeyFob
    keyfobEassistPreference {
      canChooseKeyfobMode
      keyfobEassistUnlockMode
    }
    keyfobEassistAvailableOptions {
      id
      mode
      priceText
    }
  }
  config {
    system {
      usesBikeKeys
      nfcType
      storeUrl
      userCanRemoveOwnAccessMethod
    }
  }
}
fragment EassistPreferenceDataFragment on Query {
  member(id: $memberId) {
    id
    keyfobEassistAvailableOptions {
      id
      mode
      priceText
    }
    keyfobEassistPreference {
      canChooseKeyfobMode
      keyfobEassistUnlockMode
    }
  }
}
fragment NotificationPreferencesDataFragment on Query {
  member(id: $memberId) {
    id
    shippingAddress {
      addressLine1
      addressLine2
      addressLine3
      city
      country
      region
      postalCode
    }
  }
  config {
    profile {
      showLanguageSelector
    }
  }
}
fragment AdsContainerDataFragment on Query {
  member(id: $memberId) {
    keyFob {
      id
    }
    subscriptions {
      id
    }
  }
  tcsVars {
    skipMembershipGifting
  }
  config {
    profile {
      showHelmetAd
      showBikeKeyAd
      bikeKeyPromoCode
      membershipGiftingAdPath
    }
    system {
      storeUrl
    }
  }
}
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
query Profile($isAccountHolder: Boolean!, $memberId: String) {
  ...MemberAlertsFragment
  ...MemberStatsFragment
  ...CurrentBalanceWidgetFragment
  ...YourInformationDataFragment
  ...ComembersDataFragment @include(if: $isAccountHolder)
  ...MemberSubscriptionsFragment
  ...MemberKeyfobDataFragment
  ...EassistPreferenceDataFragment
  ...NotificationPreferencesDataFragment
  ...AdsContainerDataFragment
  ...AdditionalUserInformationFragment @include(if: $isAccountHolder)
  member(id: $memberId) @include(if: $isAccountHolder) {
    firstName
    migration {
      status
    }
  }
  config {
    profile {
      showCurrentBalance
    }
    migration @include(if: $isAccountHolder) {
      supportsMigration
    }
    system {
      usesSecurityQuestions
    }
    membershipHistory {
      enabled
    }
  }
}
```

And

```json
{
  "operationName" : "Profile",
  "variables" : {
    "isAccountHolder" : false,
    "memberId" : ""
  },
  "query" : "fragment MemberAlertsFragment on Query {\n  member(id: $memberId) {\n    id\n    subscriptions {\n      id\n      endDate\n    }\n    alerts {\n      id\n      name\n      title\n      body\n      color\n      action {\n        text\n        href\n      }\n    }\n  }\n  config {\n    system {\n      giftRedemptionHelpUrl\n    }\n  }\n}\nfragment MemberStatsFragment on Query {\n  member(id: $memberId) {\n    id\n    stats {\n      numberOfRides\n    }\n  }\n}\nfragment CurrentBalanceWidgetFragment on Query {\n  member(id: $memberId) {\n    id\n    currentBalance {\n      balance {\n        amount\n        formatted\n      }\n      nextBillingDateMs\n    }\n  }\n}\nfragment YourInformationDataFragment on Query {\n  member(id: $memberId) {\n    id\n    firstName\n    lastName\n    email\n    phoneNumber\n    phoneNumberIsVerified\n    emailIsVerified\n    shippingAddress {\n      addressLine1\n      addressLine2\n      addressLine3\n      city\n      country\n      region\n      postalCode\n    }\n    memberCanAccessAccount\n  }\n  config {\n    backend\n    profile {\n      validatePhoneNumber\n    }\n  }\n}\nfragment ComembersDataFragment on Query {\n  comembers {\n    id\n    firstName\n    lastName\n    keyFob {\n      id\n    }\n  }\n  config {\n    comembers {\n      enabled\n    }\n  }\n}\nfragment MemberSubscriptionsFragment on Query {\n  member(id: $memberId) {\n    id\n    subscriptions {\n      id\n      packageId\n      chargeAccountId\n      cancellationSurvey {\n        title\n        question\n        questionDetails\n        buttonText\n        possibleAnswers {\n          id\n          text\n          detailsPlaceholderText {\n            accessibilityLabel\n            text\n          }\n        }\n      }\n      title\n      subtitle\n      status\n      offerCategory\n      canCancel\n      canPause\n      canReactivate\n      isRenewable\n      subscriptionBenefits\n      renewalType\n      businessSubscriptionOptInDetails {\n        status\n        confirmationStartDate\n        confirmationDaysLeft\n      }\n      priceLabel\n      renewalDate\n      endDate\n      corporateEmailConfirmed\n      confirmationByEmail\n      actions {\n        actionType\n        isPermitted\n        detailText\n      }\n      statusSummary {\n        title\n        body\n      }\n      promotions {\n        buttonText\n        fallbackUrl\n        title\n      }\n      renewsTo {\n        id\n        title\n        priceLabel\n      }\n      cancelMessagingDetails {\n        benefitsTitles\n      }\n      legalText\n      canBeRenewedWithDiscount\n    }\n  }\n  config {\n    system {\n      cancelInsteadOfRenewal\n    }\n  }\n}\nfragment MemberKeyfobDataFragment on Query {\n  member(id: $memberId) {\n    id\n    nfc {\n      id\n      number\n    }\n    keyFob {\n      id\n      number\n    }\n    canAddKeyFob\n    keyfobEassistPreference {\n      canChooseKeyfobMode\n      keyfobEassistUnlockMode\n    }\n    keyfobEassistAvailableOptions {\n      id\n      mode\n      priceText\n    }\n  }\n  config {\n    system {\n      usesBikeKeys\n      nfcType\n      storeUrl\n      userCanRemoveOwnAccessMethod\n    }\n  }\n}\nfragment EassistPreferenceDataFragment on Query {\n  member(id: $memberId) {\n    id\n    keyfobEassistAvailableOptions {\n      id\n      mode\n      priceText\n    }\n    keyfobEassistPreference {\n      canChooseKeyfobMode\n      keyfobEassistUnlockMode\n    }\n  }\n}\nfragment NotificationPreferencesDataFragment on Query {\n  member(id: $memberId) {\n    id\n    shippingAddress {\n      addressLine1\n      addressLine2\n      addressLine3\n      city\n      country\n      region\n      postalCode\n    }\n  }\n  config {\n    profile {\n      showLanguageSelector\n    }\n  }\n}\nfragment AdsContainerDataFragment on Query {\n  member(id: $memberId) {\n    keyFob {\n      id\n    }\n    subscriptions {\n      id\n    }\n  }\n  tcsVars {\n    skipMembershipGifting\n  }\n  config {\n    profile {\n      showHelmetAd\n      showBikeKeyAd\n      bikeKeyPromoCode\n      membershipGiftingAdPath\n    }\n    system {\n      storeUrl\n    }\n  }\n}\nfragment AdditionalUserInformationFragment on Query {\n  member(id: $memberId) {\n    id\n    dateOfBirth\n    pronoun\n    gender\n    hasSubmittedDemographicData\n    subscriptions {\n      id\n    }\n  }\n  config {\n    purchase {\n      postCollectingFields\n    }\n  }\n}\nquery Profile($isAccountHolder: Boolean!, $memberId: String) {\n  ...MemberAlertsFragment\n  ...MemberStatsFragment\n  ...CurrentBalanceWidgetFragment\n  ...YourInformationDataFragment\n  ...ComembersDataFragment @include(if: $isAccountHolder)\n  ...MemberSubscriptionsFragment\n  ...MemberKeyfobDataFragment\n  ...EassistPreferenceDataFragment\n  ...NotificationPreferencesDataFragment\n  ...AdsContainerDataFragment\n  ...AdditionalUserInformationFragment @include(if: $isAccountHolder)\n  member(id: $memberId) @include(if: $isAccountHolder) {\n    firstName\n    migration {\n      status\n    }\n  }\n  config {\n    profile {\n      showCurrentBalance\n    }\n    migration @include(if: $isAccountHolder) {\n      supportsMigration\n    }\n    system {\n      usesSecurityQuestions\n    }\n    membershipHistory {\n      enabled\n    }\n  }\n}"
}
```

The collection of JSON files can then be used as wordlists for fuzzing GraphQL endpoints.

Parameter values are defaulted like so (lists of types are also supported)

| Type    | Value       |
|---------|-------------|
| Int     | 0           |
| Long    | 0           |
| Float   | 0.0         |
| String  | ""          |
| Boolean | false       |
| Other   | {} (Object) |

It's possible to override known parameter names with default values by placing a `defaultParams.json` file in the same directory as the `input-directory`, for example:

```json
{
  "memberId": "1234567890"
}
```

The JSON output for this would then look like the below:

```json
{
  "operationName" : "Profile",
  "variables" : {
    "isAccountHolder" : false,
    "memberId" : "1234567890"
  },
  "query" : "fragment MemberAlertsFragment on Query {\n  member(id: $memberId) {\n    id\n    subscriptions {\n      id\n      endDate\n    }\n    alerts {\n      id\n      name\n      title\n      body\n      color\n      action {\n        text\n        href\n      }\n    }\n  }\n  config {\n    system {\n      giftRedemptionHelpUrl\n    }\n  }\n}\nfragment MemberStatsFragment on Query {\n  member(id: $memberId) {\n    id\n    stats {\n      numberOfRides\n    }\n  }\n}\nfragment CurrentBalanceWidgetFragment on Query {\n  member(id: $memberId) {\n    id\n    currentBalance {\n      balance {\n        amount\n        formatted\n      }\n      nextBillingDateMs\n    }\n  }\n}\nfragment YourInformationDataFragment on Query {\n  member(id: $memberId) {\n    id\n    firstName\n    lastName\n    email\n    phoneNumber\n    phoneNumberIsVerified\n    emailIsVerified\n    shippingAddress {\n      addressLine1\n      addressLine2\n      addressLine3\n      city\n      country\n      region\n      postalCode\n    }\n    memberCanAccessAccount\n  }\n  config {\n    backend\n    profile {\n      validatePhoneNumber\n    }\n  }\n}\nfragment ComembersDataFragment on Query {\n  comembers {\n    id\n    firstName\n    lastName\n    keyFob {\n      id\n    }\n  }\n  config {\n    comembers {\n      enabled\n    }\n  }\n}\nfragment MemberSubscriptionsFragment on Query {\n  member(id: $memberId) {\n    id\n    subscriptions {\n      id\n      packageId\n      chargeAccountId\n      cancellationSurvey {\n        title\n        question\n        questionDetails\n        buttonText\n        possibleAnswers {\n          id\n          text\n          detailsPlaceholderText {\n            accessibilityLabel\n            text\n          }\n        }\n      }\n      title\n      subtitle\n      status\n      offerCategory\n      canCancel\n      canPause\n      canReactivate\n      isRenewable\n      subscriptionBenefits\n      renewalType\n      businessSubscriptionOptInDetails {\n        status\n        confirmationStartDate\n        confirmationDaysLeft\n      }\n      priceLabel\n      renewalDate\n      endDate\n      corporateEmailConfirmed\n      confirmationByEmail\n      actions {\n        actionType\n        isPermitted\n        detailText\n      }\n      statusSummary {\n        title\n        body\n      }\n      promotions {\n        buttonText\n        fallbackUrl\n        title\n      }\n      renewsTo {\n        id\n        title\n        priceLabel\n      }\n      cancelMessagingDetails {\n        benefitsTitles\n      }\n      legalText\n      canBeRenewedWithDiscount\n    }\n  }\n  config {\n    system {\n      cancelInsteadOfRenewal\n    }\n  }\n}\nfragment MemberKeyfobDataFragment on Query {\n  member(id: $memberId) {\n    id\n    nfc {\n      id\n      number\n    }\n    keyFob {\n      id\n      number\n    }\n    canAddKeyFob\n    keyfobEassistPreference {\n      canChooseKeyfobMode\n      keyfobEassistUnlockMode\n    }\n    keyfobEassistAvailableOptions {\n      id\n      mode\n      priceText\n    }\n  }\n  config {\n    system {\n      usesBikeKeys\n      nfcType\n      storeUrl\n      userCanRemoveOwnAccessMethod\n    }\n  }\n}\nfragment EassistPreferenceDataFragment on Query {\n  member(id: $memberId) {\n    id\n    keyfobEassistAvailableOptions {\n      id\n      mode\n      priceText\n    }\n    keyfobEassistPreference {\n      canChooseKeyfobMode\n      keyfobEassistUnlockMode\n    }\n  }\n}\nfragment NotificationPreferencesDataFragment on Query {\n  member(id: $memberId) {\n    id\n    shippingAddress {\n      addressLine1\n      addressLine2\n      addressLine3\n      city\n      country\n      region\n      postalCode\n    }\n  }\n  config {\n    profile {\n      showLanguageSelector\n    }\n  }\n}\nfragment AdsContainerDataFragment on Query {\n  member(id: $memberId) {\n    keyFob {\n      id\n    }\n    subscriptions {\n      id\n    }\n  }\n  tcsVars {\n    skipMembershipGifting\n  }\n  config {\n    profile {\n      showHelmetAd\n      showBikeKeyAd\n      bikeKeyPromoCode\n      membershipGiftingAdPath\n    }\n    system {\n      storeUrl\n    }\n  }\n}\nfragment AdditionalUserInformationFragment on Query {\n  member(id: $memberId) {\n    id\n    dateOfBirth\n    pronoun\n    gender\n    hasSubmittedDemographicData\n    subscriptions {\n      id\n    }\n  }\n  config {\n    purchase {\n      postCollectingFields\n    }\n  }\n}\nquery Profile($isAccountHolder: Boolean!, $memberId: String) {\n  ...MemberAlertsFragment\n  ...MemberStatsFragment\n  ...CurrentBalanceWidgetFragment\n  ...YourInformationDataFragment\n  ...ComembersDataFragment @include(if: $isAccountHolder)\n  ...MemberSubscriptionsFragment\n  ...MemberKeyfobDataFragment\n  ...EassistPreferenceDataFragment\n  ...NotificationPreferencesDataFragment\n  ...AdsContainerDataFragment\n  ...AdditionalUserInformationFragment @include(if: $isAccountHolder)\n  member(id: $memberId) @include(if: $isAccountHolder) {\n    firstName\n    migration {\n      status\n    }\n  }\n  config {\n    profile {\n      showCurrentBalance\n    }\n    migration @include(if: $isAccountHolder) {\n      supportsMigration\n    }\n    system {\n      usesSecurityQuestions\n    }\n    membershipHistory {\n      enabled\n    }\n  }\n}"
}
```

Example usage (script/docker):

```bash
 ./run.sh
Usage: run.sh -i <input_directory> -o <output_directory> [-m <output_mode> (json, graphql or all)]
```

Example usage (java):

```bash
java -jar graphql-extractor-1.0.0.jar --input-directory=<input_directory> --output-directory=<output_directory> [--output-mode=<output_mode> (json, graphql or all)]
```