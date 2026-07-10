package com.example.producer.service;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class ExcelReaderService {

    private static final int BATCH_SIZE = 100;
    private static final String CODE_COLUMN = "code";

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

    public List<String[]> readExcel(InputStream inputStream, String collectionName) {
        List<String[]> rawRows = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);
            DataFormatter fmt = new DataFormatter();

            List<String> headers = new ArrayList<>();
            int codeColIndex = -1;
            for (Cell cell : header) {
                String h = cell.toString().trim();
                headers.add(h);
                if (h.equalsIgnoreCase(CODE_COLUMN)) codeColIndex = cell.getColumnIndex();
            }

            if (codeColIndex == -1) throw new RuntimeException("No 'code' column found in Excel");

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (isEmpty(row)) continue;
                StringBuilder doc = new StringBuilder();
                String code = "";
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    String value = (cell == null) ? "" : fmt.formatCellValue(cell);
                    if (j == codeColIndex) code = value.trim();
                    doc.append(headers.get(j)).append(":").append(value).append("\n");
                }
                if (!code.isEmpty()) rawRows.add(new String[]{doc.toString(), code});
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
            List<String> codes = new ArrayList<>();
            for (String[] r : batch) codes.add(r[1]);
            Set<String> existing = qdrantService.findExistingCodes(codes, collectionName);
            for (String[] r : batch) {
                if (!existing.contains(r[1])) unique.add(r);
                else System.out.println("⚠️ Duplicate code dropped: " + r[1]);
            }
        }
        return unique;
    }
}
