package com.castorama.seo;

import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;

import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryOptions;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.SortDirective;
import atg.repository.SortDirectives;

import atg.service.cache.Cache;

/**
 * ToDo: DOCUMENT ME!
 * 
 * @author EPAM team
 * @version ToDo: INSERT VERSION NUMBER
 */
public class CacheLegacyURLManager extends GenericService implements LegacyURLManager {
	/** LegacyURLCache property */
	private Cache mLegacyURLCache;

	/** SEOConfiguration property */
	private SEOConfiguration mSEOConfiguration;

	/** ProductCatalog property */
	private Repository mProductCatalog;

	/** maxNumberOfLinksToCacheOnStartup property. */
	private int mMaxNumberOfLinksToCacheOnStartup = 500;

	/**
	 * Returns legacyURLCache property.
	 * 
	 * @return legacyURLCache property.
	 */
	public Cache getLegacyURLCache() {
		return mLegacyURLCache;
	}

	/**
	 * Sets the value of the legacyURLCache property.
	 * 
	 * @param pLegacyURLCache
	 *            parameter to set.
	 */
	public void setLegacyURLCache(Cache pLegacyURLCache) {
		mLegacyURLCache = pLegacyURLCache;
	}

	/**
	 * ToDo: DOCUMENT ME!
	 * 
	 * @throws ServiceException
	 *             ToDo: DOCUMENT ME!
	 */
	@Override
	public void doStartService() throws ServiceException {
		super.doStartService();
		if (getSEOConfiguration().isSupportLegacyURLs()) {
			initLegaceyLinks();
		}
	}

	/**
	 * Returns categoryForURL property.
	 * 
	 * @param pURL
	 *            parameter to set.
	 * @return categoryForURL property.
	 */
	public String getCategoryForURL(String pURL) {
		try {
			return (String) getLegacyURLCache().get(pURL.toLowerCase());
		} catch (Exception e) {
			if (isLoggingError()) {
				logError(e);
			}
			return null;
		}
	}

	/**
	 * ToDo: DOCUMENT ME!
	 */
	protected void initLegaceyLinks() {
		if (isLoggingDebug()) {
			logDebug("initLegaceyLinks");
		}
		RepositoryItem[] links = null;
		try {
			RepositoryView legacyURLView = mProductCatalog.getView(ITEM_LEGACY_URL);
			QueryBuilder qb = legacyURLView.getQueryBuilder();
			Query query = qb.createUnconstrainedQuery();
			SortDirectives sortDirectives = new SortDirectives();
			String[] precachedPropertyNames = {PROPERTY_URI, PROPERTY_ATTACHED_TO};
			sortDirectives.addDirective(new SortDirective(PROPERTY_URI, SortDirective.DIR_ASCENDING));
			links = legacyURLView.executeQuery(query, new QueryOptions(0, getMaxNumberOfLinksToCacheOnStartup(),
					sortDirectives, precachedPropertyNames));
		} catch (Exception e) {
			if (isLoggingError()) {
				logError(e);
			}			
		}
		if (links != null) {
			logInfo(links.length + " legacy URL(s) are prechached");
			for (int i = 0; i < links.length; i++) {
				String uri = ((String) links[i].getPropertyValue(PROPERTY_URI)).toLowerCase();
				try {
					mLegacyURLCache.get(uri);
				} catch (Exception e) {
					if (isLoggingError()) {
						logError(e);
					}	
				}
			}
		}
	}

	/**
	 * Returns maxNumberOfLinksToCacheOnStartup property.
	 * 
	 * @return maxNumberOfLinksToCacheOnStartup property.
	 */
	public int getMaxNumberOfLinksToCacheOnStartup() {
		return mMaxNumberOfLinksToCacheOnStartup;
	}

	/**
	 * Sets the value of the maxNumberOfLinksToCacheOnStartup property.
	 * 
	 * @param pMaxNumberOfLinksToCacheOnStartup
	 *            parameter to set.
	 */
	public void setMaxNumberOfLinksToCacheOnStartup(int pMaxNumberOfLinksToCacheOnStartup) {
		mMaxNumberOfLinksToCacheOnStartup = pMaxNumberOfLinksToCacheOnStartup;
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
	 * @param pProductCatalog
	 *            parameter to set.
	 */
	public void setProductCatalog(Repository pProductCatalog) {
		mProductCatalog = pProductCatalog;
	}

	/**
	 * Returns SEOConfiguration property.
	 * 
	 * @return SEOConfiguration property.
	 */
	public SEOConfiguration getSEOConfiguration() {
		return mSEOConfiguration;
	}

	/**
	 * Sets the value of the SEOConfiguration property.
	 * 
	 * @param pConfiguration
	 *            parameter to set.
	 */
	public void setSEOConfiguration(SEOConfiguration pConfiguration) {
		mSEOConfiguration = pConfiguration;
	}
}
