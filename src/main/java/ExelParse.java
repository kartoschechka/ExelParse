import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

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
    private SimpleDateFormat sdf= new SimpleDateFormat("yyyy.MM.dd");

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
        int indexStarting = findIndexOfMondey(sheet);
        List<String> groupName=new ArrayList<String>();
        Row rowWichName =sheet.getRow(indexStarting-1);
        for (int i =1; i <rowWichName.getLastCellNum() ; i++) {
            groupName.add(rowWichName.getCell(i).getStringCellValue());
        }
        for (String x:groupName ) {
            System.out.println(x);
        }
        //String str = (sdf.format(sheet.getRow(9).getCell(1).getDateCellValue()));

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
}

