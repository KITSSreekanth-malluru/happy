package com.castorama.catalog.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.commerce.catalog.custom.CatalogLoopException;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.commerce.promotion.PromotionTools;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.SortDirective;
import atg.repository.SortDirectives;

/**
 * Utility class that is executed during maintenance service. It calculates prices for packs and grouped products.
 *
 * @author Igor_Kulik
 */
public class PackPriceUpdateService extends BaseUpdateService {
    /**
     * list price property name.
     */
    private static final String LIST_PRICE = "listPrice";

    /**
     * quantity property name.
     */
    private static final String QUANTITY = "quantity";

    /**
     * item property name.
     */
    private static final String ITEM = "item";

    private static final String TYPE = "type";

    /**
     * bundled links property name.
     */
    private static final String BUNDLE_LINKS = "bundleLinks";

    /**
     * child skus property name.
     */
    private static final String CHILD_SKUS = "childSKUs";

    /**
     * sale price list property name.
     */
    private static final String SALE_PRICE_LIST = "salePriceList";

    /**
     * price list property name.
     */
    private static final String PRICE_LIST = "priceList";

    /**
     * casto grouped product item name.
     */
    private static final String CASTO_GROUPED_PRODUCT = "casto-grouped-product";

    /**
     * casto sku item name.
     */
    private static final String CASTO_PACK = "casto-pack";

    /**
     * name of the service.
     */
    private static final String UPDATE_PACK_PRICE = "Update_Pack_Price";

    /**
     * name exclude local prices pExtraParameter.
     */
    private static final String EXCLUDE_LOCAL_PRICES = "excludeLocalPrices";

    /**
     * price list manger.
     */
    private PriceListManager mPriceListManager;

    private PromotionTools mPromotionTools;

    /**
     * Returns name of the service.
     *
     * @return name of the service.
     */
    public String getServiceName() {
        return UPDATE_PACK_PRICE;
    }

