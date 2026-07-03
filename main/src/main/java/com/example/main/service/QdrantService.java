package com.example.main.service;

import com.example.main.model.ExcelDocument;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.VectorFactory.vector;
import static io.qdrant.client.VectorsFactory.vectors;
import static io.qdrant.client.ValueFactory.value;
import io.qdrant.client.grpc.Points.PointStruct;
import java.util.List;
import java.util.Map;
import io.qdrant.client.grpc.Points;

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
            boolean exists = qdrantClient.listCollectionsAsync().get().contains(COLLECTION_NAME);
            if (exists) {
                System.out.println("Collection already exists, skipping creation.");
                return;
            }
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
    try {
        System.out.println(document.getContent());
        PointStruct point =
                PointStruct.newBuilder()
                        .setId(id(document.getId()))
                        .setVectors(vectors(document.getEmbedding().vectorAsList()))
                        .putAllPayload(
                                Map.of(
                                        "content",
                                        value(document.getContent())
                                )
                        )
                        .build();
        qdrantClient
                .upsertAsync(
                        COLLECTION_NAME,
                        List.of(point)
                )
                .get();
        System.out.println("✅ Stored : " + document.getId());
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}