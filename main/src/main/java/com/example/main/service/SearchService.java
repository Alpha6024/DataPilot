package com.example.main.service;

import dev.langchain4j.data.embedding.Embedding;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points.SearchPoints;
import io.qdrant.client.grpc.Points.ScoredPoint;
import static io.qdrant.client.WithPayloadSelectorFactory.enable;


import org.springframework.stereotype.Service; 
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.LinkedHashSet;

@Service
public class SearchService {
    private final EmbeddingService embeddingService;
    private final QdrantClient qdrantclient;
    
    public SearchService(EmbeddingService embeddingService,QdrantClient qdrantclient){
        this.embeddingService=embeddingService;
        this.qdrantclient=qdrantclient;
    }
    public List<String> search(String que, String collectionName){
        try{
            Embedding embedding=embeddingService.createEmbedding(que);
            List<Float> query=embedding.vectorAsList();
            List<ScoredPoint> results = qdrantclient.searchAsync(
                    SearchPoints.newBuilder()
                            .setCollectionName(collectionName)
                            .addAllVector(query)
                            .setLimit(5)
                            .setWithPayload(enable(true))
                            .build()
            ).get();
            Set<String> unique = new LinkedHashSet<>();
            for(ScoredPoint data:results){
                String text=data.getPayloadMap().get("content").getStringValue();
                unique.add(text);
            }
            return new ArrayList<>(unique);

        }catch(Exception e){
            System.out.println("Error in Search!");
            e.printStackTrace();
            return new ArrayList<>();
        }
        
    }
}
