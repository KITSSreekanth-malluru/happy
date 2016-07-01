package com.castorama.commerce.pricing;

import static com.castorama.commerce.profile.Constants.CURRENT_LOCAL_STORE_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.LOCAL_PRICE_LIST;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import atg.adapter.gsa.GSAItem;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.priceLists.Constants;
import atg.commerce.pricing.priceLists.ItemPriceCalculator;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.repository.MutableRepository;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.SortDirective;
import atg.repository.SortDirectives;

import com.castorama.pricing.CastItemPriceInfo;

/**
 * @author MK
 */
public class ItemLocalPriceListSaleCalculator extends ItemPriceCalculator {

    /** DAY_TIME constant. */
    private static final int DAY_TIME = 1000*60*60*24;
    /** DISPLAY_DISCOUNT_END_DATE constant. */
    private static final String DISPLAY_DISCOUNT_END_DATE = "displayDiscountEndDate";
    /**
     * 
     */
    private static final String DISPLAY_DISCOUNT_BEGIN_DATE = "displayDiscountBeginDate";
    /**
     * 
     */
    private static final String DISPLAY_DISCOUNT = "displayDiscount";
    /**
     *
     */
    private static final String SALE_PRICE_LOCAL_DESCRIPTION = "Sale price local";
    /**
     *
     */
    private static final String LIST_PRICE_LOCAL_DESCRIPTION = "List price local";
    /**
     *
     */
    private static final String LIST_PRICE_WEB_DESCRIPTION = "List price web";
    /**
     *
     */
    private static final String F_PROMO_PROPERTY_NAME = "fPromo";
    /**
     *
     */
    private static final String ID = "id";
    /**
     *
     */
    private static final String STORE_ID = "storeId";
    /**
     *
     */
    private static final String SKU_ID = "skuId";

    /**
     * name of store item descriptor.
     */
    private static final String STORE_ITEM_DESCRIPTOR = "magasin";

    private static final String PROMO_INFO_DESCRIPTOR_NAME = "promoInfo";
    private static final String DISCOUNT_DESCRIPTOR_NAME = "discount";
    private static final String LIST_PRICE_PROPERTY_NAME = "listPrice";
    static final String TYPE = "type";
    static final String GROUPED_PRODUCT = "casto-grouped-product";
    static final String PACK_PRODUCT = "casto-pack";
    static final String ON_SALE = "onSale";
    static final String PARENT_PRODUCTS = "parentProducts";

    /**
     * name store id pExtraParameter.
     */
    private static final String STORE_ID_MAP_KEY = "storeId";

    /**
     * name exclude local prices pExtraParameter.
     */
    private static final String EXCLUDE_LOCAL_PRICES = "excludeLocalPrices";

