package com.example.consumer.model;

import dev.langchain4j.data.embedding.Embedding;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ExcelDocument {
    private UUID id;
    private String content;
    private String code;
    private Embedding embedding;
}
