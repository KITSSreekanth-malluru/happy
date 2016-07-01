package com.castorama.deployment;

import java.util.Iterator;
import java.util.Set;

import atg.deployment.common.event.DeploymentEvent;

import atg.service.cache.Cache;

/**
 *
 * @author EPAM team
 */
public class LegacyURLCacheInvalidator extends AbstractCacheInvalidator {
    /** ITEM_LEGACY_URL constant. */
    private static final String ITEM_LEGACY_URL = "legacyURL";

    /**
     *
     * @param pObj   parameter
     * @param pEvent parameter
     */
    public void invalidateCache(Object pObj, DeploymentEvent pEvent) {
        if (isLoggingDebug()) {
            logDebug("invalidateCache " + pObj);
        }

        if (pObj != null) {
            if (pObj instanceof Cache) {
                Set repositories = pEvent.getAffectedItemTypes().keySet();
                Iterator i = repositories.iterator();
                boolean flushCache = false;
                while (i.hasNext()) {
                    String gsaPath = (String) i.next();
                    Set names = (Set) pEvent.getAffectedItemTypes().get(gsaPath);
                    if (names.contains(ITEM_LEGACY_URL)) {
                        flushCache = true;
                        break;
                    }
                }
                if (flushCache) {
                    Cache cache = (Cache) pObj;
                    cache.flush();
                }

            }
        }  // end if
    }
}
