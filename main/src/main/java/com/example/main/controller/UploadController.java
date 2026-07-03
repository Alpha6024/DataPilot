package com.example.main.controller;

import com.example.main.service.UploadService;
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

@RestController
@RequestMapping("/api")
public class UploadController {

    private final UploadService uploadService;
    private final SearchService searchService;
    private final AiService aiService;
    private final RagService ragService;

    public UploadController(UploadService uploadService,SearchService searchService,AiService aiService,RagService ragService) {
        this.uploadService = uploadService;
        this.searchService = searchService;
        this.aiService=aiService;
        this.ragService=ragService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file){
        return uploadService.upload(file);
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestParam("query") String query) {
        return ragService.ask(query);
    }
}