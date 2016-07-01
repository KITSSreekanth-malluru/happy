package com.castorama.droplet;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.commerce.pricing.Constants;

/**
 * Returns handling price.
 * 
 * @author Katsiaryna Sharstsiuk
 */
public class HandlingPriceDroplet extends DynamoServlet {
	/**
	 * shippingGroups parameter name.
	 */
	private static final ParameterName SHIPPING_GROUPS = ParameterName.getParameterName("shippingGroups");

	/**
	 * Output parameter name.
	 */
	private static final ParameterName OUTPUT = ParameterName.getParameterName("output");

	/**
	 * handlingPrice parameter name.
	 */
	private static final String HANDLING_PRICE = "handlingPrice";

	/**
	 * Returns handling price.
	 * 
	 * @param pRequest -
	 *            http request
	 * @param pResponse -
	 *            http response
	 * @throws ServletException
	 *             if error occurs
	 * @throws IOException
	 *             if error occurs
	 */
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		Object shippingGroupsParam = pRequest.getObjectParameter(SHIPPING_GROUPS);
		double handlingPrice = 0.0;
		if (shippingGroupsParam != null && shippingGroupsParam instanceof Collection) {
			for (ShippingGroup shippingGroup : (List<ShippingGroup>) shippingGroupsParam) {
				ShippingPriceInfo info = shippingGroup.getPriceInfo();
				if (info != null && info.getAdjustments() != null) {
					for (PricingAdjustment pa : (List<PricingAdjustment>) info.getAdjustments()) {
						if (Constants.HANDLING_PRICE_ADJUSTMENT_DESCRIPTION.equalsIgnoreCase(pa
								.getAdjustmentDescription())) {
							handlingPrice += pa.getTotalAdjustment();
						}

					}

				}

			}
		}

		pRequest.setParameter(HANDLING_PRICE, handlingPrice);
		pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
	}

}
