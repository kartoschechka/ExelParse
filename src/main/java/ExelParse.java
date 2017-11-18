import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.sql.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Iterator;
import java.util.List;

public class ExelParse {

    private String exelFile;
    Statement statement;
    Connection connection;

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
        List<String> groupName;
        int indexStarting = findIndexOfMondey(sheet);
        System.out.println(sheet.getRow(9).getCell(1).getNumericCellValue());
    }

    private int findIndexOfMondey(Sheet sheet) {
        String mondey = "ПОНЕДЕЛЬНИК";
        int index = 5;
        int lastIndex = sheet.getLastRowNum();
        for (int i = 0; i < lastIndex; i++) {
            Row row = sheet.getRow(i);
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

