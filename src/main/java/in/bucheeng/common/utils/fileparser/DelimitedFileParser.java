/*
 *  @version     1.0, Mar 4, 2012
 *  @author sunny
 */
package in.bucheeng.common.utils.fileparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import in.bucheeng.common.utils.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringEscapeUtils;


public class DelimitedFileParser implements FileParser {
    private File                 file;
    private char                 delim;
    private boolean              containsHeader;
    private char                 enclosedBy;
    private String               encoding;
    private boolean              isMultiline             = true;
    public static final char     DEFAULT_DELIMITER       = ',';
    public static final char     TAB_DELIMITER           = '\t';
    private static final char    DEFAULT_ENCLOSEDBY      = '"';
    private static final boolean DEFAULT_CONTAINS_HEADER = true;
    private static final String  DEFAULT_ENCODING        = "UTF-8";

    /**
     * full constructor for delimited file parser
     * 
     * @param filePath absolute file path
     * @param delim delim token delimiter default value is '\t'
     * @param enclosedBy token optional enclosed, default value is '"'
     * @param containsHeader true if file contains header line, default values is true
     * @param encoding character encoding to be used for the file
     */
    public DelimitedFileParser(String filePath, char delim, char enclosedBy, boolean containsHeader, String encoding) {
        this.delim = delim;
        this.enclosedBy = enclosedBy;
        this.containsHeader = containsHeader;
        this.encoding = encoding;
        this.file = new File(filePath);
        if (!this.file.exists()) {
            throw new IllegalArgumentException("No such File Exists.File Path :" + filePath);
        }
    }

    /**
     * with encoding defaulted to 'UTF=8'
     * 
     * @param filePath absolute file path
     * @param delim delim token delimiter default value is '\t'
     * @param enclosedBy token optional enclosed, default value is '"'
     * @param containsHeader true if file contains header line, default values is true
     */
    public DelimitedFileParser(String filePath, char delim, char enclosedBy, boolean containsHeader) {
        this(filePath, delim, enclosedBy, containsHeader, DEFAULT_ENCODING);
    }

    /**
     * with enclosedBy defaulted to '"' and containsHeader defaulted to true
     * 
     * @param filePath absolute file path
     * @param delim token delimiter, default value is '\t'
     */
    public DelimitedFileParser(String filePath, char delim) {
        this(filePath, delim, DEFAULT_ENCLOSEDBY, DEFAULT_CONTAINS_HEADER, DEFAULT_ENCODING);
    }

    /**
     * with containsHeader defaulted to true
     * 
     * @param filePath absolute file path
     * @param delim token delimiter, default value is '\t'
     * @param enclosedBy token optional enclosed, default value is '"'
     */
    public DelimitedFileParser(String filePath, char delim, char enclosedBy) {
        this(filePath, delim, enclosedBy, DEFAULT_CONTAINS_HEADER, DEFAULT_ENCODING);
    }

    /**
     * with delimiter defaulted to '\t', enclosedBy defaulted to '"' and containsHeader defaulted to true
     * 
     * @param filePath absolute file path
     */
    public DelimitedFileParser(String filePath) {
        this(filePath, DEFAULT_DELIMITER, DEFAULT_ENCLOSEDBY, DEFAULT_CONTAINS_HEADER, DEFAULT_ENCODING);
    }

