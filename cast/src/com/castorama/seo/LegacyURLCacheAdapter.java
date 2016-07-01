package com.castorama.seo;

import atg.nucleus.GenericService;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;

import atg.repository.rql.RqlStatement;

/**
 * ToDo: DOCUMENT ME!
 *
 * @author EPAM team
 */
public class LegacyURLCacheAdapter extends GenericService implements atg.service.cache.CacheAdapter {
    /** PROPERTY_ATTACHED_TO property */
    private static final String PROPERTY_ATTACHED_TO = "attachedTo";

    /** ITEM_LEGACY_URL constant. */
    private static final String ITEM_LEGACY_URL = "legacyURL";

    /** productCatalog property. */
    private Repository mProductCatalog;

    /**
     * Returns cacheElement property.
     *
     * @param  pKey parameter to set.
     *
     * @return cacheElement property.
     *
     * @throws Exception - exception
     */
    public Object getCacheElement(Object pKey) throws Exception {
        RepositoryItem ri = getLegacyItemByURI((String) pKey);

        if (ri != null) {
            RepositoryItem category = (RepositoryItem) ri.getPropertyValue(PROPERTY_ATTACHED_TO);
            return category.getRepositoryId();
        }

        return null;
    }

    /**
     * Returns cacheElements property.
     *
     * @param  pKeys parameter to set.
     *
     * @return cacheElements property.
     *
     * @throws Exception - exception
     */
    public Object[] getCacheElements(Object[] pKeys) throws Exception {
        if (pKeys != null) {
            int length = pKeys.length;
            Object[] ret = new Object[length];

            for (int i = 0; i < length; i++) {
                ret[i] = getCacheElement(pKeys[i]);
            }

            return ret;
        } else {
            return null;
        }
    }

    /**
     * Returns cacheKeySize property.
     *
     * @param  pArg0 parameter to set.
     *
     * @return cacheKeySize property.
     */
    public int getCacheKeySize(Object pArg0) {
        return 0;
    }

    /**
     * Returns cacheElementSize property.
     *
     * @param  pArg0 parameter to set.
     * @param  pArg1 parameter to set.
     *
     * @return cacheElementSize property.
     */
    public int getCacheElementSize(Object pArg0, Object pArg1) {
        return 0;
    }

    /**
     *
     * ToDo: DOCUMENT ME!
     *
     * @param pArg0 parameter
     * @param pArg1 parameter
     */
    public void removeCacheElement(Object pArg0, Object pArg1) {
    }

    /**
     * Returns productCatalog property.
     *
     * @return productCatalog property.
     */
    public Repository getProductCatalog() {
        return mProductCatalog;
    }

    /**
     * Sets the value of the productCatalog property.
     *
     * @param pProductCatalog parameter to set.
     */
    public void setProductCatalog(Repository pProductCatalog) {
        mProductCatalog = pProductCatalog;
    }

    /**
     * Returns legacyItemByURI property.
     *
     * @param  pURI parameter to set.
     *
     * @return legacyItemByURI property.
     */
    protected RepositoryItem getLegacyItemByURI(String pURI) {
        RepositoryItem[] answer = null;

        try {
            RepositoryItemDescriptor id = getProductCatalog().getItemDescriptor(ITEM_LEGACY_URL);
            RqlStatement statement = RqlStatement.parseRqlStatement(LegacyURLManager.GET_LEGACY_LINK_BY_URL_RQL);
            answer = statement.executeQuery(id.getRepositoryView(), new Object[] {pURI});
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError(e);
            }
        }

        if ((answer == null) || (answer.length == 0)) {
            return null;
        }

        return answer[0];
    }
}
