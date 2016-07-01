package com.castorama.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupRelationship;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.commerce.pricing.TaxPriceInfo;
import atg.core.util.Address;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userprofiling.Profile;

public class OrderProductsInfoDroplet extends DynamoServlet {
    
    /** Constant PARAM_ORDER for input parameter name */
    private static final String PARAM_ORDER = "order";
    
    /** Constant PARAM_ORDER_AMOUNT_TF_WITH_SF for output parameter name */
    private static final String PARAM_ORDER_AMOUNT_TF_WITH_SF = "amountTFWithSF";
    /** Constant PARAM_ORDER_AMOUNT_TF_WITHOUT_SF for output parameter name */
    private static final String PARAM_ORDER_AMOUNT_TF_WITHOUT_SF = "amountTFWithoutSF";
    /** Constant PARAM_ORDER_AMOUNT_ATI_WITH_SF for output parameter name */
    private static final String PARAM_ORDER_AMOUNT_ATI_WITH_SF = "amountATIWithSF";
    /** Constant PARAM_ORDER_AMOUNT_ATI_WITHOUT_SF for output parameter name */
    private static final String PARAM_ORDER_AMOUNT_ATI_WITHOUT_SF = "amountATIWithoutSF";
    /** Constant PARAM_ORDER_SHIPPING_TF for output parameter name */
    private static final String PARAM_ORDER_SHIPPING_TF = "shippingTF";
    /** Constant PARAM_ORDER_SHIPPING_ATI for output parameter name */
    private static final String PARAM_ORDER_SHIPPING_ATI = "shippingATI";
    /** Constant PARAM_DISCOUNT_TF for output parameter name */
    private static final String PARAM_DISCOUNT_TF = "discountTF";
    /** Constant PARAM_DISCOUNT_ATI for output parameter name */
    private static final String PARAM_DISCOUNT_ATI = "discountATI";
    /** Constant PARAM_CURRENCY_CODE for output parameter name */
    private static final String PARAM_CURRENCY_CODE = "currencyCode";
    /** Constant PARAM_ORDER_ZIP for output parameter name */
    private static final String PARAM_ORDER_ZIP = "orderZip";
    /** Constant PARAM_EMAIL for output parameter name */
    private static final String PARAM_EMAIL = "emailAddress";
    /** Constant PARAM_PRODUCT_INFO_LIST for output parameter name */
    private static final String PARAM_PRODUCT_INFO_LIST = "productsInfo";
    
    /** Constant OPARAM_OUTPUT for oparam name */
    private static final String OPARAM_OUTPUT = "output";
    
    /** Constant SKU_TYPE for property name */
    private static final String SKU_TYPE = "casto_sku";
    /** Constant CASTO_PACK_TYPE for property name */
    private static final String CASTO_PACK_TYPE = "casto-pack";
    /** Constant PROPERTY_PRICE_TTC_EURO for property name */
    private static final String PROPERTY_PRICE_TTC_EURO = "prixVenteTTCEuro";
    /** Constant PROPERTY_PRICE_HT_EURO for property name */
    private static final String PROPERTY_PRICE_HT_EURO = "prixVenteHTEuro";
    /** Constant PROPERTY_SALE_PRICE for property name */
    private static final String PROPERTY_SALE_PRICE = "PrixSolde";
    /** Constant PROPERTY_EMAIL for property name */
    private static final String PROPERTY_EMAIL = "email";
    /** Constant PROPERTY_TYPE for property name */
    private static final String PROPERTY_TYPE = "type";
    /** Constant PROPERTY_BUNDLE_LINKS for property name */
    private static final String PROPERTY_BUNDLE_LINKS = "bundleLinks";
    /** Constant PROPERTY_ITEM for property name */
    private static final String PROPERTY_ITEM = "item";
    /** Constant PROPERTY_QUANTITY for property name */
    private static final String PROPERTY_QUANTITY = "quantity";
    
    /** Constant CONFIRAMATION_PAGE_NAME for page name */
    private static final String CONFIRMATION_PAGE_NAME = "confirmation.jsp";
    /** Constant PAYMENT_PAGE_NAME for page name */
    private static final String PAYMENT_PAGE_NAME = "payment.jsp";
    /** Constant CART_PAGE_NAME for page name */
    private static final String CART_PAGE_NAME = "cart.jsp";
    /** Constant NUMBER_OF_ATTR_IN_PRODUCT_INFO */
    private static final int NUMBER_OF_ATTR_IN_PRODUCT_INFO = 8;
    
    private Repository mProductCatalog;
    private Profile mProfile;

