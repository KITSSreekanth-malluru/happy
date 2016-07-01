package com.castorama.droplet;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;

import com.castorama.commerce.pricing.Constants;

import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Returns shipping price.
 * 
 * @author Katsiaryna Sharstsiuk
 */
public class ShippingPriceDroplet extends DynamoServlet {
	/**
	 * shippingGroups parameter name.
	 */
	private static final ParameterName SHIPPING_GROUPS = ParameterName.getParameterName("shippingGroups");

	/**
	 * Output parameter name.
	 */
	private static final ParameterName OUTPUT = ParameterName.getParameterName("output");

	/**
	 * shippingPrice parameter name.
	 */
	private static final String SHIPPING_PRICE = "shippingPrice";

	/**
	 * Returns shipping price.
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
		double shippingPrice = 0.0;
		if (shippingGroupsParam != null && shippingGroupsParam instanceof Collection) {
			for (ShippingGroup shippingGroup : (List<ShippingGroup>) shippingGroupsParam) {
				ShippingPriceInfo info = shippingGroup.getPriceInfo();
				if (info != null && info.getAdjustments() != null) {
					for (PricingAdjustment pa : (List<PricingAdjustment>) info.getAdjustments()) {
						if (!Constants.HANDLING_PRICE_ADJUSTMENT_DESCRIPTION.equalsIgnoreCase(pa
								.getAdjustmentDescription())) {
							shippingPrice += pa.getTotalAdjustment();
						}

					}

				}

			}
		}

		pRequest.setParameter(SHIPPING_PRICE, shippingPrice);
		pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
	}
}
