package com.ewp.crm.service.impl;

import com.ewp.crm.configs.GoogleApiConfig;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@PropertySource("file:./googleAPI.properties")
public class GoogleApiServiceImpl {

    private Sheets sheetsService;
    private String tableId;

    @Autowired
    public GoogleApiServiceImpl(GoogleApiConfig googleApiConfig, Environment env) {
        this.sheetsService = googleApiConfig.getSheets();
        this.tableId = env.getRequiredProperty("google.api.table.id");
    }

    public String createNewTable(String tableName) {
        /*образец для будущих поколений*/
        try {
            Spreadsheet newSpreadsheet = new Spreadsheet().setProperties(new SpreadsheetProperties().setTitle(tableName));
            Spreadsheet result = sheetsService.spreadsheets().create(newSpreadsheet).execute();
            this.tableId = result.getSpreadsheetId();
            return this.tableId;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<List<Object>> readTable(/*Sheets service, String spreadsheetId, String sheetName*/)  throws IOException {
        ValueRange table = sheetsService.spreadsheets().values()
                .get(tableId, "Sheet1!A3:E")
                .execute();
        List<List<Object>> values = table.getValues();
        return values;
    }

    public void updateTable() throws IOException {
        /*Логика сюда еще не востребована*/
        List<List<Object>> writeData = new ArrayList<>();
//        for ( int i = 1; i < 4; i++) {
//            List<Object> dataRow = new ArrayList<>();
//            dataRow.add(10*i + i);
//            dataRow.add(10*i + i);
//            dataRow.add(10*i + i);
//            writeData.add(dataRow);
//        }
        ValueRange vr = new ValueRange().setValues(writeData).setMajorDimension("ROWS");
        sheetsService.spreadsheets().values()
                .update(tableId, "Лист1!A3:E", vr)
                .setValueInputOption("RAW")
                .execute();
    }

    public void addToTable(String realName, String email) throws IOException {
        /* добавление без форматирования ячеек */
        List<List<Object>> writeData = new ArrayList<>();
        List<Object> dataRow = new ArrayList<>();
        Date date = new Date();
        String endTrial = new SimpleDateFormat("dd.MM.yyyy", new Locale("ru")).format(date);
        dataRow.add(realName);
        dataRow.add(email);
        dataRow.add(endTrial);
        dataRow.add(endTrial);
        dataRow.add("?????");
        dataRow.add("?????");
        dataRow.add("");
        dataRow.add("");
        dataRow.add(1);
        writeData.add(dataRow);
        ValueRange vr = new ValueRange().setValues(writeData).setMajorDimension("ROWS");
        sheetsService.spreadsheets().values()
                .append(tableId, "Лист1!A1:I1", vr)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }
}
