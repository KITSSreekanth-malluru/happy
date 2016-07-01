package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import com.castorama.checkout.CastClaimableManager;
import com.castorama.commerce.order.CastOrderManager;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Extends DynamoServlet to store paybox handled flag into order.
 * 
 * @author Vasili_Ivus
 *
 */
public class PayboxHandledDroplet extends DynamoServlet {
    
	/** OUTPUT constant. */
    public static final String OUTPUT = "output";
    
	/** EMPTY constant. */
    public static final String EMPTY = "empty";

    /** ORDER_ID constant. */
    public static final String ORDER_ID = "orderId";

    /** USER_ID constant. */
    public static final String USER_ID = "userId";

    /** ORDER_TOTAL constant. */
    public static final String ORDER_TOTAL = "orderTotal";

    /** SOURCE constant. */
    public static final String SOURCE = "source";

    /** MIN_ORDER_TOTAL constant. */
    private static final int MIN_ORDER_TOTAL = 99;

    /** orderManager property. */
    private CastOrderManager mOrderManager;


	/**
	 * Extends DynamoServlet service method to store paybox handled flag into order.
	 * 
	 * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException,IOException {
		
		String orderId = pRequest.getParameter(ORDER_ID);
		String orderTotal = pRequest.getParameter(ORDER_TOTAL);
		if ( checkPaymetAvaible(orderId, orderTotal) ) {
			getOrderManager().markOrderAsHandledPaybox(orderId, Boolean.TRUE, pRequest.getParameter(SOURCE), pRequest.getParameter(USER_ID));
			pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
		} else {
			pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
		}
	}

	private boolean checkPaymetAvaible(String orderId, String orderTotal) {
		boolean result = false;
		if ( null != orderId && 0 < orderId.trim().length() ) {
			int total;
			try {
				total = Integer.parseInt(orderTotal);
			} catch ( Exception e ) {
				total = 0;
			}
			if ( MIN_ORDER_TOTAL < total ) {
				result = true;
			}
		}
		return result;
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
