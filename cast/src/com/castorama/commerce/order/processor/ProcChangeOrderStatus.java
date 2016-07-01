package com.castorama.commerce.order.processor;

import java.util.HashMap;
import java.util.Map;

import atg.commerce.order.PipelineConstants;

import atg.commerce.states.OrderStates;

import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import com.castorama.commerce.order.CastOrderImpl;
import com.castorama.commerce.states.BOOrderStates;
import com.castorama.commerce.states.CastOrderStates;

import com.castorama.payment.PaymentStates;

/**
 * Processor for process order status - save to repository.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class ProcChangeOrderStatus implements PipelineProcessor {
    /** SUCCESS constant. */
    private static final int SUCCESS = 1;

    /** orderStates property */
    private CastOrderStates mOrderStates;

    /** bOOrderStates property */
    private BOOrderStates mBOOrderStates;

    /** paymentStates property */
    private PaymentStates mPaymentStates;

    /* (non-Javadoc)
     * @see atg.service.pipeline.PipelineProcessor#getRetCodes()
     */
    public int[] getRetCodes() {
        int[] retCodes = {SUCCESS};
        return retCodes;
    }

    /**
     * Returns orderStates property
     *
     * @return the orderStates
     */
    public CastOrderStates getOrderStates() {
        return mOrderStates;
    }

    /**
     * Sets orderStates property
     *
     * @param pOrderStates the orderStates to set
     */
    public void setOrderStates(CastOrderStates pOrderStates) {
        mOrderStates = pOrderStates;
    }

    /**
     * Returns bOOrderStates property
     *
     * @return the bOOrderStates
     */
    public BOOrderStates getBOOrderStates() {
        return mBOOrderStates;
    }

    /**
     * Sets bOOrderStates property
     *
     * @param pBOOrderStates the bOOrderStates to set
     */
    public void setBOOrderStates(BOOrderStates pBOOrderStates) {
        mBOOrderStates = pBOOrderStates;
    }

    /**
     * Returns paymentStates property
     *
     * @return the paymentStates property value
     */
    public PaymentStates getPaymentStates() {
        return mPaymentStates;
    }

    /**
     * Sets paymentStates property
     *
     * @param pPaymentStates the paymentStates to set
     */
    public void setPaymentStates(PaymentStates pPaymentStates) {
        mPaymentStates = pPaymentStates;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.service.pipeline.PipelineProcessor#runProcess(java.lang.Object, atg.service.pipeline.PipelineResult)
     */
    public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
        Map map = (HashMap) pParam;
        String paymentMethod = (String) map.get("paymentMethod");
        CastOrderImpl order = (CastOrderImpl) map.get(PipelineConstants.ORDER);

        BOOrderStates boOrderStates = getBOOrderStates();
        CastOrderStates castOrderStates = getOrderStates();
        PaymentStates paymentStates = getPaymentStates();

        if ((boOrderStates != null) && (castOrderStates != null)) {
            if ((boOrderStates != null) && (castOrderStates != null)) {
                int boStateNum = boOrderStates.getStateValue(BOOrderStates.INCOMPLETE);
                int stateNum = castOrderStates.getStateValue(CastOrderStates.INCOMPLETE);

                if ((paymentMethod.trim()).equalsIgnoreCase(paymentStates.getStateString(paymentStates.getStateValue(paymentStates.CHEQUE)))) {
                    boStateNum = boOrderStates.getStateValue(BOOrderStates.PENDING_CHEQUE);
                    stateNum = castOrderStates.getStateValue(CastOrderStates.PENDING_CHEQUE);

                } else if ((paymentMethod.trim()).equalsIgnoreCase(paymentStates.getStateString(paymentStates
                                                                                                    .getStateValue(paymentStates.CALL_CENTER)))) {
                    boStateNum = boOrderStates.getStateValue(BOOrderStates.PENDING_CALL_CENTER);
                    stateNum = castOrderStates.getStateValue(CastOrderStates.PENDING_CALL_CENTER);

                } else if ((paymentMethod.trim()).equalsIgnoreCase(paymentStates.getStateString(paymentStates
                                                                                                    .getStateValue(paymentStates.VIREMENT)))) {
                    boStateNum = boOrderStates.getStateValue(BOOrderStates.PENDING_VIREMENT);
                    stateNum = castOrderStates.getStateValue(CastOrderStates.PENDING_VIREMENT);

                } else {
                    boStateNum = boOrderStates.getStateValue(BOOrderStates.VALIDE);
                    stateNum = castOrderStates.getStateValue(OrderStates.SUBMITTED);
                }

                order.setStateAsString(castOrderStates.getStateString(stateNum));
                order.setStateDetail(castOrderStates.getStateDescription(stateNum));

                order.setBOState(boOrderStates.getStateString(boStateNum));
                order.setBOStateNum(boStateNum);
                order.setBOStateDetail((boOrderStates.getStateDescription(boStateNum)));
            }  // end if
        }  // end if

        return SUCCESS;
    }
}
