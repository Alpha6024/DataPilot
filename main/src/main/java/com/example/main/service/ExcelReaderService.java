package com.example.main.service;
import org.apache.poi.ss.usermodel.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ExcelReaderService {
    private boolean isempt(Row row){
    if(row==null){
        return true;
    }
    DataFormatter format=new DataFormatter();
    for(Cell cell:row){
        if(!format.formatCellValue(cell).trim().isEmpty());{
            return false;
        }
    }
    return true;
}
    public List<String> readExcel(InputStream inputStream){
        List<String> documents=new ArrayList<>();
        try(Workbook workbook=WorkbookFactory.create(inputStream)){
            Sheet sheet=workbook.getSheetAt(0);
            Row header=sheet.getRow(0);
            List<String> headerrow=new ArrayList<>();
            DataFormatter format=new DataFormatter();
            for(Cell cell:header){
                headerrow.add(cell.toString());
            }
            for(int i=1;i<sheet.getLastRowNum();i++){
                Row row=sheet.getRow(i);
                if(isempt(row)){
                    continue;
                }
                StringBuilder doc=new StringBuilder();
                for(int j=0;j<headerrow.size();j++){
                    Cell cell=row.getCell(j);
                    String value=(cell ==null)?"":format.formatCellValue(cell);
                    doc.append(headerrow.get(j))
                       .append(":")
                       .append(value)
                       .append("\n");
                }
                documents.add(doc.toString());
                
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        for(String doc:documents){
            System.out.println("----------");
            System.out.println(doc);
        }
        return documents;

    }
}
