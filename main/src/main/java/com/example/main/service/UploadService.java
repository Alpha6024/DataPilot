package com.example.main.service;
import com.example.main.model.ExcelDocument;
import dev.langchain4j.data.embedding.Embedding;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UploadService {
    private final ExcelReaderService excelReaderService;
    private final EmbeddingService embeddingService;
    public UploadService(ExcelReaderService excelReaderService,
                         EmbeddingService embeddingService) {
        this.excelReaderService = excelReaderService;
        this.embeddingService = embeddingService;
    }
    public String upload(MultipartFile file) {
        List<String> documents = excelReaderService.readExcel(file);
        List<ExcelDocument> excelDocuments = new ArrayList<>();
        for (String doc : documents) {
            Embedding embedding = embeddingService.generateEmbed(doc);
            ExcelDocument excelDocument = new ExcelDocument(
                    UUID.randomUUID().toString(),
                    doc,
                    embedding
            );
            excelDocuments.add(excelDocument);
        }
        excelDocuments.forEach(System.out::println);
        return "Success";
    }
}