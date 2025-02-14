package com.pdstat.gqlextractor.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    public String readResourceFileContent(Resource resource) throws IOException {
        return String.join("\n", Files.readAllLines(Paths.get(resource.getURI())));
    }

}
