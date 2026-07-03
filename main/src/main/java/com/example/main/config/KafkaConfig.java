package com.example.main.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic excelUploadTopic(){
        return new NewTopic("excel-upload", 1, (short) 1);
    }
}
