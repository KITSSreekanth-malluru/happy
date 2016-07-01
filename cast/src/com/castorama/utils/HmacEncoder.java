package com.castorama.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacEncoder {
    private static final String HMAC_SHA512_ALGORITHM = "HmacSHA512";

    public static String calculateHmac(String encodeString, String key)
            throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] binaryKey = hexStringToByteArray(key);
        SecretKeySpec signingKey = new SecretKeySpec(binaryKey, HMAC_SHA512_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA512_ALGORITHM);
        mac.init(signingKey);
        String hmacString = toHexString(mac.doFinal(encodeString.getBytes()));
        return hmacString.toUpperCase();
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        if (!s.matches("[0-9A-Fa-f]+")) {
            throw new IllegalArgumentException("contains illegal character for hexBinary: " + s);
        }
        int length = s.length();
        if (length % 2 != 0) {
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);
        }
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
