package com.castorama.stockvisualization;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * @author EPAM team
 */
public class Stocks {

    @SerializedName("storeId")
    int storeId;
    @SerializedName("productsQuantity")
    Map<String, Long> productsQuantityMap;

    public Stocks(int storeId, Map<String, Long> productQuantities) {
        this.storeId = storeId;
        this.productsQuantityMap = productQuantities;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public Map<String, Long> getProductsQuantityMap() {
        return productsQuantityMap;
    }

    public void setProductsQuantityMap(Map<String, Long> productsQuantityMap) {
        this.productsQuantityMap = productsQuantityMap;
    }
}