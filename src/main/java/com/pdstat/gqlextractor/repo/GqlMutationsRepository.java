package com.pdstat.gqlextractor.repo;

import com.pdstat.gqlextractor.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
public class GqlMutationsRepository {

    private static final Logger logger = LoggerFactory.getLogger(GqlMutationsRepository.class);
    private final GqlFragmentsRepository gqlFragmentsRepository;

    private Map<String, String> gqlMutations = new HashMap<>();

    public GqlMutationsRepository(GqlFragmentsRepository gqlFragmentsRepository) {
        this.gqlFragmentsRepository = gqlFragmentsRepository;
    }

    public void addGqlMutation(String query) {
        if (query.contains(Constants.Gql.MUTATION)) {
            StringBuilder sb = new StringBuilder();
            Set<String> mutationFragments = new HashSet<>();
            String lines[] = query.split("\n");
            String mutationName = null;
            for (String line : lines) {
                if (line.contains("...") && !line.contains(" on ")) {
                    String fragmentName = line.split("\\.\\.\\.")[1].strip();
                    if (fragmentName.contains(" @")) {
                        fragmentName = fragmentName.split("@")[0].strip();
                    }
                    if (!mutationFragments.contains(fragmentName)) {
                        String fragment = gqlFragmentsRepository.getGqlFragment(fragmentName);
                        if (fragment != null) {
                            sb.append(fragment).append("\n");
                            mutationFragments.add(fragmentName);
                        }
                    }
                }

                if (line.contains(Constants.Gql.MUTATION)) {
                    mutationName = line.strip().split(" ")[1];
                    if (mutationName.contains("(")) {
                        mutationName = mutationName.split("\\(")[0];
                    }
                }
            }
            sb.append(query);
            if (!gqlMutations.containsKey(mutationName)) {
                logger.info("Adding mutation: {}", mutationName);
                gqlMutations.put(mutationName, sb.toString());
            }
        }
    }

    public Map<String, String> getGqlMutations() {
        return gqlMutations;
    }
}