    /**
     * Calculate parameters' values depending on page name request came from
     */
    public void service(DynamoHttpServletRequest request, DynamoHttpServletResponse response) 
        throws ServletException, IOException {
        Object obj = request.getObjectParameter(PARAM_ORDER);
        if (obj == null) {
            if (isLoggingError()) {
                logError("Input parameter '" + PARAM_ORDER + "' is required");
            }
            throw new ServletException("Input parameter '" + PARAM_ORDER + "' is required");
        }
        Order order = null;
        if (obj instanceof Order) {
            order = (Order)obj;
        } else {
            if (isLoggingError()) {
                logError("Input parameter '" + PARAM_ORDER + "' must be of type " + Order.class.getName());
            }
            throw new ServletException("Input parameter '" + PARAM_ORDER + "' must be of type " + Order.class.getName());
        }
        
        String requestURI = request.getRequestURI();
        
        OrderPriceInfo priceInfo = order.getPriceInfo();
        
        double orderItemsTF = 0;
        double orderItemsATI = 0;
        
        double orderDiscountTF = 0;
        double orderDiscountATI = 0;
        
        if (requestURI.contains(CONFIRMATION_PAGE_NAME) || requestURI.contains(CART_PAGE_NAME)) {
            
            // getting from catalog price of item with ttc and without it
            List<String[]> productsInfo = new ArrayList<String[]>();
            
            RqlStatement rqlStatement= null;
            RepositoryView view = null;
            try {
                rqlStatement = RqlStatement.parseRqlStatement("childSKUs INCLUDES ?0");
                view = getProductCatalog().getView("product");
            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    logError(e);
                }
                throw new ServletException(e);
            }
            
            if (isLoggingDebug()) {
                logDebug("OrderProductsInfoDroplet: Start iterate over order's items");
            }
            for (CommerceItem item: (List<CommerceItem>)order.getCommerceItems()) {
                String[] info = new String[NUMBER_OF_ATTR_IN_PRODUCT_INFO];
                String skuId = item.getCatalogRefId();
                if (isLoggingDebug()) {
                    logDebug("OrderProductsInfoDroplet: Processing sku with id = " + skuId);
                }
                RepositoryItem sku = null;
                RepositoryItem product = null;
                String productType = null;
                try {
                    sku = getProductCatalog().getItem(skuId, SKU_TYPE);
                    
                    Object[] rqlParams = new Object[1];
                    rqlParams[0] = sku;
                    
                    RepositoryItem[] result = rqlStatement.executeQuery(view, rqlParams);
                    if (result.length > 0) {
                        product = result[0];
                        productType = (String)product.getPropertyValue(PROPERTY_TYPE);
                    }
                    
                } catch (RepositoryException e) {
                    if (isLoggingError()) {
                        logError(e.getMessage());
                    }
                    
                }
                
                //calculate order's discounts
                orderDiscountATI += Math.abs(item.getPriceInfo().getAmount() - item.getPriceInfo().getListPrice());
                
                float atiPriceEuro = 0;
                float tfPriceEuro = 0;                
                if (sku != null && product != null) {
                    if (CASTO_PACK_TYPE.equals(productType)) {
                        if (isLoggingDebug()) {
                            logDebug("Calculate products pack prices");
                        }
                        List<RepositoryItem> bundleLinks = (List<RepositoryItem>)sku.getPropertyValue(PROPERTY_BUNDLE_LINKS);
                        for (RepositoryItem bundleItem: bundleLinks) {
                            RepositoryItem skuItem = (RepositoryItem)bundleItem.getPropertyValue(PROPERTY_ITEM);
                            long quantityInPack = (Long)bundleItem.getPropertyValue(PROPERTY_QUANTITY);
                            atiPriceEuro += (Float)skuItem.getPropertyValue(PROPERTY_PRICE_TTC_EURO) * quantityInPack;
                            tfPriceEuro += (Float)skuItem.getPropertyValue(PROPERTY_PRICE_HT_EURO) * quantityInPack;
                            
                        }
                        
                    } else {
                        atiPriceEuro = (Float)sku.getPropertyValue(PROPERTY_PRICE_TTC_EURO);
                        tfPriceEuro = (Float)sku.getPropertyValue(PROPERTY_PRICE_HT_EURO);
                    }
                    
                    info[0] = ((Integer)sku.getPropertyValue("codeArticle")).toString();
                    info[1] = (String)product.getPropertyValue("displayName");
                    info[2] = "" + atiPriceEuro;
                    info[3] = "" + tfPriceEuro;
                    info[4] = "" + item.getPriceInfo().getOrderDiscountShare();
                    info[5] = "" + item.getPriceInfo().getOrderDiscountShare();
                    info[6] = "" + item.getQuantity();
                    info[7] = (String)((RepositoryItem)product.getPropertyValue("parentCategory")).getPropertyValue("displayName");
                    
                    orderItemsATI += atiPriceEuro * item.getQuantity();
                    orderItemsTF += tfPriceEuro * item.getQuantity();
                }
                productsInfo.add(info);
            }
            request.setParameter(PARAM_PRODUCT_INFO_LIST, productsInfo);
            if (isLoggingDebug()) {
                logDebug("OrderProductsInfoDroplet: Finish iterate over order's items");
            }
        }
        
