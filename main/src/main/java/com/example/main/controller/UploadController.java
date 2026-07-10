package com.example.main.controller;

import com.example.main.model.ChatResponse;
import com.example.main.model.JobStatus;
import com.example.main.service.RagService;
import com.example.main.service.RedisService;
import com.example.main.service.UploadService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class UploadController {

    private final UploadService uploadService;
    private final RagService ragService;
    private final RedisService redisService;

    public UploadController(UploadService uploadService, RagService ragService, RedisService redisService) {
        this.uploadService = uploadService;
        this.ragService = ragService;
        this.redisService = redisService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        return uploadService.upload(file);
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestParam("query") String query, @RequestParam("collectionName") String collectionName) {
        return ragService.ask(query, collectionName);
    }

    @GetMapping("/status/{jobId}")
    public JobStatus getStatus(@PathVariable String jobId) {
        return redisService.getJobStatus(jobId);
    }
}
