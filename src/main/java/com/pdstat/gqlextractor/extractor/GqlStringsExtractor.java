package com.pdstat.gqlextractor.extractor;

import com.pdstat.gqlextractor.Constants;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GqlStringsExtractor {

    private static final Pattern GQL_PATTERN = Pattern.compile("`\\n([\\s\\S]*?)`");

    public Set<String> extract(String gql) {
        Matcher matcher = GQL_PATTERN.matcher(gql);
        Set<String> matches = new HashSet<>();
        while (matcher.find()) {
            String match = matcher.group(1);
            String line1 = match.split("\n")[0];
            if (line1.contains(Constants.Gql.FRAGMENT)||line1.contains(Constants.Gql.QUERY)||
                    line1.contains(Constants.Gql.MUTATION)||
                    line1.contains(Constants.Gql.SUBSCRIPTION)) {
                match = match.replaceAll("\\$\\{[a-zA-Z$]{1}}","");
                match = match.strip();
                matches.add(match);
            }
        }
        return matches;
    }

}
