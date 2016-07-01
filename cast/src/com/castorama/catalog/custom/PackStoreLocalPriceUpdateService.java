package com.castorama.catalog.custom;

import static com.castorama.commerce.profile.Constants.LOCAL_PRICE_LIST;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.time.StopWatch;

import atg.commerce.catalog.custom.CatalogLoopException;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.SortDirective;
import atg.repository.SortDirectives;

import com.castorama.pricing.CastItemPriceInfo;

/**
 * Author: EPAM Team
 * Maintenance Optimisation done by Philippe Mouawad based on P30 Code
 * comments by PMD :
 * - FIXME : Would be better to create promos, discount in same transaction used per store (@ EPAM don't implement for now)
 */
public class PackStoreLocalPriceUpdateService extends PackPriceUpdateService {
    /**
     * Used to mark end of queue
     */
    private static final Object END_OF_QUEUE = new Object();
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

    /**
     * type property name.
     */
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
    private static final String UPDATE_PACK_LOCAL_PRICE = "Update_Pack_Local_Price";

    /**
     * name of store item descriptor.
     */
    private static final String STORE_ITEM_DESCRIPTOR = "magasin";

    /**
     * name of promoInfo item descriptor.
     */
    private static final String PROMO_ITEM_DESCRIPTOR = "promoInfo";

    /**
     * name of discount item descriptor.
     */
    private static final String DISCOUNT_ITEM_DESCRIPTOR = "discount";

    /**
     * name of store id item.
     */
    private static final String PROPERTY_STORE_ID = "Id";

    /**
     * web store id.
     */
    private static final String WEB_STORE_ID = "999";

    /**
     * name of localPrix item.
     */
    private static final String PROPERTY_LOCAL_PRIX = "localPrix";

    /**
     * name store id pExtraParameter.
     */
    private static final String STORE_ID_MAP_KEY = "storeId";

    /**
     * name of promo id property
     */
    private static final String PROMOTION_ID = "id";

    /**
     * name of promo store id property
     */
    private static final String PROMOTION_STORE_ID = "storeId";

    /**
     * name of promo SKU id property
     */
    private static final String PROMOTION_SKU_ID = "skuId";

    /**
     * name of promo flag property
     */
    private static final String PROMOTION_FLAG = "fPromo";

    /**
     * name of promo code property
     */
    private static final String PROMOTION_CODE = "cPromo";

    /**
     * name of discount id property
     */
    private static final String DISCOUNT_ID = "id";

    /**
     * name of discount store id property
     */
    private static final String DISCOUNT_STORE_ID = "storeId";

    /**
     * name of discount SKU id property
     */
    private static final String DISCOUNT_SKU_ID = "skuId";

    /**
     * name of discount dd begin date property
     */
    private static final String DISCOUNT_DD_BEGIN_DATE = "displayDiscountBeginDate";

    /**
     * name of discount dd end date property
     */
    private static final String DISCOUNT_DD_END_DATE = "displayDiscountEndDate";

    /**
     * name of discount dd property
     */
    private static final String DISCOUNT_DD = "displayDiscount";

    /**
     * pricing tools.
     */
    private PricingTools mPricingTools;

    /**
     * Magasin repository.
     */
    private Repository mMagasinRepository;

    /**
     * LocalStoresInfoRepository repository.
     */
    private Repository mLocalStoresInfoRepository;

    /**
     * StoreLocalPricesRepository repository.
     */
    private Repository mStoreLocalPricesRepository;
    
    /**
     * Map<StoreId, Lists for storing [storeId, skuId] pairs>
     */
    private Map<String, List<Object[]>> mapPromosToCreate;
    /**
     * Lists for storing [storeId, skuId] pairs.
     */
    private List<String[]> createDiscounts;
    /**
     * Lists for storing [storeId, skuId] pairs.
     */
    private List<String[]> removeDiscounts;
    
    /**
     * Max number of rows returned when querying database to get PACK or GROUPED PRODUCT
     * Increasing consumes more memory
     */
    private int maxItemsToLoad = 1000;
    
    /**
     * Size of thread pool used for all work processing per store
     */
    private int threadPoolSize = 10;
    
    /**
     * Size of the consumers thread pool for price computation
     */
    private int consumersThreadPoolSize = 10;
    
    /**
     * Size of queue used to communicate between consumers / producers 
     * Avoids overloading memory
     */
    private int itemProcessingQueueSize = 50;
    
    /**
     * Processing commit size
     */
    private int priceProcessingCommitSize = 500;


    public Repository getStoreLocalPricesRepository() {
        return mStoreLocalPricesRepository;
    }

    public void setStoreLocalPricesRepository(Repository mStoreLocalPricesRepository) {
        this.mStoreLocalPricesRepository = mStoreLocalPricesRepository;
    }

    public Repository getLocalStoresInfoRepository() {
        return mLocalStoresInfoRepository;
    }

    public void setLocalStoresInfoRepository(Repository mLocalStoresInfoRepository) {
        this.mLocalStoresInfoRepository = mLocalStoresInfoRepository;
    }

