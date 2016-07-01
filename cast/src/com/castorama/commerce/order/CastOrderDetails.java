package com.castorama.commerce.order;

import java.util.Iterator;
import java.util.List;

import atg.nucleus.GenericService;

import atg.repository.RepositoryItem;

import com.castorama.commerce.pricing.Constants;
import com.castorama.constantes.CastoConstantesOrders;

/**
 * Collect order information to shows order details.
 *
 * @author  EPAM team
 * 
 */
public class CastOrderDetails extends GenericService {

    /**
     * Default order manager name
     */
    private final static String DEFAULT_ORDER_MANAGER = "/atg/commerce/order/OrderManager";
    /**
     * Order manager
     */
    private CastOrderManager mOrderManager;
    /**
     * Front Office order repository item
     */
    private RepositoryItem mOrderItem;
    /**
     * Back Office order repository item
     */
    private RepositoryItem mOrderBOItem;

    /**
     * Creates CastOrderDetails
     */
    public CastOrderDetails() {
    }

    /**
     * Creates CastOrderDetails
     *
     * @param pOrderItem    - Front Office order repository item
     * @param pOrderManager - Order manager
     */
    public CastOrderDetails(RepositoryItem pOrderItem, CastOrderManager pOrderManager) {
        this.mOrderManager = pOrderManager;
        this.mOrderItem = pOrderItem;
        updateOrderBO();
    }

    /**
     * Updates Back Office order repository item
     */
    private void updateOrderBO() {
        if (null != this.mOrderManager) {
            this.mOrderBOItem = this.mOrderManager.getOrderBO(this.mOrderItem);
        }
    }

    /**
     * Returns messages for order from Back Office
     *
     * @return messages array
     */
    public RepositoryItem[] getMessages() {
        RepositoryItem[] result = null;
        CastOrderManager orderManager = getOrderManager();
        if (null != orderManager) {
            result = orderManager.getMessages(getOrderItem(), getOrderBOItem(), true);
        }
        return (null == result) ? new RepositoryItem[] {} : result;
    }

    /**
     * Returns refunds for order from Back Office
     *
     * @return refunds array
     */
    public RepositoryItem[] getRefunds() {
        RepositoryItem[] result = null;
        CastOrderManager orderManager = getOrderManager();
        if (null != orderManager) {
            result = orderManager.getRefunds(getOrderItem(), getOrderBOItem());
        }
        return (null == result) ? new RepositoryItem[] {} : result;
    }

    /**
     * Returns returns for order from Back Office
     *
     * @return returns array
     */
    public RepositoryItem[] getReturns() {
        RepositoryItem[] result = null;
        CastOrderManager orderManager = getOrderManager();
        if (null != orderManager) {
            result = orderManager.getReturns(getOrderItem(), getOrderBOItem());
        }
        return (null == result) ? new RepositoryItem[] {} : result;
    }

    /**
     * Returns deliveries for order from Back Office
     *
     * @return deliveries array
     */
    public RepositoryItem[] getDeliveries() {
        RepositoryItem[] result = null;
        CastOrderManager orderManager = getOrderManager();
        if (null != orderManager) {
            result = orderManager.getDeliveries(getOrderItem(), getOrderBOItem());
        }
        return (null == result) ? new RepositoryItem[] {} : result;
    }

