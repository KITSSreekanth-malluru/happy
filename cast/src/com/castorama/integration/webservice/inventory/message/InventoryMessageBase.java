package com.castorama.integration.webservice.inventory.message;

import java.util.Date;

/**
 * @author EPAM team
 */
public class InventoryMessageBase {

    private String languageCode;
    protected Date creationDateTime;
    protected int storeId;

    public InventoryMessageBase(String languageCode,
                                Date creationDateTime,
                                int storeId) {
        this.languageCode = languageCode;
        this.creationDateTime = creationDateTime;
        this.storeId = storeId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public Date getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(Date creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    @Override
    public String toString() {
        return "InventoryMessageBase{" +
                "languageCode='" + languageCode + '\'' +
                ", creationDateTime=" + creationDateTime +
                ", storeId='" + storeId + '\'' +
                "}";
    }
}
