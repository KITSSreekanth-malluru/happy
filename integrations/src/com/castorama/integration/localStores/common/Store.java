package com.castorama.integration.localStores.common;

public class Store {
    private String castoramaStoreId;
    private String atgStoreId;
    private String localPriceList;
    private boolean retraitMagasin;
    private boolean localPrix;

    public Store(String castoramaStoreId, String atgStoreId, String localPriceList, boolean retraitMagasin, boolean localPrix) {
        this.castoramaStoreId = castoramaStoreId;
        this.atgStoreId = atgStoreId;
        this.localPriceList = localPriceList;
        this.retraitMagasin = retraitMagasin;
        this.localPrix = localPrix;
    }

    public String getCastoramaStoreId() {
        return castoramaStoreId;
    }

    public void setCastoramaStoreId(String castoramaStoreId) {
        this.castoramaStoreId = castoramaStoreId;
    }

    public String getAtgStoreId() {
        return atgStoreId;
    }

    public void setAtgStoreId(String atgStoreId) {
        this.atgStoreId = atgStoreId;
    }

    public String getLocalPriceList() {
        return localPriceList;
    }

    public void setLocalPriceList(String localPriceList) {
        this.localPriceList = localPriceList;
    }

    public boolean isRetraitMagasin() {
        return retraitMagasin;
    }

    public void setRetraitMagasin(boolean retraitMagasin) {
        this.retraitMagasin = retraitMagasin;
    }

    public boolean isLocalPrix() {
        return localPrix;
    }

    public void setLocalPrix(boolean localPrix) {
        this.localPrix = localPrix;
    }
}
