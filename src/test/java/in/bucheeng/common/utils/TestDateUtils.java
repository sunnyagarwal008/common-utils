/*
 *  @version     1.0, Jun 10, 2012
 *  @author sunny
 */
package in.bucheeng.common.utils;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class TestDateUtils {

    @Test
    public void clearDate() {
        Date dateTime = new Date(1339296767148L);
        System.out.println(DateUtils.clearTime(dateTime));
        Assert.assertEquals(1339266600000L, DateUtils.clearTime(dateTime).getTime());
        Assert.assertEquals(10367148, DateUtils.clearDate(dateTime).getTime());
    }
}
