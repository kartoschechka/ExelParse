import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.sql.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExelParse {

    private String exelFile;
    private Statement statement;
    private Connection connection;


    public ExelParse(String exelFile) {
        this.exelFile = exelFile;
    }


    public void parse() throws IOException, ClassNotFoundException, SQLException {
        try {
            connection = Database.getConnection();
            HSSFWorkbook gh = new HSSFWorkbook(new FileInputStream(new File(exelFile)));
            Iterator<Sheet> name = gh.iterator();
            while (name.hasNext()) {
                parseSheet(name.next());
            }
        } catch (SQLException ex) {
            System.out.println("Что то пошло не так с БД");
            return;
        } finally {
            System.out.println("disconnect");
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    private void parseSheet(Sheet sheet) {
        List<CellRangeAddress> mergeRegion = sheet.getMergedRegions();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String daysOfWeek = "ПОНЕДЕЛЬНИК ВТОРНИК СРЕДА ЧЕТВЕРГ ПЯТНИЦА СУББОТА";
        int indexStarting = findIndexOfMondey(sheet);
        List<String> groupName = getGroupName(sheet.getRow(indexStarting - 1));
        int numberOfPair = 1;
        String date = "";
        for (int i = indexStarting; i < sheet.getLastRowNum(); i++) {
            if (sheet.getRow(i).getCell(0) == null || sheet.getRow(i).getCell(0).getStringCellValue().equals("")) {
                return;
            }
            if (daysOfWeek.contains(sheet.getRow(i).getCell(0).getStringCellValue())) {
                date = sdf.format(sheet.getRow(i).getCell(1).getDateCellValue());
                numberOfPair = 1;
                System.out.println(date);
                continue;
            }
            parseRow(sheet.getRow(i), numberOfPair, date, mergeRegion, groupName);
            numberOfPair++;
        }
    }

    private void parseRow(Row row, int numberOfPair, String date, List<CellRangeAddress> mergeRegion, List<String> groupName) {
        String cellValue = "";
        for (int i = 1; i < groupName.size(); i++) {
            if (groupName.get(i - 1).equals("")) {
                continue;
            }
            if (row.getCell(i) == null) {
                cellValue = "";
                parseCell(cellValue);
                continue;
            }
            if (row.getCell(i).getStringCellValue().equals("")) {
                cellValue = getMergeCellValue(row, row.getCell(i), mergeRegion);
                parseCell(cellValue);
            } else {
                cellValue = row.getCell(i).getStringCellValue();
                parseCell(cellValue);
            }
        }
    }

    private void parseCell(String cellValue){
        String[] strings =cellValue.split("\n");

    }

    private String getMergeCellValue(Row row, Cell cell, List<CellRangeAddress> mergeRegion) {
        String value = "";
        for (int i = 0; i < mergeRegion.size(); i++) {
            if (cell.getRowIndex() == mergeRegion.get(i).getFirstRow()) {
                if (cell.getColumnIndex() <= mergeRegion.get(i).getLastColumn() && cell.getColumnIndex() >= mergeRegion.get(i).getFirstColumn()) {
                    return row.getCell(mergeRegion.get(i).getFirstColumn()).getStringCellValue();
                }
            } else continue;
        }
        return value;
    }

    private int findIndexOfMondey(Sheet sheet) {
        String mondey = "ПОНЕДЕЛЬНИК";
        int index = 5;
        int lastIndex = sheet.getLastRowNum();
        for (int i = 0; i < lastIndex; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            Cell cell = row.getCell(0);
            if (cell == null || cell.getCellTypeEnum() != CellType.STRING) {
                continue;
            }
            if (mondey.contains(cell.getStringCellValue()) || mondey.toLowerCase().contains(cell.getStringCellValue())) {
                return i;
            }
        }
        return index;
    }

    private ArrayList<String> getGroupName(Row row) {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 1; i < row.getLastCellNum(); i++) {
            list.add(row.getCell(i).getStringCellValue());
        }
        return list;
    }
}

