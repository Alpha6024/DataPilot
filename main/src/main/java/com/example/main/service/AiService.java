package com.example.main.service;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AiService {
    private final ChatModel chatModel;

    public AiService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }
    public String ask(String question,List<String> context) {
        String contextText = String.join("\n\n", context);
        String prompt = String.format("""
                You are an AI assistant that answers questions based on Excel data.
                Use ONLY the information provided in the context below.
                CONTEXT
                %s
                QUESTION
                %s
                Instructions:
                1. Answer only using the context.
                2. Do not make up any information.
                3. If the answer is not present in the context, reply exactly:
                   "I cant't find releted data in document."
                4. Keep the answer clear, concise, and well formatted.
                """, contextText, question);
        String response = chatModel.chat(prompt);
        return response;
    }   
}