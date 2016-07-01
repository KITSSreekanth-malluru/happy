package com.castorama.cache.lowmemory;

import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;

import atg.service.cache.Cache;

/**
 * Given recovery action invalidates cache of configured via <code>caches</code>
 * atg.service.cache.Cache services.
 *
 * @author  EPAM team
 * @version ToDo: INSERT VERSION NUMBER
 */
public class ServiceCacheLowMemoryRecoveryAction extends GenericService implements LowMemoryRecoveryAction {
    /** cacheDroplets property */
    private Cache[] mCaches;

    /**
     * Invalidates caches
     */
    public void runAction() {
        if (mCaches != null) {
            for (int i = 0; i < mCaches.length; i++) {
                if (isLoggingDebug()) {
                    logDebug("Flushing cache for " + mCaches[i].getAbsoluteName());
                }
                mCaches[i].flush();
            }
        }
    }

    /**
     * Returns caches property.
     *
     * @return caches property.
     */
    public Cache[] getCaches() {
        return mCaches;
    }

    /**
     * Sets the value of the caches property.
     *
     * @param pCaches parameter to set.
     */
    public void setCaches(Cache[] pCaches) {
        mCaches = pCaches;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @throws ServiceException ToDo: DOCUMENT ME!
     */
    public void doStartService() throws ServiceException {
        super.doStartService();
        if (mCaches != null) {
            for (int i = 0; i < mCaches.length; i++) {
                if (isLoggingDebug()) {
                    logDebug(mCaches[i].getAbsoluteName() + " has been added to the low memory recovery service.");
                }
            }
        }
    }

}
