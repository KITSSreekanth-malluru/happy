package com.castorama.commerce.catalog;

import atg.commerce.catalog.custom.CustomCatalogTools;

import atg.nucleus.ServiceException;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * Add Castorama catalog id retrieving and check
 *
 * @author Epam Team
 */
public class CastCatalogTools extends CustomCatalogTools {
    /** Root navigation category id. */
    private String mRootNavigationCategoryId = "rootCategory";

    /** Castorama catalog id */
    private String mCastoramaCatalogId;

    /** Castorama catalog */
    private RepositoryItem mCastoramaCatalog;

    /**
     * Returns rootNavigationCategoryId property.
     *
     * @return rootNavigationCategoryId property.
     */
    public String getRootNavigationCategoryId() {
        return mRootNavigationCategoryId;
    }

    /**
     * Sets the value of the rootNavigationCategoryId property.
     *
     * @param pRootNavigationCategoryId parameter to set.
     */
    public void setRootNavigationCategoryId(String pRootNavigationCategoryId) {
        mRootNavigationCategoryId = pRootNavigationCategoryId;
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
     * Returns castoramaCatalog property.
     *
     * @return castoramaCatalog property.
     */
    public RepositoryItem getCastoramaCatalog() {
        return mCastoramaCatalog;
    }

    /**
     * Sets the value of the castoramaCatalog property.
     *
     * @param pCastoramaCatalog parameter to set.
     */
    public void setCastoramaCatalog(RepositoryItem pCastoramaCatalog) {
        mCastoramaCatalog = pCastoramaCatalog;
    }

    /**
     * Verifies whether castorama catalog id exist in repository
     *
     * @throws ServiceException if error occurs
     */
    public void doStartService() throws ServiceException {
        super.doStartService();

        Repository catalog = getCatalog();

        try {
            RepositoryItem catalogItem = catalog.getItem(getCastoramaCatalogId(), getBaseCatalogItemType());

            if (catalogItem == null) {
                if (isLoggingError()) {
                    logError("The catalog id " + getCastoramaCatalogId() + " is not valid.");
                }
            }

            setCastoramaCatalog(catalogItem);
        } catch (RepositoryException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Get the catalog with the given id.
     *
     * @return the catalog with the given id
     *
     * @param  pCatalogId - catalog id
     *
     * @throws RepositoryException if error occurs
     */
    public RepositoryItem getCatalog(String pCatalogId) throws RepositoryException {
        Repository catalog = getCatalog();
        RepositoryItem catalogItem = catalog.getItem(pCatalogId, getBaseCatalogItemType());

        if (catalogItem == null) {
            if (isLoggingError()) {
                logError("The catalog id " + pCatalogId + " is not valid.");
            }
        }

        return catalogItem;
    }

}
