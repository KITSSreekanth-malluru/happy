package com.castorama.scenario;

import java.security.SecureRandom;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

/**
 * Utility class providing method for generating unique coupon numbers.
 *
 * @author Andrei_Raichonak
 */
public class CouponIDGenerator {
    /** Date formatter */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    /** Number formatter */
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    static {
        NUMBER_FORMAT.setMinimumIntegerDigits(5);
        NUMBER_FORMAT.setMaximumIntegerDigits(5);
        NUMBER_FORMAT.setGroupingUsed(false);
    }

    /** Secure Random */
    private static final SecureRandom secureRandom = new SecureRandom();

    /** Random */
    private static final java.util.Random random = new java.util.Random();

    /** goodChar constant. */
    protected static char[] goodChar = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9'};

    /**
     * Generates a string of specified length
     *
     * @param  length string length
     *
     * @return generated string
     */
    private static String getNext(int length) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < length; i++) {
            sb.append(goodChar[random.nextInt(goodChar.length)]);
        }

        return sb.toString();
    }

    /**
     * Generates a random alphanumeric string of the fixed length
     *
     * @return generated string
     */
    public static String generate() {
        Date date = new Date();
        String dateString = DATE_FORMAT.format(date);
        Calendar.getInstance().get(Calendar.HOUR);

        String dd = dateString.substring(0, 2);
        String mm = dateString.substring(3, 5);
        String yyyy = dateString.substring(8, 10);

        String id =
            getNext(3) + mm + NUMBER_FORMAT.format(secureRandom.nextInt(10000)) + yyyy + getNext(6) + dd +
            NUMBER_FORMAT.format(secureRandom.nextInt(10000));

        return id;
    }
}
