package com.castorama.utils;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordLinkGenerator {

    public static String generateKeyParameter(String message) throws NoSuchAlgorithmException {
        String encode = EncodeUtil.encodeParameter(message);
        String key = generateRandomString() + encode + generateRandomString();
        return key;
    }

    public static String generateRandomString() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
}
