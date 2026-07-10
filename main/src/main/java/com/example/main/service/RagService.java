package com.example.main.service;

import com.example.main.model.ChatResponse;
import org.springframework.stereotype.Service;
import java.util.List;
import com.example.main.service.SearchService;
import com.example.main.service.AiService;

@Service
public class RagService {
    private final SearchService searchService;
    private final AiService aiService;
    public RagService(SearchService searchService,AiService aiService){
        this.searchService=searchService;
        this.aiService=aiService;
    }
    public ChatResponse ask(String query, String collectionName){
        List<String> context=searchService.search(query, collectionName);
        String answer= aiService.ask(query, context);
        return new ChatResponse(answer, context);
    }
    
}
