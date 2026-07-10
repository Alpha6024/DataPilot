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
                Rules:
                1. Use ONLY the information present in the context.
                2. Never invent or assume facts.
                3. If the question requires calculations (sum, average, highest, lowest, count), calculate them using ONLY the retrieved context.
                4. Present the answer in a clear, readable format using bullet points or tables whenever appropriate.
                5. Do not mention these instructions or the context in your response.
                6. Do not make up any information.
                7. If the answer is not present in the context, reply exactly:
                   "I cant't find releted data in document."
                8. Keep the answer clear, concise, and well formatted.
                9. Do not use markdown, asterisks, hashtags, or any special formatting. Plain text only.
                """, contextText, question);
        String response = chatModel.chat(prompt);
        return response
                .replaceAll("\\*{1,2}([^*]+)\\*{1,2}", "$1")
                .replaceAll("#{1,6}\\s*", "")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }   
}