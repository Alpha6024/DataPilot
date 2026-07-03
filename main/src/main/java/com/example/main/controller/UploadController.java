package com.example.main.controller;

import com.example.main.service.UploadService;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.example.main.service.SearchService;
import com.example.main.model.ChatResponse;
import com.example.main.service.AiService;
import com.example.main.service.RagService;
import com.example.main.service.KafkaProducerService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api")
public class UploadController {

    private final UploadService uploadService;
    private final SearchService searchService;
    private final AiService aiService;
    private final RagService ragService;
    private final KafkaProducerService kafkaProducerService;
   

    public UploadController(UploadService uploadService,SearchService searchService,AiService aiService,RagService ragService,KafkaProducerService kafkaProducerService) {
        this.uploadService = uploadService;
        this.searchService = searchService;
        this.aiService=aiService;
        this.ragService=ragService;
        this.kafkaProducerService=kafkaProducerService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        Path uploadDir=Paths.get("./uploads");
        Files.createDirectories(uploadDir);
        Path filePath = uploadDir.resolve(file.getOriginalFilename());

    Files.copy(
            file.getInputStream(),
            filePath,
            StandardCopyOption.REPLACE_EXISTING
    );
    kafkaProducerService.sendMessage(filePath.toString());
        return "File uploaded successfully. Processing started...";
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestParam("query") String query) {
        return ragService.ask(query);
    }

    @GetMapping("/kafka")
    public String sendKafkaMessage(){
        kafkaProducerService.sendMessage("Kafka is working fine");
        return "Message sent to Kafka";
    }
    
}