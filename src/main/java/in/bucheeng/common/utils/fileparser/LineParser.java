/*
 *  @version     1.0, Mar 4, 2012
 *  @author sunny
 */
package in.bucheeng.common.utils.fileparser;

import java.util.ArrayList;
import java.util.List;

import in.bucheeng.common.utils.StringUtils;

public class LineParser {

    private final char        delim;

    private final char        enclosedBy;

    private StringBuilder     tokenBuilder;
    private boolean           inField;
    private List<String>      tokensOnThisLine = new ArrayList<String>();

    private static final char ESCAPE_CHARACTER = '\\';

    public LineParser(char delim, char enclosedBy) {
        this.delim = delim;
        this.enclosedBy = enclosedBy;
    }

    public boolean parseLine(String nextLine) {
        boolean inQuotes = false;
        if (tokenBuilder != null) {
            inQuotes = true;
            inField = true;
        } else {
            tokenBuilder = new StringBuilder();
        }
        for (int i = 0; i < nextLine.length(); i++) {
            char c = nextLine.charAt(i);
            if (c == ESCAPE_CHARACTER) {
                if (nextLine.length() > i + 1) {
                    Character eChar = getEscapedChar(nextLine.charAt(i + 1));
                    if (eChar != null) {
                        tokenBuilder.append(eChar);
                        i++;
                    } else {
                        tokenBuilder.append(c);
                    }
                } else {
                    tokenBuilder.append(c);
                }
            } else if (c == enclosedBy) {
                if (!inField) {
                    inQuotes = true;
                    inField = true;
                } else if (inQuotes) {
                    if ((nextLine.length() > i + 1) && nextLine.charAt(i + 1) == enclosedBy) {
                        tokenBuilder.append(enclosedBy);
                        i = i + 1;
                        continue;
                    }
                    i = nextLine.indexOf(delim, i);
                    inQuotes = false;
                    tokensOnThisLine.add(tokenBuilder.toString().trim());
                    tokenBuilder.setLength(0);
                    inField = false;
                    if (i == -1) {
                        return false;
                    }
                } else {
                    tokenBuilder.append(c);
                }
            } else if (c == delim && !inQuotes) {
                tokensOnThisLine.add(tokenBuilder.toString().trim());
                tokenBuilder.setLength(0);
                inField = false;
            } else if (inField || !Character.isWhitespace(c)) {
                tokenBuilder.append(c);
                inField = true;
            }
        }
        // line is done - check status
        if (inQuotes) {
            // continuing a quoted section, re-append newline
            tokenBuilder.append("\n");
            return true;
        } else {
            tokensOnThisLine.add(tokenBuilder.toString().trim());
            return false;
        }
    }

    private Character getEscapedChar(char ch) {
        switch (ch) {
            case '\\':
                return '\\';
            case '\'':
                return '\'';
            case '\"':
                return '"';
            case 'r':
                return '\r';
            case 'f':
                return '\f';
            case 't':
                return '\t';
            case 'n':
                return '\n';
            case 'b':
                return '\b';
            default:
                return null;
        }
    }

    protected boolean isAllWhiteSpace(CharSequence sb) {
        boolean result = true;
        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);

            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return result;
    }

    public String[] getTokens() {
        return tokensOnThisLine.toArray(new String[tokensOnThisLine.size()]);
    }

    public static String[] parseTokens(String line) {
        return parseTokens(line, DelimitedFileParser.DEFAULT_DELIMITER);
    }

    public static String[] parseTokens(String line, char delimiter) {
        LineParser parser = new LineParser(delimiter, '"');
        parser.parseLine(line.toLowerCase());
        String[] tokens = parser.getTokens();
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = StringUtils.removeNonWordChars(tokens[i]);
        }
        return tokens;
    }

}
