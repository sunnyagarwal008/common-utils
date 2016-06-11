/*
 *  @version     1.0, Jun 10, 2012
 *  @author sunny
 */
package in.bucheeng.common.utils;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;

public class TestEncryptionUtils {

    @Test
    public void testEncrypt() {
        assertEquals(EncryptionUtils.encrypt("TEST_STRING"), "KrdR8qtXVHKF28tcj6b/cQ==");
        assertEquals(EncryptionUtils.encrypt("Uniware"), "W/mDEuunTq8=");
    }

    @Test
    public void testDecrypt() {
        assertEquals(EncryptionUtils.decrypt("KrdR8qtXVHKF28tcj6b/cQ=="), "TEST_STRING");
        assertEquals(EncryptionUtils.decrypt("W/mDEuunTq8="), "Uniware");
    }

    @Test
    public void testBase64Encode() throws UnsupportedEncodingException {
        assertEquals(EncryptionUtils.base64Encode("TEST_STRING".getBytes("UTF-8")), "VEVTVF9TVFJJTkc=");
    }

    @Test
    public void testBase64Decode() {
        Assert.assertEquals(new String(EncryptionUtils.base64Decode("VEVTVF9TVFJJTkc="), Charset.forName("UTF-8")), "TEST_STRING");
    }
}
