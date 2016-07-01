package com.castorama.utils;

public class CastUtils {
    private static final int PROD_ID_LENGTH = 8;
    
    /**
     * StockViz requires productIds with 8 char length. This method converts 
     * any code article according to supported StockViz format.
     * 
     * @param prodId
     * @return correct stock visualization productId
     */
    public static String buildSVSProductId(String prodId) {
        int zeroToAdd = PROD_ID_LENGTH - prodId.length();
        StringBuilder productId = new StringBuilder(prodId);
        for (int i = 0; i < zeroToAdd; i++) {
            productId.insert(0, "0");
        }
        return productId.toString();
    }

}
