package com.example.main.service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
public class UploadService {
    private final ExcelReaderService excelreaderservice;
    private final EmbeddingService embeddingservice;
    public UploadService(ExcelReaderService excelreaderservice,EmbeddingService embeddingservice){
            this.excelreaderservice=excelreaderservice;
            this.embeddingservice=embeddingservice;
        }
    public String upload(MultipartFile file){
       
        List<String> documents=excelreaderservice.readExcel(file);
        embeddingservice.generateEmbed(documents);
            return "Success";
    }

}