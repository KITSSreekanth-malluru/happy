package com.castorama.commerce.order.processor;

import atg.commerce.order.PipelineConstants;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import com.castorama.commerce.order.CastOrderImpl;
import com.castorama.commerce.order.CastOrderManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Processor for saving additional information for displaying on order history
 * page.
 *
 * @author Katsiaryna Sharstsiuk
 * @author Vasili Ivus
 */
public class ProcSaveInfoForOrderSummary extends GenericService implements PipelineProcessor {


    /**
     * SUCCESS property
     */
    private static final int SUCCESS = 1;


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
            CastOrderImpl order = (CastOrderImpl) map.get(PipelineConstants.ORDER);
            CastOrderManager orderManager = (CastOrderManager) map.get(PipelineConstants.ORDERMANAGER);
            orderManager.saveInfoForOrderSummary(order);
        } else {
            if (isLoggingError()) {
                logError("Error in ProcSaveInfoForOrderSummary.runProcess: map is null");
            }
        }

        return SUCCESS;
    }


}
