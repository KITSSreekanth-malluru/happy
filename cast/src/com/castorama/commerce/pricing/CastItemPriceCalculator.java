package com.castorama.commerce.pricing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.priceLists.ItemPriceCalculator;
import atg.repository.RepositoryItem;
import atg.service.util.CurrentDate;

import com.castorama.pricing.CastItemPriceInfo;


/**
 * @author Alena_Karpenkava
 * 
 */
public class CastItemPriceCalculator extends ItemPriceCalculator {

    static final String TYPE = "type";
    static final String GROUPED_PRODUCT = "casto-grouped-product";
    static final String PACK_PRODUCT = "casto-pack";
    static final String ON_SALE = "onSale";
    static final String END_DATE = "dateFinFonctionnelle";
    static final String PARENT_PRODUCTS = "parentProducts";
    
    private static final String DISPLAY_DISCOUNT = "onSaleDiscountDisplay";
        
    /**
     * Extends the based behavior of ItemPriceCalculator to check "onSale" and "saleEndDate" properties of the SKU.
     * According to this parameters SKU will have sale and list price or just list price.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void priceItem(ItemPriceInfo pPriceQuote, CommerceItem pItem, RepositoryItem pPricingModel, Locale pLocale,
            RepositoryItem pProfile, Map pExtraParameters) throws PricingException {

        RepositoryItem castoSku = (RepositoryItem) pItem.getAuxiliaryData().getCatalogRef();
        if (castoSku != null) {
            Set<RepositoryItem> productSet = (Set<RepositoryItem>) castoSku.getPropertyValue(PARENT_PRODUCTS);
            Object onSaleDiscountDisplay = castoSku.getPropertyValue(DISPLAY_DISCOUNT);
            if (onSaleDiscountDisplay != null && pPriceQuote instanceof CastItemPriceInfo){
                ((CastItemPriceInfo)pPriceQuote).setOnSaleDiscountDisplay((Boolean)onSaleDiscountDisplay);
            }else{
                ((CastItemPriceInfo)pPriceQuote).setOnSaleDiscountDisplay(Boolean.FALSE);
            }
            if (productSet != null && !productSet.isEmpty()) {
                List<RepositoryItem> productList = new ArrayList<RepositoryItem>(productSet);
                String type = (String) productList.get(0).getPropertyValue(TYPE);
                if (PACK_PRODUCT.equals(type) || GROUPED_PRODUCT.equals(type)) {
                    super.priceItem(pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters);
                } else {
                    boolean isOnSale = (Boolean) castoSku.getPropertyValue(ON_SALE);

                    if (isOnSale) {
                        Date endDate = ((Date) castoSku.getPropertyValue(END_DATE));
                        if (endDate != null) {
                            CurrentDate date = new CurrentDate();
                            long currentDate = date.getTime();
                            long saleEndDate = endDate.getTime();
                            if (currentDate < saleEndDate) {
                                super.priceItem(pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters);
                            } else {
                                pPriceQuote.setOnSale(false);
                                return;
                            }
                        } else {
                            super.priceItem(pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters);
                        }
                        
                    } else {
                        pPriceQuote.setOnSale(false);
                        
                        return;
                    }
                }
            } else {
                if (isLoggingWarning())
                    logWarning("Please check property 'parent products' for sku with id " + castoSku.getRepositoryId()
                            + " in the catalog.");
                super.priceItem(pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters);
            }
            
        } else {
            if (isLoggingError())
                logError("Please check product with id " + pItem.getAuxiliaryData().getProductId() + " in the catalog.");
        }
    }
}
