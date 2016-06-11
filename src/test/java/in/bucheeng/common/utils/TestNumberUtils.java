/*
 *  @version     1.0, Nov 27, 2011
 *  @author sunny
 */
package in.bucheeng.common.utils;

import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestNumberUtils {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void toWords() {
        assertEquals("Three", NumberUtils.toWords(3));
        assertEquals("Ten Lakh Forty Five Thousand Five Hundred and Fifty Six", NumberUtils.toWords(1045556));
    }

}
