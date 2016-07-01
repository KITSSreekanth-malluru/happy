package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import com.castorama.commerce.order.CastOrderDetails;
import com.castorama.commerce.order.CastOrderManager;

import atg.commerce.order.OrderManager;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Extends DynamoServlet to returns order details.
 * 
 * @author EPAM team
 */
public class CastOrderDetailsDroplet extends DynamoServlet {

	/** OUTPUT constant. */
    private static final String OUTPUT = "output";

    /** EMPTY constant. */
    private static final String EMPTY = "empty";

    /** DETAILS constant. */
    private static final String DETAILS = "details";

    /** ORDER_ID constant. */
    private static final String ORDER_ID = "orderId";

    /** PROFILE_ID constant. */
    private static final String PROFILE_ID = "profileId";

    /** orderManager property. */
    private OrderManager mOrderManager;

    /**
     * Extends service method of DynamoServlet to returns order details.
     * 
     * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse) 
     */
    @Override 
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                           throws ServletException, IOException {
        boolean empty = true;
        String orderId = pRequest.getParameter(ORDER_ID);
        String profileId = pRequest.getParameter(PROFILE_ID);

        OrderManager om = getOrderManager();
        if (om instanceof CastOrderManager) {
            CastOrderManager com = (CastOrderManager) om;
            if ((null != orderId) && (0 < orderId.length())) {
                CastOrderDetails orderDetails = com.getOrderDetails(orderId, profileId); 
            	if ( null != orderDetails ) {
            		pRequest.setParameter(DETAILS, orderDetails);
            		pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
            		empty = false;
            	}
            }
        }
        if (empty) {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
        }

    }

    /**
     * Returns orderManager property.
     *
     * @return orderManager property.
     */
    public OrderManager getOrderManager() {
        return mOrderManager;
    }

    /**
     * Sets the value of the orderManager property.
     *
     * @param pOrderManager parameter to set.
     */
    public void setOrderManager(OrderManager pOrderManager) {
        mOrderManager = pOrderManager;
    }

}
