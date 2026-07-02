package com.example.main.model;

import dev.langchain4j.data.embedding.Embedding;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExcelDocument {

    private String id;

    private String content;

    private Embedding embedding;

}