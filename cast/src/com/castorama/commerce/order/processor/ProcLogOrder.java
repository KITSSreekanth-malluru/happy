package com.castorama.commerce.order.processor;

import java.util.Map;

import atg.commerce.order.PipelineConstants;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import com.castorama.commerce.order.CastOrderImpl;
import com.castorama.commerce.order.CastOrderManager;
import com.castorama.constantes.CastoConstantesOrders;

/**
 * Process log order into casto journal.
 * 
 * @author Vasili_Ivus
 *
 */

public class ProcLogOrder implements PipelineProcessor  {

	/** SUCCESS constant. */
    public static final int SUCCESS = 1;

    /** CALL_CENTER_ACTION constant. */
    public static final String CALL_CENTER_ACTION = "validation_CALL_CENTER_PAYBOX";
    
	/** orderManager propertiy. */
    private CastOrderManager mOrderManager;
    
    

	/**
	 * Returns all result codes of this PipelineProcessor.
	 * @return all result codes of this PipelineProcessor.
	 */
    public int[] getRetCodes() {
        int[] retCodes = {SUCCESS};
        return retCodes;
	}

    /**
     * Process log order into casto journal.
     */
	public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
		CastOrderManager com = getOrderManager();
		if ( null != com ) {
			if (pParam instanceof Map) {
				CastOrderImpl order = (CastOrderImpl) ((Map) pParam).get(PipelineConstants.ORDER);
				if ( CastoConstantesOrders.PAYMENT_SOURCE_CALL_CENTER.equalsIgnoreCase(order.getPaymentSource()) ) {
					com.logOrder(order, CALL_CENTER_ACTION);
				}
			}
		}
		return SUCCESS;
	}

	/**
	 * @return the orderManager
	 */
	public CastOrderManager getOrderManager() {
		return mOrderManager;
	}

	/**
	 * @param pOrderManager the orderManager to set
	 */
	public void setOrderManager(CastOrderManager pOrderManager) {
		this.mOrderManager = pOrderManager;
	}

}
