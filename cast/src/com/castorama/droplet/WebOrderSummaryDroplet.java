package com.castorama.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.pricing.Constants;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.constantes.CastoConstantes;
import com.castorama.constantes.CastoConstantesOrders;

/**
 * Droplet for return order summary info. used by
 * /com/castorama/droplet/WebOrderSummaryDroplet component.
 *
 * @author Vasili Ivus.
 */
public class WebOrderSummaryDroplet extends DynamoServlet {
    /** SALE_PRICE_LOCAL_DESCRIPTION constant. */
    private static final String SALE_PRICE_LOCAL_DESCRIPTION = "Sale price local";

    /** ADJUSTMENT_DESCRIPTION constant. */
    private static final String ADJUSTMENT_DESCRIPTION = "adjustmentDescription";

    /** TOTAL_ADJUSTMENT constant. */
    private static final String TOTAL_ADJUSTMENT = "totalAdjustment";

    /** ADJUSTMENTS constant. */
    private static final String ADJUSTMENTS = "adjustments";

    /** ON_SALE_DISCOUNT_DISPLAY constant. */
    private static final String ON_SALE_DISCOUNT_DISPLAY = "onSaleDiscountDisplay";

    /** ON_SALE constant. */
    private static final String ON_SALE = "onSale";

    /** PRICE_INFO constant. */
    private static final String PRICE_INFO = "priceInfo";

    /** COMMERCE_ITEMS constant. */
    private static final String COMMERCE_ITEMS = "commerceItems";

    /** Output parameter name. */
    private static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** SHIPPING_DISCOUNT constant. */
    private static final String SHIPPING_DISCOUNT = "shippingDiscount";

    /** HAS_SHIPPING_PROMOTIONS constant. */
    private static final String HAS_SHIPPING_PROMOTIONS = "hasShippingPromotions";

    /** ORDER constant. */
    public static final String ORDER = "order";

    /** TOTAL_DISCOUNT constant. */
    public static final String TOTAL_DISCOUNT = "totalDiscount";

    /** HANDLING_PRICE constant. */
    public static final String HANDLING_PRICE = "handlingPrice";

    /** SHIPPING_PRICE constant. */
    public static final String SHIPPING_PRICE = "shippingPrice";

    /** DELIVERY_AMOUNT constant. */
    public static final String DELIVERY_AMOUNT = "deliveryAmount";

    /** DELIVERY_WEIGHT constant. */
    public static final String DELIVERY_WEIGHT = "deliveryWeight";

    /** HAS_PRODUCTS_WITH_DISCOUNT constant */
    public static final String HAS_PRODUCTS_WITH_DISCOUNT = "hasProductsWithDiscount";

    /** HAS_PRODUCTS_WITH_DISCOUNT constant */
    public static final String CURRENCY_CODE = "currencyCode";

    /** HAS_PRODUCTS_WITH_DISCOUNT constant */
    public static final String TOTAL = "total";

    /** HAS_PRODUCTS_WITH_DISCOUNT constant */
    public static final String RAW_SUBTOTAL = "rawSubTotal";

    /** WEIGHT constant */
    public static final String WEIGHT = "PoidsUV";

    /** ITEM constant. */
    public static final String ITEM = "item";

    /** QUANTITY constant. */
    public static final String QUANTITY = "quantity";

    /** AMOUNT constant. */
    public static final String AMOUNT = "amount";

    /** SHIPPING constant. */
    public static final String SHIPPING = "shipping";

    /** TAX constant. */
    public static final String TAX = "tax";

    /** CATALOG_REF_ID constant. */
    public static final String CATALOG_REF_ID = "catalogRefId";

