/*
 *  @version     1.0, Mar 4, 2012
 *  @author sunny
 */
package in.bucheeng.common.utils.fileparser;

import java.util.Iterator;

public interface FileParser {
    /**
     * Parses the file and provides an Iterator over rows in the file
     * 
     * @return Iterator<Row> iterate over the lines of the file
     */
    public Iterator<Row> parse();

    public Iterator<Row> parse(int skipLines);
}