    /*
     * (non-Javadoc)
     * @see com.unifier.core.fileparse.FileParser#parse()
     */
    @Override
    public RowIterator parse() {
        try {
            return new RowIterator(IOUtils.lineIterator(new FileInputStream(file), encoding), delim, enclosedBy, containsHeader, isMultiline);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("No such File Exists.File Path :" + file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public RowIterator parse(int skipLines) {
        try {
            return new RowIterator(IOUtils.lineIterator(new FileInputStream(file), encoding), delim, enclosedBy, containsHeader, isMultiline, skipLines);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("No such File Exists.File Path :" + file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RowIterator is a standard iterator which provides hasNext and next method. If there are no more rows hasNext will
     * return false.next will return the next row and will throw NoSuchElementException if no next row is found
     * Internally it uses LineIterator over the file to iterate over the file
     */
    public static class RowIterator implements Iterator<Row> {
        // iterator over the lines of File
        private LineIterator         lineIterator;
        // Next row to be returned in next method
        private Row                  next;
        // current line number
        private int                  lineNo;
        // delim
        private char                 delim;
        private char                 enclosedBy;
        // column names , it will be null if containsHeader is false
        private Map<String, Integer> columnNamesToIndex;
        // it indicates whether we have iterated over the underlying line iterator and already processed all the rows.
        private boolean              completed;
        private boolean              isMultiline = true;
        private String[]             columnNames;

        /**
         * Constructs a Row Iterator
         * 
         * @param lineIterator iterator over the File
         * @param delim for identifying columns
         * @param containsHeader whether first line in the file is column header
         */
        public RowIterator(LineIterator lineIterator, char delim, char enclosedBy, boolean containsHeader, boolean isMultiline) {
            this(lineIterator, delim, enclosedBy, containsHeader, isMultiline, 0);
        }

        public RowIterator(LineIterator lineIterator, char delim, char enclosedBy, boolean containsHeader, boolean isMultiline, int skipInitialLines) {
            this.lineIterator = lineIterator;
            this.delim = delim;
            this.enclosedBy = enclosedBy;
            if (skipInitialLines > 0) {
                skip(skipInitialLines);
            }
            if (containsHeader) {
                // reads till we get the first line
                String[] columnNames = getNextRowValues(lineIterator);
                this.columnNames = columnNames;
                if (columnNames != null) {
                    columnNamesToIndex = Row.getHeaderPositionsMap(columnNames);
                }
            }
        }

        private String[] getNextRowValues(LineIterator lineIterator) {
            while (lineIterator.hasNext()) {
                String line = lineIterator.nextLine();
                lineNo++;
                if (StringUtils.hasText(line) && !line.startsWith("#")) {
                    LineParser parser = new LineParser(delim, enclosedBy);
                    while (parser.parseLine(line) && isMultiline && lineIterator.hasNext()) {
                        line = lineIterator.nextLine();
                        lineNo++;
                    }
                    return parser.getTokens();
                }
            }
            return null;
        }

        public void skip(int noOfLines) {
            while (lineNo <= noOfLines && lineIterator.hasNext()) {
                lineNo++;
                lineIterator.nextLine();
            }
        }

        /**
         * Computes the Next element
         * 
         * @return true it computed next element or false if it is done and there are no rows left
         */
        private boolean computeNext() {
            String[] nextRowValues = getNextRowValues(lineIterator);

            if (nextRowValues != null) {
                if (columnNamesToIndex != null) {
                    // if contains header is set and column names has been read, lets read data now
                    // we need to make sure column names are matching with the columns found in the row, otherwise
                    // we cannot find which column is missing
                    // constructs the row and set it to next so that it can be returned in next method call
                    next = new Row(lineNo, nextRowValues, columnNamesToIndex, delim);
                    return true;
                } else {
                    // this case is when containsHeader is false
                    // directly create a Row with column values
                    next = new Row(lineNo, nextRowValues, delim);
                    // yes it should return true as we have a next element which can be returned in next method
                    // call
                    return true;
                }
            }
            return false;
        }

        @Override
        /*
         * Determines whether there are more rows to be returned
         */
        public boolean hasNext() {
            // if we completed iterating and there are no more elements
            if (completed) {
                return false;
            }
            // otherwise computeNext and check completed again
            if (next == null) {
                completed = !computeNext();
            }
            return !completed;
        }

        @Override
        public Row next() {
            // if we are not complete and next is null then lets compute next
            if (!completed && next == null) {
                completed = !computeNext();
            }
            // check if we have next, if yes then return it and set next to null
            if (next != null) {
                try {
                    return next;
                } finally {
                    next = null;
                }
            }
            // we dont have any more elements throw the NoSuchElementException
            throw new NoSuchElementException();
        }

        /*
         * We are not supporting remove operation
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        public String getColumnNames() {
            StringBuilder builder = new StringBuilder();
            for (String s : columnNames) {
                builder.append(StringEscapeUtils.escapeCsv(s)).append(delim);
            }
            return builder.deleteCharAt(builder.length() - 1).toString();
        }

        public boolean hasColumn(String columnName) {
            return columnNamesToIndex.containsKey(StringUtils.removeNonWordChars(columnName).toLowerCase());
        }
    }
}
