package com.castorama.commerce.pricing;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import atg.commerce.order.Order;

import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.ItemDiscountCalculator;
import atg.commerce.pricing.PricingException;

import atg.repository.RepositoryItem;

/**
 * Override findAdjustedPrice method to return rounded price.
 *
 * @author  EPAM team
 */
public class CastItemDiscountCalculator extends ItemDiscountCalculator {
    /**
     * Return rounded adjusted price. 
     *
     * @see atg.commerce.pricing.ItemDiscountCalculator
     */
    @Override public double findAdjustedPrice(DetailedItemPriceInfo detailedItemPriceInfo, List priceQuotes, List items,
                                              RepositoryItem pricingModel, RepositoryItem profile, Locale locale,
                                              Order order, Map extraParameters) throws PricingException {
        double adjustedPrice =
            super.findAdjustedPrice(detailedItemPriceInfo, priceQuotes, items, pricingModel, profile, locale, order,
                                    extraParameters);
        return getPricingTools().round(adjustedPrice);
    }

}
