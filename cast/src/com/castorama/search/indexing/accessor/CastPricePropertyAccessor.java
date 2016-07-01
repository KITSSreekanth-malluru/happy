package com.castorama.search.indexing.accessor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.commerce.search.producer.PriceListMapPropertyAccessor;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

/**
 * @author Mikalai_Khatsko
 * 
 */
public class CastPricePropertyAccessor extends PriceListMapPropertyAccessor {

    /**
     * M2 constant
     */
    private static final String M2 = "m2";
    /**
     * PUPUV2 constant
     */
    private static final String PUPUV2 = "PUPUV";
    /**
     * CUP constant
     */
    private static final String CUP = "CUP";
    /**
     * SHOW_M2_PRICE_FIRST constant
     */
    private static final String SHOW_M2_PRICE_FIRST = "showM2PriceFirst";
    /**
     * ON_SALE constant
     */
    private static final String ON_SALE = "onSale";
    /**
     * SALE_PRICES constant
     */
    private static final String SALE_PRICES = "salePrices";

    private PriceListManager mLocalStoresPriceListManager;

    protected Map<String, Object> getPropertyNamesAndValues(Context pContext, RepositoryItem pItem, String pPropertyName, 
            PropertyTypeEnum pType, boolean pIsMeta) {

        Map<String, Object> res = new HashMap<String, Object>();

        Collection<RepositoryItem> priceLists = getPriceLists();

        if (priceLists != null) {

            Object objOnSale = pItem.getPropertyValue(ON_SALE);
            boolean onSale = objOnSale instanceof Boolean && (Boolean) objOnSale;

            for (RepositoryItem priceList : priceLists) {
                RepositoryItem price;

                try {
                    price = getPriceListManager().getPrice(priceList, null, pItem);

                    if (price != null) {
                        Object p = price.getPropertyValue(LIST_PRICE_PROPERTY);
                        Object objShowM2PriceFirst = pItem.getPropertyValue(SHOW_M2_PRICE_FIRST);
                        if (objShowM2PriceFirst != null && objShowM2PriceFirst instanceof Boolean) {
                            Boolean showM2PriceFirst = (Boolean) objShowM2PriceFirst;
                            if (showM2PriceFirst) {
                                Object objCodeDeVente = pItem.getPropertyValue(CUP);
                                if (objCodeDeVente != null && objCodeDeVente instanceof String) {
                                    String codeDeVente = (String) objCodeDeVente;
                                    if (codeDeVente.equalsIgnoreCase(M2)) {
                                        Object objPUPUV = pItem.getPropertyValue(PUPUV2);
                                        if (objPUPUV != null && objPUPUV instanceof Float) {
                                            Float PUPUV = (Float) objPUPUV;
                                            if (PUPUV > 0) {
                                                Object objListPrice = price.getPropertyValue(LIST_PRICE_PROPERTY);
                                                if (objListPrice != null && objListPrice instanceof Double) {
                                                    Double m2Price = (Double) objListPrice / PUPUV;
                                                    p = m2Price;
                                                }

                                            }
                                        }
                                    }

                                }
                            }
                        }

                        if (priceList.getRepositoryId().equals(SALE_PRICES) && !onSale) {
                            continue;
                        }

                        res.put(getPricePropertyPrefix() + priceList.getRepositoryId(), p);
                    }
                } catch (PriceListException e) {
                    pContext.getIndexingOutputConfig().logError("Cannot retrieve price for sku: " + pItem.getRepositoryId(), e);
                }
            }
        }

        return res;
    }

    public PriceListManager getLocalStoresPriceListManager() {
        return mLocalStoresPriceListManager;
    }

    public void setLocalStoresPriceListManager(PriceListManager pLocalStoresPriceListManager) {
        mLocalStoresPriceListManager = pLocalStoresPriceListManager;
    }

}
