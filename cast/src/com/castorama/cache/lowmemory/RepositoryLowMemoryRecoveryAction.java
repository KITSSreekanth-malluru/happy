package com.castorama.cache.lowmemory;

import atg.adapter.gsa.GSARepository;

import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;

/**
 * Given recovery action invalidates caches of configured via <code>
 * repositories</code> GSA repositories.
 *
 * @author  EPAM team
 * @version ToDo: INSERT VERSION NUMBER
 */
public class RepositoryLowMemoryRecoveryAction extends GenericService implements LowMemoryRecoveryAction {
    /** repositories property */
    private GSARepository[] mRepositories;

    /**
     * Returns repositories property.
     *
     * @return repositories property.
     */
    public GSARepository[] getRepositories() {
        return mRepositories;
    }

    /**
     * Sets the value of the repositories property.
     *
     * @param pRepositories parameter to set.
     */
    public void setRepositories(GSARepository[] pRepositories) {
        mRepositories = pRepositories;
    }

    /**
     * ToDo: DOCUMENT ME!
     */
    public void runAction() {
        if (mRepositories != null) {
            for (int i = 0; i < mRepositories.length; i++) {
                mRepositories[i].invalidateCaches();
                if (isLoggingDebug()) {
                    logDebug(mRepositories[i].getAbsoluteName() + "'s cache has been invalidated.");
                }
            }
        }
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @throws ServiceException ToDo: DOCUMENT ME!
     */
    public void doStartService() throws ServiceException {
        super.doStartService();
        if (mRepositories != null) {
            for (int i = 0; i < mRepositories.length; i++) {
                if (isLoggingDebug()) {
                    logDebug(mRepositories[i].getAbsoluteName() +
                             " has been added to the low memory recovery service.");
                }
            }
        }
    }
}
