package com.example.producer.controller;

import com.example.producer.model.ChatResponse;
import com.example.producer.model.JobStatus;
import com.example.producer.service.KafkaAdminService;
import com.example.producer.service.RagService;
import com.example.producer.service.RedisService;
import com.example.producer.service.UploadService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UploadController {

    private final UploadService uploadService;
    private final RagService ragService;
    private final RedisService redisService;
    private final KafkaAdminService kafkaAdminService;

    public UploadController(UploadService uploadService, RagService ragService,
                            RedisService redisService, KafkaAdminService kafkaAdminService) {
        this.uploadService = uploadService;
        this.ragService = ragService;
        this.redisService = redisService;
        this.kafkaAdminService = kafkaAdminService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        return uploadService.upload(file);
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestParam("query") String query,
                             @RequestParam("collectionName") String collectionName) {
        return ragService.ask(query, collectionName);
    }

    @GetMapping("/status/{jobId}")
    public JobStatus getStatus(@PathVariable String jobId) {
        return redisService.getJobStatus(jobId);
    }

    @GetMapping("/kafka/info")
    public Map<String, Object> getKafkaInfo() {
        return kafkaAdminService.getTopicInfo();
    }
}
