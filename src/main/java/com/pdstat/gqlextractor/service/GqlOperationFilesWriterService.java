package com.pdstat.gqlextractor.service;

import com.pdstat.gqlextractor.Constants;
import com.pdstat.gqlextractor.repo.GqlOperationsRepository;
import graphql.language.AstPrinter;
import graphql.language.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class GqlOperationFilesWriterService {

    private static final Logger logger = LoggerFactory.getLogger(GqlOperationFilesWriterService.class);

    private final GqlOperationsRepository gqlOperationsRepository;

    public GqlOperationFilesWriterService(GqlOperationsRepository gqlOperationsRepository) {
        this.gqlOperationsRepository = gqlOperationsRepository;
    }

    public void writeOperationFiles(String outputDirectory)  {
        for (Map.Entry<String, Document> entry : gqlOperationsRepository.getGqlOperations().entrySet()) {
            String gqlFileName = outputDirectory + "/" + Constants.Output.DIRECTORIES.OPERATIONS + "/"
                    + entry.getKey() + ".graphql";
            Path gqlFilePath = Paths.get(gqlFileName);
            logger.info("Writing operation file: {}", gqlFilePath.getFileName());
            try {
                Files.createDirectories(gqlFilePath.getParent());
                Files.write(gqlFilePath, AstPrinter.printAst(entry.getValue()).getBytes());
            } catch (Exception e) {
                logger.error("Error writing operation file: {}", gqlFilePath.getFileName(), e);
            }
        }

    }

}
