package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.pricing.PriceItemDroplet;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * Extends the based behavior to create an additional output parameter.
 * 
 * @author Alena_Karpenkava
 * 
 */
public class CastPriceItem extends PriceItemDroplet {

	/** Input parameters */
	static final ParameterName ELEMENT_NAME_PARAM = ParameterName.getParameterName("elementName");

	/** Output parameters */
	static final ParameterName OUTPUT = ParameterName.getParameterName("output");
	static final ParameterName EMPTY = ParameterName.getParameterName("empty");

	/** Constants */
	static final String ELEMENT_PARAM = "element";

	/**
	 * Extends the based behavior to create an additional output parameter.
	 * 
	 * @param pRequest -
	 *            dynamo http request
	 * @param pResponse -
	 *            dynamo http response
	 * @throws IOException -
	 *             exception
	 * @throws ServletException -
	 *             exception
	 */
	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		Object obj = performPricing(pRequest, pResponse);
		if (obj != null) {
			String elementName = pRequest.getParameter(ELEMENT_NAME_PARAM);
			if (elementName == null)
				elementName = ELEMENT_PARAM;
			pRequest.setParameter(elementName, obj);
			pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
			return;
		} else {
			pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
		}
	}
}
