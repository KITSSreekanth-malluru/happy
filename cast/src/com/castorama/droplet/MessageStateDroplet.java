package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import com.castorama.commerce.order.CastOrderManager;

import atg.commerce.order.OrderManager;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Droplet returns order messages states for order id or message state for message id.
 * 
 * @author EPAM team
 */
public class MessageStateDroplet extends DynamoServlet {
    
	/** OUTPUT constant. */
    public static final String OUTPUT = "output";

    /** EMPTY constant. */
    public static final String EMPTY = "empty";

    /** STATES constant. */
    public static final String STATES = "states";

    /** MESSAGE_ID constant. */
    public static final String MESSAGE_ID = "messageId";

    /** ORDER_ID constant. */
    public static final String ORDER_ID = "orderId";

    /** mOrderManager constant. */
    private OrderManager mOrderManager;

    /**
     * Returns order messages states for order id or message state for message id.
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    @Override 
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                           throws ServletException, IOException {

    	boolean empty = true;
        String messageId = pRequest.getParameter(MESSAGE_ID);
        String orderId = pRequest.getParameter(ORDER_ID);

        OrderManager om = getOrderManager();
        if (om instanceof CastOrderManager) {
            CastOrderManager com = (CastOrderManager) om;
            if ((null != messageId) && (0 < messageId.length())) {
                pRequest.setParameter(STATES, com.getMessageState(messageId));
                pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
                empty = false;
            } else if ((null != orderId) && (0 < orderId.length())) {
                pRequest.setParameter(STATES, com.getOrderMessageStates(orderId));
                pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
                empty = false;
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
