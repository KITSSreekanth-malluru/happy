/**
 *
 */
package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.order.Order;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.commerce.order.CastOrderTools;

/**
 * ShoppingCartDiscountDroplet class
 *
 * @author  EPAM team
 */
public class ShoppingCartDiscountDroplet extends DynamoServlet {
    /** Oparam: output. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** Oparam: empty. */
    public static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /** ORDER_PARAM constant */
    public static final String ORDER_PARAM = "order";

    /** HAS_PRODUCTS_WITH_DISCOUNT constant */
    public static final String HAS_PRODUCTS_WITH_DISCOUNT = "hasProductsWithDiscount";

    /** TOTAL_DISCOUNT constant */
    public static final String TOTAL_DISCOUNT = "totalDiscount";
    
    /** orderTools property */
    private CastOrderTools mOrderTools = null;

    /**
     * Checks if Order has products with discount
     *
     * @param  pRequest  DynamoHttpServletRequest object
     * @param  pResponse DynamoHttpServletResponse object
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        Order order = null;
        try {
            order = (Order) pRequest.getObjectParameter(ORDER_PARAM);
        } catch (ClassCastException ex) {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
            return;
        }

        if(order != null) {
            boolean hasProductsWithDiscount = getHasProductsWithDiscount(order);
            pRequest.setParameter(HAS_PRODUCTS_WITH_DISCOUNT, hasProductsWithDiscount);
            pRequest.setParameter(TOTAL_DISCOUNT, getTotalDiscount(order));
            pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        }
    }

    /**
     * Returns hasProductsWithDiscount property.
     *
     * @param  order parameter to set.
     *
     * @return hasProductsWithDiscount property.
     */
    public boolean getHasProductsWithDiscount(Order order) {
        CastOrderTools orderTools = getOrderTools();
        return (orderTools.getItemsDiscount(order) != 0);
    }

    /**
     * Returns totalDiscount property.
     *
     * @param  order parameter to set.
     *
     * @return totalDiscount property.
     */
    public double getTotalDiscount(Order order) {
        CastOrderTools orderTools = getOrderTools();
    	return orderTools.getItemsDiscount(order) + orderTools.getOrderDiscount(order);
    }

	/**
	 * @return the orderTools
	 */
	public CastOrderTools getOrderTools() {
		return mOrderTools;
	}

	/**
	 * @param pOrderTools the orderTools to set
	 */
	public void setOrderTools(CastOrderTools pOrderTools) {
		this.mOrderTools = pOrderTools;
	}
}
