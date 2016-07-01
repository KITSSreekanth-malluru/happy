package com.castorama.catalog.custom;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.time.StopWatch;

import atg.commerce.catalog.custom.CatalogLoopException;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.castorama.commerce.inventory.CastRepositoryInventoryManager;
import com.castorama.commerce.inventory.CastRepositoryInventoryManager.StockLevelStatus;

/**
 * Utility class that is executed during maintenance service. It calculates
 * count of products in category and all sub-categories. Count is stored in
 * property productsCount for category.
 *
 * @author Igor_Kulik
 * Optimized (Multi-threaded) by Ubik-Ingenierie , addition of threadPoolSize and related processing
 * Optimized (Commit size) by Ubik-Ingenierie , Commit of categories update is done by blocks of getMaxItemsPerTransaction()
 */
public class CategoryProductAmountUpdateService extends BaseUpdateService {
    /**
     * products count property name.
     */
    private static final String PRODUCTS_COUNT = "productsCount";

    /**
     * child products property name.
     */
    private static final String CHILD_PRODUCTS = "childProducts";

    /**
     * child categories property name.
     */
    private static final String CHILD_CATEGORIES = "childCategories";

    /**
     * top navigation categories property name.
     */
    private static final String TOP_NAVIGATION_CATEGORIES = "topNavigationCategories";

    /**
     * service name.
     */
    private static final String UPDATE_CHILDREN_QUANTITY = "Update_Children_Quantity";

    /**
     * Inventory manager
     */
    private CastRepositoryInventoryManager mInventoryManager;

    /**
     * CastoramaCatalogId property
     */
    private String mCastoramaCatalogId;

    /**
     * UseInventoryStock property
     */
    private boolean mUseInventoryStock = true;

    /**
     * Default to 10 threads
     */
    private int threadPoolSize = 10;

    /**
     * Get service name.
     *
     * @return service name.
     */
    public String getServiceName() {
        return UPDATE_CHILDREN_QUANTITY;
    }

    /**
     * @return the mInventoryManager
     */
    public CastRepositoryInventoryManager getInventoryManager() {
        return mInventoryManager;
    }

    /**
     * @param pInventoryManager the mInventoryManager to set
     */
    public void setInventoryManager(CastRepositoryInventoryManager pInventoryManager) {
        mInventoryManager = pInventoryManager;
    }

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
            if(threadPoolSize > 1) {
                logInfo("ThreadpoolSize="+threadPoolSize+", processing will be multi-threaded");
            }
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            processCategories(mCatalogRepository);
           
