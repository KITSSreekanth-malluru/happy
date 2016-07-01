package com.castorama.droplet;

import static com.castorama.constantes.CastoConstantesOrders.ORDER_PROPERTY_SHIPPING_GROUPS;
import static com.castorama.checkout.CastShippingManager.SALE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.ShippingGroup;
import atg.nucleus.naming.ParameterName;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.servlet.RequestLocale;

import com.castorama.checkout.CastDeliveryBean;
import com.castorama.checkout.CastShippingManager;
import com.castorama.commerce.pricing.CastoShippingTools;

/**
 * CastShippingDroplet class shows delivery periods for the given products.
 *
 * @author EPAM team
 */
public class CastShippingDroplet extends DynamoServlet {
    /** CASTO_SKU constant */
    private static final String CASTO_SKU = "casto_sku";

    /** DELIVERY_BY_US constant */
    public static final int PNS = 1;

    /** DELIVERY_BY_PROVIDER constant */
    public static final int LDF = 2;

    /** OUTPUT constant */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** EMPTY constant */
    private static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /** SKU_ID constant */
    private static final String SKU_ID = "skuId";

    /** QUANTITY constant */
    private static final String QUANTITY = "quantity";

    /** DELIVERY_PNS constant */
    private static final String DELIVERY_PNS = "expeditionPNS";

    /** DELIVERIES constant */
    private static final String DELIVERIES = "deliveries";

    /** DELIVERY_LDF constant */
    private static final String DELIVERY_LDF = "expeditionsLDF";

    /** TOTAL_WEIGHT constant */
    private static final String TOTAL_WEIGHT = "totalWeight";
    
    /** SALE_DELIVERY_DAYS constant */
    private static final int SALE_DELIVERY_DAYS = 28;

    /** repository property */
    private Repository mRepository;

    /** shippingManager property */
    private CastShippingManager mShippingManager;

    /** shippingTools property */
    private CastoShippingTools mShippingTools;

    /**
     * Returns repository property.
     *
     * @return repository property.
     */
    public Repository getRepository() {
        return mRepository;
    }

    /**
     * Sets the value of the repository property.
     *
     * @param pRepository parameter to set.
     */
    public void setRepository(Repository pRepository) {
        mRepository = pRepository;
    }

    /**
     * Returns shippingManager property.
     *
     * @return shippingManager property.
     */
    public CastShippingManager getShippingManager() {
        return mShippingManager;
    }

    /**
     * Sets the value of the shippingManager property.
     *
     * @param pShippingManager parameter to set.
     */
    public void setShippingManager(CastShippingManager pShippingManager) {
        this.mShippingManager = pShippingManager;
    }

    /**
     * Returns shippingTools property.
     *
     * @return the shippingTools
     */
    public CastoShippingTools getShippingTools() {
        return mShippingTools;
    }

    /**
     * Sets the value of the shippingTools property.
     *
     * @param pShippingTools the shippingTools to set
     */
    public void setShippingTools(CastoShippingTools pShippingTools) {
        mShippingTools = pShippingTools;
    }

