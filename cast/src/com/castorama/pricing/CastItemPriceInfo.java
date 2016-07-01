package com.castorama.pricing;

import java.util.List;

import atg.commerce.pricing.Constants;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment;

/**
 * ToDo: DOCUMENT ME!
 * 
 * @author  Katsiaryna Sharstsiuk, MK
 */
public class CastItemPriceInfo extends ItemPriceInfo {
    /** onSaleDiscountDisplay property. */
    private Boolean mOnSaleDiscountDisplay;

    /** Card Price property. */
    double mCardPrice;

    /** Local price applied property. */
    boolean mIsLocalPriceApplied = false;

    /** IsValidByDateCardPrice property. */
    boolean mIsValidByDateCardPrice = false;

    /**
     * Returns onSaleDiscountDisplay property.
     * 
     * @return onSaleDiscountDisplay property.
     */
    public Boolean getOnSaleDiscountDisplay() {
        return mOnSaleDiscountDisplay;
    }

    /**
     * Sets the value of the onSaleDiscountDisplay property.
     * 
     * @param pOnSaleDiscountDisplay parameter to set.
     */
    public void setOnSaleDiscountDisplay(Boolean pOnSaleDiscountDisplay) {
        if (pOnSaleDiscountDisplay != null) {
            mOnSaleDiscountDisplay = pOnSaleDiscountDisplay;
        } else {
            mOnSaleDiscountDisplay = Boolean.FALSE;
        }

    }

    public void setCardPrice(double pCardPrice) {
        mCardPrice = pCardPrice;
    }

    public double getCardPrice() {
        return mCardPrice;
    }

    public void setLocalPriceApplied(boolean pApplied) {
        mIsLocalPriceApplied = pApplied;
    }
    
    public boolean isLocalPriceApplied() {
        return mIsLocalPriceApplied;
    }

    public boolean getIsValidByDateCardPrice() {
        return mIsValidByDateCardPrice;
    }

    public void setIsValidByDateCardPrice(boolean pIsValidByDateCardPrice) {
        mIsValidByDateCardPrice = pIsValidByDateCardPrice;
    }
}
