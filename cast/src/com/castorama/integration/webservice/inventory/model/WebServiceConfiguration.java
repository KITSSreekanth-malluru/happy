package com.castorama.integration.webservice.inventory.model;

/**
 * @author EPAM team
 */
public class WebServiceConfiguration {

    private String webServiceRequestUrl;
    private String stockIsNotEnoughUri;
    private String ajaxRequestUrlTemplate;
    private int ajaxRequestTimeout;
    private int popupDelayTime;
    private boolean inventoryCheckIsActive;
    private String requestWebServiceDateFormat;
    private String responseWebServiceDateFormat;
    private int waitingForWebserviceResponse;

    public String getWebServiceRequestUrl() {
        return webServiceRequestUrl;
    }

    public void setWebServiceRequestUrl(String webServiceRequestUrl) {
        this.webServiceRequestUrl = webServiceRequestUrl;
    }

    public String getAjaxRequestUrlTemplate() {
        return ajaxRequestUrlTemplate;
    }

    public void setAjaxRequestUrlTemplate(String ajaxRequestUrlTemplate) {
        this.ajaxRequestUrlTemplate = ajaxRequestUrlTemplate;
    }

    public int getAjaxRequestTimeout() {
        return ajaxRequestTimeout;
    }

    public void setAjaxRequestTimeout(int ajaxRequestTimeout) {
        this.ajaxRequestTimeout = ajaxRequestTimeout;
    }

    public boolean isInventoryCheckIsActive() {
        return inventoryCheckIsActive;
    }

    public void setInventoryCheckIsActive(boolean inventoryCheckIsActive) {
        this.inventoryCheckIsActive = inventoryCheckIsActive;
    }

    public String getStockIsNotEnoughUri() {
        return stockIsNotEnoughUri;
    }

    public void setStockIsNotEnoughUri(String stockIsNotEnoughUri) {
        this.stockIsNotEnoughUri = stockIsNotEnoughUri;
    }

    public String getRequestWebServiceDateFormat() {
        return requestWebServiceDateFormat;
    }

    public void setRequestWebServiceDateFormat(String requestWebServiceDateFormat) {
        this.requestWebServiceDateFormat = requestWebServiceDateFormat;
    }

    public String getResponseWebServiceDateFormat() {
        return responseWebServiceDateFormat;
    }

    public void setResponseWebServiceDateFormat(String responseWebServiceDateFormat) {
        this.responseWebServiceDateFormat = responseWebServiceDateFormat;
    }

    public int getPopupDelayTime() {
        return popupDelayTime;
    }

    public void setPopupDelayTime(int popupDelayTime) {
        this.popupDelayTime = popupDelayTime;
    }

    public int getWaitingForWebserviceResponse() {
        return waitingForWebserviceResponse;
    }

    public void setWaitingForWebserviceResponse(int waitingForWebserviceResponse) {
        this.waitingForWebserviceResponse = waitingForWebserviceResponse;
    }
}
