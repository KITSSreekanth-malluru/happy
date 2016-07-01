package com.castorama.integration.webservice.inventory.model;

import com.castorama.utils.ContextState;

import java.util.Date;
import java.util.Set;

/**
 * @author EPAM team
 */
public class RequestParametersWrapper {
    private String orderId;
    private int storeId;
    private Date time;
    private Set<Integer> codeArticles;
    private ContextState contextState;

    public RequestParametersWrapper(String orderId, int storeId, Date time,
                                    Set<Integer> codeArticles, ContextState contextState) {
        this.orderId = orderId;
        this.storeId = storeId;
        this.time = time;
        this.codeArticles = codeArticles;
        this.contextState = contextState;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Set<Integer> getCodeArticles() {
        return codeArticles;
    }

    public void setCodeArticles(Set<Integer> codeArticles) {
        this.codeArticles = codeArticles;
    }

    public ContextState getContextState() {
        return contextState;
    }

    public void setContextState(ContextState contextState) {
        this.contextState = contextState;
    }
}
