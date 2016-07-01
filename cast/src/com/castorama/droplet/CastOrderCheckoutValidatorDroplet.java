package com.castorama.droplet;

import java.io.IOException;

import java.util.List;

import javax.servlet.ServletException;

import com.castorama.commerce.order.CastHardgoodShippingGroup;
import com.castorama.commerce.order.CastOrderImpl;

import atg.commerce.CommerceException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.OrderManager;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.OrderPriceInfo;

import atg.core.util.Address;
import atg.core.util.StringUtils;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * ToDo: DOCUMENT ME!
 *
 * @author  EPAM team
 * @version ToDo: INSERT VERSION NUMBER
 */
public class CastOrderCheckoutValidatorDroplet extends DynamoServlet {
    /** TRUE constant. */
    public static final String TRUE = "true";

    /** FALSE constant. */
    public static final String FALSE = "false";

    /** EXAMINED_ORDER constant. */
    public static final String EXAMINED_ORDER = "examinedOrder";
    /** PBX_TOTAL constant. */
    public static final String PBX_TOTAL = "pbx_total";

    /** orderManager property */
    private OrderManager mOrderManager;

    /**
     * Extends service method of DynamoServlet to returns order details.
     *
     * @param  pRequest  ToDo: DOCUMENT ME!
     * @param  pResponse ToDo: DOCUMENT ME!
     *
     * @throws ServletException ToDo: DOCUMENT ME!
     * @throws IOException      ToDo: DOCUMENT ME!
     *
     * @see    atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest,
     *         atg.servlet.DynamoHttpServletResponse)
     */
    @Override public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                           throws ServletException, IOException {
        Object orderObj = pRequest.getObjectParameter(EXAMINED_ORDER);

        boolean isValid = true;
        if ((orderObj != null) && (orderObj instanceof CastOrderImpl)) {
            CastOrderImpl order = (CastOrderImpl) orderObj;
            
            try {
                order = (CastOrderImpl) getOrderManager().loadOrder(order.getId());
            } catch (CommerceException e) {
                if (isLoggingError()) {
                    logError(this.getClass()+"Atout property wasn't set to order correctly. \n" + e);
                }
            }
            
            order.invalidateOrder();

            // validate order total
            OrderPriceInfo priceInfo = order.getPriceInfo();
            if(priceInfo == null || priceInfo.getTotal() < 1.0) {
                isValid = false;
                if(isLoggingInfo()) {
                    logInfo("Order total price is less than 1.00.");
                }
            }
            // validate order total and page total
            String pbxTotal = (String)pRequest.getObjectParameter(PBX_TOTAL);
            Double total = Double.valueOf(pbxTotal)/100;
            boolean totalIsOk = priceInfo.getTotal()==total;
            if (!totalIsOk){
                isValid = false;
                if(isLoggingInfo()) {
                    logInfo("Order total price ("+priceInfo.getTotal()+") and page total price("+total+") is not equals");
                }
            }
            
            // validate shipping addresses
            List<ShippingGroup> shippingGroups = order.getShippingGroups();
            if ((shippingGroups != null) && !shippingGroups.isEmpty()) {
                for (ShippingGroup shippingGroup : shippingGroups) {
                    if (shippingGroup instanceof HardgoodShippingGroup) {
                        CastHardgoodShippingGroup hgsg = (CastHardgoodShippingGroup) shippingGroup;
                        isValid &= validateAddress(hgsg.getShippingAddress());
                    }
                }

                // validate billing address
                List<PaymentGroup> paymentGroups = order.getPaymentGroups();
                CreditCard cc = null;
                if ((paymentGroups != null) && !paymentGroups.isEmpty()) {
                    for (PaymentGroup paymentGroup : paymentGroups) {
                        if (paymentGroup instanceof CreditCard) {
                            cc = (CreditCard) paymentGroup;
                        }
                    }
                }
                if (cc != null) {
                    isValid &= validateAddress(cc.getBillingAddress());
                } else {
                    isValid = false;
                }
            } else {
                isValid = false;
            }  // end if-else
        }  // end if
        if (isValid) {
            pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
        } else {
            if(isLoggingWarning()) {
                logWarning("Order is not valid for checkout.");
            }
            pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
        }

    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  address ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     */
    private boolean validateAddress(Address address) {
        if (StringUtils.isEmpty(address.getAddress1()) || StringUtils.isEmpty(address.getCity()) ||
                StringUtils.isEmpty(address.getCountry()) || StringUtils.isEmpty(address.getFirstName()) ||
                StringUtils.isEmpty(address.getLastName()) || StringUtils.isEmpty(address.getPostalCode()) ||
                StringUtils.isEmpty(address.getState())) {
            return false;
        }

        return true;
    }

    /**
     * Returns orderManager property.
     *
     * @return mOrderManager property.
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
        this.mOrderManager = pOrderManager;
    }
}
