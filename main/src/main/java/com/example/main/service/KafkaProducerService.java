package com.example.main.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private static final String TOPIC = "excel-upload";
    private final KafkaTemplate<String, String> kafkaTemplate;
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void sendMessage(String message) {
        kafkaTemplate.send(TOPIC, message);
        System.out.println("Message Sent to Kafka");
        System.out.println("Topic : " + TOPIC);
        System.out.println("Message : " + message);
    }
}