    public Repository getMagasinRepository() {
        return mMagasinRepository;
    }

    public void setMagasinRepository(Repository mMagasinRepository) {
        this.mMagasinRepository = mMagasinRepository;
    }

    public PricingTools getPricingTools() {
        return mPricingTools;
    }

    public void setPricingTools(PricingTools mPricingTools) {
        this.mPricingTools = mPricingTools;
    }

    /**
     * Returns name of the service.
     *
     * @return name of the service.
     */
    public String getServiceName() {
        return UPDATE_PACK_LOCAL_PRICE;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param pCatalogRepository ToDo: DOCUMENT ME!
     * @throws CatalogLoopException ToDo: DOCUMENT ME!
     */
    @Override
    protected void calculatePrices(Repository pCatalogRepository) throws CatalogLoopException {
        ExecutorService executorService=null;
        try {
            RepositoryItemDescriptor storeDescriptor = mMagasinRepository.getItemDescriptor(STORE_ITEM_DESCRIPTOR);
            RepositoryView storeView = storeDescriptor.getRepositoryView();
            QueryBuilder storeBuilder = storeView.getQueryBuilder();
            Query getStoreQuery = storeBuilder.createAndQuery(new Query[]{
                    storeBuilder.createComparisonQuery(
                            storeBuilder.createPropertyQueryExpression(PROPERTY_LOCAL_PRIX),
                            storeBuilder.createConstantQueryExpression(Boolean.TRUE),
                            QueryBuilder.EQUALS),
                    storeBuilder.createComparisonQuery(
                            storeBuilder.createPropertyQueryExpression(PROPERTY_STORE_ID),
                            storeBuilder.createConstantQueryExpression(WEB_STORE_ID),
                            QueryBuilder.NOT_EQUALS)
            });
            // Recupere les magasins ok localPrix == true et id != 999 (web)
            RepositoryItem[] storeItems = storeView.executeQuery(getStoreQuery);
            executorService = Executors.newFixedThreadPool(threadPoolSize);
            if (storeItems != null) {
                mapPromosToCreate = new ConcurrentHashMap<String, List<Object[]>>();
                createDiscounts = Collections.synchronizedList(new ArrayList<String[]>());
                removeDiscounts = Collections.synchronizedList(new ArrayList<String[]>());
                Calendar cal = new GregorianCalendar();
                cal.clear(Calendar.HOUR_OF_DAY);
                cal.clear(Calendar.HOUR);
                cal.clear(Calendar.MINUTE);
                cal.clear(Calendar.SECOND);
                cal.clear(Calendar.MILLISECOND);
                /**
                 * Variable for holding the todays date.
                 */
                final Date todaysDate = cal.getTime();
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                int numberOfGroupedProducts = countNumberOf(pCatalogRepository, CASTO_GROUPED_PRODUCT);
                int numberOfPacks = countNumberOf(pCatalogRepository, CASTO_PACK);
                CountDownLatch countDownLatch = new CountDownLatch(storeItems.length);
                for (RepositoryItem store : storeItems) {
                    //calculatePriceForOneStore(pCatalogRepository, store);
                    vlogInfo("Putting in executor for store {0} ", store.getPropertyValue(PROPERTY_STORE_ID));
                    executorService.execute(new PriceCalculatorPerStore(countDownLatch, pCatalogRepository, store, numberOfGroupedProducts, numberOfPacks));
                }
                logInfo("Waiting for end of store calculators threads");
                countDownLatch.await();
                stopWatch.stop();
                vlogInfo("Ended Store processing took: {0} ms", stopWatch.getTime());
                
                stopWatch.reset();
                stopWatch.start();
                countDownLatch = new CountDownLatch(mapPromosToCreate.size());
                for (Map.Entry<String, List<Object[]>> promoToCreateListEntry : mapPromosToCreate.entrySet()) {
                    vlogInfo("Putting in executor promo creation for store {0} ", promoToCreateListEntry.getKey());
                    executorService.execute(new PromosCreatorPerStore(countDownLatch, promoToCreateListEntry.getKey(), promoToCreateListEntry.getValue()));
                }
                logInfo("Waiting for end of promo creator threads");
                countDownLatch.await();
                vlogInfo("Promo creation took: {0} ms", stopWatch.getTime());
                executorService.shutdown();
                stopWatch.reset();
                stopWatch.start();
                // DON'T TOUCH THIS 
                BookkeepingForThread bookkeeping = getBookkeepingForThread(pCatalogRepository);
                try {
                    bookkeeping.beginTransaction(getTransactionManager());
                    if (isLoggingInfo()) {
                        vlogInfo("Will create {0} Discounts ", createDiscounts.size());
                    }
                    createDiscounts(todaysDate);
                    if (isLoggingInfo()) {
                        vlogInfo("Create Discount took: {0} ms", stopWatch.getTime());
                    }
                    stopWatch.reset();
                    stopWatch.start();
                    if (isLoggingInfo()) {
                        vlogInfo("Will remove {0} Discounts ", removeDiscounts.size());
                    }
                    removeDiscounts();
                    if (isLoggingInfo()) {
                        vlogInfo("Discount remove took: {0} ms", stopWatch.getTime());
                    }
                }
                finally {
                    try {
                        bookkeeping.endTransaction();
                    } catch (TransactionDemarcationException tde) {
                        logError("TransactionDemarcationException during prices calculation: ", tde);
                    }
                } // end try-catch-finally
            }
        } catch (RepositoryException re) {
            if (isLoggingError()) {
                logError("RepositoryException during prices calculation: ", re);
            }
        } catch (TransactionDemarcationException e) {
            if (isLoggingError()) {
                logError("TransactionDemarcationException during prices calculation: ", e);
            }
        } catch (InterruptedException e) {
            if (isLoggingError()) {
                logError("InterruptedException during prices calculation: ", e);
            }
        } finally {
            if(executorService != null) {
                try {
                    executorService.shutdownNow();
                } catch (Exception e) {
                    if (isLoggingError()) {
                        logError("Exception on executorService.shutdownNow during prices calculation: ", e);
                    }
                }
            }
        }
    }
    
    /**
     * Count number of rows of itemType
     * @param pCatalogRepository {@link Repository}
     * @param itemType String
     * @return number of rows of itemType 
     * @throws RepositoryException
     */
    private int countNumberOf(Repository pCatalogRepository, String itemType) 
            throws RepositoryException {
        RepositoryView catalogView = pCatalogRepository.getView(itemType);
        QueryBuilder catBuilder = catalogView.getQueryBuilder();
        Query getPackQuery = catBuilder.createUnconstrainedQuery();
        int numberOfRows = catalogView.executeCountQuery(getPackQuery);
        if(isLoggingInfo()) {
            logInfo("Found "+ numberOfRows+ " rows of type:"+itemType);
        }
        return numberOfRows;
    }

    /**
     * Philippe Mouawad comments:
     * Compute promos for one store, must use only thread safe objects
     * Processing is embedded in one transaction.
     * It would have been better to have all processing related to one store in one transaction, but it was 
     * like this before optimisation
     */
    private final class PromosCreatorPerStore implements Runnable {
        private CountDownLatch countDownLatch;
        private List<Object[]> createPromos;
        private String storeId;
        private PromosCreatorPerStore(CountDownLatch countDownLatch, String storeId, List<Object[]> createPromos) {
            this.countDownLatch= countDownLatch;
            this.storeId = storeId;
            this.createPromos = createPromos;
        }
        @Override
        public void run() {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            if(createPromos.size()==0) {
                vlogInfo("Time to process promo creation for store id: {0} : {1}, No promos for store id: {0} : {1}" ,
                        storeId,
                        Long.valueOf(stopWatch.getTime()));
            }
            // Don't use get getBookkeepingForThread(pRepository) as we would have synchronization issues
            BookkeepingForThread bookKeepingForThread = new BookkeepingForThread();
            try {
                bookKeepingForThread.beginTransaction(getTransactionManager());
                if (isLoggingInfo()) {
                    vlogInfo("Processing promo creation for store with id: {0}", storeId);
                }
                createPromos(createPromos);
                if (isLoggingInfo()) {
                    vlogInfo("Time to process promo creation for store with id: {0} : {1} ms" ,
                            storeId,
                            Long.valueOf(stopWatch.getTime()));
                }
            } // end try-catch-finally
            catch (TransactionDemarcationException e) {
                if (isLoggingError()) {
                    vlogError(e, 
                            "TransactionDemarcationException during promo creation for store with id = {0}: {1} ms", storeId, e.getMessage());
                }
            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    vlogError(e, 
                            "RepositoryException during promo creation for store with id = {0}: {1}", storeId, e.getMessage());
                }
            }
            finally {
                try {
                    bookKeepingForThread.endTransaction();
                } catch (TransactionDemarcationException e) {
                    logError("TransactionDemarcationException during promo creation: ", e);
                }
                countDownLatch.countDown();
            }
        }
    }

