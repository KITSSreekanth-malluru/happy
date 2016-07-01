package com.castorama.deployment;

import atg.deployment.common.event.DeploymentEvent;
import atg.deployment.common.event.DeploymentEventListener;

import atg.nucleus.GenericService;

/**
 * ToDo: DOCUMENT ME!
 *
 * @author EPAM team
 */
public abstract class AbstractCacheInvalidator extends GenericService implements DeploymentEventListener {
    /** mCache constant. */
    private Object[] mCache;

    /** mState property */
    private int mState = DeploymentEvent.IDLE;

    /**
     * Sets the value of the cache property.
     *
     * @param pCache parameter to set.
     */
    public void setCache(Object[] pCache) {
        mCache = pCache;
    }

    /**
     * Returns cache property.
     *
     * @return cache property.
     */
    public Object[] getCache() {
        return mCache;
    }

    /**
     *
     * ToDo: DOCUMENT ME!
     *
     * @param pEvent parameter
     */
    public void deploymentEvent(DeploymentEvent pEvent) {
        if (pEvent == null) {
            return;
        }

        if (isLoggingDebug()) {
            logDebug("Deployment event: " + DeploymentEvent.stateToString(pEvent.getNewState()));
        }

        if (mState == pEvent.getNewState()) {
            Object[] caches = getCache();

            if (caches != null) {
                for (int i = 0; i < caches.length; i++) {
                    invalidateCache(caches[i], pEvent);
                }
            }
        }
    }

    /**
     *
     * ToDo: DOCUMENT ME!
     *
     * @param pObj   parameter
     * @param pEvent parameter
     */
    public abstract void invalidateCache(Object pObj, DeploymentEvent pEvent);

    /**
     * Returns state property.
     *
     * @return state property.
     */
    public int getState() {
        return mState;
    }

    /**
     * Sets the value of the state property.
     *
     * @param pState parameter to set.
     */
    public void setState(int pState) {
        mState = pState;
    }
}
