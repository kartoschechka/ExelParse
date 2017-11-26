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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExelParse {

    private String exelFile;
    private Statement statement;
    private Connection connection;


    public ExelParse(String exelFile) {
        this.exelFile = exelFile;
    }


    public void parse() throws IOException, ClassNotFoundException, SQLException {
        try {
            connection = Database.getConnection ();
            HSSFWorkbook gh = new HSSFWorkbook (new FileInputStream (new File (exelFile)));
            Iterator<Sheet> name = gh.iterator ();
            while (name.hasNext ()) {
                parseSheet (name.next ());
            }
        } catch (SQLException ex) {
            System.out.println ("Что то пошло не так с БД");
            return;
        } finally {
            System.out.println ("disconnect");
            try {
                if (statement != null)
                    statement.close ();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close ();
            } catch (SQLException se) {
                se.printStackTrace ();
            }
        }
    }

    private void parseSheet(Sheet sheet) {
        List<CellRangeAddress> mergeRegion = sheet.getMergedRegions ();
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy.MM.dd");
        String daysOfWeek = "ПОНЕДЕЛЬНИК ВТОРНИК СРЕДА ЧЕТВЕРГ ПЯТНИЦА СУББОТА";
        int indexStarting = findIndexOfMondey (sheet);
        List<String> groupName = getGroupName (sheet.getRow (indexStarting - 1));
        int numberOfPair = 1;
        String date = "";
        for (int i = indexStarting; i < sheet.getLastRowNum (); i++) {
            if (sheet.getRow (i).getCell (0) == null || sheet.getRow (i).getCell (0).getStringCellValue ().equals ("")) {
                return;
            }
            if (daysOfWeek.contains (sheet.getRow (i).getCell (0).getStringCellValue ())) {
                date = sdf.format (sheet.getRow (i).getCell (1).getDateCellValue ());
                numberOfPair = 1;
                System.out.println (date);
                continue;
            }
            parseRow (sheet.getRow (i), numberOfPair, date, mergeRegion, groupName);
            numberOfPair++;
        }
    }

    private void parseRow(Row row, int numberOfPair, String date, List<CellRangeAddress> mergeRegion, List<String> groupName) {
        String cellValue = "";
        for (int i = 1; i < groupName.size (); i++) {
            if (groupName.get (i - 1).equals ("")) {
                continue;
            }
            if (row.getCell (i) == null) {
                cellValue = "";
                parseCell (new Info (date, groupName.get (i), numberOfPair), cellValue);
                continue;
            }
            if (row.getCell (i).getStringCellValue ().equals ("")) {
                cellValue = getMergeCellValue (row, row.getCell (i), mergeRegion);
                parseCell (new Info (date, groupName.get (i), numberOfPair), cellValue);
            } else {
                cellValue = row.getCell (i).getStringCellValue ();
                parseCell (new Info (date, groupName.get (i), numberOfPair), cellValue);
            }
        }
    }

    private void parseCell(Info info, String cellValue) {
        Pattern patternOfFIO = Pattern.compile ("([А-Я]+[\\s][А-Я]\\.[А-Я]\\.)");
        Pattern patternOfAudience = Pattern.compile ("[0-9]{3}[а-я]?(\\D+№[0-9])?");
        Pattern patternOfSubgroup = Pattern.compile ("[\\(][0-9][\\)]");
        Pattern patternOfType = Pattern.compile ("[\\(][А-Яа-я]+[\\)]");
        String[] strings = cellValue.split ("\n");
        if (cellValue.equals ("")) {
            info.addDiscipline ("");
            info.addNumberSubgroup (0);
            info.addType ("");
            info.addAudience ("");
            info.addTeacher ("");
            sendResponse (info);
            return;
        }
        if (cellValue.contains ("Ф   И   З   И   Ч   Е   С   К   А   Я        К   У   Л   Ь   Т   У   Р   А ") || cellValue.contains ("ФИЗИЧЕСКАЯ КУЛЬТУРА")) {
            info.addDiscipline ("ФИЗИЧЕСКАЯ КУЛЬТУРА");
            info.addNumberSubgroup (0);
            if (strings.length > 1) {
                Matcher audience = patternOfAudience.matcher (strings[1]);
                Matcher prepod = patternOfFIO.matcher (strings[1]);
                if (prepod.find ()) {
                    info.addTeacher (strings[1].substring (prepod.start (), prepod.end ()));
                } else info.addTeacher ("");
                if (audience.find ()) {
                    info.addAudience (strings[1].substring (audience.start (), audience.end ()));
                    info.addType ("лк.");
                }
                sendResponse (info);
            } else {
                info.addAudience ("");
                info.addType ("пр.");
                sendResponse (info);
            }
            return;
        }
        if (cellValue.contains ("ИНОСТРАННЫЙ ЯЗЫК")) {
            info.addDiscipline ("ИНОСТРАННЫЙ ЯЗЫК");
            info.addType ("пр.");

            Matcher matcherS = patternOfSubgroup.matcher (cellValue);
            if (matcherS.matches ()) {
                while (matcherS.find ()) {
                    info.addNumberSubgroup (Integer.parseInt (cellValue.substring (matcherS.start (), matcherS.end ()).replaceAll ("[()]", "")));
                }
                Matcher audeince = patternOfAudience.matcher (cellValue);
                while (audeince.find ()) {
                    info.addAudience (cellValue.substring (audeince.start (), audeince.end ()));
                }
                Matcher prepod = patternOfFIO.matcher (cellValue);
                while (prepod.find ()) {
                    info.addTeacher (cellValue.substring (prepod.start (), prepod.end ()));
                }
            }
            sendResponse (info);
            return;
        }
        if (!(patternOfSubgroup.matcher (cellValue).find ())) {
            info.addNumberSubgroup (0);
            Matcher type = patternOfType.matcher (cellValue);
            Matcher prepod = patternOfFIO.matcher (cellValue);
            Matcher aud = patternOfAudience.matcher (cellValue);
            if (aud.find ()) {
                info.addAudience (cellValue.substring (aud.start (), aud.end ()));
            } else info.addAudience ("");
            if (prepod.find ()) {
                info.addTeacher (cellValue.substring (prepod.start (), prepod.end ()));
            } else info.addTeacher ("");
            if (type.find ()) {
                info.addType (cellValue.substring (type.start (), type.end ()).replaceAll ("[()]", ""));
            } else info.addType ("лк.");
            info.addDiscipline (strings[0].replaceAll ("[(][А-Яа-я]+[)]", ""));

        }

    }

    private void sendResponse(Info info) {

    }

    private String getMergeCellValue(Row row, Cell cell, List<CellRangeAddress> mergeRegion) {
        String value = "";
        for (int i = 0; i < mergeRegion.size (); i++) {
            if (cell.getRowIndex () == mergeRegion.get (i).getFirstRow ()) {
                if (cell.getColumnIndex () <= mergeRegion.get (i).getLastColumn () && cell.getColumnIndex () >= mergeRegion.get (i).getFirstColumn ()) {
                    return row.getCell (mergeRegion.get (i).getFirstColumn ()).getStringCellValue ();
                }
            } else continue;
        }
        return value;
    }

    private int findIndexOfMondey(Sheet sheet) {
        String mondey = "ПОНЕДЕЛЬНИК";
        int index = 5;
        int lastIndex = sheet.getLastRowNum ();
        for (int i = 0; i < lastIndex; i++) {
            Row row = sheet.getRow (i);
            if (row == null) continue;
            Cell cell = row.getCell (0);
            if (cell == null || cell.getCellTypeEnum () != CellType.STRING) {
                continue;
            }
            if (mondey.contains (cell.getStringCellValue ()) || mondey.toLowerCase ().contains (cell.getStringCellValue ())) {
                return i;
            }
        }
        return index;
    }

    private ArrayList<String> getGroupName(Row row) {
        ArrayList<String> list = new ArrayList<String> ();
        for (int i = 1; i < row.getLastCellNum (); i++) {
            list.add (row.getCell (i).getStringCellValue ());
        }
        return list;
    }
}

