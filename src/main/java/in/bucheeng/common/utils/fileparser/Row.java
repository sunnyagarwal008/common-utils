/*
 *  @version     1.0, Mar 4, 2012
 *  @author sunny
 */
package in.bucheeng.common.utils.fileparser;

import java.util.HashMap;
import java.util.Map;

import in.bucheeng.common.utils.StringUtils;

public class Row {
    private int                  lineNo;
    private Map<String, Integer> headerPositions;
    private String[]             columnValues;
    private char                 delimiter = DelimitedFileParser.DEFAULT_DELIMITER;

    public Row(int lineNo, String[] columnValues) {
        this.lineNo = lineNo;
        this.columnValues = columnValues;
    }

    public Row(String[] columnValues, Map<String, Integer> headerPositions) {
        this.columnValues = columnValues;
        this.headerPositions = headerPositions;
    }

    public Row(int lineNo, String[] columnValues, char delim) {
        this.lineNo = lineNo;
        this.columnValues = columnValues;
        this.delimiter = delim;
    }

    public Row(int lineNo, String[] columnValues, Map<String, Integer> headerPositions) {
        this(lineNo, columnValues);
        this.headerPositions = headerPositions;
    }

    public Row(int lineNo, String[] columnValues, Map<String, Integer> headerPositions, char delim) {
        this(lineNo, columnValues);
        this.headerPositions = headerPositions;
        this.delimiter = delim;
    }

    public int getLineNo() {
        return lineNo;
    }

    /**
     * Index starts from 0
     * 
     * @param index
     * @return
     */
    public String getColumnValue(int index) {
        if (columnValues != null) {
            if (index < 0 || index >= columnValues.length) {
                return null;
            }
            return columnValues[index];
        }
        return null;
    }

    public void setColumnValue(int index, String value) {
        if (columnValues != null) {
            if (index > 0 && index < columnValues.length) {
                columnValues[index] = value;
            }
        }
    }

    public String[] getColumnValues() {
        return columnValues;
    }

    public String getColumnValue(String columnName) {
        if (headerPositions == null) {
            throw new UnsupportedOperationException("Access by Column Name is not allowed for files without header.");
        }

        Integer columnIndex = headerPositions.get(StringUtils.removeNonWordChars(columnName).toLowerCase());
        if (columnIndex == null) {
            return null;
        }
        return getColumnValue(columnIndex);
    }

    public void setColumnValue(String columnName, String value) {
        if (headerPositions == null) {
            throw new UnsupportedOperationException("Access by Column Name is not allowed for files without header.");
        }

        Integer columnIndex = headerPositions.get(StringUtils.removeNonWordChars(columnName).toLowerCase());
        if (columnIndex != null) {
            setColumnValue(columnIndex, value);
        }
    }

    public boolean hasColumn(String columnName) {
        return headerPositions != null ? headerPositions.containsKey(StringUtils.removeNonWordChars(columnName).toLowerCase()) : false;
    }

    @Override
    public String toString() {
        if (columnValues.length == 0) {
            return StringUtils.EMPTY_STRING;
        }
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (String s : columnValues) {
            builder.append('"').append(s).append('"').append(',');
        }
        return builder.deleteCharAt(builder.length() - 1).append(']').toString();
    }

    public String toCSV() {
        return toCSV(columnValues, delimiter);
    }

    public char getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }

    public static String toCSV(String[] columnValues) {
        return toCSV(columnValues, DelimitedFileParser.DEFAULT_DELIMITER);
    }

    public static String toCSV(String[] columnValues, char delimiter) {
        if (columnValues == null || columnValues.length == 0) {
            return StringUtils.EMPTY_STRING;
        }
        StringBuilder builder = new StringBuilder();
        for (String s : columnValues) {
            builder.append(StringUtils.escapeCsv(s)).append(delimiter);
        }
        return builder.deleteCharAt(builder.length() - 1).toString();
    }

    public static Map<String, Integer> getHeaderPositionsMap(String[] columnNames) {
        Map<String, Integer> headerPositions = new HashMap<String, Integer>();
        for (int i = 0; i < columnNames.length; i++) {
            headerPositions.put(StringUtils.removeNonWordChars(columnNames[i]).toLowerCase(), i);
        }
        return headerPositions;
    }
}
