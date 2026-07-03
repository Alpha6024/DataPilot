package com.example.main.service;

import com.example.main.model.ExcelDocument;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class QdrantService {

    private static final String COLLECTION_NAME = "excel_documents";
    private static final int VECTOR_SIZE = 768;

    private final QdrantClient qdrantClient;

    public QdrantService(QdrantClient qdrantClient) {
        this.qdrantClient = qdrantClient;
    }

    @PostConstruct
    public void createCollection() {

        try {

            qdrantClient.createCollectionAsync(
                    COLLECTION_NAME,
                    Collections.VectorParams.newBuilder()
                            .setSize(VECTOR_SIZE)
                            .setDistance(Collections.Distance.Cosine)
                            .build()
            ).get();

            System.out.println("✅ Collection Created Successfully");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Collection already exists or could not be created.");
        }
    }

    public void saveDocument(ExcelDocument document) {

        
    }
}