    /**
     * @param pCatalogRepository parameter
     * @throws CatalogLoopException parameter
     */
    public void updateCatalog(Repository pCatalogRepository) throws CatalogLoopException {
        Repository mCatalogRepository = null;

        if (pCatalogRepository == null) {
            mCatalogRepository = getCatalogTools().getCatalog();
        } else {
            mCatalogRepository = pCatalogRepository;
        }

        if (mCatalogRepository == null) {
            return;
        }

        try {
            calculatePrices(mCatalogRepository);
        } finally {
            clearBookkeepingForThread(mCatalogRepository);
        }

        return;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param pCatalogRepository ToDo: DOCUMENT ME!
     * @throws CatalogLoopException ToDo: DOCUMENT ME!
     */
    protected void calculatePrices(Repository pCatalogRepository) throws CatalogLoopException {
        BookkeepingForThread bookkeeping = getBookkeepingForThread(pCatalogRepository);

        try {
            if (isLoggingInfo()) {
                logInfo("Process packs");
            }
            bookkeeping.beginTransaction(getTransactionManager());

            processItems(pCatalogRepository, bookkeeping, CASTO_PACK);

            bookkeeping.restartTransaction(getTransactionManager());

            if (isLoggingInfo()) {
                logInfo("Process grouped-products");
            }
            processItems(pCatalogRepository, bookkeeping, CASTO_GROUPED_PRODUCT);
        } catch (TransactionDemarcationException exc) {
            if (isLoggingError()) {
                logError("Transaction Demarcation Exception", exc);
            }
        } catch (RepositoryException re) {
            if (isLoggingError()) {
                logError("Repository Exception", re);
            }
        } catch (PriceListException e) {
            if (isLoggingError()) {
                logError("PriceListException", e);
            }
        } finally {
            try {
                bookkeeping.endTransaction();
            } catch (TransactionDemarcationException tde) {
                logError("Transaction Demarcation Exception", tde);
            }
        } // end try-catch-finally
    }

    /**
     * Calculate price for packs or grouped products. Process mMaxItemsPerTransaction items in one transaction.
     *
     * @param pCatalogRepository
     * @param pBookkeeping
     * @param itemType
     * @throws RepositoryException
     * @throws TransactionDemarcationException
     *
     * @throws PriceListException
     */
    private void processItems(Repository pCatalogRepository, BookkeepingForThread pBookkeeping, String itemType)
            throws RepositoryException, TransactionDemarcationException, PriceListException {
        RepositoryView catalogView = pCatalogRepository.getView(itemType);
        QueryBuilder catBuilder = catalogView.getQueryBuilder();
        Query getPackQuery = catBuilder.createUnconstrainedQuery();
        int startIndex = 0;
        int count = getMaxItemsPerTransaction();
        int endIndex = count;
        RepositoryItem[] items;
        SortDirectives sortDirectives = new SortDirectives();
        sortDirectives.addDirective(new SortDirective("id", SortDirective.DIR_ASCENDING));

        do {
            items = catalogView.executeQuery(getPackQuery, startIndex, endIndex, sortDirectives);
            if (items != null) {
                processPrice(items);
                pBookkeeping.restartTransaction(getTransactionManager());
            }
            startIndex += count;
            endIndex += count;
        } while ((items != null) && (items.length == count));
    }

    /**
     * Calculate price for pack/grouped-product as sum of prices for all skus in bundleLinks list.
     *
     * @param items array of packs/grouped products
     * @throws PriceListException  if price is not available or error during price calculation appeared
     * @throws RepositoryException if repository error occurs
     * @throws TransactionDemarcationException
     *
     * @throws PricingException
     */
    private void processPrice(RepositoryItem[] items) throws PriceListException, RepositoryException, TransactionDemarcationException {

        RepositoryItem priceList = mPriceListManager.getPriceList(null, PRICE_LIST);
        RepositoryItem salePriceList = mPriceListManager.getPriceList(null, SALE_PRICE_LIST);

        for (RepositoryItem item : items) {
            List<RepositoryItem> skus = (List<RepositoryItem>) item.getPropertyValue(CHILD_SKUS);
            Double listPrice = 0.0;
            Double salePrice = 0.0;
            if ((skus != null) && (skus.size() > 0)) {
                RepositoryItem itemSku = skus.get(0);
                String skuRepositoryId = itemSku.getRepositoryId();
                List<RepositoryItem> bundledLinks = (List<RepositoryItem>) itemSku.getPropertyValue(BUNDLE_LINKS);
                ItemPriceInfo priceInfo = null;
                try {
                    for (RepositoryItem o : bundledLinks) {
                        RepositoryItem sku = (RepositoryItem) o.getPropertyValue(ITEM);
                        Long quantity = (Long) o.getPropertyValue(QUANTITY);
                        Map<String, Object> extraParams = new HashMap<String, Object>();
                        extraParams.put(EXCLUDE_LOCAL_PRICES, true);
                        priceInfo = mPromotionTools.getPricingTools().calculatePrice(item, sku, quantity, false, null,
                                null, extraParams);
                        Double skuSalePrice = priceInfo.getAmount();
                        Double skuListPrice = (priceInfo.getListPrice()) * quantity;
                        listPrice += skuListPrice;
                        salePrice += skuSalePrice;
                    }
                } catch (PricingException e) {
                    priceInfo = null;
                    logWarning("No price found for one of the bundledLinks, main sku = " + itemSku.getRepositoryId());
                }

                RepositoryItem itemListPrice = mPriceListManager.getPrice(priceList, null, skuRepositoryId);
                RepositoryItem itemSalePrice = mPriceListManager.getPrice(salePriceList, null, skuRepositoryId);
                if (priceInfo != null) {
                    String type = (String) item.getPropertyValue(TYPE);
                    if (itemListPrice == null) {
                        if (CASTO_GROUPED_PRODUCT.equals(type)) {
                            mPriceListManager.createListPrice(priceList, null, skuRepositoryId, listPrice);
                        } else {
                            mPriceListManager.createListPrice(priceList, null, skuRepositoryId, salePrice);
                        }
                    } else {
                        if (CASTO_GROUPED_PRODUCT.equals(type)) {
                            setPropertyValue((MutableRepositoryItem) itemListPrice, LIST_PRICE, listPrice);
                        } else {
                            setPropertyValue((MutableRepositoryItem) itemListPrice, LIST_PRICE, salePrice);
                        }
                    }
                    if (itemSalePrice == null) {
                        mPriceListManager.createListPrice(salePriceList, null, skuRepositoryId, salePrice);
                    } else {
                        setPropertyValue((MutableRepositoryItem) itemSalePrice, LIST_PRICE, salePrice);
                    }
                } else {
                    if (itemListPrice != null) {
                        setPropertyValue((MutableRepositoryItem) itemListPrice, LIST_PRICE, null);
                    }
                    if (itemSalePrice != null) {
                        setPropertyValue((MutableRepositoryItem) itemSalePrice, LIST_PRICE, null);
                    }
                }
            }
        }
    }

    /**
     * Set price list manager property.
     *
     * @param pPriceListManager price list manager
     */
    public void setPriceListManager(PriceListManager pPriceListManager) {
        mPriceListManager = pPriceListManager;
    }

    /**
     * Get price list manager property.
     *
     * @return price list manager
     */
    public PriceListManager getPriceListManager() {
        return mPriceListManager;
    }

    /**
     * Get promotion tools property.
     *
     * @return promotion tools
     */
    public PromotionTools getPromotionTools() {
        return mPromotionTools;
    }

    /**
     * Set price list manager property.
     *
     * @param pPromotionTools promotion tools
     */
    public void setPromotionTools(PromotionTools pPromotionTools) {
        mPromotionTools = pPromotionTools;
    }
}