    private MutableRepository mRepository;
    private MutableRepository mInfosRepository;
    private MutableRepository mMagasinRepository;

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
                        new Object[]{Integer.valueOf(pPriceQuotes.size()), Integer.valueOf(pItems.size())}));
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
            CastItemPriceInfo cipi = ((CastItemPriceInfo) pPriceQuote);
            try {
                RepositoryItem localPriceList = null;
                RepositoryItem store = null;
                if (pProfile != null) {
                    store = (RepositoryItem) pProfile.getPropertyValue(CURRENT_LOCAL_STORE_PROFILE_PROP);
                    //print error message when store is empty
                    //stop calculating local price
                    if (!checkStore(store)) return;
                    localPriceList = getPriceListManager().getPriceList(pProfile,
                            LOCAL_PRICE_LIST, false);
                } else if (pExtraParameters != null) {
                    // Exclude local prices calculator for some maintenance services to avoid logging of misplaced error messages
                    Boolean excludeLocalPrices = (Boolean)pExtraParameters.get(EXCLUDE_LOCAL_PRICES);
                    if (excludeLocalPrices!=null && excludeLocalPrices) {
                        return;
                    }
                    
                    // case when basic maintenance working
                    RepositoryView storeView = null;
                    String storeId = (String) pExtraParameters.get(STORE_ID_MAP_KEY);
                    if (storeId != null) {
                        try {
                            RepositoryItemDescriptor itemDescriptor = mMagasinRepository.getItemDescriptor(STORE_ITEM_DESCRIPTOR);
                            storeView = mMagasinRepository.getView(itemDescriptor);
                            QueryBuilder storeBuilder = storeView.getQueryBuilder();
                            Query query = storeBuilder.createAndQuery(new Query[]{
                                    storeBuilder.createComparisonQuery(
                                            storeBuilder.createPropertyQueryExpression(ID),
                                            storeBuilder.createConstantQueryExpression(storeId),
                                            QueryBuilder.EQUALS)
                            });
                            int startIndex = 0;
                            int endIndex = 1;
                            RepositoryItem[] items;
                            SortDirectives sortDirectives = new SortDirectives();
                            sortDirectives.addDirective(new SortDirective(ID, SortDirective.DIR_ASCENDING));
                            items = storeView.executeQuery(query, startIndex, endIndex, sortDirectives);
                            if (!checkStore(items)){
                                return;
                            }
                            store = items[0];
                            if (!checkStore(store)){
                                return;
                            }
                            localPriceList = (RepositoryItem) store.getPropertyValue(LOCAL_PRICE_LIST);
                        } catch (RepositoryException e) {
                            throw new PricingException(e);
                        }
                    } else {
                        checkStore(null);
                        return;
                    }
                }
                //at this place we have not empty store
                if (localPriceList == null){
                    logError("Can't read price for store \"" + (store != null ? store.getRepositoryId() : "unknown")+ "\" cause price list empty.");
                    return;
                }
                
                //local price list is not empty
                String sku = pItem.getCatalogRefId();
                String magasinId = store.getRepositoryId();
                RepositoryItem promoInfo = null;
                RepositoryItem discount = null;
                Boolean localOnSale = false;
                Boolean localDisplayDiscount = false;
                boolean useWebListPriceInsteadOfLocalPrice = false;
                boolean salePriceGreaterThanListPrice = false;
                try {
                    RepositoryItemDescriptor itemDescriptor = mInfosRepository.getItemDescriptor(PROMO_INFO_DESCRIPTOR_NAME);
                    promoInfo = getFirstItemFomInfos(sku, magasinId, itemDescriptor);
                    if (promoInfo == null) {
                        if (isLoggingDebug()){
                            StringBuffer sb = new StringBuffer();
                            sb.append("Can't find promo info item for sku:");
                            sb.append(sku);
                            sb.append(" and store:");
                            sb.append(magasinId);
                            logDebug(sb.toString());
                        }
                        useWebListPriceInsteadOfLocalPrice = true;
                    } else {
                        localOnSale = (Boolean) promoInfo.getPropertyValue(F_PROMO_PROPERTY_NAME);
                        if (localOnSale == null) {
                            localOnSale = false;
                        }
                        
                        if (localOnSale) {
                            itemDescriptor = mInfosRepository.getItemDescriptor(DISCOUNT_DESCRIPTOR_NAME);
                            discount = getFirstItemFomInfos(sku, magasinId, itemDescriptor);
                            
                            if (discount != null) {
                                localDisplayDiscount = (Boolean) discount.getPropertyValue(DISPLAY_DISCOUNT);
                                if (localDisplayDiscount == null) {
                                    localDisplayDiscount = false;
                                }
                                
                                Date current = new Date();
                                Date ddBeginDate = (Date) discount.getPropertyValue(DISPLAY_DISCOUNT_BEGIN_DATE);
                                Date ddEndDate = (Date) discount.getPropertyValue(DISPLAY_DISCOUNT_END_DATE);
                                if (ddBeginDate != null && ddEndDate != null){
                                    //check case when current after end date or end date earlier than begin
                                    ddEndDate = new Date(ddEndDate.getTime() + DAY_TIME);
                                    if (!ddBeginDate.before(ddEndDate) || ddEndDate.before(current) || ddBeginDate.after(current)){
                                        localDisplayDiscount = false;
                                    }
                                } else {
                                    localDisplayDiscount= false;
                                    if (isLoggingError())
                                        logError("Empty display discount dates in item: " + discount);
                                }
                            }
                        }
                    }
                } catch (RepositoryException e) {
                    throw new PricingException(e);
                }
                
                try {
                    RepositoryItem localPrice = getPriceListManager().getPrice(localPriceList, null, sku, false);
                    if (localPrice == null) useWebListPriceInsteadOfLocalPrice = true;
                    Double lcp = 0.0;
                    String adjustmentDescription = null;
                    
                    if (useWebListPriceInsteadOfLocalPrice) {
                        lcp = cipi.getListPrice();
                        localOnSale = false;
                        localDisplayDiscount = false;
                        adjustmentDescription = LIST_PRICE_WEB_DESCRIPTION;
                    } else {
                        lcp = (Double) localPrice.getPropertyValue(LIST_PRICE_PROPERTY_NAME);
                        lcp = getPricingTools().round(lcp);
                        salePriceGreaterThanListPrice = lcp > cipi.getListPrice();
                        localDisplayDiscount = localDisplayDiscount && !salePriceGreaterThanListPrice;
                        adjustmentDescription = localOnSale ? SALE_PRICE_LOCAL_DESCRIPTION : LIST_PRICE_LOCAL_DESCRIPTION;
                    }
                    
                    //update amount according to new price
                    double oldAmount = cipi.getAmount();
                    cipi.setAmount(lcp*pItem.getQuantity());
                    
                    //add new adjustment for new price
                    PricingAdjustment localAdjustment = new PricingAdjustment(adjustmentDescription, pPricingModel, 
                            getPricingTools().round(cipi.getAmount() - oldAmount) , pItem.getQuantity());
                    cipi.getAdjustments().add(localAdjustment);
                    
                    //update detailed price info
                    List detailsList = pPriceQuote.getCurrentPriceDetails();
                    if (detailsList == null) {
                      throw new PricingException(MessageFormat.format(Constants.ITEM_NOT_LIST_PRICED,
                                  new Object[] {pItem.getId()}));
                    }
                    getPricingTools()
                    .getDetailedItemPriceTools()
                    .assignSalePriceToDetails(detailsList, lcp,
                        pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters, adjustmentDescription);
                    
                    if (localOnSale) {
                        cipi.setSalePrice(lcp);
                    } else { 
                        cipi.setListPrice(lcp);
                    }
                    
                    cipi.setRawTotalPrice(lcp);
                    cipi.setOnSale(localOnSale);
                    cipi.setOnSaleDiscountDisplay(localDisplayDiscount);
                    
                    // if local price applied
                    if (!useWebListPriceInsteadOfLocalPrice) {
                        cipi.setPriceList(localPriceList);
                        cipi.setLocalPriceApplied(true);
                    } else {
                        cipi.setLocalPriceApplied(false);
                    }
                } catch (PriceListException pe) {
                    throw new PricingException(pe);
                }
            } catch (PriceListException pe) {
                throw new PricingException(pe);
            }
        } else {
            if (isLoggingError())
                logError("Please check product with id " + pItem.getAuxiliaryData().getProductId()
                        + " in the catalog. Property childSKUs is empty or auxiliary data not generated on maintanance.");
        }
    }

    /**
     * Throw error message when store is empty.
     * 
     * @param store
     * @return
     */
    private boolean checkStore(Object store) {
        if (store == null) {
            logError("Can't create local price cause store is null");
            return false;
        } else if (store instanceof List && ((List)store).size()==0){
            logError("Can't create local price cause stores is empty");
            return false;
        } else if (store instanceof GSAItem){
            String id = ((GSAItem)store).getRepositoryId();
            if (id == null || id.equals("999")) return false;
            Boolean localPrix = (Boolean) ((GSAItem)store).getPropertyValue("localPrix");
            if (!localPrix) return false;
        }
        return true;
    }

    /**
     * @param sku
     * @param magasinId
     * @param itemDescriptor
     * @return
     * @throws RepositoryException
     */
    private RepositoryItem getFirstItemFomInfos(String sku, String magasinId, RepositoryItemDescriptor itemDescriptor)
            throws RepositoryException {
        RepositoryItem promoInfo;
        RepositoryView promoView;
        promoView = mInfosRepository.getView(itemDescriptor);
        QueryBuilder promoBuilder = promoView.getQueryBuilder();
        Query query = promoBuilder.createAndQuery(new Query[]{
                promoBuilder.createComparisonQuery(
                        promoBuilder.createPropertyQueryExpression(SKU_ID),
                        promoBuilder.createConstantQueryExpression(sku),
                        QueryBuilder.EQUALS),
                promoBuilder.createComparisonQuery(
                        promoBuilder.createPropertyQueryExpression(STORE_ID),
                        promoBuilder.createConstantQueryExpression(magasinId),
                        QueryBuilder.EQUALS)
        });
        int startIndex = 0;
        int endIndex = 1;
        RepositoryItem[] items;
        SortDirectives sortDirectives = new SortDirectives();
        sortDirectives.addDirective(new SortDirective(ID, SortDirective.DIR_ASCENDING));
        items = promoView.executeQuery(query, startIndex, endIndex, sortDirectives);
        if (items == null || items.length == 0) return null;
        promoInfo = items[0];
        return promoInfo;
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

    public MutableRepository getInfosRepository() {
        return mInfosRepository;
    }

    public void setInfosRepository(MutableRepository pRepository) {
        this.mInfosRepository = pRepository;
    }

    public MutableRepository getMagasinRepository() {
        return mMagasinRepository;
    }

    public void setMagasinRepository(MutableRepository mMagasinRepository) {
        this.mMagasinRepository = mMagasinRepository;
    }
}
