package com.example.producer.service;

import io.qdrant.client.ConditionFactory;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Common.Filter;
import io.qdrant.client.grpc.Points.ScrollPoints;
import io.qdrant.client.grpc.Points.ScrollResponse;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class QdrantService {

    private final QdrantClient qdrantClient;

    public QdrantService(QdrantClient qdrantClient) {
        this.qdrantClient = qdrantClient;
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
