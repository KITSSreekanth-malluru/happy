package com.castorama.stock.supervision;

import java.io.Serializable;
import java.util.Date;

public class SupervisionRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date date;
    private int stockOkCount;
    private int stockKoCount;
    private int priceOkCount;
    private int priceKoCount;
    private int orderCount;

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getPriceKoCount() {
        return priceKoCount;
    }

    public void setPriceKoCount(int priceKoCount) {
        this.priceKoCount = priceKoCount;
    }

    public int getPriceOkCount() {
        return priceOkCount;
    }

    public void setPriceOkCount(int priceOkCount) {
        this.priceOkCount = priceOkCount;
    }

    public int getStockKoCount() {
        return stockKoCount;
    }

    public void setStockKoCount(int stockKoCount) {
        this.stockKoCount = stockKoCount;
    }

    public int getStockOkCount() {
        return stockOkCount;
    }

    public void setStockOkCount(int stockOkCount) {
        this.stockOkCount = stockOkCount;
    }
}
