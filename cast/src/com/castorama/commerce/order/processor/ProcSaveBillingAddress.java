package com.castorama.commerce.order.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.commerce.order.CreditCard;
import atg.commerce.order.Order;
import atg.commerce.order.OrderTools;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PipelineConstants;

import atg.commerce.profile.CommercePropertyManager;

import atg.repository.RepositoryItem;

import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import atg.userprofiling.Profile;

import com.castorama.commerce.order.CastOrderTools;

/**
 * Processor for saving billing address
 *
 * @author Katsiaryna Sharstsiuk
 */
public class ProcSaveBillingAddress implements PipelineProcessor {
    /** SUCCESS property */
    private static final int SUCCESS = 1;

    /** orderTools property */
    private CastOrderTools mOrderTools;

    /**
     * Returns orderTools property.
     *
     * @return orderTools property.
     */
    public CastOrderTools getOrderTools() {
        return mOrderTools;
    }

    /**
     * Sets the value of the orderTools property.
     *
     * @param pOrderTools parameter to set.
     */
    public void setOrderTools(CastOrderTools pOrderTools) {
        mOrderTools = pOrderTools;
    }

    /* (non-Javadoc)
     * @see atg.service.pipeline.PipelineProcessor#getRetCodes()
     */
    public int[] getRetCodes() {
        int[] retCodes = {SUCCESS};
        return retCodes;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.service.pipeline.PipelineProcessor#runProcess(java.lang.Object, atg.service.pipeline.PipelineResult)
     */
    public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
        Map map = (HashMap) pParam;
        Order order = (Order) map.get(PipelineConstants.ORDER);
        Profile profile = (Profile) map.get(PipelineConstants.PROFILE);
        try {
            if ((order != null) && (profile != null) && (order.getPaymentGroupCount() > 0)) {
                /*
                 * copy billing address from profile to paymentGroup
                 */
                OrderTools orderTools = getOrderTools();

                if ((orderTools != null) && (order != null)) {
                    CreditCard cc = getCreditCard(order);
                    final CommercePropertyManager cpmgr =
                        (CommercePropertyManager) getOrderTools().getProfileTools().getPropertyManager();
                    if (cc != null) {
                        orderTools.copyAddress((RepositoryItem) profile.getPropertyValue(cpmgr
                                                                                         .getBillingAddressPropertyName()),
                                               cc.getBillingAddress());
                    }

                }

            }
        } catch (Exception e) {
            pResult.addError("ProcSaveBillingAddress", e.getMessage());
        }  // end try-catch

        if (pResult.hasErrors()) {
            return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
        }

        return SUCCESS;
    }

    /**
     * Returns creditCard property.
     *
     * @param  pOrder parameter to set.
     *
     * @return creditCard property.
     */
    public CreditCard getCreditCard(Order pOrder) {
        if (pOrder == null) {
            return null;
        }

        List paymentGroups = pOrder.getPaymentGroups();

        if ((paymentGroups == null) || paymentGroups.isEmpty()) {
            return null;
        }

        PaymentGroup pg = null;

        for (int i = 0; i < paymentGroups.size(); i++) {
            pg = (PaymentGroup) paymentGroups.get(i);

            if (pg instanceof CreditCard) {
                return (CreditCard) pg;
            }
        }

        return null;
    }
}
