package com.example.main.service;

import com.example.main.model.ExcelDocument;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
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
    private static final int VECTOR_SIZE = 768;
    private final QdrantClient qdrantClient;
    public QdrantService(QdrantClient qdrantClient) {
        this.qdrantClient = qdrantClient;
    }
    public void ensureCollection(String collectionName) {
        try {
            boolean exists = qdrantClient.listCollectionsAsync().get().contains(collectionName);
            if (exists) return;
            qdrantClient.createCollectionAsync(
                    collectionName,
                    Collections.VectorParams.newBuilder()
                            .setSize(VECTOR_SIZE)
                            .setDistance(Collections.Distance.Cosine)
                            .build()
            ).get();
            System.out.println("✅ Collection Created: " + collectionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void saveDocument(ExcelDocument document, String collectionName) {
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
                            collectionName,
                            List.of(point)
                    )
                    .get();
            System.out.println("✅ Stored : " + document.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}