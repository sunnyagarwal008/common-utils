/*
 *  @version     1.0, Nov 27, 2011
 *  @author sunny
 */
package in.bucheeng.common.utils;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestStringUtils {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        System.out.println("@Before");
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("@After");
    }

    @Test
    public void isEmpty() {
        assertEquals(true, StringUtils.isEmpty(""));
        assertEquals(false, StringUtils.isEmpty("NOTEMPTY"));
    }

    @Test
    public void isNotEmpty() {
        assertEquals(false, StringUtils.isNotEmpty(""));
        assertEquals(true, StringUtils.isNotEmpty("NOTEMPTY"));
    }

    @Test
    public void getAccessorNameForField() {
        assertEquals("getValue", StringUtils.getAccessorNameForField("value"));
    }

}
