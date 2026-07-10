package com.example.main.service;

import com.example.main.model.UploadMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class UploadService {
    private final ExcelReaderService excelReaderService;
    private final RedisService redisService;
    private final KafkaProducerService kafkaProducerService;
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    public UploadService(ExcelReaderService excelReaderService,
            RedisService redisService,
            KafkaProducerService kafkaProducerService) {
        this.excelReaderService = excelReaderService;
        this.redisService = redisService;
        this.kafkaProducerService = kafkaProducerService;
    }

    public String upload(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String originalName = file.getOriginalFilename();
            String collectionName = originalName.replaceAll("\\.[^.]+$", "").replaceAll("[^a-zA-Z0-9_]", "_").toLowerCase();

            List<String> documents = excelReaderService.readExcel(Files.newInputStream(filePath));
            String jobId = UUID.randomUUID().toString();
            redisService.initializeJob(jobId, documents.size());

            for (String doc : documents) {
                kafkaProducerService.sendMessage(new UploadMessage(jobId, doc, collectionName));
            }

            System.out.println("✅ Sent " + documents.size() + " rows to Kafka. JobId: " + jobId);
            return jobId;
        } catch (Exception e) {
            e.printStackTrace();
            return "Upload Failed";
        }
    }
}