        if (requestURI.contains(CONFIRMATION_PAGE_NAME)) {
            //currencyCode param
            String currencyCode = priceInfo.getCurrencyCode();        
            
            //orderState and orderZip params
            String orderZip = "";
            if (order.getShippingGroupCount() > 0) {
                ShippingGroup shippingGroup = (ShippingGroup)order.getShippingGroups().get(0);
                if (shippingGroup instanceof HardgoodShippingGroup) {
                    HardgoodShippingGroup hardgoodShippingGroup = (HardgoodShippingGroup)shippingGroup;
                    Address address = hardgoodShippingGroup.getShippingAddress();
                    orderZip = address.getPostalCode();
                }
            }
            
            //email param
            String email = (String)getProfile().getPropertyValue(PROPERTY_EMAIL);
            
            //calculate order shipping and shipping tax
            double orderShippingATI = priceInfo.getShipping();        
            double shippinTax = 0.0;            
            
            for (String id : (Set<String>)priceInfo.getShippingItemsSubtotalPriceInfos().keySet()) {
                OrderPriceInfo shippingPriceInfo = (OrderPriceInfo)priceInfo.getShippingItemsSubtotalPriceInfos().get(id);                
                shippinTax += shippingPriceInfo.getTax();
            }
            double orderShippingTF = orderShippingATI - shippinTax;
            
            double orderItemsTax = orderItemsATI - orderItemsTF;
            
            double orderAmountTFWithSF = priceInfo.getTotal() - orderItemsTax - priceInfo.getTax();
            double orderAmountTFWithoutSF = priceInfo.getTotal() - orderItemsTax - priceInfo.getTax() - priceInfo.getShipping();
            double orderAmountATIWithSF = priceInfo.getTotal();
            double orderAmountATIWithoutSF =  priceInfo.getTotal() - priceInfo.getShipping();
            orderDiscountTF = orderDiscountATI;
            
            if (isLoggingDebug()) {
                logDebug("OrderProductsInfoDroplet: Setting output parameters");
                logDebug("param" + PARAM_ORDER_AMOUNT_TF_WITH_SF + " " + orderAmountTFWithSF );
                logDebug("param" + PARAM_ORDER_AMOUNT_TF_WITHOUT_SF + " " + orderAmountTFWithoutSF );
                logDebug("param" + PARAM_ORDER_AMOUNT_ATI_WITH_SF + " " + orderAmountATIWithSF );
                logDebug("param" + PARAM_ORDER_AMOUNT_ATI_WITHOUT_SF + " " + orderAmountATIWithoutSF );
                logDebug("param" + PARAM_ORDER_SHIPPING_TF + " " + orderShippingTF );
                logDebug("param" + PARAM_ORDER_SHIPPING_ATI + " " + orderShippingATI );
                logDebug("param" + PARAM_DISCOUNT_TF + " " + orderDiscountTF );
                logDebug("param" + PARAM_DISCOUNT_ATI + " " + orderDiscountATI );
            }
            request.setParameter(PARAM_ORDER_AMOUNT_TF_WITH_SF, orderAmountTFWithSF);
            request.setParameter(PARAM_ORDER_AMOUNT_TF_WITHOUT_SF, orderAmountTFWithoutSF);
            request.setParameter(PARAM_ORDER_AMOUNT_ATI_WITH_SF, orderAmountATIWithSF);
            request.setParameter(PARAM_ORDER_AMOUNT_ATI_WITHOUT_SF, orderAmountATIWithoutSF);
            request.setParameter(PARAM_ORDER_SHIPPING_TF, orderShippingTF);
            request.setParameter(PARAM_ORDER_SHIPPING_ATI, orderShippingATI);
            request.setParameter(PARAM_DISCOUNT_TF, orderDiscountTF);
            request.setParameter(PARAM_DISCOUNT_ATI, orderDiscountATI);
            
            request.setParameter(PARAM_CURRENCY_CODE, currencyCode);
            request.setParameter(PARAM_ORDER_ZIP, orderZip);
            request.setParameter(PARAM_EMAIL, email);
        }
        
        request.serviceParameter(OPARAM_OUTPUT, request, response);
    }

    public Repository getProductCatalog() {
        return mProductCatalog;
    }

    public void setProductCatalog(Repository pProductCatalog) {
        this.mProductCatalog = pProductCatalog;
    }

    public Profile getProfile() {
        return mProfile;
    }

    public void setProfile(Profile pProfile) {
        this.mProfile = pProfile;
    }
    
}
