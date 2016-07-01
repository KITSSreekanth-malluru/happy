package com.castorama.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import javax.servlet.ServletException;

import com.castorama.checkout.CastClaimableManager;

import atg.commerce.claimable.ClaimableManager;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.promotion.PromotionTools;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userprofiling.Profile;
import atg.commerce.promotion.PromotionException;
/**
 * Class shows all promotions on Order and ShippingGroup levels
 *
 * @author  EPAM team
 */
public class OrderPromotionsDroplet extends DynamoServlet {

    /** OUTPUT constant */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** EMPTY constant */
    public static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /** ORDER_PARAM constant */
    public static final String ORDER_PARAM = "order";

    /** ORDER_LOCAL_PARAM constant */
    public static final String ORDER_LOCAL_PARAM = "orderLocal";

    /** PROMOTIONS constant */
    public static final String PROMOTIONS = "orderPromotions";

    public static final String CLAIMED_PROMOTIONS = "claimedPromotions";
    
    /** END_OF_USAGE constant */
    public static final String END_OF_USAGE = "endUsable";
    
    /** PAGE_NAME constant */
    public static final String PAGE_NAME = "pageName";
    
    public static final int SHIPPING_PROMOTION_TYPE = 6;
    
    /** profile property */
    private Profile mProfile;

    /**
     * Iterates over promotions on Order and ShippingGroup levels and saves them into Map
     *
     * @param  pRequest  DynamoHttpServletRequest object
     * @param  pResponse DynamoHttpServletResponse object
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        Order order = null, orderLocal = null;
        String pageName = null;
        
        try {
            pageName = String.valueOf(pRequest.getObjectParameter(PAGE_NAME));
            order = (Order) pRequest.getObjectParameter(ORDER_PARAM);
            orderLocal = (Order) pRequest.getObjectParameter(ORDER_LOCAL_PARAM);
        } catch (ClassCastException ex) {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
        }

        if (order != null) {
        	List<RepositoryItem> promotions = new ArrayList<RepositoryItem>();
            getPromotionTools().getOrderPromotions(order, promotions);
            for (ShippingGroup shippingGroup : (List<ShippingGroup>) order.getShippingGroups()) {
                getPromotionTools().getShippingPromotions(shippingGroup, promotions);
            }
            
            if (orderLocal != null) {
                List<RepositoryItem> promotionsLocal = new ArrayList<RepositoryItem>();
                getPromotionTools().getOrderPromotions(orderLocal, promotionsLocal);
                for (ShippingGroup shippingGroup : (List<ShippingGroup>) orderLocal.getShippingGroups()) {
                    getPromotionTools().getShippingPromotions(shippingGroup, promotionsLocal);
                }
                promotions.removeAll(promotionsLocal);
                promotions.addAll(promotionsLocal);
            }
            
            if(!pageName.equals("orderPage")) {
                CastClaimableManager manager = (CastClaimableManager) getClaimableManager();
                List<RepositoryItem> couponsPromotions = manager.getClimedPromotions(getProfile());
                if (couponsPromotions != null){
    	            promotions.removeAll(couponsPromotions);
    	            promotions.addAll(couponsPromotions);
                }
            }

            DateComparator compare = new DateComparator();
            Collections.sort(promotions, compare);

            pRequest.setParameter(PROMOTIONS, promotions);
            pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        }  
    }

    /**
     * Custom comparator which compares <RepositoryItem> objects by its value's
     * given property.
     */
    class DateComparator implements Comparator<RepositoryItem> {
        /**
         * Overrides the Comparator.compare() method
         */
        public int compare(RepositoryItem one, RepositoryItem two) {
            Date oneDate = (Date) one.getPropertyValue(END_OF_USAGE);
            Date twoDate = (Date) two.getPropertyValue(END_OF_USAGE);
            if ((oneDate != null) && (twoDate != null)) {
                return oneDate.compareTo(twoDate);
            } else {
                if (oneDate == null) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }
    

    /**
     * Returns profile property.
     *
     * @return profile property.
     */
    public Profile getProfile() {
        return mProfile;
    }

    /**
     * Sets the value of the profile property.
     *
     * @param pProfile parameter to set.
     */
    public void setProfile(Profile pProfile) {
        mProfile = pProfile;
    }

    //-------------------------------------
    // property: PromotionTools
    /** Promotion tools to help in calculating prices */
    PromotionTools mPromotionTools;

    /**
     * Promotion tools to help in calculating prices
     * @beaninfo description: Promotion tools to help in calculating promotions
     * @param pPromotionTools new value to set
     */
    public void setPromotionTools(PromotionTools pPromotionTools)
    {mPromotionTools = pPromotionTools;}

    /**
     * Promotion tools to help in calculating prices
     * @beaninfo description: Promotion tools to help in calculating prices
     * @return property PromotionTools
     */
    public PromotionTools getPromotionTools()
    {return mPromotionTools;}
    
    ClaimableManager mClaimableManager;

    /**
     * Set the ClaimableManager property.
     */
    public void setClaimableManager(ClaimableManager pClaimableManager) {
      mClaimableManager = pClaimableManager;
    }

    /**
     * Return the ClaimableManager property.
     */
    public ClaimableManager getClaimableManager() {
      return mClaimableManager;
    }    
    
}
