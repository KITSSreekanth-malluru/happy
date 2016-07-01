package com.castorama.commerce.order.processor;

import atg.commerce.order.Order;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.ShippingGroup;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import com.castorama.commerce.order.CastOrderImpl;
import com.castorama.commerce.order.CastOrderTools;
import com.castorama.commerce.pricing.CastoShippingTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processor for process order amounts - save to repository.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class ProcAffectAmount extends GenericService implements PipelineProcessor {
    /**
     * SUCCESS constant
     */
    private static final int SUCCESS = 1;

    /**
     * mOrderTools property
     */
    private CastOrderTools mOrderTools;

    /**
     * mShippingTools property
     */
    private CastoShippingTools mShippingTools;

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

    /**
     * Returns shippingTools property.
     *
     * @return shippingTools property.
     */
    public CastoShippingTools getShippingTools() {
        return mShippingTools;
    }

    /**
     * Sets the value of the shippingTools property.
     *
     * @param pShippingTools parameter to set.
     */
    public void setShippingTools(CastoShippingTools pShippingTools) {
        mShippingTools = pShippingTools;
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

        if (order != null) {
            List shippingGroups = order.getShippingGroups();
            getOrderTools().affecterTotalCommande((CastOrderImpl) order);
            getOrderTools().affecterMontantFraisLivraisonNonRemise((CastOrderImpl) order);
            getOrderTools().affecterMontantRemiseLivraisonEtMonteeEtage((CastOrderImpl) order);
            getOrderTools().affecterMontantFraisDePreparationRemise((CastOrderImpl) order);
            getOrderTools().affecterMontantRemiseTotal((CastOrderImpl) order);
            getOrderTools().affecterMontantsPFLPFT((CastOrderImpl) order);
            if ((getShippingTools() != null) && (shippingGroups != null)) {
                for (ShippingGroup shippingGroup : (List<ShippingGroup>) shippingGroups) {
                    getShippingTools().affecterPoidsTotalShippingGroup(shippingGroup);
                }
            } else {
                if (isLoggingError()) {
                    logError("Error in ProcAffectAmount.runProcess: ShippingTools or ShippingGroups is null");
                }
            }
        }

        return SUCCESS;
    }

}
