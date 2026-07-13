package com.example.producer.service;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@Service
public class ExcelReaderService {

    private static final int BATCH_SIZE = 100;
    private final QdrantService qdrantService;

    public ExcelReaderService(QdrantService qdrantService) {
        this.qdrantService = qdrantService;
    }

    private boolean isEmpty(Row row) {
        if (row == null) return true;
        DataFormatter fmt = new DataFormatter();
        for (Cell cell : row)
            if (!fmt.formatCellValue(cell).trim().isEmpty()) return false;
        return true;
    }

    private String hash(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return String.valueOf(content.hashCode());
        }
    }

    public List<String[]> readExcel(InputStream inputStream, String collectionName) {
        List<String[]> rawRows = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);
            DataFormatter fmt = new DataFormatter();

            List<String> headers = new ArrayList<>();
            for (Cell cell : header)
                headers.add(cell.toString().trim());

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (isEmpty(row)) continue;
                StringBuilder doc = new StringBuilder();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    String value = (cell == null) ? "" : fmt.formatCellValue(cell);
                    doc.append(headers.get(j)).append(":").append(value).append("\n");
                }
                String content = doc.toString();
                rawRows.add(new String[]{content, hash(content)});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deduplicateInBatches(rawRows, collectionName);
    }

    private List<String[]> deduplicateInBatches(List<String[]> rows, String collectionName) {
        List<String[]> unique = new ArrayList<>();
        for (int i = 0; i < rows.size(); i += BATCH_SIZE) {
            List<String[]> batch = rows.subList(i, Math.min(i + BATCH_SIZE, rows.size()));
            List<String> hashes = new ArrayList<>();
            for (String[] r : batch) hashes.add(r[1]);
            Set<String> existing = qdrantService.findExistingHashes(hashes, collectionName);
            for (String[] r : batch)
                if (!existing.contains(r[1])) unique.add(r);
        }
        return unique;
    }
}
