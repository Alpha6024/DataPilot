package com.example.main.service;

import com.example.main.model.ExcelDocument;
import dev.langchain4j.data.embedding.Embedding;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class UploadService {

    private final ExcelReaderService excelReaderService;
    private final EmbeddingService embeddingService;
    private final QdrantService qdrantService;

    public UploadService(
            ExcelReaderService excelReaderService,
            EmbeddingService embeddingService,
            QdrantService qdrantService
    ) {
        this.excelReaderService = excelReaderService;
        this.embeddingService = embeddingService;
        this.qdrantService = qdrantService;
    }

    public String upload(MultipartFile file) {

        List<String> documents = excelReaderService.readExcel(file);

        for (String doc : documents) {

            Embedding embedding = embeddingService.createEmbedding(doc);

            ExcelDocument excelDocument = new ExcelDocument(
                    UUID.randomUUID().toString(),
                    doc,
                    embedding
            );

            qdrantService.saveDocument(excelDocument);

            System.out.println(excelDocument);
        }

        return "Upload Successful";
    }
}