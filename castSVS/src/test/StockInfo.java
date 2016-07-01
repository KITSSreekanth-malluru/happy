package test;

import java.util.Date;

public class StockInfo {
    int storeId;
    String productId;
    double stockAvailable;
    Date date;

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int pStoreId) {
        storeId = pStoreId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String pProductId) {
        productId = pProductId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date pDate) {
        date = pDate;
    }

    public double getStockAvailable() {
        return stockAvailable;
    }

    public void setStockAvailable(double stockAvailable) {
        this.stockAvailable = stockAvailable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockInfo stockInfo = (StockInfo) o;

        if (Double.compare(stockInfo.stockAvailable, stockAvailable) != 0) return false;
        if (storeId != stockInfo.storeId) return false;
        if (date != null ? !date.equals(stockInfo.date) : stockInfo.date != null) return false;
        if (productId != null ? !productId.equals(stockInfo.productId) : stockInfo.productId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = storeId;
        result = 31 * result + (productId != null ? productId.hashCode() : 0);
        temp = stockAvailable != +0.0d ? Double.doubleToLongBits(stockAvailable) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StockInfo{" +
                "storeId=" + storeId +
                ", productId='" + productId + '\'' +
                ", stockAvailable=" + stockAvailable +
                ", date=" + date +
                '}';
    }
}