package com.castorama.seo;

import atg.nucleus.GenericService;

import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;

import atg.repository.rql.RqlStatement;

/**
 * Given implementation of LegacyURLManager works directly with the catalog repository 
 * and doesn't perform any internal caching totally relying on the repository routine.
 *
 * @author  EPAM team
 */
public class RepositoryLegacyURLManager extends GenericService implements LegacyURLManager {
    
    /** ProductCatalog property */
    private Repository mProductCatalog;

    /**
     *  Returns id of category mapped to the passed URL, if any.
     *
     * @param  pURL parameter to set.
     *
     * @return categoryForURL property.
     */
    public String getCategoryForURL(String pURL) {
        try {
            RepositoryItemDescriptor id = getProductCatalog().getItemDescriptor(ITEM_LEGACY_URL);
            RqlStatement statement = RqlStatement.parseRqlStatement(GET_LEGACY_LINK_BY_URL_RQL);
            RepositoryItem[] items = statement.executeQuery(id.getRepositoryView(), new Object[] {pURL});
            if ((items != null) && (items.length > 0)) {
                return ((RepositoryItem) items[0].getPropertyValue(PROPERTY_ATTACHED_TO)).getRepositoryId();
            }
            return null;
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
            return null;
        }
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

}
