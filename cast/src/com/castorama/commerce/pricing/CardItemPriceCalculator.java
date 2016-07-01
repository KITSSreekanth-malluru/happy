package com.castorama.commerce.pricing;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.castorama.commerce.order.CastOrderImpl;
import com.castorama.pricing.CastItemPriceInfo;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.pricing.AmountInfo;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.priceLists.Constants;
import atg.commerce.pricing.priceLists.ItemPriceCalculator;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.service.perfmonitor.PerformanceMonitor;

/**
 * @author MK
 */
public class CardItemPriceCalculator extends ItemPriceCalculator {

    private static final String CARD_PRICE_ADJUSTMENT_DESCRIPTION = "Card price";
    private static final String CASTORAMA_PRICE_END_DATE = "castoramaPriceEndDate";
    private static final String CASTORAMA_PRICE_START_DATE = "castoramaPriceStartDate";
    private static final String LIST_PRICE_PROPERTY_NAME = "listPrice";
    private static final String CARD_PRICES = "cardPrices";
    static final String TYPE = "type";
    static final String GROUPED_PRODUCT = "casto-grouped-product";
    static final String PACK_PRODUCT = "casto-pack";
    static final String ON_SALE = "onSale";
    static final String PARENT_PRODUCTS = "parentProducts";
    
    private MutableRepository mRepository;

    public void priceItems(List pPriceQuotes, List pItems, RepositoryItem pPricingModel, Locale pLocale,
            RepositoryItem pProfile, Order pOrder, Map pExtraParameters) throws PricingException {
        priceEachItem(pPriceQuotes, pItems, pPricingModel, pLocale, pProfile, pExtraParameters, pOrder);
    }

    /**
     * @param pPriceQuotes
     * @param pItems
     * @param pPricingModel
     * @param pLocale
     * @param pProfile
     * @param pExtraParameters
     * @param pOrder
     */
    private void priceEachItem(List pPriceQuotes, List pItems, RepositoryItem pPricingModel, Locale pLocale,
            RepositoryItem pProfile, Map pExtraParameters, Order pOrder) throws PricingException {

        boolean isEnded = false;
        try {
            if (pPriceQuotes.size() != pItems.size()) {
                throw new PricingException(MessageFormat.format(Constants.BAD_LIST_PARAMS,
                        new Object[] { Integer.valueOf(pPriceQuotes.size()), Integer.valueOf(pItems.size()) }));
            }
            ListIterator listIt = pItems.listIterator();
            ListIterator quoteIt = pPriceQuotes.listIterator();
            while (quoteIt.hasNext() && listIt.hasNext()) {
                ItemPriceInfo amount = (ItemPriceInfo) quoteIt.next();
                CommerceItem item = (CommerceItem) listIt.next();
                priceItem(amount, item, pPricingModel, pLocale, pProfile, pExtraParameters, pOrder);
            }
        } finally {
            if (!isEnded) {
                isEnded = true;
            }
        }
    }

    public void priceItem(ItemPriceInfo pPriceQuote, CommerceItem pItem, RepositoryItem pPricingModel, Locale pLocale,
            RepositoryItem pProfile, Map pExtraParameters, Order pOrder) throws PricingException {
        RepositoryItem castoSku = (RepositoryItem) pItem.getAuxiliaryData().getCatalogRef();
        if (castoSku != null) {
            Set<RepositoryItem> productSet = (Set<RepositoryItem>) castoSku.getPropertyValue(PARENT_PRODUCTS);
            if (productSet != null && !productSet.isEmpty()) {
                List<RepositoryItem> productList = new ArrayList<RepositoryItem>(productSet);
                String type = (String) productList.get(0).getPropertyValue(TYPE);
                if (GROUPED_PRODUCT.equals(type) || PACK_PRODUCT.equals(type)) {
                    super.priceItem(pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters);
                } else {
                    CastItemPriceInfo cipi = ((CastItemPriceInfo) pPriceQuote);
                    double amount = cipi.getAmount();
                    try {

                        RepositoryItem priceList = null;

                        // First look in the extra parameters for the price list
                        if (pExtraParameters != null) {
                            priceList = getDifferentPriceList(pPriceQuote, pItem, pPricingModel, pLocale, pProfile,
                                    pExtraParameters);
                        }

                        if (priceList == null) {
                            if (pProfile != null)
                                priceList = getPriceListManager().getPriceList(pProfile,
                                        getProfilePriceListPropertyName(), false);
                        }
                        if (priceList == null) {
                            priceList = getPriceListManager().getPriceList(CARD_PRICES);
                        }

                        String sku = pItem.getCatalogRefId();
                        String product = pItem.getAuxiliaryData().getProductId();
                        RepositoryItem price = null;

                        try {
                            price = getPriceListManager().getPrice(priceList, product, sku);
                            if (price != null) {
                                Double cp = (Double) price.getPropertyValue(LIST_PRICE_PROPERTY_NAME);
                                long quantity = pItem.getQuantity();
                                double fullCP = cp*quantity;
                                if (fullCP <= amount) {
                                    cipi.setCardPrice(cp);
                                    Date cur = new Date();
                                    Date from = (Date) castoSku.getPropertyValue(CASTORAMA_PRICE_START_DATE);
                                    Date to = (Date) castoSku.getPropertyValue(CASTORAMA_PRICE_END_DATE);
                                    if (from != null && to != null && cur.compareTo(from) > 0 && cur.compareTo(to) < 0) {
                                        cipi.setIsValidByDateCardPrice(true);
                                        if (pOrder!= null && pOrder instanceof CastOrderImpl) {
                                            CastOrderImpl cOrder = (CastOrderImpl) pOrder;
                                            Boolean mIsCardEnabled = cOrder.getPayeCarteAtout();
                                            if (mIsCardEnabled != null && mIsCardEnabled.booleanValue()) {
                                                List<PricingAdjustment> adjustments = cipi.getAdjustments();
                                                PricingAdjustment cardPricingAdjustment = new PricingAdjustment();
                                                cardPricingAdjustment.setTotalAdjustment((fullCP - amount));
                                                cardPricingAdjustment.setAdjustmentDescription(CARD_PRICE_ADJUSTMENT_DESCRIPTION);
                                                cardPricingAdjustment.setQuantityAdjusted(quantity);
                                                adjustments.add(cardPricingAdjustment);
                                                cipi.setAmount(fullCP);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (PriceListException pe) {
                            throw new PricingException(pe);
                        }

                        if (price == null) {
                            if (isNoPriceIsError()) {
                                throw new PricingException(MessageFormat.format(Constants.PRICE_NOT_FOUND,
                                        priceList.getRepositoryId(), product, sku));
                            } else {
                                return;
                            }
                        }

                    } catch (PriceListException pe) {
                        throw new PricingException(pe);
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
                logError("Please check product with id " + pItem.getAuxiliaryData().getProductId()
                        + " in the catalog. Property childSKUs is empty.");
        }

    }

    /**
     * Extends the based behavior of ItemPriceCalculator to check "cardPrice",
     * "castoramaPriceStartDate" and "castoramaPriceEndDate" properties of the
     * SKU. According to this parameters SKU will have card, sale and list price
     * or just sale and list price.
     */
    @Override
    public void priceItem(ItemPriceInfo pPriceQuote, CommerceItem pItem, RepositoryItem pPricingModel, Locale pLocale,
            RepositoryItem pProfile, Map pExtraParameters) throws PricingException {
        priceItem(pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters, null);
    }

    public MutableRepository getRepository() {
        return mRepository;
    }

    public void setRepository(MutableRepository pRepository) {
        this.mRepository = pRepository;
    }

}