    /**
     * Extends service method of DynamoServlet to returns order summury info.
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    @Override public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                           throws ServletException, IOException {
        String currencyCode = null;
        double total = 0.0;
        Double rawSubTotal = new Double(0.0);
        Double totalDiscount = new Double(0.0);
        Double shippingPrice = new Double(0.0);
        Double shippingDiscount = new Double(0.0);
        Double handlingPrice = new Double(0.0);
        Double itemsDiscount = new Double(0.0);
        Double weightTotal = new Double(0.0);
        boolean hasShippingPromotions = false;
        boolean hasProductsWithDiscount = false;

        try {
            RepositoryItem order = (RepositoryItem) pRequest.getObjectParameter(ORDER);
            if (null != order) {
                weightTotal = (Double) order.getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_TOTAL_WEIGHT);
                handlingPrice = (Double) order.getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PROCESSING_FEES);
                shippingDiscount =
                    (Double) order.getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_SHIPPING_DISCOUNT);
                shippingPrice = (Double) order.getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_SHIPPING_FEES);

                itemsDiscount = getItemsDiscount(order);
                RepositoryItem priceInfo =
                    (RepositoryItem) order.getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PRICE_INFO);
                totalDiscount = getOrderDiscount(priceInfo) + itemsDiscount;
                if (null != priceInfo) {
                    currencyCode = (String) priceInfo.getPropertyValue(CURRENCY_CODE);
                    rawSubTotal = (Double) priceInfo.getPropertyValue(RAW_SUBTOTAL);
                    double amount = (Double) priceInfo.getPropertyValue(AMOUNT);
                    double shipping = (Double) priceInfo.getPropertyValue(SHIPPING);
                    double tax = (Double) priceInfo.getPropertyValue(TAX);
                    total = amount + shipping + tax + ((null != handlingPrice) ? handlingPrice : 0.0);
                }

                hasProductsWithDiscount = ((null != itemsDiscount) && (0 != itemsDiscount));
                hasShippingPromotions = ((null != shippingDiscount) && (0 != shippingDiscount));
            }  // end if
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }  // end try-catch

        if (null == currencyCode) {
            currencyCode = CastoConstantes.EURO;
        }

        pRequest.setParameter(HAS_PRODUCTS_WITH_DISCOUNT, hasProductsWithDiscount);
        pRequest.setParameter(TOTAL_DISCOUNT, totalDiscount);
        pRequest.setParameter(HAS_SHIPPING_PROMOTIONS, hasShippingPromotions);
        pRequest.setParameter(CURRENCY_CODE, currencyCode);
        pRequest.setParameter(SHIPPING_PRICE, shippingPrice);
        pRequest.setParameter(SHIPPING_DISCOUNT, shippingDiscount);
        pRequest.setParameter(HANDLING_PRICE, handlingPrice);
        pRequest.setParameter(TOTAL, total);
        pRequest.setParameter(RAW_SUBTOTAL, rawSubTotal);
        pRequest.setParameter(DELIVERY_WEIGHT, weightTotal);
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }

    /**
     * Returns itemsDiscount. Logic should be the same as in CastOrderTools public double getItemsDiscount(Order order)
     *
     * @param  pOrder parameter.
     *
     * @return itemsDiscount.
     */
    private Double getItemsDiscount(RepositoryItem pOrder) {
        double result = 0.0;
        List commerceItems = (List) pOrder.getPropertyValue(COMMERCE_ITEMS);
        if ((commerceItems != null) && !commerceItems.isEmpty()) {
            for (Object ci : commerceItems) {
                if (ci != null) {
                    RepositoryItem ciRep = (RepositoryItem) ci;
                    RepositoryItem itemPriceInfo = (RepositoryItem) ciRep.getPropertyValue(PRICE_INFO);
                    Boolean onSale = (Boolean) itemPriceInfo.getPropertyValue(ON_SALE);
                    Boolean onSaleDiscountDisplay = (Boolean) itemPriceInfo.getPropertyValue(ON_SALE_DISCOUNT_DISPLAY);
                    if (itemPriceInfo != null) {
                        List adjustments = (List) itemPriceInfo.getPropertyValue(ADJUSTMENTS);
                        if ((adjustments != null) && !adjustments.isEmpty()) {
                            for (Object adj : adjustments) {
                                if (adj != null) {
                                    RepositoryItem adjRep = (RepositoryItem) adj;
                                    double tmpAmount = (Double) adjRep.getPropertyValue(TOTAL_ADJUSTMENT);
                                    String adjDescr = (String) adjRep.getPropertyValue(ADJUSTMENT_DESCRIPTION);
                                    if (tmpAmount < 0) {
                                        //web and local sale price 
                                        if ((Constants.SALE_PRICE_ADJUSTMENT_DESCRIPTION.equalsIgnoreCase(adjDescr) || SALE_PRICE_LOCAL_DESCRIPTION.equalsIgnoreCase(adjDescr)) 
                                                && onSale && onSaleDiscountDisplay) {
                                            result += tmpAmount;
                                            continue;
                                        }

                                        // promotions
                                        if (Constants.ITEM_DISCOUNT_PRICE_ADJUSTMENT_DESCRIPTION.equals(adjDescr)) {
                                            result += tmpAmount;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }  // end if
            }  // end for
        }  // end if
        return result;
    }

    /**
     * Returns orderDiscount.  Logic should be the same as in CastOrderTools public double getOrderDiscount(Order order)
     *
     * @param  pOrderPriceInfo parameter.
     *
     * @return orderDiscount.
     */
    private Double getOrderDiscount(RepositoryItem pOrderPriceInfo) {
        double result = 0.0;
        if (pOrderPriceInfo != null) {
            List orderAdjs = (List) pOrderPriceInfo.getPropertyValue(ADJUSTMENTS);
            if ((orderAdjs != null) && !orderAdjs.isEmpty()) {
                for (Object orderAdj : orderAdjs) {
                    if (orderAdj != null) {
                        double tmpAmount = (Double) ((RepositoryItem) orderAdj).getPropertyValue(TOTAL_ADJUSTMENT);
                        if (tmpAmount < 0) {
                            result += tmpAmount;
                        }
                    }

                }
            }
        }
        return result;
    }

}
