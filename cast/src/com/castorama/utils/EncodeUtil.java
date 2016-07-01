package com.castorama.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class EncodeUtil {

    public static String calculateHash(MessageDigest alg, String messString) {
        alg.update(messString.getBytes());
        byte[] hash = alg.digest();
        return byteArrayHex(hash);
    }

    public static String byteArrayHex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public static String encodeParameter(String message) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return calculateHash(md5, calculateHash(sha1, message));

    }
}
