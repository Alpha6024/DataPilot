package com.example.main.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.main.model.UploadMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaProducerService {
    private static final String TOPIC = "excel-upload";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper=objectMapper;
    }
    public void sendMessage(UploadMessage message) {
       try {

            String json =
                    objectMapper.writeValueAsString(message);

            kafkaTemplate.send(TOPIC, json);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}