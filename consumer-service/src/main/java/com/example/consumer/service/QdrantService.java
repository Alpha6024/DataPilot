package com.example.consumer.service;

import com.example.consumer.model.ExcelDocument;
import io.qdrant.client.ConditionFactory;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Common.Filter;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.Points.ScrollPoints;
import io.qdrant.client.grpc.Points.ScrollResponse;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
                            "content_hash", value(document.getContentHash())
                    ))
                    .build();
            qdrantClient.upsertAsync(collectionName, List.of(point)).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hashExists(String hash, String collectionName) {
        try {
            Filter filter = Filter.newBuilder()
                    .addMust(ConditionFactory.matchKeyword("content_hash", hash))
                    .build();
            ScrollResponse response = qdrantClient.scrollAsync(
                    ScrollPoints.newBuilder()
                            .setCollectionName(collectionName)
                            .setFilter(filter)
                            .setLimit(1)
                            .build()
            ).get();
            return !response.getResultList().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public Set<String> findExistingHashes(List<String> hashes, String collectionName) {
        Set<String> found = new HashSet<>();
        try {
            boolean exists = qdrantClient.listCollectionsAsync().get().contains(collectionName);
            if (!exists) return found;
            for (String hash : hashes) {
                Filter filter = Filter.newBuilder()
                        .addMust(ConditionFactory.matchKeyword("content_hash", hash))
                        .build();
                ScrollResponse response = qdrantClient.scrollAsync(
                        ScrollPoints.newBuilder()
                                .setCollectionName(collectionName)
                                .setFilter(filter)
                                .setLimit(1)
                                .build()
                ).get();
                if (!response.getResultList().isEmpty()) found.add(hash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return found;
    }
}
