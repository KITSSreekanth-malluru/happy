package com.castorama.utils;

import java.io.IOException;

import javax.servlet.ServletException;

import com.castorama.commerce.order.CastOrderManager;

import atg.commerce.order.OrderManager;
import atg.droplet.GenericFormHandler;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class UtilFormHandler extends GenericFormHandler {
	
	/**
	 * Form redirect success URL
	 */
	private String mSuccessURL;
	
	/**
	 * Form redirect failure URL
	 */
	private String mFailureURL;

	/**
	 * User Id
	 */
	private String mUserId;
	
	/**
	 * Reads Back Office orders to Front Office for user
	 * @param pRequest - the servlet's request
	 * @param pResponse - the servlet's response
	 * @return - If redirect (for whatever reason) to a new page occurred,
	 *           return false.  If NO redirect occurred, return true.
	 * @throws ServletException - if there was an error while executing the code
	 * @throws IOException      - if there was an error with servlet input output
	 */
	public boolean handleReadOrders(
			DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		
		String userId = getUserId();
		if ( null != userId ) {
			OrderManager om = (OrderManager) resolveName("/atg/commerce/order/OrderManager");
			if (om instanceof CastOrderManager) {
				CastOrderManager com = (CastOrderManager) om;
				com.readOrders(userId);
			}
		}
		return checkFormRedirect(getSuccessURL(), getFailureURL(), pRequest, pResponse);
	}

	/**
	 * Gets form redirect success URL
	 * @return Form redirect success URL
	 */
	public String getSuccessURL() {
		return mSuccessURL;
	}

	/**
	 * Sets form redirect success URL
	 * @param pSuccessURL - form redirect success URL
	 */
	public void setSuccessURL(String pSuccessURL) {
		this.mSuccessURL = pSuccessURL;
	}

	/**
	 * Gets form redirect failure URL
	 * @return Form redirect failure URL
	 */
	public String getFailureURL() {
		return mFailureURL;
	}

	/**
	 * Sets form redirect failure URL
	 * @param pSuccessURL - form redirect failure URL
	 */
	public void setFailureURL(String pFailureURL) {
		this.mFailureURL = pFailureURL;
	}

	/**
	 * Gets user id
	 * @return - user id
	 */
	public String getUserId() {
		return mUserId;
	}

	/**
	 * Sets user id
	 * @param pUserId user id
	 */
	public void setUserId(String pUserId) {
		this.mUserId = pUserId;
	}

}
