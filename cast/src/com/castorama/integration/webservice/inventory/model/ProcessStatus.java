package com.castorama.integration.webservice.inventory.model;


import com.google.gson.annotations.SerializedName;

/**
 * @author EPAM team
 */
public enum ProcessStatus {

    @SerializedName("STOCK_IS_ENOUGH")
    STOCK_IS_ENOUGH("STOCK_IS_ENOUGH"),

    @SerializedName("STOCK_IS_NOT_ENOUGH")
    STOCK_IS_NOT_ENOUGH("STOCK_IS_NOT_ENOUGH"),

    @SerializedName("CALL_PROCESS_ERROR")
    CALL_PROCESS_ERROR("CALL_PROCESS_ERROR");

    private ProcessStatus(final String text) {
        this.status = text;
    }

    private final String status;

    @Override
    public String toString() {
        return status;
    }
}
