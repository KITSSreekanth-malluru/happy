package com.castorama.cache.lowmemory;

import atg.droplet.Cache;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;

/**
 * Given recovery action flushes caches of configured via <code>cacheDroplets</code> droplets.
 *
 * @author  EPAM team
 * @version ToDo: INSERT VERSION NUMBER
 */
public class CacheDropletLowMemoryRecoveryAction extends GenericService implements LowMemoryRecoveryAction {
    /** cacheDroplets property */
    private Cache[] mCacheDroplets;

    /**
     * Flushes caches
     *
     * @param pMemoryMXBean ToDo: DOCUMENT ME!
     */
    public void runAction() {
        if (mCacheDroplets != null) {
            for (int i = 0; i < mCacheDroplets.length; i++) {
            	if (isLoggingDebug()) {
                    logDebug("Flushing cache for " + mCacheDroplets[i].getAbsoluteName());
                }
                mCacheDroplets[i].flushCache();
            }
        }
    }

    /**
     * Returns cacheDroplets property.
     *
     * @return cacheDroplets property.
     */
    public Cache[] getCacheDroplets() {
        return mCacheDroplets;
    }

    /**
     * Sets the value of the cacheDroplets property.
     *
     * @param pCacheDroplets parameter to set.
     */
    public void setCacheDroplets(Cache[] pCacheDroplets) {
        mCacheDroplets = pCacheDroplets;
    }
    
    /**
     * ToDo: DOCUMENT ME!
     *
     * @throws ServiceException ToDo: DOCUMENT ME!
     */
    public void doStartService() throws ServiceException {
        super.doStartService();
        if (mCacheDroplets != null) {
            for (int i = 0; i < mCacheDroplets.length; i++) {
                if (isLoggingDebug()) {
                    logDebug(mCacheDroplets[i].getAbsoluteName() +
                             " has been added to the low memory recovery service.");
                }
            }
        }
    }

}
