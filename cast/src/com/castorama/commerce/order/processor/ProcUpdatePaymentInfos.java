package com.castorama.commerce.order.processor;

import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.PipelineConstants;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import com.castorama.commerce.order.CastOrderImpl;
import com.castorama.commerce.order.CastOrderTools;

import java.util.HashMap;
import java.util.Map;

/**
 * Update payment info's for order(move from astorama V3).
 *
 * @author Katsiaryna Sharstsiuk
 */
public class ProcUpdatePaymentInfos extends GenericService implements PipelineProcessor {
    /**
     * SUCCESS constant.
     */
    private static final int SUCCESS = 1;

    /**
     * orderTools property
     */
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
        if (null != map) {
            Order order = (Order) map.get(PipelineConstants.ORDER);
            OrderManager om = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);
            Map paymentInfos = (Map) map.get("paymentInfos");
            Map paymentInfosAtout = (Map) map.get("paymentInfosAtout");
            String paymentMethod = (String) map.get("paymentMethod");
            String profile = (String) map.get("profile");

            if (om != null) {
                if (order != null) {
                    CastOrderImpl ord;
                    try {
                        ord = (CastOrderImpl) order;

                        if (paymentInfos != null) {
                            String pid = (String) paymentInfos.get("profileId");
                            if (paymentInfos.get("profileId") != null) {
                                order.setProfileId(pid);
                            } else {
                                if (isLoggingError()) {
                                    logError("Error in ProcUpdatePaymentInfos.runProcess: profile id is null");
                                }
                            }
                        } else {
                        }
                    } catch (ClassCastException e) {
                        if (isLoggingError()) {
                            logError("Error in ClassCastException in ProcUpdatePaymentInfos.runProcess:", e);
                        }
                    }  // end try-catch
                } else {
                    if (isLoggingError()) {
                        logError("Error in ProcUpdatePaymentInfos.runProcess: order is null");
                    }
                }  // end if-else
            } else {
                if (isLoggingError()) {
                    logError("Error in ProcUpdatePaymentInfos.runProcess: OrderManager is null");
                }
            }  // end if-else
        } else {
            if (isLoggingError()) {
                logError("Error in ProcUpdatePaymentInfos.runProcess: map is null");
            }
        }  // end if-else

        return SUCCESS;
    }

}
