package com.castorama.commerce.pricing;

import java.util.Locale;
import java.util.Map;

import com.castorama.commerce.order.CastOrderImpl;

import atg.commerce.catalog.CatalogTools;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;

import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.commerce.pricing.WeightRangeShippingCalculator;

import atg.repository.RepositoryItem;


/**
 * Calculator for calculating handling amount.
 *
 * @author Katsiaryna Sharstsiuk, Peter Tolochko
 */
public class CASTHandlingWeightShippingCalculatorImpl extends WeightRangeShippingCalculator {

    /** ORDER_TYPE_CLICK_AND_COLLECT constant. */
    private static final String ORDER_TYPE_CLICK_AND_COLLECT = "clickAndCollect";

    /** catalogTools property */
    private CatalogTools mCatalogTools;

    /** shippingTools property */
    private CastoShippingTools mShippingTools;

    /** clickAndCollectProcessingFee property */
    private float clickAndCollectProcessingFee;

    /**
     * Sets property catalogTools.
     *
     * @param pCatalogTools the catalogTools which provides utility methods.
     */
    public void setCatalogTools(CatalogTools pCatalogTools) {
        mCatalogTools = pCatalogTools;
    }

    /**
     * Returns catalogTools property.
     *
     * @return value of the property CatalogTools.
     */
    public CatalogTools getCatalogTools() {
        return mCatalogTools;
    }

    /**
     * Sets property shippingTools.
     *
     * @param pShippingTools the shippingTools.
     */
    public void setShippingTools(CastoShippingTools pShippingTools) {
        mShippingTools = pShippingTools;
    }

    /**
     * Returns property shippingTools.
     *
     * @return The value of the property shippingTools.
     */
    public CastoShippingTools getShippingTools() {
        return mShippingTools;
    }

    /**
     * Returns property clickAndCollectProcessingFee.
     *
     * @return the clickAndCollectProcessingFee
     */
    public float getClickAndCollectProcessingFee() {
        return clickAndCollectProcessingFee;
    }

    /**
     * Sets property clickAndCollectProcessingFee.
     *
     * @param clickAndCollectProcessingFee the clickAndCollectProcessingFee to set
     */
    public void setClickAndCollectProcessingFee(float clickAndCollectProcessingFee) {
        this.clickAndCollectProcessingFee = clickAndCollectProcessingFee;
    }

    /* (non-Javadoc)
     * @see atg.commerce.pricing.WeightRangeShippingCalculator#getWeight(atg.commerce.order.CommerceItem)
     */
    @Override protected double getWeight(CommerceItem pCommerceItem) throws PricingException {
        return getShippingTools().getWeightForHandling(pCommerceItem);
    }

    /* (non-Javadoc)
     * @see atg.commerce.pricing.DoubleRangeShippingCalculator#getAmount(params)
     */
    @Override protected double getAmount(Order pOrder, ShippingPriceInfo pPriceQuote, ShippingGroup pShippingGroup,
                                         RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile,
                                         Map pExtraParameters) throws PricingException {
        double result = 0.0;
        //if order type is only CC - return 0.0
        if(pOrder instanceof CastOrderImpl){
            String orderType = ((CastOrderImpl)pOrder).getDeliveryType();
            if(orderType != null && orderType.equals(ORDER_TYPE_CLICK_AND_COLLECT)){
                pOrder.getPriceInfo().getAdjustments().add(new PricingAdjustment(Constants.HANDLING_PRICE_ADJUSTMENT_DESCRIPTION, null,
                        getPricingTools().round(getClickAndCollectProcessingFee()), 1));
                return 0.0;
            }
        }
        double value = getRangeComparisonValue(pOrder,pShippingGroup);
        int length = mLowRanges.length;
        for (int c=0; c<length; c++) {
          if ((value >= mLowRanges[c]) && (value <= mHighRanges[c])) {
              result = mAmounts[c];
              break;
          }
        }
        
        pOrder.getPriceInfo().getAdjustments().add(new PricingAdjustment(Constants.HANDLING_PRICE_ADJUSTMENT_DESCRIPTION, null,
                getPricingTools().round(result), 1));
        
        return 0.0;
    }

}
