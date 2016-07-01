package com.castorama.integration.webservice.inventory.model;

import java.util.Date;
import java.util.Set;

/**
 * @author EPAM team
 */
public class LogMessage {

    private Date requestDate;
    private String orderId;
    private String storeId;
    private Set<Integer> productsIds;
    private String requestStatus;

    public LogMessage(Date requestDate,
                      String orderId,
                      String storeId,
                      Set<Integer> productsIds,
                      String requestStatus) {
        this.requestDate = requestDate;
        this.orderId = orderId;
        this.storeId = storeId;
        this.productsIds = productsIds;
        this.requestStatus = requestStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public Set<Integer> getProductsIds() {
        return productsIds;
    }

    public void setProductsIds(Set<Integer> productsIds) {
        this.productsIds = productsIds;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }
}
