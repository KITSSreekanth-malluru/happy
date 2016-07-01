package com.castorama.stock.mchannel.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.Iterator;
import java.util.Map;

/**
 * Stocks bean.
 *
 * @author EPAM team
 */
public class Stocks {

    @SerializedName("storeId")
    int storeId;
    @SerializedName("productsQuantity")
    Map<String, Integer> productsQuantityMap;

    public Stocks(int storeId, Map<String, Integer> productQuantities) {
        this.storeId = storeId;
        this.productsQuantityMap = productQuantities;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public Map<String, Integer> getProductsQuantityMap() {
        return productsQuantityMap;
    }

    public void setProductsQuantityMap(Map<String, Integer> productsQuantityMap) {
        this.productsQuantityMap = productsQuantityMap;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, Integer>> iterator = productsQuantityMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            sb.append(entry.getKey());
            sb.append('=').append('"');
            sb.append(entry.getValue());
            sb.append('"');
            if (iterator.hasNext()) {
                sb.append(',').append(' ');
            }
        }

        return "Stocks{" +
                "storeId=" + storeId +
                ", productsQuantityMap=" + sb.toString() +
                "}";
    }
}
