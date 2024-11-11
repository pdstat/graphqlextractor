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
public class GqlQueryRepository {

    private static final Logger logger = LoggerFactory.getLogger(GqlQueryRepository.class);
    private final GqlFragmentsRepository gqlFragmentsRepository;

    public GqlQueryRepository(GqlFragmentsRepository gqlFragmentsRepository) {
        this.gqlFragmentsRepository = gqlFragmentsRepository;
    }

    private Map<String, String> gqlQueries = new HashMap<>();

    public void addGqlQuery(String query) {
        if (query.contains(Constants.Gql.QUERY)) {
            StringBuilder sb = new StringBuilder();
            Set<String> queryFragments = new HashSet<>();
            String lines[] = query.split("\n");
            String queryName = null;
            for (String line : lines) {
                if (line.contains("...") && !line.contains(" on ")) {
                    String fragmentName = line.split("\\.\\.\\.")[1].strip();
                    if (fragmentName.contains(" @")) {
                        fragmentName = fragmentName.split("@")[0].strip();
                    }
                    if (!queryFragments.contains(fragmentName)) {
                        String fragment = gqlFragmentsRepository.getGqlFragment(fragmentName);
                        if (fragment != null) {
                            sb.append(fragment).append("\n");
                            queryFragments.add(fragmentName);
                        }
                    }
                }

                if (line.contains(Constants.Gql.QUERY)) {
                    queryName = line.strip().split(" ")[1];
                    if (queryName.contains("(")) {
                        queryName = queryName.split("\\(")[0];
                    }
                }
            }
            sb.append(query);
            if (!gqlQueries.containsKey(queryName)) {
                logger.info("Adding query: {}", queryName);
                gqlQueries.put(queryName, sb.toString());
            }
        }
    }


    public Map<String, String> getGqlQueries() {
        return gqlQueries;
    }

}
