package com.castorama.commerce.order;

import java.math.BigDecimal;

import java.util.List;

import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingAdjustment;

import com.castorama.commerce.pricing.Constants;

/**
 * Extends OrderPriceInfo castorama extentions.
 *
 * @author Vasili_Ivus
 */
public class CastOrderPriceInfo extends OrderPriceInfo {
    /**
     * ACCURACY constant - defines accuracy for decimal numbers used as price
     */
    public static final int ACCURACY = 2;

    /**
     * Extends getTotal of the OrderPriceInfo to add shipping fee.
     *
     * @return total order's amount
     *
     * @see    atg.commerce.pricing.OrderPriceInfo#getTotal()
     */
    @Override public double getTotal() {
        double handlingPrice = 0.0;
        List<PricingAdjustment> pricingAdjustments = (List<PricingAdjustment>) getAdjustments();

        if (null != pricingAdjustments) {
            for (PricingAdjustment pa : pricingAdjustments) {
                if (Constants.HANDLING_PRICE_ADJUSTMENT_DESCRIPTION.equalsIgnoreCase(pa.getAdjustmentDescription())) {
                    handlingPrice += pa.getTotalAdjustment();
                }
            }
        }

        double totalAmount = super.getTotal() + handlingPrice;

        if (needsRounding(totalAmount)) {
            return round(totalAmount);
        } else {
            return totalAmount;
        }
    }

    /**
     * Checks whether rounding is required.
     *
     * @param  pNumber - number for rounding.
     *
     * @return true if rounding is required.
     */
    public boolean needsRounding(double pNumber) {
        BigDecimal bd = new BigDecimal(Double.toString(pNumber));
        int length = bd.scale();

        return length > ACCURACY;
    }

    /**
     * Rounds decimal number with definite accuracy.
     *
     * @param  pNumber - number for rounding.
     *
     * @return rounded value.
     */
    public double round(double pNumber) {
        return round(pNumber, ACCURACY);
    }

    /**
     * Rounds decimal number with definite accuracy.
     *
     * @param  pNumber   - number for rounding
     * @param  pAccuracy - accuracy
     *
     * @return rounded double value.
     */
    public double round(double pNumber, int pAccuracy) {
        BigDecimal bd = new BigDecimal(Double.toString(pNumber));
        bd = bd.setScale(pAccuracy, BigDecimal.ROUND_HALF_UP);

        return bd.doubleValue();
    }
}
