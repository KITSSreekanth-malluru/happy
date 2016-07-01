package com.castorama.commerce.order.processor;

import atg.commerce.order.PipelineConstants;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import atg.userprofiling.Profile;
import com.castorama.commerce.order.CastOrderImpl;
import com.castorama.commerce.order.CastOrderTools;

import java.util.HashMap;
import java.util.Map;

/**
 * Processor for back-up-ing order.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class ProcOrderBackup extends GenericService implements PipelineProcessor {
    /**
     * SUCCESS property
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
        CastOrderImpl order = (CastOrderImpl) map.get(PipelineConstants.ORDER);
        Profile profile = (Profile) map.get(PipelineConstants.PROFILE);
        if ((order != null) && (profile != null) && (getOrderTools() != null)) {
            getOrderTools().sauvegarderCommande(order, profile);
        } else {
            if (isLoggingError()) {
                logError("Error in ProcOrderBackup.runProcess: order, or profile, or OrderTools is null");
            }
        }
        return SUCCESS;
    }
}
