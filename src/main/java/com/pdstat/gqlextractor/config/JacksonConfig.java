package com.pdstat.gqlextractor.config;

import com.pdstat.gqlextractor.deserialiser.DocumentDeserialiser;
import graphql.language.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Document.class, new DocumentDeserialiser());
        objectMapper.registerModule(module);
        return objectMapper;
    }

}
