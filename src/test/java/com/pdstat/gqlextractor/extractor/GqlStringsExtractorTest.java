package com.pdstat.gqlextractor.extractor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class GqlStringsExtractorTest {

    private static final String JS_FILE = "Object.freeze(Object.defineProperties(e,{raw:{value:Object.freeze(t)}}))}([\"\\n  query GetUserUUID {\\n    user {\\n      uuid\\n    }\\n  }\\n\"]))),l=()=>{var e;const{data:t,loading:n,error:i}=(0,r.aM)(o);return n||i||!t?\"\":(null===(e=t.user)||void 0===e?void 0:e.uuid)||\"\"}},52473:(e,t,n)=>{\"use strict\";n.d(t,{K:()=>a});var i=n(24671),r=n.n(i);function a(){const e=r().navigator;return e?null!=e.languages?e.languages[0]:e.language||\"en\":\"en\"}},83857:(e,t,n)=>{\"use strict\";n.d(t,{Pz:()=>o,Xk:()=>a});var i=n(35514),r=n(39371);function a(e){return{key:e,namespace:i.kp}}function o(e){return{key:e,namespace:r.Z.NAMESPACE}}},1691:(e,t,n)=>{\"use strict\";n.d(t,{g:()=>_8,Z:()=>D8});var i=n(36151),r=n(88712);const\n" +
            "        function zt(e) {\n" +
            "          const t = u(u({}, f), e);\n" +
            "          return i.aM(Xt, t);\n" +
            "        }\n" +
            "        function Qt(e) {\n" +
            "          const t = u(u({}, f), e);\n" +
            "          return s.D(Zt, t);\n" +
            "        }\n" +
            "        const Xt = o.Ps`\n" +
            "    query NotificationPreferencesData($memberId: String) {\n" +
            "  ...NotificationPreferencesDataFragment\n" +
            "}\n" +
            "    ${W}`;\n" +
            "        function zt(e) {\n" +
            "          const t = u(u({}, f), e);\n" +
            "          return i.aM(Xt, t);\n" +
            "        }\n" +
            "        const en = o.Ps`\n" +
            "    query Profile($isAccountHolder: Boolean!, $memberId: String) {\n" +
            "  ...MemberAlertsFragment\n" +
            "  ...MemberStatsFragment\n" +
            "  ...CurrentBalanceWidgetFragment\n" +
            "  ...YourInformationDataFragment\n" +
            "  ...ComembersDataFragment @include(if: $isAccountHolder)\n" +
            "  ...MemberSubscriptionsFragment\n" +
            "  ...MemberKeyfobDataFragment\n" +
            "  ...EassistPreferenceDataFragment\n" +
            "  ...NotificationPreferencesDataFragment\n" +
            "  ...AdsContainerDataFragment\n" +
            "  ...AdditionalUserInformationFragment @include(if: $isAccountHolder)\n" +
            "  member(id: $memberId) @include(if: $isAccountHolder) {\n" +
            "    firstName\n" +
            "    migration {\n" +
            "      status\n" +
            "    }\n" +
            "  }\n" +
            "  config {\n" +
            "    profile {\n" +
            "      showCurrentBalance\n" +
            "    }\n" +
            "    migration @include(if: $isAccountHolder) {\n" +
            "      supportsMigration\n" +
            "    }\n" +
            "    system {\n" +
            "      usesSecurityQuestions\n" +
            "    }\n" +
            "    membershipHistory {\n" +
            "      enabled\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "    ${$}\n" +
            "${V}\n" +
            "${v}\n" +
            "${j}\n" +
            "${k}\n" +
            "${F}\n" +
            "${x}\n" +
            "${q}\n" +
            "${W}\n" +
            "${B}\n" +
            "${N}`;\n" +
            "        function tn(e) {\n" +
            "          const t = u(u({}, f), e);\n" +
            "          return i.aM(en, t);\n" +
            "        }";

    @Test
    void testExtract() {
        GqlStringsExtractor gqlStringsExtractor = new GqlStringsExtractor();
        Set<String> gqlStrings = gqlStringsExtractor.extract(JS_FILE);
        Assertions.assertFalse(gqlStrings.isEmpty());
        Assertions.assertEquals(3, gqlStrings.size());
    }
}
