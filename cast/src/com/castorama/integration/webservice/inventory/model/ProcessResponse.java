package com.castorama.integration.webservice.inventory.model;

import com.google.gson.GsonBuilder;

public class ProcessResponse {

    ProcessStatus processStatus;
    String processMessage;

    public ProcessResponse(ProcessStatus processStatus, String processMessage) {
        this.processStatus = processStatus;
        this.processMessage = processMessage;
    }

    public ProcessStatus getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(ProcessStatus processStatus) {
        this.processStatus = processStatus;
    }

    public String getProcessMessage() {
        return processMessage;
    }

    public void setProcessMessage(String processMessage) {
        this.processMessage = processMessage;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this);
    }
}
