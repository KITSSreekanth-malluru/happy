package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userdirectory.droplet.Constants;
import com.castorama.commerce.pricing.CastoShippingTools;

/**
 * Droplet for calculating total weight for order. <dt>Input params:
 * <dd>
 * <li><code>order</code> - order for calculation.
 * 
 * <pre>
 * <code>
 *    uneAdresse</code> - read data from request and create new address for current
 *    profile<br>
 * 
 * @author Epam Team
 */
public class OrderTotalWeightDroplet extends DynamoServlet implements Constants {

    /** ORDER_PARAMETER_NAME constant. */
    private static final String ORDER_PARAMETER_NAME = "order";

    /** TOTAL_WEIGHT constant. */
    public static final String TOTAL_WEIGHT = "totalWeight";

    private CastoShippingTools mShippingTools;

    /**
     * @return the shippingTools
     */
    public CastoShippingTools getShippingTools() {
        return mShippingTools;
    }

    /**
     * Order total weight calculation servlet.<br>
     * 
     * @param pRequest the servlet's request
     * @param pResponse the servlet's response
     * 
     * @throws ServletException if servlet exception occures
     * @throws IOException if servlet io exception occures
     */
    @Override
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        Object orderObj = pRequest.getLocalParameter(ORDER_PARAMETER_NAME);
        if (orderObj instanceof Order) {
            // return first shipping group
            ShippingGroup shippingGroup = (ShippingGroup) ((Order) orderObj).getShippingGroups().get(0);
            Double totalWeight = getShippingTools().getTotalWeight(shippingGroup, false, false);
            pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
            pRequest.setParameter(TOTAL_WEIGHT, totalWeight);
        }
    }

    /**
     * @param pShippingTools the shippingTools to set
     */
    public void setShippingTools(CastoShippingTools pShippingTools) {
        this.mShippingTools = pShippingTools;
    }

}
