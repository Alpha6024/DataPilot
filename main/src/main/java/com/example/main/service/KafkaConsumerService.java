package com.example.main.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class KafkaConsumerService {
    private final UploadService uploadService;
    public KafkaConsumerService(UploadService uploadService) {
        this.uploadService = uploadService;
    }
    @KafkaListener(topics = "excel-upload", groupId = "excel-group")
    public void consume(String message) {
        System.out.println("Message Received from Kafka");
        System.out.println("File : " + message);
        Path filePath = Paths.get(message);
        uploadService.processFile(filePath);
    }
}