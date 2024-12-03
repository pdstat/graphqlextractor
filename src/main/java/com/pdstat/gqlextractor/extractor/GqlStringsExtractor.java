package com.pdstat.gqlextractor.extractor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdstat.gqlextractor.Constants;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GqlStringsExtractor {

    private static final Pattern GQL_PATTERN = Pattern.compile("([`\"'])([\\s\\S]*?)\\1");

    public Set<String> extract(String gql) {
        Matcher matcher = GQL_PATTERN.matcher(gql);
        Set<String> matches = new HashSet<>();
        while (matcher.find()) {
            String match = matcher.group(2);
            if (isGraphQLString(match)) {
                match = match.replaceAll("\\$\\{[a-zA-Z$]{1}}","").replaceAll("\\\\n"," ");
                match = match.strip();
                matches.add(match);
            }
        }
        return matches;
    }

    private boolean isGraphQLString(String match) {
        if ((match.contains(Constants.Gql.FRAGMENT) || match.contains(Constants.Gql.QUERY) ||
                match.contains(Constants.Gql.MUTATION) ||
                match.contains(Constants.Gql.SUBSCRIPTION)) && !isJavaScript(match)) {
            if (match.contains("{") && match.contains("}")) {
                int leftBraceCount = match.length() - match.replace("{", "").length();
                int rightBraceCount = match.length() - match.replace("}", "").length();
                return leftBraceCount == rightBraceCount;
            }

        }
        return false;
    }

    private boolean isJavaScript(String match) {
        return match.contains("function") || match.contains("=>") || match.contains("return") || match.contains("!=")
                || match.contains("==") || match.contains(">=") || match.contains("<=")
                || match.contains("&&") || match.contains("||") || match.contains("constructor(");
    }

}
