package com.castorama.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * ItemPromotionsDroplet class shows all promotions for the given <CommerceItem>
 * object
 *
 * @author EPAM team
 */
public class ItemPromotionsDroplet extends DynamoServlet {
    /** OUTPUT constant */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** EMPTY constant */
    public static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /** PARAM_ITEM constant */
    private static final String PARAM_ITEM = "commerceItem";

    /** END_OF_USAGE constant */
    private static final String END_OF_USAGE = "endUsable";

    /** PROMOTIONS constant */
    private static final String PROMOTIONS = "itemsPromotions";


    /**
     * Iterates over promotions for the given CommerceItem and saves them into
     * List
     *
     * @param  pRequest  DynamoHttpServletRequest object
     * @param  pResponse DynamoHttpServletResponse object
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        CommerceItem commerceItem = null;
        try {
            commerceItem = (CommerceItem) pRequest.getObjectParameter(PARAM_ITEM);
        } catch (ClassCastException ex) {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
            return;
        }

        if(commerceItem != null) {
            ItemPriceInfo priceInfo = commerceItem.getPriceInfo();
            List<RepositoryItem> promotions = new ArrayList<RepositoryItem>();
            if ((priceInfo != null) && (priceInfo.getAdjustments() != null)) {
                for (PricingAdjustment pa : (List<PricingAdjustment>) priceInfo.getAdjustments()) {
                    RepositoryItem promotion = pa.getPricingModel();
                    if (promotion != null) {
                        promotions.add(promotion);
                    }  // end if
                }  // end for
            }  // end if
    
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

}
