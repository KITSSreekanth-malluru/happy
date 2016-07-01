package com.castorama.stock.supervision;

import java.io.Serializable;
import java.util.Date;

public class SyncJournalRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private int storeId;
    private int stockEntityNumber;
    private int stockFileSequence;
    private int stockStatus;
    private int priceEntityNumber;
    private int priceFileSequence;
    private int priceStatus;
    private int ordersCount;
    private int orderStatus;
    private Date lastUpdate;

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getStockFileSequence() {
        return stockFileSequence;
    }

    public void setStockFileSequence(int stockFileSequence) {
        this.stockFileSequence = stockFileSequence;
    }

    public int getStockEntityNumber() {
        return stockEntityNumber;
    }

    public void setStockEntityNumber(int stockEntityNumber) {
        this.stockEntityNumber = stockEntityNumber;
    }

    public int getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(int stockStatus) {
        this.stockStatus = stockStatus;
    }

    public int getPriceEntityNumber() {
        return priceEntityNumber;
    }

    public void setPriceEntityNumber(int priceEntityNumber) {
        this.priceEntityNumber = priceEntityNumber;
    }

    public int getPriceFileSequence() {
        return priceFileSequence;
    }

    public void setPriceFileSequence(int priceFileSequence) {
        this.priceFileSequence = priceFileSequence;
    }

    public int getPriceStatus() {
        return priceStatus;
    }

    public void setPriceStatus(int priceStatus) {
        this.priceStatus = priceStatus;
    }

    public int getOrdersCount() {
        return ordersCount;
    }

    public void setOrdersCount(int ordersCount) {
        this.ordersCount = ordersCount;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
