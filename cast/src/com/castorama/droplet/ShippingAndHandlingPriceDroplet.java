package com.castorama.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.commerce.pricing.Constants;

/**
 * Returns shipping and handling prices.
 * 
 * @author Katsiaryna Sharstsiuk, Peter Tolochko
 */
public class ShippingAndHandlingPriceDroplet extends DynamoServlet {

    /** OUTPUT constant */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** EMPTY constant */
    public static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /**
     * handlingPrice parameter name.
     */
    private static final String HANDLING_PRICE = "handlingPrice";

    /**
     * shippingPrice parameter name.
     */
    private static final String SHIPPING_PRICE = "shippingPrice";

    /**
     * shippingDiscount parameter name.
     */
    private static final String SHIPPING_DISCOUNT = "shippingDiscount";

    /**
     * hasShippingPromotions parameter name.
     */
    private static final String HAS_SHIPPING_PROMOTIONS = "hasShippingPromotions";
    
    /** ORDER_PARAM constant */
    public static final String ORDER_PARAM = "order";

    /**
     * Returns shipping and handling prices.
     * 
     * @param pRequest -
     *                http request
     * @param pResponse -
     *                http response
     * @throws ServletException
     *                 if error occurs
     * @throws IOException
     *                 if error occurs
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

        Order order = null;
        try {
            order = (Order) pRequest.getObjectParameter(ORDER_PARAM);
        } catch (ClassCastException ex) {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
            return;
        }

        double shippingPrice = 0.0;
        double shippingDiscount = 0.0;
        double handlingPrice = 0.0;
        boolean hasShippingPromotions = false;

        if (order != null) {
            OrderPriceInfo orderPriceInfo = order.getPriceInfo();
            if ((orderPriceInfo != null) && (orderPriceInfo.getAdjustments() != null)) {
                for (PricingAdjustment pa : (List<PricingAdjustment>) orderPriceInfo.getAdjustments()) {
                    if (Constants.HANDLING_PRICE_ADJUSTMENT_DESCRIPTION.equalsIgnoreCase(pa.getAdjustmentDescription())) {
                        handlingPrice += pa.getTotalAdjustment();
                    }
                }
            }
            for (ShippingGroup shippingGroup : (List<ShippingGroup>) order.getShippingGroups()) {
                ShippingPriceInfo info = shippingGroup.getPriceInfo();
                if (info != null && info.getAdjustments() != null) {
                    for (PricingAdjustment pa : (List<PricingAdjustment>) info.getAdjustments()) {
                        double tmpAmount = pa.getTotalAdjustment();
                        if (tmpAmount < 0) {
                            shippingDiscount += tmpAmount;
                            hasShippingPromotions = true;
                        } else {
                            shippingPrice += tmpAmount;
                            
                        }
                    }
                }
            }
            pRequest.setParameter(HAS_SHIPPING_PROMOTIONS, hasShippingPromotions);
            pRequest.setParameter(SHIPPING_PRICE, shippingPrice);
            pRequest.setParameter(SHIPPING_DISCOUNT, shippingDiscount);
            pRequest.setParameter(HANDLING_PRICE, handlingPrice);
            pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        }    
    }
}
