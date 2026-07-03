package com.example.main.service;

import com.example.main.model.ExcelDocument;
import dev.langchain4j.data.embedding.Embedding;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class UploadService {
    private final ExcelReaderService excelReaderService;
    private final EmbeddingService embeddingService;
    private final QdrantService qdrantService;
    private static final String UPLOAD_DIR =
            System.getProperty("user.dir") + "/uploads/";
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
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            Files.copy(
                    file.getInputStream(),
                    filePath,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
            return "File uploaded successfully. Processing started.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Upload Failed";
        }
    }
    public void processFile(Path filePath) {
        try {
            List<String> documents =
                    excelReaderService.readExcel(Files.newInputStream(filePath));
            for (String doc : documents) {
                Embedding embedding = embeddingService.createEmbedding(doc);
                ExcelDocument excelDocument = new ExcelDocument(
                        UUID.randomUUID(),
                        doc,
                        embedding
                );
                
                qdrantService.saveDocument(excelDocument);
                System.out.println(excelDocument);
            }
            Files.deleteIfExists(filePath);
            System.out.println("File Processed and Deleted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}