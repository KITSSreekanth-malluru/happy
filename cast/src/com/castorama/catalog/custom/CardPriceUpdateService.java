package com.castorama.catalog.custom;

import atg.commerce.catalog.custom.CatalogLoopException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.dtm.TransactionDemarcationException;
import atg.repository.*;

/**
 * Utility class that is executed during maintenance service. It calculates prices for packs and grouped products.
 *
 * @author Igor_Kulik
 */
public class CardPriceUpdateService extends BaseUpdateService {

    /**
     * LIST_PRICE constant.
     */
    private static final String LIST_PRICE = "listPrice";

    /**
     * SHOW_CARD_PRICE constant.
     */
    private static final String SHOW_CARD_PRICE = "showCardPrice";

    /**
     * SKU_ID constant.
     */
    private static final String SKU_ID = "skuId";

    /**
     * ID constant.
     */
    private static final String ID = "id";

    /**
     * CARD_PRICES constant.
     */
    private static final String CARD_PRICES = "cardPrices";

    /**
     * PRICE_LIST constant.
     */
    private static final String PRICE_LIST = "priceList";

    /**
     * PRICE constant.
     */
    private static final String PRICE = "price";

    /**
     * casto sku item name.
     */
    private static final String CASTO_SKU = "casto_sku";

    /**
     * name of the service.
     */
    private static final String UPDATE_CARD_PRICE = "Update_Card_Price";

    /**
     * price list manger.
     */
    private PriceListManager mPriceListManager;

    /**
     * Returns name of the service.
     *
     * @return name of the service.
     */
    public String getServiceName() {
        return UPDATE_CARD_PRICE;
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
                logInfo("Process casto_skus");
            }
            bookkeeping.beginTransaction(getTransactionManager());

            processItems(pCatalogRepository, bookkeeping, PRICE);

            bookkeeping.restartTransaction(getTransactionManager());

        } catch (TransactionDemarcationException exc) {
            if (isLoggingError()) {
                logError("Transaction Demarcation Exception", exc);
            }
        } catch (RepositoryException re) {
            if (isLoggingError()) {
                logError("Repository Exception", re);
            }
        } finally {
            try {
                bookkeeping.endTransaction();
            } catch (TransactionDemarcationException tde) {
                logError("Transaction Demarcation Exception", tde);
            }
            if (isLoggingInfo()) {
                logInfo("End process casto_skus");
            }
        } // end try-catch-finally
    }

    /**
     * Find items with castorama card prices and process them. Process mMaxItemsPerTransaction items in one transaction.
     *
     * @param pCatalogRepository
     * @param pBookkeeping
     * @param itemType
     * @throws RepositoryException
     * @throws TransactionDemarcationException
     */
    private void processItems(Repository pCatalogRepository, BookkeepingForThread pBookkeeping, String itemType)
            throws RepositoryException, TransactionDemarcationException{
        RepositoryView catalogView = getPriceListManager().getPriceListRepository().getView(itemType);
        QueryBuilder catBuilder = catalogView.getQueryBuilder();
        Query query = catBuilder.createAndQuery(new Query[]{
                catBuilder.createComparisonQuery(
                        catBuilder.createPropertyQueryExpression(PRICE_LIST),
                        catBuilder.createConstantQueryExpression(CARD_PRICES),
                        QueryBuilder.EQUALS)});
        int startIndex = 0;
        int count = getMaxItemsPerTransaction();
        int endIndex = count;
        RepositoryItem[] items;
        SortDirectives sortDirectives = new SortDirectives();
        sortDirectives.addDirective(new SortDirective(ID, SortDirective.DIR_ASCENDING));

        do {
            items = catalogView.executeQuery(query, startIndex, endIndex, sortDirectives);
            if (items != null) {
                processPrice(pCatalogRepository, items);
                pBookkeeping.restartTransaction(getTransactionManager());
            }
            startIndex += count;
            endIndex += count;
        } while ((items != null) && (items.length == count));
    }

    /**
     * Calculate showCardPrice property for skus with castorama card price.
     *
     * @param pCatalogRepository
     * @param items array of prices from cardPrices pricelist
     * @throws RepositoryException if repository error occurs
     *
     */
    private void processPrice(Repository pCatalogRepository, RepositoryItem[] items) throws RepositoryException{

        MutableRepository mr = (MutableRepository) pCatalogRepository;

        for (RepositoryItem item : items) {
            String skuId = (String) item.getPropertyValue(SKU_ID);
            MutableRepositoryItem sku = (MutableRepositoryItem) mr.getItem(skuId, CASTO_SKU);
            if (sku == null) {
                continue;
            }

            Double cardPrice = (Double) item.getPropertyValue(LIST_PRICE);
            if (cardPrice == null) {
                cardPrice = 0.0;
            }
            
            if (isLoggingDebug()) {
                StringBuffer sb = new StringBuffer();
                sb.append("Process cardPrice: ").append(item).append(", sku : ").append(sku);
                sb.append(", price : ").append(cardPrice);
                logDebug(sb.toString());
            }
            
            Boolean oldShowValue = (Boolean) sku.getPropertyValue(SHOW_CARD_PRICE);
            // Indexing will create indexes according to this value.
            boolean newShowValue = cardPrice > 0;
            
            if ((oldShowValue == null) || (oldShowValue != newShowValue)) {
                sku.setPropertyValue(SHOW_CARD_PRICE, newShowValue);
                mr.updateItem(sku);
                if (isLoggingDebug()) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("sku : ").append(sku).append(" was updated. ShowCardPrice = " + newShowValue);
                    logDebug(sb.toString());
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

}
