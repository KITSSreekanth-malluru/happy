package com.castorama.commerce.order.processor;

import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.processor.EventSender;
import atg.commerce.profile.CommerceProfileTools;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineResult;
import com.castorama.scenario.CastoOrderMessage;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Send event for castorama referer program.
 *
 * @author EPAM team
 */
public class ProcSendCastoOrderMessage extends EventSender {
    /**
     * profileTools property
     */
    private CommerceProfileTools mProfileTools;

    /**
     * messageSourceName property
     */
    private String mMessageSourceName = null;

    /**
     * Sets the value of the profileTools property.
     *
     * @param pProfileTools parameter to set.
     */
    public void setProfileTools(CommerceProfileTools pProfileTools) {
        mProfileTools = pProfileTools;
    }

    /**
     * Returns profileTools property.
     *
     * @return profileTools property.
     */
    public CommerceProfileTools getProfileTools() {
        return mProfileTools;
    }

    /**
     * Sets the value of the messageSourceName property.
     *
     * @param pMessageSourceName parameter to set.
     */
    public void setMessageSourceName(String pMessageSourceName) {
        mMessageSourceName = pMessageSourceName;
    }

    /**
     * Returns messageSourceName property.
     *
     * @return messageSourceName property.
     */
    public String getMessageSourceName() {
        return mMessageSourceName;
    }

    /**
     * Create event for Castorama referer program.
     *
     * @param pParam  parameter
     * @param pResult parameter
     * @return created event
     * @throws Exception                 exception
     * @throws InvalidParameterException exception
     */
    public Serializable createEventToSend(Object pParam, PipelineResult pResult) throws Exception {
        HashMap map = (HashMap) pParam;
        Order order = (Order) map.get(PipelineConstants.ORDER);

        if (order == null) {
            if (isLoggingError()) {
                logError("Error in ProcSendCastoOrderMessage.createEventToSend: order is null");
            }
        }

        RepositoryItem profile = getProfileTools().getProfileForOrder(order);
        CastoOrderMessage soMessage = new CastoOrderMessage(profile, order);
        soMessage.setOrder(order);
        soMessage.setSource(getMessageSourceName());
        soMessage.setId(getNextMessageId());
        soMessage.setOriginalSource(getMessageSourceName());
        soMessage.setOriginalId(soMessage.getId());

        return soMessage;

    }
}
