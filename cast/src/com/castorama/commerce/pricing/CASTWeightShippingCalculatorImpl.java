package com.castorama.commerce.pricing;

import java.util.Locale;
import java.util.Map;

import com.castorama.commerce.order.CastOrderImpl;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.catalog.CatalogTools;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;

import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.commerce.pricing.WeightRangeShippingCalculator;

import atg.repository.RepositoryItem;

/**
 * Calculator for calculating shipping amount.
 *
 * @author Katsiaryna Sharstsiuk, Peter Tolochko
 */
public class CASTWeightShippingCalculatorImpl extends WeightRangeShippingCalculator {
    
    /** ORDER_TYPE_CLICK_AND_COLLECT constant. */
    private static final String ORDER_TYPE_CLICK_AND_COLLECT = "clickAndCollect";

    /** BUNDLE_LINKS constant */
    public static final String BUNDLE_LINKS = "bundleLinks";

    /** ITEM constant */
    public static final String ITEM = "item";

    /** FREE_SHIPPING constant. */
    private static final String FREE_SHIPPING = "exonerationPFE";

    /** FIXED_SHIPPING constant. */
    private static final String FIXED_SHIPPING = "sommeForfaitaire";

    /** catalogTools property */
    private CatalogTools mCatalogTools;

    /** shippingTools property */
    private CastoShippingTools mShippingTools;
    
    private int[] rangesWithFixedShippingPrices;

  
    public int[] getRangesWithFixedShippingPrices() {
		return rangesWithFixedShippingPrices;
	}
    
    public void setRangesWithFixedShippingPrices(
			int[] rangesWithFixedShippingPrices) {
		this.rangesWithFixedShippingPrices = rangesWithFixedShippingPrices;
	}
    
    
    /**
     * Sets property catalogTools.
     *
     * @param pCatalogTools the catalogTools which provides low level operations
     *                      on the catalog.   *
     */
    public void setCatalogTools(CatalogTools pCatalogTools) {
        mCatalogTools = pCatalogTools;
    }

    /**
     * Returns property catalogTools.
     *
     * @return The value of the property CatalogTools.
     */
    public CatalogTools getCatalogTools() {
        return mCatalogTools;
    }

    /**
     * Returns shippingTools property.
     *
     * @return shippingTools property.
     */
    public CastoShippingTools getShippingTools() {
        return mShippingTools;
    }

    /**
     * Sets the value of the shippingTools property.
     *
     * @param pShippingTools parameter to set.
     */
    public void setShippingTools(CastoShippingTools pShippingTools) {
        mShippingTools = pShippingTools;
    }

    /* (non-Javadoc)
     * @see atg.commerce.pricing.DoubleRangeShippingCalculator#getAmount(atg.commerce.order.Order, atg.commerce.pricing.ShippingPriceInfo, atg.commerce.order.ShippingGroup, atg.repository.RepositoryItem, java.util.Locale, atg.repository.RepositoryItem, java.util.Map)
     */
    @Override protected double getAmount(Order pOrder, ShippingPriceInfo pPriceQuote, ShippingGroup pShippingGroup,
                                         RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile,
                                         Map pExtraParameters) throws PricingException {
        if (getShippingTools() != null) {
            getShippingTools().affecterPoidsTotalShippingGroup(pShippingGroup);
            getShippingTools().copierInformationsSkusToCommerceItems(pShippingGroup);
        }

        //if order type is only CC - return 0.0
        if (pOrder instanceof CastOrderImpl){
            String shippingType = ((CastOrderImpl)pOrder).getDeliveryType();
            if (shippingType != null && shippingType.equals(ORDER_TYPE_CLICK_AND_COLLECT)){
                return 0.0;
            }
        }
        double totalWeight = getWeightTotal(pShippingGroup);
        double fixedShippingPrice = getShippingTools().getFixedShippingPrice(pShippingGroup);
        if(totalWeight > 0){
            double regularShippingPrice = 0;
            int length = mLowRanges.length;
            for (int c = 0; c < length; c++) {
                if ((totalWeight >= mLowRanges[c]) && (totalWeight <= mHighRanges[c])) {
                    regularShippingPrice = mAmounts[c];
                    for(int i=0; i<rangesWithFixedShippingPrices.length; i++){
                    	if (rangesWithFixedShippingPrices[i]==c){
                    		return regularShippingPrice + fixedShippingPrice;
                    		 
                    	}
                   }
                   return (regularShippingPrice * totalWeight)+fixedShippingPrice;
                }
            }
        } else {
            return fixedShippingPrice;
        }

        String msg = "No price could be determined for shipping. (range comparison value=" + totalWeight + ")";
        throw new PricingException(msg);
    }
    /**
     * Returns weight property.
     *
     * @param  pCommerceItem parameter to set.
     *
     * @return weight property.
     *
     * @throws PricingException - exception
     */
    @Override protected double getWeight(CommerceItem pCommerceItem) throws PricingException {
        return getShippingTools().getWeightForShipping(pCommerceItem);
    }

    /**
     * Gets shipping amount for one particulary commerce item.
     * @param commerceItem - commerce item
     * @return shipping amount
     */
    public double getAmount(CommerceItem commerceItem) {
        double totalWeight = getShippingTools().getWeight(commerceItem);
        double fixedShippingPrice = 0.0;

        Object sku = commerceItem.getAuxiliaryData().getCatalogRef();

        Boolean freeShipping;
        try {
            freeShipping = (Boolean) DynamicBeans.getPropertyValue(sku, FREE_SHIPPING);
            if (!freeShipping.booleanValue()) {
                Float fPrice = (Float) DynamicBeans.getPropertyValue(sku, FIXED_SHIPPING);
                if(fPrice != null) {
                    fixedShippingPrice = fPrice.doubleValue();
                }

            }
        } catch (PropertyNotFoundException e) {
       }
        if(totalWeight > 0){
            double regularShippingPrice = 0;
            int length = mLowRanges.length;
            for (int c = 0; c < length; c++) {
                if ((totalWeight >= mLowRanges[c]) && (totalWeight <= mHighRanges[c])) {
                    regularShippingPrice = mAmounts[c];
                    for(int i=0; i<rangesWithFixedShippingPrices.length; i++){
                    	if (rangesWithFixedShippingPrices[i]==c){
                    		return regularShippingPrice + fixedShippingPrice;
                    		 
                    	}
                   }
                   return (regularShippingPrice * totalWeight)+fixedShippingPrice;
                }
            }
        } else {
            return fixedShippingPrice;
        }
   	 return 0.0;
    }
    
 }
