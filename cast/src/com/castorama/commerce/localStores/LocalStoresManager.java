package com.castorama.commerce.localStores;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;


/**
 *
 * @author
 */
public class LocalStoresManager extends GenericService {

    private static final String SKU_CC_FLAG = "skuClickCollectFlag";
    private static final String SKU_CC_FLAG_TRUE_RQL = "skuId = ?0 AND storeId = ?1 RANGE +1";

    private static final String ITEM = "item";

    private static final String NOT_ALLOWED_SKUS_ITEM_DESCRIPTOR = "notAllowedSkus";
    private static final String NOT_ALLOWED_SKUS_ID = "1";
    private static final String NOT_ALLOWED_SKUS_STRING = "skus";
    private static final String NOT_ALLOWED_SKUS_DELIMETER = ",";

    private static final long TIME_TO_REFRESH = 10 * 60 * 1000;

    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Lock readLock = lock.readLock();
    private static final Lock writeLock = lock.writeLock();

    private Repository localStoresRepository;

    private Set<String> notAllowedSkus = Collections.emptySet();
    private long lastRefreshTime;

    public Repository getLocalStoresRepository() {
        return localStoresRepository;
    }

    public void setLocalStoresRepository(Repository localStoresRepository) {
        this.localStoresRepository = localStoresRepository;
    }

    /*
     * Made according to http://docs.oracle.com/javase/6/docs/api/java/util/concurrent/locks/ReentrantReadWriteLock.html
     */
    public Set<String> getNotAllowedSkus() {
        readLock.lock();
        if ((System.currentTimeMillis() - lastRefreshTime) > TIME_TO_REFRESH) {
            readLock.unlock();
            writeLock.lock();
            try {
                if ((System.currentTimeMillis() - lastRefreshTime) > TIME_TO_REFRESH) {
                    notAllowedSkus = new HashSet<String>();
                    RepositoryItem notAllowedSkusItem = localStoresRepository.getItem(NOT_ALLOWED_SKUS_ID, NOT_ALLOWED_SKUS_ITEM_DESCRIPTOR);
                    if (notAllowedSkusItem != null) {
                        String notAllowedSkusString = (String) notAllowedSkusItem.getPropertyValue(NOT_ALLOWED_SKUS_STRING);
                        if (notAllowedSkusString != null && notAllowedSkusString.length() > 0) {
                            notAllowedSkus.addAll(Arrays.asList(notAllowedSkusString.split(NOT_ALLOWED_SKUS_DELIMETER)));
                        }
                        lastRefreshTime = System.currentTimeMillis();
                    }
                }
            } catch (RepositoryException re) {
                if (isLoggingError()) {
                    logError(re);
                }
            } finally {
                readLock.lock();
                writeLock.unlock();
            }
        }
        readLock.unlock();

        return notAllowedSkus;
    }

    public boolean isEligibleForCcInStore(RepositoryItem pSku, RepositoryItem pStore) {
        boolean result = false;
        if (pSku != null && pStore != null && !getNotAllowedSkus().contains(pSku.getRepositoryId())) {
            try {
                RepositoryView skuCcFlagView = localStoresRepository.getView(SKU_CC_FLAG);
                RqlStatement skuCCFlagTrueStatement = RqlStatement.parseRqlStatement(SKU_CC_FLAG_TRUE_RQL,true);
                RepositoryItem[] items =
                        skuCCFlagTrueStatement.executeQuery(skuCcFlagView, new Object[] {pSku.getRepositoryId(), pStore.getRepositoryId()});
                if (items == null || items.length == 0) {
                    result = true;
                }
            } catch (RepositoryException re) {
                if (isLoggingError()) {
                    logError(re);
                }
            }
        }
        return result;
    }

    public boolean isEligibleForCcInStore(List<RepositoryItem> pBundleLinks, RepositoryItem pStore) {
        boolean result = false;
        if ((pBundleLinks != null) && !pBundleLinks.isEmpty() && (pStore != null)) {
            try {
                RepositoryView skuCcFlagView = localStoresRepository.getView(SKU_CC_FLAG);
                RqlStatement skuCCFlagTrueStatement = RqlStatement.parseRqlStatement(SKU_CC_FLAG_TRUE_RQL, true);

                for (RepositoryItem skuLink : pBundleLinks) {
                    RepositoryItem sku = (RepositoryItem) skuLink.getPropertyValue(ITEM);
                    if (sku != null) {
                        if (getNotAllowedSkus().contains(sku.getRepositoryId())) {
                            return false;
                        } else {
                            RepositoryItem[] items =
                                    skuCCFlagTrueStatement.executeQuery(skuCcFlagView, new Object[] {sku.getRepositoryId(), pStore.getRepositoryId()});
                            if (items != null && items.length > 0) {
                                return false;
                            }
                        }
                    }
                }

                result = true;
            } catch (RepositoryException re) {
                if (isLoggingError()) {
                    logError(re);
                }
            }
        }
        return result;
    }
}
