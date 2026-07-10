package com.example.consumer.service;

import com.example.consumer.model.ExcelDocument;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Points.PointStruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveDocument(ExcelDocument document, String collectionName) {
        try {
            PointStruct point = PointStruct.newBuilder()
                    .setId(id(document.getId()))
                    .setVectors(vectors(document.getEmbedding().vectorAsList()))
                    .putAllPayload(Map.of(
                            "content", value(document.getContent()),
                            "code", value(document.getCode())
                    ))
                    .build();
            qdrantClient.upsertAsync(collectionName, List.of(point)).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
