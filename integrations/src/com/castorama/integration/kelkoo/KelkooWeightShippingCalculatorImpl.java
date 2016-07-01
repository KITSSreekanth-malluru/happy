package com.castorama.integration.kelkoo;

import java.util.List;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;

import atg.commerce.pricing.WeightRangeShippingCalculator;

import atg.repository.RepositoryItem;

/**
 * Calculator for calculating shipping amount.
 *
 * @author EPAM Team
 */
public class KelkooWeightShippingCalculatorImpl extends WeightRangeShippingCalculator {
    
    /* FREE_SHIPPING constant. */
    private static final String FREE_SHIPPING = "exonerationPFE";

    /* FIXED_SHIPPING constant. */
    private static final String FIXED_SHIPPING = "sommeForfaitaire";

    /* POIDS_UV constant. */
    private static final String POIDS_UV = "PoidsUV";
    
    /* BUNDLE_LINKS constant. */
    private static final String BUNDLE_LINKS = "bundleLinks";
    
    /* ITEM constant. */
    private static final String ITEM = "item";
    
    /* QUANTITY constant. */
    public static final String QUANTITY = "quantity";
    
    /**
     * Gets shipping amount for SKU.
     * @param sku
     * @return shipping amount
     */
    @SuppressWarnings("unchecked")
    public double getSkuShipmentFee(RepositoryItem sku) {
    	double fixedShippingPrice = 0.0;
    	double totalWeight = 0.0;
    	
		try {
		    Boolean freeShipping = (Boolean) DynamicBeans.getPropertyValue(sku, FREE_SHIPPING);
	        if (!freeShipping.booleanValue()) {
	        	Float fPrice = (Float) DynamicBeans.getPropertyValue(sku, FIXED_SHIPPING);
	        	if(fPrice != null) {
	        		fixedShippingPrice = fPrice.doubleValue();
	        	}
	        }
		
            Integer poidsUV = (Integer) sku.getPropertyValue(POIDS_UV);
            if (poidsUV != null) {
                totalWeight = getSkuShipmentWeight(sku);
            } else {
                List<RepositoryItem> bundledLinks =
                    (List<RepositoryItem>) DynamicBeans.getPropertyValue(sku, BUNDLE_LINKS);
                for (RepositoryItem link : bundledLinks) {
                    double tempWeight = getSkuShipmentWeight(link.getPropertyValue(ITEM));
                    totalWeight += (tempWeight * ((Long) link.getPropertyValue(QUANTITY)));
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
                    if(totalWeight > 1) {
                        regularShippingPrice = (mAmounts[c] * totalWeight);
                    }
                    return regularShippingPrice + fixedShippingPrice;
                }
            }
        } else {
            return fixedShippingPrice;
        }
        return 0.0;
    }
    
    /* */
    private double getSkuShipmentWeight(Object sku) {
        double weight = 0.0;
        try {
            Object weightObject = DynamicBeans.getPropertyValue(sku, POIDS_UV);
            Boolean freeShipping = (Boolean) DynamicBeans.getPropertyValue(sku, FREE_SHIPPING);
            Float fixedShippingPrice = (Float) DynamicBeans.getPropertyValue(sku, FIXED_SHIPPING);
            
            if (!freeShipping) {
                if (fixedShippingPrice == null || fixedShippingPrice.equals(new Float("0"))) {
                    if (weightObject != null) {
                        weight = ((Number) weightObject).doubleValue() / 1000;
                    }
                }
            }
        } catch (PropertyNotFoundException pnfe) {
            pnfe.printStackTrace();
            weight = 0;
        }
        
        return weight;
    }
}
