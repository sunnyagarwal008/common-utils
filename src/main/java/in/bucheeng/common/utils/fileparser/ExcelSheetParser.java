/*
 *  @version     1.0, Aug 9, 2012
 *  @author sunny
 */
package in.bucheeng.common.utils.fileparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import in.bucheeng.common.utils.StringUtils;

public class ExcelSheetParser implements FileParser {

    private static final String EMPTY_STRING   = "";
    private final File          file;
    private boolean             containsHeader = true;
    private String              sheetName;
    private boolean             xlsx;

    public ExcelSheetParser(String filePath, String sheetName, boolean containsHeader) {
        this(filePath, sheetName);
        this.containsHeader = containsHeader;
    }

    public ExcelSheetParser(String filePath, boolean containsHeader) {
        this(filePath);
        this.containsHeader = containsHeader;
    }

    public ExcelSheetParser(String filePath, String sheetName) {
        this(filePath);
        this.sheetName = sheetName;
    }

    public ExcelSheetParser(String filePath) {
        this.file = new File(filePath);
        if (!this.file.exists()) {
            throw new IllegalArgumentException("No such file exists at path :" + filePath);
        }
        if (filePath.endsWith(".xlsx")) {
            xlsx = true;
        }
    }

    @Override
    public Iterator<Row> parse(int skipInitialLine) {
        if (xlsx) {
            try {
                return parseXLSX(skipInitialLine);
            } catch (Exception e) {
                return parseXLS(skipInitialLine);
            }
        } else {
            try {
                return parseXLS(skipInitialLine);
            } catch (Exception e) {
                return parseXLSX(skipInitialLine);
            }
        }
    }

    @Override
    public Iterator<Row> parse() {
        return parse(0);
    }

    private Iterator<Row> parseXLSX(int skipInitialLines) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
            XSSFSheet s;
            if (StringUtils.isBlank(sheetName)) {
                s = workbook.getSheetAt(0);
            } else {
                s = workbook.getSheet(sheetName);
            }
            return new XLSXSheetIterator(s, containsHeader, skipInitialLines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Iterator<Row> parseXLS(int skipInitialLines) {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(file));
            HSSFSheet s;
            if (StringUtils.isBlank(sheetName)) {
                s = workbook.getSheetAt(0);
            } else {
                s = workbook.getSheet(sheetName);
            }
            return new XLSSheetIterator(s, containsHeader, skipInitialLines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class XLSXSheetIterator implements Iterator<Row> {

        private XSSFSheet            xlsxSheet;
        private int                  lineNo;
        private Map<String, Integer> columnNamesToIndex;
        private String[]             columnNames;

        public XLSXSheetIterator(XSSFSheet sheet, boolean containsHeader, int skipInitialLines) {
            this.xlsxSheet = sheet;
            if (skipInitialLines > 0) {
                skip(skipInitialLines);
            }
            if (containsHeader) {
                // read the column names and store the position for headers
                columnNames = getColumnValues(sheet.getRow(lineNo++));
                columnNamesToIndex = new HashMap<String, Integer>(columnNames.length);
                for (int i = 0; i < columnNames.length; i++) {
                    columnNamesToIndex.put(StringUtils.removeNonWordChars(columnNames[i]).toLowerCase(), i);
                }
            }
        }

        private String[] getColumnValues(XSSFRow row) {
            if (row == null) {
                return new String[0];
            } else {
                String[] columnValues = new String[row.getLastCellNum()];
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    columnValues[i] = row.getCell(i) != null ? getValue(row.getCell(i)) : EMPTY_STRING;
                }
                return columnValues;
            }
        }

        private String getValue(XSSFCell cell) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                        return sdf.format(cell.getDateCellValue());
                    }
                    return cell.getRawValue();
                default:
                    return String.valueOf(cell);
            }
        }

        @Override
        public boolean hasNext() {
            return lineNo <= xlsxSheet.getLastRowNum();
        }

        public String getColumnNames() {
            StringBuilder builder = new StringBuilder();
            for (String s : columnNames) {
                builder.append(StringEscapeUtils.escapeCsv(s)).append(',');
            }
            return builder.deleteCharAt(builder.length() - 1).toString();
        }

        @Override
        public Row next() {
            if (hasNext()) {
                String[] columnValues = getColumnValues(xlsxSheet.getRow(lineNo++));
                if (columnNamesToIndex != null) {
                    return new Row(lineNo, columnValues, columnNamesToIndex);
                } else {
                    return new Row(lineNo, columnValues);
                }
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void skip(int noOfLines) {
            while (noOfLines-- > 0 && hasNext()) {
                lineNo++;
            }
        }

    }

    public static class XLSSheetIterator implements Iterator<Row> {

        private HSSFSheet            xlsSheet;
        private int                  lineNo;
        private Map<String, Integer> columnNamesToIndex;
        private String[]             columnNames;

        public XLSSheetIterator(HSSFSheet sheet, boolean containsHeader, int skipInitialLines) {
            this.xlsSheet = sheet;
            if (skipInitialLines > 0) {
                skip(skipInitialLines);
            }
            if (containsHeader) {
                // read the column names and store the position for headers
                columnNames = getColumnValues(sheet.getRow(lineNo++));
                columnNamesToIndex = new HashMap<String, Integer>(columnNames.length);
                for (int i = 0; i < columnNames.length; i++) {
                    columnNamesToIndex.put(StringUtils.removeNonWordChars(columnNames[i]).toLowerCase(), i);
                }
            }
        }

        private String[] getColumnValues(HSSFRow row) {
            if (row == null) {
                return new String[0];
            } else {
                String[] columnValues = new String[row.getLastCellNum()];
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    columnValues[i] = row.getCell(i) != null ? String.valueOf(row.getCell(i)) : null;
                }
                return columnValues;

            }
        }

        @Override
        public boolean hasNext() {
            return lineNo <= xlsSheet.getLastRowNum();
        }

        public String getColumnNames() {
            StringBuilder builder = new StringBuilder();
            for (String s : columnNames) {
                builder.append(StringEscapeUtils.escapeCsv(s)).append(',');
            }
            return builder.deleteCharAt(builder.length() - 1).toString();
        }

        @Override
        public Row next() {
            if (hasNext()) {
                String[] columnValues = getColumnValues(xlsSheet.getRow(lineNo++));
                if (columnNamesToIndex != null) {
                    return new Row(lineNo, columnValues, columnNamesToIndex);
                } else {
                    return new Row(lineNo, columnValues);
                }
            }
            throw new NoSuchElementException();
        }

        public void skip(int noOfLines) {
            while (noOfLines-- > 0 && hasNext()) {
                lineNo++;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