    /**
     * Calculates a delivery for the given ShippingGroup or SKU object,
     * depending on request parameters.
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        Locale locale = getUserLocale(pRequest);

        String skuId = (String) pRequest.getObjectParameter(SKU_ID);
        if (skuId != null) {
            try {
                final RepositoryItem sku = getRepository().getItem(skuId, CASTO_SKU); 
                String quantityObject = pRequest.getParameter(QUANTITY);
                long quantity = 1;
                if(quantityObject != null && !quantityObject.equalsIgnoreCase("undefined")) {
                    try {
                        quantity = Long.parseLong(quantityObject);
                    } catch (NumberFormatException e) {
                        if(isLoggingWarning()) {
                            logWarning(e);
                        }
                    }
                }
                CastDeliveryBean deliveryPNS = calculateDeliveries(sku, locale, quantity );
                pRequest.setParameter(DELIVERY_PNS, deliveryPNS);
                pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
            } catch (RepositoryException exception) {
                pRequest.serviceParameter(EMPTY, pRequest, pResponse);
            }
        } else {
            List<ShippingGroup> shippingGroups = (List<ShippingGroup>) pRequest.getObjectParameter(ORDER_PROPERTY_SHIPPING_GROUPS);
            if(shippingGroups != null) {

                CastDeliveryBean pnsDelivery = new CastDeliveryBean();
                Map<String, CastDeliveryBean> lDFdeliveries = new HashMap<String, CastDeliveryBean>();
                List<CastDeliveryBean> ldfDeliveries = new ArrayList<CastDeliveryBean>();

                for (ShippingGroup shippingGroup : shippingGroups) {
                    for (CommerceItemRelationship commerceItemRelationShip :
                         (List<CommerceItemRelationship>) shippingGroup.getCommerceItemRelationships()) {
                        CommerceItem commerceItem = commerceItemRelationShip.getCommerceItem();
                        RepositoryItem sku = (RepositoryItem) commerceItem.getAuxiliaryData().getCatalogRef();
                        
    
                        CastDeliveryBean thisBean = getShippingManager().calculateSKUDeliveryInDays(sku, commerceItem.getQuantity());
                        double calculatedDelivery = thisBean.getDelivery();
                        String deliveryTime = thisBean.getDeliveryTime();
                        if (deliveryTime == null) {
                            deliveryTime = getShippingManager().returnDeliveryString(calculatedDelivery, locale);
                            thisBean.setDeliveryTime(deliveryTime);
                        }
                        // SALE constant is "-2" which means that all other delivery amounts (in days) are greater than it
                        // We must set it to 4 weeks amount of days
                        if (calculatedDelivery == SALE) {
                            calculatedDelivery = SALE_DELIVERY_DAYS;
                        }
    
                        int deliveryType = thisBean.getType();
                            if (deliveryType == PNS) {
                                double tmpDelivery = pnsDelivery.getDelivery();
                                if ((calculatedDelivery > tmpDelivery) || (tmpDelivery == 0)) {
                                    pnsDelivery.setDelivery(calculatedDelivery);
                                    pnsDelivery.setDeliveryTime(deliveryTime);
                                }
                                pnsDelivery.addDeliveryItem(sku);
                            } else if (deliveryType == LDF) {
                                String skuSupplier = thisBean.getSupplierName();
                                
                                if (lDFdeliveries.containsKey(skuSupplier)) {
                                    CastDeliveryBean bean = lDFdeliveries.get(skuSupplier);
                                    double tmpDelivery = bean.getDelivery();
                                    if ((calculatedDelivery > tmpDelivery) || (tmpDelivery == 0)) {
                                        bean.setDelivery(calculatedDelivery);
                                        bean.setDeliveryTime(deliveryTime);
                                    }
                                    bean.addDeliveryItem(sku);
                                } else {
                                    lDFdeliveries.put(skuSupplier, thisBean);
                                }
                            } else {
                                /*Delivery type is null*/
                            }  
                          
                    }  
                }  
    
                for(Map.Entry<String, CastDeliveryBean> entry : lDFdeliveries.entrySet()) {
                    ldfDeliveries.add(entry.getValue());
                }
                
                if(pnsDelivery.getDeliveryItems().isEmpty()) {
                    pnsDelivery = null;
                }
                if(ldfDeliveries.isEmpty()) {
                    ldfDeliveries = null;
                }
                
                int deliveries = getShippingManager().calculateAllDeliveries(pnsDelivery, ldfDeliveries);
                
                //returns total weight for first shipping group
                double totalWeight = getShippingTools().getTotalWeight((ShippingGroup) shippingGroups.get(0));
    
                pRequest.setParameter(DELIVERIES, new Integer(deliveries));
                pRequest.setParameter(DELIVERY_PNS, pnsDelivery);
                pRequest.setParameter(DELIVERY_LDF, ldfDeliveries);
                pRequest.setParameter(TOTAL_WEIGHT, totalWeight);
                pRequest.serviceParameter(OUTPUT, pRequest, pResponse);

            } else {
                pRequest.serviceParameter(EMPTY, pRequest, pResponse);
            }
        }  // end if-else
    }

    /**
     * Gets the user's Locale object.
     *
     * @param  pRequest ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     *
     * @throws ServletException ToDo: DOCUMENT ME!
     * @throws IOException      ToDo: DOCUMENT ME!
     */
    private Locale getUserLocale(DynamoHttpServletRequest pRequest) throws ServletException, IOException {
        RequestLocale requestLocale = pRequest.getRequestLocale();
        if (requestLocale != null) {
            return requestLocale.getLocale();
        }

        return Locale.getDefault();
    }

    /**
     * Calculates a delivery for the given SKU object.
     *
     * @param  sku      ToDo: DOCUMENT ME!
     * @param  pLocale  ToDo: DOCUMENT ME!
     * @param  quantity ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     */
    private CastDeliveryBean calculateDeliveries(RepositoryItem sku, Locale pLocale, double quantity) {
        CastDeliveryBean deliveryBean = getShippingManager().calculateSKUDeliveryInDays(sku, quantity);
        String aDeliveryTime = deliveryBean.getDeliveryTime();
        if (aDeliveryTime == null) {
            aDeliveryTime = getShippingManager().returnDeliveryString(deliveryBean.getDelivery(), pLocale);
            if ((aDeliveryTime != null) && !aDeliveryTime.equals("")) {
                deliveryBean.setDeliveryTime(aDeliveryTime);
            }
        }
        return deliveryBean;

    }
}