    /**
     * Philippe Mouawad comments:
     * Compute prices for one store, must use only thread safe objects
     * Processing is embedded in one transaction.
     * It would have been better to have all processing related to one store in one transaction, 
     * but it was like this before optimisation
     */
    private final class PriceCalculatorPerStore implements Runnable {
        private Repository pCatalogRepository;
        private RepositoryItem store;
        private CountDownLatch countDownLatch;
        private int numberOfGroupedProducts;
        private int numberOfPacks;

        private PriceCalculatorPerStore(CountDownLatch countDownLatch, Repository pCatalogRepository,
                RepositoryItem store, int numberOfGroupedProducts, int numberOfPacks) {
            this.countDownLatch= countDownLatch;
            this.pCatalogRepository= pCatalogRepository;
            this.store = store;
            this.numberOfGroupedProducts = numberOfGroupedProducts;
            this.numberOfPacks = numberOfPacks;
        }
        @Override
        public void run() {
            StopWatch stopWatch = new StopWatch();
            try {
                if (isLoggingInfo()) {
                    vlogInfo("Processing local packs for store with id: {0}", store.getPropertyValue(PROPERTY_STORE_ID));
                }
                stopWatch.reset();
                stopWatch.start();
                processItems(pCatalogRepository, CASTO_PACK, store, numberOfPacks);
                if (isLoggingInfo()) {
                    vlogInfo("Time to process local packs for store id: {0} : {1}" ,
                            store.getPropertyValue(PROPERTY_STORE_ID),
                            Long.valueOf(stopWatch.getTime()));
                }
                stopWatch.reset();
                stopWatch.start();
                if (isLoggingInfo()) {
                    vlogInfo("Processing local grouped-products for store with id: {0}" ,
                            store.getPropertyValue(PROPERTY_STORE_ID));
                }
                processItems(pCatalogRepository, CASTO_GROUPED_PRODUCT, store, numberOfGroupedProducts);
                if (isLoggingInfo()) {
                    vlogInfo("Time to process grouped-products for store id: {0} : {1}" ,
                            store.getPropertyValue(PROPERTY_STORE_ID),
                            Long.valueOf(stopWatch.getTime()));
                }
            } catch (PriceListException e) {
                if (isLoggingError()) {
                    vlogError("PriceListException during prices calculation: store id = {0}: {1}", store.getPropertyValue(PROPERTY_STORE_ID), e);
                }
            } catch (TransactionDemarcationException e) {
                if (isLoggingError()) {
                    vlogError(e, 
                            "TransactionDemarcationException during prices calculation: store id = {0}: {1}", store.getPropertyValue(PROPERTY_STORE_ID), e.getMessage());
                }
            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    vlogError(e, 
                            "RepositoryException during prices calculation: store id = {0}: {1}", store.getPropertyValue(PROPERTY_STORE_ID), e.getMessage());
                }
            } catch (InterruptedException e) {
                if (isLoggingError()) {
                    vlogError(e, 
                            "InterruptedException during prices calculation: store id = {0}: {1}", store.getPropertyValue(PROPERTY_STORE_ID), e.getMessage());
                }
            }
            finally {
                countDownLatch.countDown();
            }
        }
        
    }

    /**
     * Calculate price for packs or grouped products in local stores. 
     * Process 1 item in one transaction.
     *
     * @param pCatalogRepository
     * @param itemType
     * @param store
     * @throws RepositoryException
     * @throws TransactionDemarcationException
     *
     * @throws PriceListException
     * @throws InterruptedException 
     */
    private void processItems(Repository pCatalogRepository, 
            String itemType, 
            RepositoryItem store,
            int numberOfRowsOfItemType)
            throws RepositoryException, TransactionDemarcationException, 
            PriceListException, InterruptedException {
        
        ArrayBlockingQueue<Object> blockingQueueOfArrayOfRepositoryItems = 
                new ArrayBlockingQueue<Object>(itemProcessingQueueSize);
        int consumersThreadPoolSize = getConsumersThreadPoolSize();
        CountDownLatch countDownLatch = new CountDownLatch(
                1 //Producer Thread
                + consumersThreadPoolSize);
        ExecutorService itemConsumerThreadPool = null;
        try {
            if(isLoggingDebug()) {
                logDebug("Creating ThreadPool for store:"+store
                        +" and itemType:"
                        + itemType
                        +", with size:"+consumersThreadPoolSize);
            }
            AtomicInteger counter = new AtomicInteger(0);
            itemConsumerThreadPool = Executors.newFixedThreadPool(consumersThreadPoolSize);
            if(isLoggingDebug()) {
                logDebug("Creating Producer Thread for store:"+store+" and itemType:"
                        + itemType
                        +", with maxItemsToLoad:"
                        +getMaxItemsToLoad());
            }

            Thread producerThread = new Thread(
                    new ItemProducer(pCatalogRepository, 
                            itemType,
                            blockingQueueOfArrayOfRepositoryItems,  
                            getMaxItemsToLoad(), 
                            countDownLatch), 
                    "CASTO_PROCESS_ITEMS_PRODUCER-"+store.getRepositoryId()+"-"+itemType);
            if(isLoggingDebug()) {
                logDebug("Creating Consumer Threads for store:"+store+" and itemType:"
                        + itemType);
            }
            for (int i = 0; i < consumersThreadPoolSize; i++) {
                itemConsumerThreadPool.execute(
                        new ItemProcessor(
                                blockingQueueOfArrayOfRepositoryItems, 
                                store, 
                                itemType,
                                countDownLatch,
                                counter));
            }
            
            if(isLoggingDebug()) {
                logDebug("Starting Producer Thread for store:"+store+" and itemType:"
                        + itemType);
            }
            producerThread.start();
            countDownLatch.await();
            if(numberOfRowsOfItemType == counter.get()) {
                logInfo("Producing/Consuming ended for store:"+store+" and itemType:"
                        + itemType
                        + " ended successfully processing:"+numberOfRowsOfItemType + " rows");
            } else {
                logWarning("Producing/Consuming ended for store:"+store+" and itemType:"
                        + itemType
                        + " ended, but expected to process :"+numberOfRowsOfItemType
                        +" and processed:"+counter.get());
            }
        } finally {
            if(itemConsumerThreadPool != null) {
                try {
                    List<Runnable> waiting = itemConsumerThreadPool.shutdownNow();
                    if(waiting.size()>0) {
                        logWarning("Producing/Consuming ended for store:"+store+" and itemType:"
                                + itemType
                                + " ended, "
                                + waiting.size() 
                                + " runnable were waiting");
                    }
                } catch (Exception e) {
                    if (isLoggingError()) {
                        logError("Exception on itemConsumerThreadPool.shutdownNow during prices calculation: ", e);
                    }
                }
            }
        }
    }

    /**
     * Processes the blockingQueueOfArrayOfRepositoryItems
     *
     */
    private final class ItemProcessor implements Runnable {
        private ApplicationLoggingImpl mLogging = new ApplicationLoggingImpl(
                this.getClass().getName());
        private ArrayBlockingQueue<Object> blockingQueueOfArrayOfRepositoryItems;
        private RepositoryItem store;
        private CountDownLatch latch;
        private AtomicInteger counter;
        private String itemType;

        public ItemProcessor(
                ArrayBlockingQueue<Object> blockingQueueOfArrayOfRepositoryItems,
                RepositoryItem store, 
                String itemType,
                CountDownLatch countDownLatch, AtomicInteger counter) {
            this.blockingQueueOfArrayOfRepositoryItems = blockingQueueOfArrayOfRepositoryItems;
            this.store = store;
            this.itemType = itemType;
            this.latch = countDownLatch;
            this.counter = counter;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            Object currentItem = null;
            try {
                BookkeepingForThread bookKeepingForThread = new BookkeepingForThread();
                try {
                    bookKeepingForThread
                            .beginTransaction(getTransactionManager());
                    while ((currentItem = blockingQueueOfArrayOfRepositoryItems
                            .take()) != END_OF_QUEUE) {
                        RepositoryItem item = (RepositoryItem) currentItem;
                        try {
                                processPrice(new RepositoryItem[] { item }, store);
                                counter.incrementAndGet();
                                if(counter.get()%getPriceProcessingCommitSize()==0) {
                                    if(mLogging.isLoggingDebug()) {
                                        mLogging.logDebug(threadName+":Restarting transaction for store:"
                                                +store+", itemType:"+itemType);
                                    }
                                    bookKeepingForThread.restartTransaction(getTransactionManager());
                                }
                        } catch (Exception e) {
                            mLogging.logError(threadName+":Error processing price for item:"
                                    + item.getRepositoryId()+", store:"
                                    +store+", itemType:"+itemType);
                        } 
                    } 
                }finally {
                    try {
                        bookKeepingForThread.endTransaction();
                    } catch (TransactionDemarcationException e) {
                        mLogging.logError(threadName+":Error ending transaction", e);
                    }
                }
            } catch (Exception e) {
                mLogging.logError(threadName+":Error processing price for item:"
                        + currentItem+", store:"
                        +store+", itemType:"+itemType);
            } finally {
                try {
                    // Put back so other runnable also stop
                    blockingQueueOfArrayOfRepositoryItems.put(END_OF_QUEUE);
                } catch (InterruptedException e) {
                    // risque de blocage;
                }
                if(mLogging.isLoggingDebug()) {
                    mLogging.logDebug(threadName+":Releasing latch, store:"
                            +store+", itemType:"+itemType);
                }
                latch.countDown();
            }
        }
    }

    /**
     * Gets maxItemsToLoadInMemory itemType from database and
     * puts them in blockingQueueOfArrayOfRepositoryItems
     */
    private static final class ItemProducer implements Runnable {
        private ApplicationLoggingImpl mLogging = new ApplicationLoggingImpl(this.getClass().getName());
        private Repository catalogRepository;
        private String itemType;
        private ArrayBlockingQueue<Object> blockingQueueOfArrayOfRepositoryItems;
        private int maxItemsToLoadInMemory;
        private CountDownLatch latch;

        public ItemProducer(
                Repository pCatalogRepository,
                String itemType, 
                ArrayBlockingQueue<Object> blockingQueueOfArrayOfRepositoryItems,
                int maxItemsToLoadInMemory,
                CountDownLatch latch) {
            this.catalogRepository = pCatalogRepository;
            this.itemType = itemType;
            this.blockingQueueOfArrayOfRepositoryItems = blockingQueueOfArrayOfRepositoryItems;
            this.maxItemsToLoadInMemory = maxItemsToLoadInMemory;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                RepositoryView catalogView = catalogRepository.getView(itemType);
                QueryBuilder catBuilder = catalogView.getQueryBuilder();
                Query getPackQuery = catBuilder.createUnconstrainedQuery();
                int startIndex = 0;
                int endIndex = maxItemsToLoadInMemory;
                SortDirectives sortDirectives = new SortDirectives();
                sortDirectives.addDirective(new SortDirective("id", SortDirective.DIR_ASCENDING));
                RepositoryItem[] items = null;

                do {
                    // Return 6272 for casto-pack and 830 casto-grouped-product currently in PROD
                    items = catalogView.executeQuery(getPackQuery, startIndex, endIndex, sortDirectives);
                    if(mLogging.isLoggingDebug()) {
                        mLogging.logDebug("Got "+items.length+" "+ itemType 
                                +" between:"+startIndex+" and:"+endIndex);
                    }
                    if (items != null) {
                        for (RepositoryItem item : items) {
                            blockingQueueOfArrayOfRepositoryItems.put(item);
                        }
                    }
                    startIndex += maxItemsToLoadInMemory;
                    endIndex += maxItemsToLoadInMemory;
                } while ((items != null) && (items.length == maxItemsToLoadInMemory));
                if(mLogging.isLoggingDebug()) {
                    mLogging.logDebug("Finished producing data of type:"+itemType);
                }
            } catch(InterruptedException ex) {
                mLogging.logError("InterruptedException occured during production, ending processing");
            }  catch( RepositoryException ex) {
                mLogging.logError("Exception occured during production");
            } finally {
                try {
                    blockingQueueOfArrayOfRepositoryItems.put(END_OF_QUEUE);
                } catch (InterruptedException e) {
                    mLogging.logError("Error putting END_OF_QUEUE in queue");
                }
                if(mLogging.isLoggingDebug()) {
                    mLogging.logDebug(Thread.currentThread().getName()+":Releasing latch");
                }
                latch.countDown();
            }
        }
        
    }

    /**
     * Calculate price for pack/grouped-product in local stores as sum of prices for all skus in bundleLinks list.
     *
     * @param items array of packs/grouped products
     * @throws PriceListException  if price is not available or error during price calculation appeared
     * @throws RepositoryException if repository error occurs
     * @throws atg.commerce.pricing.PricingException
     */
    private void processPrice(RepositoryItem[] items, RepositoryItem store) throws PriceListException, RepositoryException {
        RepositoryItem localPriceList = (RepositoryItem) store.getPropertyValue(LOCAL_PRICE_LIST);
        String storeIdAsString = (String)store.getPropertyValue(PROPERTY_STORE_ID);
        List<Object[]> createPromos = mapPromosToCreate.get(storeIdAsString);
        if(createPromos == null) {
            createPromos = Collections.synchronizedList(new ArrayList<Object[]>());
            mapPromosToCreate.put(storeIdAsString, createPromos);
        }
        if (localPriceList != null) {
            for (RepositoryItem item : items) {
                List<RepositoryItem> skus = (List<RepositoryItem>) item.getPropertyValue(CHILD_SKUS);
                if ((skus != null) && (skus.size() > 0)) {
                    RepositoryItem itemSku = skus.get(0);
                    String skuRepositoryId = itemSku.getRepositoryId();
                    List<RepositoryItem> bundledLinks = (List<RepositoryItem>) itemSku.getPropertyValue(BUNDLE_LINKS);
                    ItemPriceInfo priceInfo = null;
                    double price = 0.0;
                    boolean onSale = false;
                    boolean displayDiscount = false;
                    try {
                        for (RepositoryItem o : bundledLinks) {
                            RepositoryItem sku = (RepositoryItem) o.getPropertyValue(ITEM);
                            long quantity = (Long) o.getPropertyValue(QUANTITY);
                            Map<String, Object> storeId = new HashMap<String, Object>();
                            storeId.put(STORE_ID_MAP_KEY, store.getPropertyValue(PROPERTY_STORE_ID));
                            priceInfo = mPricingTools.calculatePrice(item, sku, quantity, false, null, null, storeId);
                            if (priceInfo.isOnSale()) {
                                price += priceInfo.getSalePrice() * quantity;
                            } else {
                                price += priceInfo.getListPrice() * quantity;
                            }
                            onSale = onSale || priceInfo.isOnSale();
                            displayDiscount = displayDiscount || ((CastItemPriceInfo) priceInfo).getOnSaleDiscountDisplay();
                        }
                    } catch (PricingException e) {
                        priceInfo = null;
                        logWarning("No price found for one of the bundledLinks, main sku = " + itemSku.getRepositoryId());
                    }
                    RepositoryItem itemPrice = getPriceListManager().getPrice(localPriceList, null, skuRepositoryId, false);
                    if (priceInfo != null) {
                        if (itemPrice == null) {
                            itemPrice = getPriceListManager().createListPrice(localPriceList, null, skuRepositoryId, price);
                        } else {
                            setPropertyValue((MutableRepositoryItem) itemPrice, LIST_PRICE, price);
                        }

                        String type = (String) item.getPropertyValue(TYPE);
                        if (CASTO_GROUPED_PRODUCT.equals(type)) {
                            createPromos.add(new Object[]{store.getRepositoryId(), skuRepositoryId, onSale});
                            if (displayDiscount) {
                                createDiscounts.add(new String[]{store.getRepositoryId(), skuRepositoryId});
                            } else {
                                removeDiscounts.add(new String[]{store.getRepositoryId(), skuRepositoryId});
                            }
                        } else if (CASTO_PACK.equals(type)) {
                            createPromos.add(new Object[]{store.getRepositoryId(), skuRepositoryId, Boolean.FALSE});
                        }
                    } else {
                        if (itemPrice != null) {
                            setPropertyValue((MutableRepositoryItem) itemPrice, LIST_PRICE, null);
                        }
                    }
                }
            }
        }
    }

    private void createPromos(List<Object[]> createPromos) throws RepositoryException {
        if (createPromos.size() == 0) {
            return;
        }
        RepositoryItemDescriptor promoDescriptor = mLocalStoresInfoRepository.getItemDescriptor(PROMO_ITEM_DESCRIPTOR);
        RepositoryView promoView = promoDescriptor.getRepositoryView();
        QueryBuilder promoBuilder = promoView.getQueryBuilder();
        for (Object[] promo : createPromos) {
            try {
                Query getPromoQuery = promoBuilder.createAndQuery(new Query[]{
                        promoBuilder.createComparisonQuery(
                                promoBuilder.createPropertyQueryExpression(PROMOTION_STORE_ID),
                                promoBuilder.createConstantQueryExpression(promo[0]),
                                QueryBuilder.EQUALS),
                        promoBuilder.createComparisonQuery(
                                promoBuilder.createPropertyQueryExpression(PROMOTION_SKU_ID),
                                promoBuilder.createConstantQueryExpression(promo[1]),
                                QueryBuilder.EQUALS)
                });
                RepositoryItem[] promoItems = promoView.executeQuery(getPromoQuery);
                if (promoItems == null) {
                    MutableRepository mutableRepository = (MutableRepository) mLocalStoresInfoRepository;
                    MutableRepositoryItem mutableRepositoryItem = mutableRepository.createItem(PROMO_ITEM_DESCRIPTOR);
                    mutableRepositoryItem.setPropertyValue(PROMOTION_ID, (String) promo[0] + promo[1]);
                    mutableRepositoryItem.setPropertyValue(PROMOTION_STORE_ID, promo[0]);
                    mutableRepositoryItem.setPropertyValue(PROMOTION_SKU_ID, promo[1]);
                    mutableRepositoryItem.setPropertyValue(PROMOTION_FLAG, promo[2]);
                    mutableRepositoryItem.setPropertyValue(PROMOTION_CODE, null);
                    mutableRepository.addItem(mutableRepositoryItem);
                } else {
                    MutableRepository mutableRepository = (MutableRepository) mLocalStoresInfoRepository;
                    MutableRepositoryItem mutableRepositoryItem = 
                            mutableRepository.getItemForUpdate(promoItems[0].getRepositoryId(), PROMO_ITEM_DESCRIPTOR);
                    mutableRepositoryItem.setPropertyValue(PROMOTION_FLAG, promo[2]);
                    mutableRepository.updateItem(mutableRepositoryItem);
                }
            } catch (RepositoryException e) {
                String message = "RepositoryException during promo creation or updating: store id = {0}, sku id = {1} : {2}";
                if (isLoggingError()) {
                    vlogError(message, promo[0], promo[1], e.getMessage());
                } else if (isLoggingDebug()) {
                    vlogError(message, promo[0], promo[1], e);
                }
            }
        }
    }

    private void createDiscounts(Date todaysDate) throws RepositoryException {
        if (createDiscounts.size() == 0) return;
        RepositoryItemDescriptor discountDescriptor = mLocalStoresInfoRepository.getItemDescriptor(DISCOUNT_ITEM_DESCRIPTOR);
        RepositoryView discountView = discountDescriptor.getRepositoryView();
        QueryBuilder discountBuilder = discountView.getQueryBuilder();
        for (String[] discount : createDiscounts) {
            try {
                Query getDiscountQuery = discountBuilder.createAndQuery(new Query[]{
                        discountBuilder.createComparisonQuery(
                                discountBuilder.createPropertyQueryExpression(DISCOUNT_STORE_ID),
                                discountBuilder.createConstantQueryExpression(discount[0]),
                                QueryBuilder.EQUALS),
                        discountBuilder.createComparisonQuery(
                                discountBuilder.createPropertyQueryExpression(DISCOUNT_SKU_ID),
                                discountBuilder.createConstantQueryExpression(discount[1]),
                                QueryBuilder.EQUALS)
                });
                RepositoryItem[] discountItems = discountView.executeQuery(getDiscountQuery);
                if (discountItems == null) {
                    MutableRepository mutableRepository = (MutableRepository) mLocalStoresInfoRepository;
                    MutableRepositoryItem mutableRepositoryItem = mutableRepository.createItem(DISCOUNT_ITEM_DESCRIPTOR);
                    mutableRepositoryItem.setPropertyValue(DISCOUNT_ID, discount[0] + discount[1]);
                    mutableRepositoryItem.setPropertyValue(DISCOUNT_STORE_ID, discount[0]);
                    mutableRepositoryItem.setPropertyValue(DISCOUNT_SKU_ID, discount[1]);
                    mutableRepositoryItem.setPropertyValue(DISCOUNT_DD_BEGIN_DATE, todaysDate);
                    mutableRepositoryItem.setPropertyValue(DISCOUNT_DD_END_DATE, todaysDate);
                    mutableRepositoryItem.setPropertyValue(DISCOUNT_DD, Boolean.TRUE);
                    mutableRepository.addItem(mutableRepositoryItem);
                } else {
                    MutableRepository mutableRepository = (MutableRepository) mLocalStoresInfoRepository;
                    MutableRepositoryItem mutableRepositoryItem = 
                            mutableRepository.getItemForUpdate(discountItems[0].getRepositoryId(), DISCOUNT_ITEM_DESCRIPTOR);
                    mutableRepositoryItem.setPropertyValue(DISCOUNT_DD_BEGIN_DATE, todaysDate);
                    mutableRepositoryItem.setPropertyValue(DISCOUNT_DD_END_DATE, todaysDate);
                    mutableRepository.updateItem(mutableRepositoryItem);
                }
            } catch (RepositoryException e) {
                String message = "RepositoryException during discount creation or updating: store id = {0}, sku id = {1} : {2}";
                if (isLoggingError()) {
                    vlogError(message, discount[0], discount[1], e.getMessage());
                } else if (isLoggingDebug()) {
                    vlogError(message, discount[0], discount[1], e);
                }
            }
        }
    }

    private void removeDiscounts() throws RepositoryException {
        if (removeDiscounts.size() == 0) return;
        RepositoryItemDescriptor discountDescriptor = mLocalStoresInfoRepository.getItemDescriptor(DISCOUNT_ITEM_DESCRIPTOR);
        RepositoryView discountView = discountDescriptor.getRepositoryView();
        QueryBuilder discountBuilder = discountView.getQueryBuilder();
        for (String[] discount : removeDiscounts) {
            try {
                Query getDiscountQuery = discountBuilder.createAndQuery(new Query[]{
                        discountBuilder.createComparisonQuery(
                                discountBuilder.createPropertyQueryExpression(DISCOUNT_STORE_ID),
                                discountBuilder.createConstantQueryExpression(discount[0]),
                                QueryBuilder.EQUALS),
                        discountBuilder.createComparisonQuery(
                                discountBuilder.createPropertyQueryExpression(DISCOUNT_SKU_ID),
                                discountBuilder.createConstantQueryExpression(discount[1]),
                                QueryBuilder.EQUALS)
                });
                RepositoryItem[] discountItems = discountView.executeQuery(getDiscountQuery);
                if (discountItems != null) {
                    for (RepositoryItem discountItem : discountItems) {
                        MutableRepository mutableRepository = (MutableRepository) mLocalStoresInfoRepository;
                        mutableRepository.removeItem(discountItem.getRepositoryId(), DISCOUNT_ITEM_DESCRIPTOR);
                    }
                }
            } catch (RepositoryException e) {
                String message = "RepositoryException during discount removing: store id = {0}, sku id = {1} : {2}";
                if (isLoggingError()) {
                    vlogError(message, discount[0], discount[1], e.getMessage());
                } else if (isLoggingDebug()) {
                    vlogError(message, discount[0], discount[1], e);
                }
            }
        }
    }

    /**
     * @return the threadPoolSize
     */
    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    /**
     * @param threadPoolSize the threadPoolSize to set
     */
    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    /**
     * @return the consumersThreadPoolSize
     */
    public int getConsumersThreadPoolSize() {
        return consumersThreadPoolSize;
    }

    /**
     * @param consumersThreadPoolSize the consumersThreadPoolSize to set
     */
    public void setConsumersThreadPoolSize(int consumersThreadPoolSize) {
        this.consumersThreadPoolSize = consumersThreadPoolSize;
    }

    /**
     * @return the itemProcessingQueueSize
     */
    public int getItemProcessingQueueSize() {
        return itemProcessingQueueSize;
    }

    /**
     * @param itemProcessingQueueSize the itemProcessingQueueSize to set
     */
    public void setItemProcessingQueueSize(int itemProcessingQueueSize) {
        this.itemProcessingQueueSize = itemProcessingQueueSize;
    }

    /**
     * @return the maxItemsToLoad
     */
    public int getMaxItemsToLoad() {
        return maxItemsToLoad;
    }

    /**
     * @param maxItemsToLoad the maxItemsToLoad to set
     */
    public void setMaxItemsToLoad(int maxItemsToLoad) {
        this.maxItemsToLoad = maxItemsToLoad;
    }

    /**
     * @return the priceProcessingCommitSize
     */
    public int getPriceProcessingCommitSize() {
        return priceProcessingCommitSize;
    }

    /**
     * @param priceProcessingCommitSize the priceProcessingCommitSize to set
     */
    public void setPriceProcessingCommitSize(int priceProcessingCommitSize) {
        this.priceProcessingCommitSize = priceProcessingCommitSize;
    }
}
