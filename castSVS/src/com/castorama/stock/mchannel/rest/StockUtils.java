package com.castorama.stock.mchannel.rest;


public class StockUtils {

    private static final int PROD_ID_LENGTH = 8;

    public static String codeArticleToProductId(String prodId) {
        int zeroToAdd = PROD_ID_LENGTH - prodId.length();
        StringBuilder productId = new StringBuilder(prodId);
        for (int i = 0; i < zeroToAdd; i++) {
            productId.insert(0, "0");
        }
        return productId.toString();
    }
}
