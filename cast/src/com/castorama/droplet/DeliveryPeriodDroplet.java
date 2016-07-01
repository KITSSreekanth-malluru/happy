package com.castorama.droplet;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;

import com.castorama.commerce.order.CastCommerceItemImpl;
import com.castorama.commerce.pricing.CastoShippingTools;
import com.castorama.constantes.CastoConstantesOrders;
import com.castorama.utils.CommonHelper;

import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Returns delivery period code localized string description.
 * Used by /com/castorama/droplet/DeliveryPeriodDroplet component.
 * @author Vasili_Ivus
 *
 */
public class DeliveryPeriodDroplet extends DynamoServlet {

	/** Input parameters */
	static final ParameterName ELEMENT_NAME_PARAM = ParameterName.getParameterName("elementName");
	static final ParameterName COMMERCE_ITEM_PARAM = ParameterName.getParameterName("commerceItem");

	/** Output parameters */
	static final ParameterName OUTPUT = ParameterName.getParameterName("output");
	static final ParameterName EMPTY = ParameterName.getParameterName("empty");

	/** Constants */
	static final String ELEMENT_PARAM = "element";

    /** shippingTools property */
    private CastoShippingTools mShippingTools;
	
	/**
	 * Extends the based behavior to return delivery period code localized string description.
	 * 
	 * @param pRequest -
	 *            dynamo http request
	 * @param pResponse -
	 *            dynamo http response
	 * @throws IOException -
	 *             exception, throws when logic errors.
	 * @throws ServletException -
	 *             exception, throws when io errors.
	 */
	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {

		boolean isEmpty = true;
		
		CastoShippingTools shippingTools = getShippingTools();
		if ( null != shippingTools ) {
			Object commerceItemParam = pRequest.getObjectParameter(COMMERCE_ITEM_PARAM);
			Integer code = null;
			if (commerceItemParam instanceof RepositoryItem) {
				RepositoryItem commerceItem = (RepositoryItem) commerceItemParam;
				code = (Integer) commerceItem.getPropertyValue(CastoConstantesOrders.CITEM_PROPERTY_DELIVERY_PERIOD);
				
			} else if (commerceItemParam instanceof CastCommerceItemImpl) {
				CastCommerceItemImpl castCommerceItem = (CastCommerceItemImpl) commerceItemParam;
				code = castCommerceItem.getDeliveryPeriod();
			}
			if ( null != code ) {
				Locale locale = CommonHelper.getUserLocale(pRequest);
				String elementName = pRequest.getParameter(ELEMENT_NAME_PARAM);
				if ( null == elementName ) {
					elementName = ELEMENT_PARAM;
				}
				pRequest.setParameter(elementName, shippingTools.getDeliveryPeriodDescription(code.intValue(), locale));
				pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
				isEmpty = false;
			}
		} 
		if (isEmpty) {
			pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
		}
	}

	/**
	 * @return the mShippingTools
	 */
	public CastoShippingTools getShippingTools() {
		return mShippingTools;
	}

	/**
	 * @param shippingTools the mShippingTools to set
	 */
	public void setShippingTools(CastoShippingTools pShippingTools) {
		mShippingTools = pShippingTools;
	}

}
