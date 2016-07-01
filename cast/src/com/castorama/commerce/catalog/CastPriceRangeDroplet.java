package com.castorama.commerce.catalog;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;

import com.castorama.pricing.CastItemPriceInfo;

import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PriceRangeDroplet;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingTools;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * Retrieves cheapest SKU of the product.
 * 
 * @author EPAM Team
 */ 
public class CastPriceRangeDroplet extends PriceRangeDroplet {
    /** SKU constant. */
    public static String SKU = "sku";

    /** EMPTY constant. */
    public static String EMPTY = "empty";

    private static final String NOT_CHEAPEST_SKU_PROMO = "notCheapestSkuPromo";

    private PricingTools mPricingTools;

    /**
     * @return the pricingTools
     */
    public PricingTools getPricingTools() {
        return mPricingTools;
    }

    /**
     * @param pPricingTools the pricingTools to set
     */
    public void setPricingTools(PricingTools pPricingTools) {
        mPricingTools = pPricingTools;
    }

    /**
     * Extends the based behavior to create an additional cheapest SKU output parameter.
     * 
     * @param pRequest
     *            parameter
     * @param pResponse
     *            parameter
     * 
     * @throws ServletException
     *             exception
     * @throws IOException
     *             exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        Object productId = pRequest.getObjectParameter(PRODUCT_ID);
        Map<Object, Object> priceSkuMap = new HashMap<Object, Object>();
        if (productId != null) {
            try {
                RepositoryItem product = getCatalogRepository().getItem(productId.toString(), getProductItemType());
                if (product != null) {
                    if (isUsingPriceLists()) {
                        generatePriceListPriceRange(product, priceSkuMap, pRequest, pResponse);
                    }
                }
            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    logError("RepositoryException in PriceRangeDroplet: cannot get product item with productId " + productId.toString());
                }
            }
        }
    }

    /**
     * Generates prices list based on price list
     * 
     * @param pProduct
     *            product
     * @param pPriceList
     *            price list
     * @param pPrices
     *            list of prices
     */
    protected void generatePriceListPrices(RepositoryItem pProduct, Map<Object, Object> priceSkuMap, List pPrices) {
        List childSKUs = (List) pProduct.getPropertyValue(getChildSKUsPropertyName());

        if (childSKUs != null) {
            for (int i = 0; i < childSKUs.size(); i++) {
                RepositoryItem sku = (RepositoryItem) childSKUs.get(i);
                try {
                    ItemPriceInfo ipi = getPricingTools().calculatePrice(pProduct, sku, 1);

                    if (ipi != null) {
                        double doublePrice = ipi.getAmount();
                        if (!priceSkuMap.containsKey(doublePrice)) {
                            priceSkuMap.put(doublePrice, sku);
                        }
                        pPrices.add(doublePrice);
                    } else {
                        priceSkuMap.put(0.0, sku);
                    }
                } catch (PricingException e) {
                    if (isLoggingDebug()) {
                        logError("PricingException in PriceRangeDroplet: can't calculate commerce item for product: " + (pProduct != null ? pProduct : "empty") + ". ", e);
                    } else if (isLoggingError()) {
                        logError("PricingException in PriceRangeDroplet: can't calculate commerce item for product: " + (pProduct != null ? pProduct : "empty") + ". " + e.getMessage());
                    }
                    priceSkuMap.put(0.0, sku);
                }
            } // end for
        } // end if
    }

    /**
     * Find SKU with the lowest price.
     * 
     * @param product
     *            product
     * @param pRequest
     *            request
     * @param pResponse
     *            response
     */
    public void generatePriceListPriceRange(RepositoryItem product, Map<Object, Object> priceSkuMap, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
        List prices = new Vector();
        double lowestPrice = 0.0;
        RepositoryItem cheapestChildSkuWithBOPromotion = null;
        generatePriceListPrices(product, priceSkuMap, prices);
        cheapestChildSkuWithBOPromotion = getCheapestChildSkuWithBOPromotion(product);
        lowestPrice = getLowestPrice(prices);

        RepositoryItem sku = (RepositoryItem) priceSkuMap.get(lowestPrice);
        pRequest.setParameter(SKU, sku);
        if (lowestPrice != 0.0) {
            if ((cheapestChildSkuWithBOPromotion != null) && !cheapestChildSkuWithBOPromotion.equals(sku)) {
                pRequest.setParameter(NOT_CHEAPEST_SKU_PROMO, true);
            }
        }

        try {
            pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
            if (lowestPrice == 0.0) {
                pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
            }
        } catch (ServletException se) {
            if (isLoggingError()) {
                logError(se);
            }
        } catch (IOException ioe) {
            if (isLoggingError()) {
                logError(ioe);
            }
        }
    }

    private RepositoryItem getCheapestChildSkuWithBOPromotion(RepositoryItem pProduct) {
        RepositoryItem result = null;
        double lowestPrice = Double.MAX_VALUE;
        List childSKUs = (List) pProduct.getPropertyValue(getChildSKUsPropertyName());
        if (childSKUs != null) {
            for (int i = 0; i < childSKUs.size(); i++) {
                RepositoryItem sku = (RepositoryItem) childSKUs.get(i);
                Boolean onSaleDiscountDisplay = false;
                Boolean isOnSale = (Boolean) false;

                try {
                    ItemPriceInfo ipi = getPricingTools().calculatePrice(pProduct, sku, 1);
                    if (ipi != null && ipi instanceof CastItemPriceInfo) {
                        isOnSale = ipi.isOnSale();
                        onSaleDiscountDisplay = ((CastItemPriceInfo) ipi).getOnSaleDiscountDisplay();
                    }

                    if (isOnSale && (onSaleDiscountDisplay != null) && onSaleDiscountDisplay) {
                        double doublePrice = ipi.getAmount();
                        if (doublePrice < lowestPrice) {
                            lowestPrice = doublePrice;
                            result = sku;
                        }
                    }
                } catch (PricingException e) {
                    if (isLoggingError())
                        logError(e);
                }
            }
        }
        return result;
    }

    /**
     * Support local prices
     * @param prices list of prices
     * @return the lowest price
     */
    protected double getLowestPrice(List prices) {
        double lowestPrice = 0.0;
        if (prices != null) {
            for (int i = 0; i < prices.size(); i++) {
                double doublePrice = ((Double) prices.get(i)).doubleValue();
                if (i == 0)
                    lowestPrice = doublePrice;
                else if (doublePrice < lowestPrice)
                    lowestPrice = doublePrice;
            }
        }
        return lowestPrice;
    }
}
