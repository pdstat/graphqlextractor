package com.pdstat.gqlextractor.service;

import com.pdstat.gqlextractor.repo.GqlFieldRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class GqlFieldWordListWriterService {

    private static final Logger logger = LoggerFactory.getLogger(GqlFieldWordListWriterService.class);

    private final GqlFieldRepository gqlFieldRepository;

    public GqlFieldWordListWriterService(GqlFieldRepository gqlFieldRepository) {
        this.gqlFieldRepository = gqlFieldRepository;
    }

    public void writeFieldsFile(String outputDirectory) {
        logger.info("Writing GQL fields file");
        String fieldsFileName = outputDirectory + "/graphql-fields.txt";
        Path fieldsFilePath = Paths.get(fieldsFileName);
        List<String> gqlFields = this.gqlFieldRepository.getGqlFields();
        try (BufferedWriter writer = Files.newBufferedWriter(fieldsFilePath)) {
            for (String field : gqlFields) {
                writer.write(field);
                writer.newLine();
            }
        } catch (IOException e) {
            logger.error("Error writing fields file: {}", fieldsFileName, e);
        }

    }

}
