package com.example.main.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class OllamaConfig {

    @Value("${ollama.base-url}")
    private String baseUrl;

    @Value("${ollama.embedding.model}")
    private String embeddingModel;

    @Value("${ollama.chat.model}")
    private String chatModel;

    @Bean
    public EmbeddingModel embeddingModel() {

        return OllamaEmbeddingModel.builder()
                .baseUrl(baseUrl)
                .modelName(embeddingModel)
                .build();

    }

}