            logInfo("Computation for categories took:"+stopWatch.getTime() +" ms with threadPoolSize:"+threadPoolSize);
        } finally {
            clearBookkeepingForThread(mCatalogRepository);
        }
    }

    @Override
    protected void calculatePrices(Repository pCatalogRepository) throws CatalogLoopException {
        //To change body of implemented methods
    }

    /**
     * Process top categories in catalog.
     *
     * @param pCatalogRepository catalog repository
     * @throws CatalogLoopException if there are loops in catalog
     */
    private void processCategories(Repository pCatalogRepository) throws CatalogLoopException {
        if (isLoggingInfo()) {
            logInfo(getServiceName() + ": Starting Process Categories. Use inventory stock level flag = " +
                    isUseInventoryStock());
            logInfo(getServiceName() + ": Catalog Id = " + getCastoramaCatalogId());

            if (!isUseInventoryStock()) {
                logInfo("Do not check inventory status.");
            }

        }

        if (getCastoramaCatalogId() == null) {
            if (isLoggingInfo()) {
                logInfo(getServiceName() + ": Catalog Id is not specified. Exiting.");
            }
            return;
        }

        BookkeepingForThread bookkeeping = getBookkeepingForThread(pCatalogRepository);
        RepositoryItem catalog = null;
        ExecutorService executorService=null;

        try {
            catalog = pCatalogRepository.getItem(getCastoramaCatalogId(), getCatalogProperties().getCatalogItemName());
            if (catalog == null) {
                if (isLoggingInfo()) {
                    logInfo(getServiceName() + ": Can not find catalog for id = " + getCastoramaCatalogId());
                }
                return;
            }

        } catch (RepositoryException re) {
            logError("Repository Exception", re);
        } // end try-catch-finally

        if (catalog != null) {
            try {
                if(threadPoolSize > 1) {
                    executorService = Executors.newFixedThreadPool(threadPoolSize);
                }
                @SuppressWarnings("rawtypes")
                List rootCategories = (List) catalog.getPropertyValue(TOP_NAVIGATION_CATEGORIES);
                try {
                    boolean forceRestartTransaction = false;
                    bookkeeping.beginTransaction(getTransactionManager());
                    int categoriesProcessed = 0;
                    for (Object category : rootCategories) {
                        try {
                            if(forceRestartTransaction) {
                                bookkeeping.restartTransaction(getTransactionManager());
                                forceRestartTransaction = false;
                            }
                            processCategory(executorService, (RepositoryItem) category);
                            categoriesProcessed++;
                            if(categoriesProcessed % getMaxItemsPerTransaction() == 0) {
                                if(isLoggingDebug()) {
                                    logDebug("Commiting after "+getMaxItemsPerTransaction()+" updates");
                                }
                                bookkeeping.restartTransaction(getTransactionManager());                                
                            }
                        } catch (Exception exc) {
                            try {
                                bookkeeping.getTransactionDemarcation().getTransaction().setRollbackOnly();
                                forceRestartTransaction = true;
                            } catch (Exception e) {
                                logError("Error on setRollbackOnly after exception, orginal exception:"+exc.getMessage(), e);
                            }
                        } 
                    }
                    
                    if(isLoggingDebug()) {
                        logDebug("Processed "+categoriesProcessed+" categories");
                    }
                } catch (Exception exc) {
                    try {
                        bookkeeping.getTransactionDemarcation().getTransaction().setRollbackOnly();
                    } catch (Exception e) {
                        logError("Error on setRollbackOnly after exception, orginal exception:"+exc.getMessage(), e);
                    }
                } finally{
                    try {
                        bookkeeping.endTransaction();
                    } catch (TransactionDemarcationException e) {
                        logError("Error ending transaction:", e);
                    }
                }
            } finally {
                if(executorService != null) { 
                    try {
                        executorService.shutdownNow();
                    } catch (Exception e) {
                        if (isLoggingError()) {
                            logError("Exception on executorService.shutdownNow during product amount calculation: ", e);
                        }
                    }
                }
            }
        }
    }

    /**
     * Calculate count of products in category and all sub-categories 
     * and updates property PRODUCTS_COUNT of category
     *
     * @param executorService ExecutorService
     * @param pCategory category to process
     * @return count of the products
     * @throws RepositoryException if error occurs
     * @throws TransactionDemarcationException
     * @throws {@link InterruptedException}
     *
     */
    private int processCategory(ExecutorService executorService, RepositoryItem pCategory) 
            throws RepositoryException, TransactionDemarcationException, InterruptedException {
        StopWatch catSW = new StopWatch();
        catSW.start();
        int count = 0;
        if(isLoggingDebug()) {
            logDebug("Processing category:"+pCategory.getRepositoryId());
        }
        @SuppressWarnings("unchecked")
        List<RepositoryItem> childCategories = (List<RepositoryItem>) pCategory.getPropertyValue(CHILD_CATEGORIES);
        if (childCategories != null) {
            for (RepositoryItem item : childCategories) {
                count += processCategory(executorService, item);
            }
        }

        //calculate product count
        @SuppressWarnings("rawtypes")
        List childProducts = (List) pCategory.getPropertyValue(CHILD_PRODUCTS);
        if (childProducts != null) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            if (isUseInventoryStock()) {
                CountDownLatch countDownLatch = new CountDownLatch(childProducts.size());
                AtomicInteger parallelCounter = new AtomicInteger(0);
                for (final Object product : childProducts) {
                    //TODO store parameter
                    //TODO svAvailableMap parameter
                    if(executorService != null) {
                        if(isLoggingDebug()) {
                            logDebug("Stacking computation of:"+(product != null ? ((RepositoryItem) product).getRepositoryId() : "null"));
                        }
                        executorService.execute(new StockLevelCalculator(countDownLatch, getInventoryManager(), (RepositoryItem) product, parallelCounter));
                    } else {
                        StockLevelStatus stockLevel = getInventoryManager().inventoryStockLevelStatus((RepositoryItem) product, null, null);
                        if (stockLevel != StockLevelStatus.S0) {
                            count++;
                        }
                    }
                }
                if(executorService != null) {
                    if(isLoggingDebug()) {
                        logDebug("Waiting for end of stock availability for:"+childProducts);
                    }
                    countDownLatch.await();
                    if(isLoggingDebug()) {
                        logDebug("End of stock availability computation for:"+childProducts+", result:"+parallelCounter.get());
                    }
                    count += parallelCounter.get();
                }
            } else {
                count += childProducts.size();
            }
            stopWatch.stop();
            if(isLoggingDebug()) {
                logDebug("Stock availability computation for:"+childProducts+"(useInventoryStock="+isUseInventoryStock()+", numberOfThreads=" + getThreadPoolSize() + ") took:"+stopWatch.getTime()+" ms");
            }
        }
        setPropertyValue((MutableRepositoryItem) pCategory, PRODUCTS_COUNT, Integer.valueOf(count));

        catSW.stop();
        if(isLoggingDebug()) {
            logDebug("Computation for category:"+pCategory.getRepositoryId()+" took:"+catSW.getTime() +" ms");
        }
        return count;
    }
    
    /**
     * Computed inventory level 
     */
    private static final class StockLevelCalculator implements Runnable {
        
        private CountDownLatch latch;
        private CastRepositoryInventoryManager inventoryManager;
        private RepositoryItem product;
        private AtomicInteger parallelCounter;
        
        public StockLevelCalculator(CountDownLatch countDownLatch,
                CastRepositoryInventoryManager inventoryManager,
                RepositoryItem product, AtomicInteger parallelCounter) {
            this.latch = countDownLatch;
            this.inventoryManager = inventoryManager;
            this.product = product;
            this.parallelCounter = parallelCounter;
        }

        @Override
        public void run() {
            try {
                StockLevelStatus stockLevel = inventoryManager.inventoryStockLevelStatus(product, null, null);
                if (stockLevel != StockLevelStatus.S0) {
                    parallelCounter.incrementAndGet();
                }
            } finally {
                latch.countDown();
            }
        }
    }

    /**
     * Returns useInventoryStock property.
     *
     * @return useInventoryStock property.
     */
    public boolean isUseInventoryStock() {
        return mUseInventoryStock;
    }

    /**
     * Sets the value of the useInventoryStock property.
     *
     * @param pUseInventoryStock parameter to set.
     */
    public void setUseInventoryStock(boolean pUseInventoryStock) {
        mUseInventoryStock = pUseInventoryStock;
    }

    /**
     * Returns castoramaCatalogId property.
     *
     * @return castoramaCatalogId property.
     */
    public String getCastoramaCatalogId() {
        return mCastoramaCatalogId;
    }

    /**
     * Sets the value of the castoramaCatalogId property.
     *
     * @param pCastoramaCatalogId parameter to set.
     */
    public void setCastoramaCatalogId(String pCastoramaCatalogId) {
        mCastoramaCatalogId = pCastoramaCatalogId;
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
}
