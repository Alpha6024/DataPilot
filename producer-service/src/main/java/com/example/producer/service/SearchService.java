package com.example.producer.service;

import dev.langchain4j.data.embedding.Embedding;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points.SearchPoints;
import io.qdrant.client.grpc.Points.ScoredPoint;
import org.springframework.stereotype.Service;

import java.util.*;

import static io.qdrant.client.WithPayloadSelectorFactory.enable;

@Service
public class SearchService {

    private final EmbeddingService embeddingService;
    private final QdrantClient qdrantClient;

    public SearchService(EmbeddingService embeddingService, QdrantClient qdrantClient) {
        this.embeddingService = embeddingService;
        this.qdrantClient = qdrantClient;
    }

    public List<String> search(String query, String collectionName) {
        try {
            Embedding embedding = embeddingService.createEmbedding(query);
            List<ScoredPoint> results = qdrantClient.searchAsync(
                    SearchPoints.newBuilder()
                            .setCollectionName(collectionName)
                            .addAllVector(embedding.vectorAsList())
                            .setLimit(5)
                            .setWithPayload(enable(true))
                            .build()
            ).get();
            Set<String> unique = new LinkedHashSet<>();
            for (ScoredPoint p : results)
                unique.add(p.getPayloadMap().get("content").getStringValue());
            return new ArrayList<>(unique);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
