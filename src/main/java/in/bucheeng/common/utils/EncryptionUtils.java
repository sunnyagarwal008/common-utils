/*
 *  @version     1.0, Dec 20, 2011
 *  @author sunny
 */
package in.bucheeng.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class EncryptionUtils {

    public static final  String ENCRYPTION_SCHEME = "DESede";
    public static final  String ENCRYPTION_KEY    = "UCUNIWARE ENCRYPTION KEY";
    private static final String UNICODE_FORMAT    = "UTF8";

    private static KeySpec          keySpec;
    private static SecretKeyFactory keyFactory;
    private static Cipher           cipher;
    private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    static {
        try {
            byte[] keyAsBytes = ENCRYPTION_KEY.getBytes(UNICODE_FORMAT);
            keySpec = new DESedeKeySpec(keyAsBytes);
            keyFactory = SecretKeyFactory.getInstance(ENCRYPTION_SCHEME);
            cipher = Cipher.getInstance(ENCRYPTION_SCHEME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String unencryptedString) {
        if (unencryptedString == null || unencryptedString.trim().length() == 0)
            return unencryptedString;

        try {
            SecretKey key = keyFactory.generateSecret(keySpec);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] clearText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] cipherText = cipher.doFinal(clearText);
            Base64 base64encoder = new Base64();
            return base64encoder.encodeBase64String(cipherText);
        } catch (Exception e) {
            // Unable to encrypt
            return unencryptedString;
        }
    }

    public static String encrypt(int unencryptedInt) {
        return encrypt(String.valueOf(unencryptedInt));
    }

    public static String decrypt(String encryptedString) {
        if (encryptedString == null || encryptedString.trim().length() <= 0)
            return encryptedString;

        try {
            SecretKey key = keyFactory.generateSecret(keySpec);
            cipher.init(Cipher.DECRYPT_MODE, key);
            Base64 base64decoder = new Base64();
            byte[] cleartext = base64decoder.decode(encryptedString);
            byte[] ciphertext = cipher.doFinal(cleartext);

            return new String(ciphertext, UNICODE_FORMAT);
        } catch (Exception e) {
            // Unable to decrypt
            return encryptedString;
        }
    }

    public static String md5Encode(String input, String salt) {
        return md5Encode(mergePasswordAndSalt(input, salt));
    }

    private static String mergePasswordAndSalt(String input, String salt) {
        if (input == null) {
            input = "";
        }

        if (StringUtils.isEmpty(salt)) {
            return input;
        } else {
            return input + "{" + salt + "}";
        }
    }

    public static String md5Encode(String input) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e1) {
            throw new IllegalArgumentException("No such algorithm MD5");
        }

        byte[] digest;

        try {
            digest = messageDigest.digest(input.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported!");
        }
        return hexEncode(digest);
    }

    public static String hexEncode(byte[] bytes) {
        final int nBytes = bytes.length;
        char[] result = new char[2 * nBytes];

        int j = 0;
        for (int i = 0; i < nBytes; i++) {
            // Char for top 4 bits
            result[j++] = HEX[(0xF0 & bytes[i]) >>> 4];
            // Bottom 4
            result[j++] = HEX[(0x0F & bytes[i])];
        }

        return new String(result);
    }

    public static byte[] hexDecode(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String hexEncode(String input) {
        try {
            return hexEncode(input.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String hexDecodeAsString(String input) {
        try {
            return new String(hexDecode(input), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String base64UrlDecode(String input) {
        Base64 decoder = new Base64(true);
        byte[] decodedBytes = decoder.decode(input);
        return new String(decodedBytes);
    }

    public static byte[] base64Decode(String input) {
        Base64 decoder = new Base64();
        return decoder.decode(input);

    }

    public static String base64Encode(byte[] input) {
        Base64 encoder = new Base64();
        return encoder.encodeBase64String(input);
    }

    public static String createBase64EncodedHMACSHA1(String secretKey, String message) {
        try {
            Mac sha1HMAC = Mac.getInstance("HmacSHA1");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");
            sha1HMAC.init(secret_key);
            return base64Encode(sha1HMAC.doFinal(message.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String createHMACSHA256(String secretKey, String message) {
        try {
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            sha256HMAC.init(secret_key);
            return base64Encode(sha256HMAC.doFinal(message.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String createHexEncodedHMACSHA256(String secretKey, String message) {
        try {
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            sha256HMAC.init(secret_key);
            return hexEncode(sha256HMAC.doFinal(message.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String createHexEncodedMDSHA256(String message) {
        return createHexEncodedMessageDigest("SHA-256", message);
    }
    
    public static String createHexEncodedMessageDigest(String algorithm, String message) {
        try {
            MessageDigest sha256MD = MessageDigest.getInstance(algorithm);
            sha256MD.update(message.getBytes("UTF-8"));
            byte[] digest = sha256MD.digest();
            return hexEncode(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String createHexEncodedHMACSHA1(String secretKey, String message) {
        try {
            Mac sha1HMAC = Mac.getInstance("HmacSHA1");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");
            sha1HMAC.init(secret_key);
            return hexEncode(sha1HMAC.doFinal(message.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String urlEncode(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String urlDecode(String encodedString) {
        try {
            return URLDecoder.decode(encodedString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
