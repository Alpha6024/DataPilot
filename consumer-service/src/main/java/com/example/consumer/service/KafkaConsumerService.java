package com.example.consumer.service;

import com.example.consumer.model.ExcelDocument;
import com.example.consumer.model.JobStatus;
import com.example.consumer.model.UploadMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.embedding.Embedding;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final EmbeddingService embeddingService;
    private final QdrantService qdrantService;
    private final RedisService redisService;

    public KafkaConsumerService(ObjectMapper objectMapper,
                                EmbeddingService embeddingService,
                                QdrantService qdrantService,
                                RedisService redisService) {
        this.objectMapper = objectMapper;
        this.embeddingService = embeddingService;
        this.qdrantService = qdrantService;
        this.redisService = redisService;
    }

    @KafkaListener(topics = "excel-upload", groupId = "excel-group", concurrency = "1")
    public void consume(String json) {
        UploadMessage message = null;
        try {
            message = objectMapper.readValue(json, UploadMessage.class);
            qdrantService.ensureCollection(message.getCollectionName());
            if (qdrantService.hashExists(message.getContentHash(), message.getCollectionName())) {
                return;
            }
            Embedding embedding = embeddingService.createEmbedding(message.getContent());
            qdrantService.saveDocument(
                    new ExcelDocument(UUID.randomUUID(), message.getContent(), message.getContentHash(), embedding),
                    message.getCollectionName()
            );
            redisService.incrementDone(message.getJobId());
            checkAndComplete(message.getJobId());
        } catch (Exception e) {
            e.printStackTrace();
            if (message != null) {
                redisService.incrementFailed(message.getJobId());
                checkAndComplete(message.getJobId());
            }
        }
    }

    private void checkAndComplete(String jobId) {
        JobStatus status = redisService.getJobStatus(jobId);
        if (status != null && (status.getDone() + status.getFailed()) >= status.getTotal()) {
            redisService.completeJob(jobId);
        }
    }
}
