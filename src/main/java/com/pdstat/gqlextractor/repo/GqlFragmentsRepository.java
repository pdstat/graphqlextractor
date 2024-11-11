package com.pdstat.gqlextractor.repo;

import com.pdstat.gqlextractor.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class GqlFragmentsRepository {

    private static final Logger logger = LoggerFactory.getLogger(GqlFragmentsRepository.class);
    private final Map<String, String> gqlFragments = new HashMap<>();

    public void addGqlFragment(String fragment) {
        String line1 = fragment.split("\n")[0];
        if (line1.contains(Constants.Gql.FRAGMENT) &&
                !(fragment.contains(Constants.Gql.QUERY) ||
                        fragment.contains(Constants.Gql.MUTATION) ||
                        fragment.contains(Constants.Gql.SUBSCRIPTION))) {
            line1 = line1.split(" on ")[0].strip();
            String fragmentName = line1.split(" ")[1];
            if (!gqlFragments.containsKey(fragmentName)) {
                logger.info("Adding fragment: {}", fragmentName);
                gqlFragments.put(fragmentName, fragment);
            }
        }
    }

    public String getGqlFragment(String fragmentName) {
        return gqlFragments.get(fragmentName);
    }

}