    /**
     * Returns totalDiscount property.
     *
     * @return totalDiscount property.
     */
    public double getTotalDiscount() {
        double totalDiscount = 0;
        List items = (List) getOrderItem().getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_COMMERCE_ITEMS);
        if (null != items) {
            for (Iterator<RepositoryItem> i = items.iterator(); i.hasNext(); ) {
                RepositoryItem item = i.next();
                RepositoryItem priceInfo = (RepositoryItem) item
                        .getPropertyValue(CastoConstantesOrders.CITEM_PROPERTY_PRICE_INFO);
                if (null != priceInfo) {
                    double listPrice = (Double) priceInfo
                            .getPropertyValue(CastoConstantesOrders.PINFO_PROPERTY_LIST_PRICE);
                    double salePrice = (Double) priceInfo
                            .getPropertyValue(CastoConstantesOrders.PINFO_PROPERTY_SALE_PRICE);
                    long quantity = (Long) item.getPropertyValue(CastoConstantesOrders.CITEM_PROPERTY_QUANTITY);
                    if ((salePrice != 0) && (listPrice != salePrice)) {
                        totalDiscount += (listPrice - salePrice) * quantity;
                    }
                    List adjustments = (List) priceInfo
                            .getPropertyValue(CastoConstantesOrders.PINFO_PROPERTY_ADJUSTMENTS);
                    if (null != adjustments) {
                        for (Iterator adjustmenterator = adjustments.iterator(); adjustmenterator.hasNext(); ) {
                            RepositoryItem adjustment = (RepositoryItem) adjustmenterator.next();
                            RepositoryItem promotion = (RepositoryItem) adjustment
                                    .getPropertyValue(CastoConstantesOrders.ADJUSTMENT_PROPERTY_PRICING_MODEL);
                            if (promotion != null) {
                                double discount = (Double) adjustment
                                        .getPropertyValue(CastoConstantesOrders.ADJUSTMENT_PROPERTY_TOTAL_ADJUSTMENT);
                                totalDiscount += discount;
                            }
                        }
                    }
                }
            }
        }
        return totalDiscount;
    }

    /**
     * Returns shippingPrice property.
     *
     * @return shippingPrice property.
     */
    public double getShippingPrice() {
        double result = 0.0;
        List shippingGroups = (List) getOrderItem().getPropertyValue(
                CastoConstantesOrders.ORDER_PROPERTY_SHIPPING_GROUPS);
        if (null != shippingGroups) {
            for (Iterator<RepositoryItem> i = shippingGroups.iterator(); i.hasNext(); ) {
                RepositoryItem item = i.next();
                RepositoryItem priceInfo = (RepositoryItem) item
                        .getPropertyValue(CastoConstantesOrders.CITEM_PROPERTY_PRICE_INFO);
                if (null != priceInfo) {
                    List adj = (List) priceInfo.getPropertyValue(CastoConstantesOrders.PINFO_PROPERTY_ADJUSTMENTS);
                    if (null != adj) {
                        for (Iterator<RepositoryItem> j = adj.iterator(); i.hasNext(); ) {
                            RepositoryItem a = j.next();
                            String desc = (String) a
                                    .getPropertyValue(CastoConstantesOrders.ADJUSTMENT_PROPERTY_DESCRIPTION);
                            if (!Constants.HANDLING_PRICE_ADJUSTMENT_DESCRIPTION.equalsIgnoreCase(desc)) {
                                result += (Double) a
                                        .getPropertyValue(CastoConstantesOrders.ADJUSTMENT_PROPERTY_TOTAL_ADJUSTMENT);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns handlingPrice property.
     *
     * @return handlingPrice property.
     */
    public double getHandlingPrice() {
        double result = 0.0;
        List shippingGroups = (List) getOrderItem().getPropertyValue(
                CastoConstantesOrders.ORDER_PROPERTY_SHIPPING_GROUPS);
        if (null != shippingGroups) {
            for (Iterator<RepositoryItem> i = shippingGroups.iterator(); i.hasNext(); ) {
                RepositoryItem item = i.next();
                RepositoryItem priceInfo = (RepositoryItem) item
                        .getPropertyValue(CastoConstantesOrders.CITEM_PROPERTY_PRICE_INFO);
                if (null != priceInfo) {
                    List adj = (List) priceInfo.getPropertyValue(CastoConstantesOrders.PINFO_PROPERTY_ADJUSTMENTS);
                    if (null != adj) {
                        for (Iterator<RepositoryItem> j = adj.iterator(); i.hasNext(); ) {
                            RepositoryItem a = j.next();
                            String desc = (String) a
                                    .getPropertyValue(CastoConstantesOrders.ADJUSTMENT_PROPERTY_DESCRIPTION);
                            if (Constants.HANDLING_PRICE_ADJUSTMENT_DESCRIPTION.equalsIgnoreCase(desc)) {
                                result += (Double) a
                                        .getPropertyValue(CastoConstantesOrders.ADJUSTMENT_PROPERTY_TOTAL_ADJUSTMENT);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns the Front Office order property of the given name
     *
     * @param  pPropertyName order property name
     *
     * @return order property of the given name
     */
    public Object getPropertyValue(String pPropertyName) {
        RepositoryItem orderItem = getOrderItem();
        return (null == orderItem) ? null : orderItem.getPropertyValue(pPropertyName);
    }

    /**
     * Gets order manager
     *
     * @return - order manager
     */
    public CastOrderManager getOrderManager() {
        if (null == mOrderManager) {
            Object om = resolveName(DEFAULT_ORDER_MANAGER);
            if ( om instanceof CastOrderManager ) {
                mOrderManager = (CastOrderManager) om;
            }
        }
        return mOrderManager;
    }

    /**
     * Returns order total.
     *
     * @return order total.
     */
    public Double getOrderTotal() {
        Double result = null;
        RepositoryItem orderItem = getOrderItem();
        if (null != orderItem) {
            RepositoryItem priceInfo = (RepositoryItem) orderItem.getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PRICE_INFO);
            double total = 0.0;
            if (null != priceInfo) {
                total += getDoubleValue(priceInfo.getPropertyValue("amount"), 0D);
                total += getDoubleValue(priceInfo.getPropertyValue("shipping"), 0D);
                total += getDoubleValue(priceInfo.getPropertyValue("tax"), 0D);
            }
            total += getDoubleValue(orderItem.getPropertyValue("processingFees"), 0D);
            result = total;
        }
        return result;
    }

    /**
     * Returns double value of Object when it is Double, otherwise default value.
     *
     * @param value    object.
     * @param defvalue default value.
     * @return double value of Object when it is Double, otherwise default value.
     */
    private double getDoubleValue(Object value, double defvalue) {
        double result;
        if (value instanceof Double) {
            result = (Double) value;
        } else {
            result = defvalue;
        }
        return result;
    }

    /**
     * Sets order manager
     *
     * @param orderManager - order manager
     */
    public void setOrderManager(CastOrderManager orderManager) {
        this.mOrderManager = orderManager;
    }

    /**
     * Gets Front Office order repository item
     *
     * @return - Front Office order repository item
     */
    public RepositoryItem getOrderItem() {
        return mOrderItem;
    }

    /**
     * Sets Front Office order repository item
     *
     * @param orderItem - Front Office order repository item
     */
    public void setOrderItem(RepositoryItem orderItem) {
        this.mOrderItem = orderItem;
        updateOrderBO();
    }

    /**
     * Gets Back Office order repository item
     *
     * @return - Back Office order repository item
     */
    public RepositoryItem getOrderBOItem() {
        return mOrderBOItem;
    }

    /**
     * Sets Back Office order repository item
     *
     * @param pOrderBOItem - Back Office order repository item
     */
    public void setOrderBOItem(RepositoryItem pOrderBOItem) {
        mOrderBOItem = pOrderBOItem;
    }

}
