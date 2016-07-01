package com.castorama.search;

import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;

/**
 * Utility class for search configuration
 *
 * @author Katsiaryna Sharstsiuk
 */
public class SearchConfiguration extends GenericService {
    /**
     * ajouterColumnWidth property - determines width of columns for "Ajouter"
     * and "Voir plus..." pop-ups.
     */
    private Integer mAjouterColumnWidth;
    /**
     * ajouterSingleColumnWidth property - determines width of single column for "Ajouter"
     * and "Voir plus..." pop-ups.
     */
    private Integer mAjouterSingleColumnWidth;

    /** ajouterRowAmount property - number of rows in the "ajouter" table. */
    private Integer mAjouterRowAmount;

    /**
     * facetRowAmount property - number of facet's values which should be shown
     * at the "left navigation area" section for each facet in visible area.
     */
    private Integer mFacetRowAmount;

    /**
     * Returns ajouterColumnWidth property.
     *
     * @return ajouterColumnWidth property.
     */
    public Integer getAjouterColumnWidth() {
        return mAjouterColumnWidth;
    }

    /**
     * Sets the value of the ajouterColumnWidth property.
     *
     * @param pAjouterColumnWidth parameter to set.
     */
    public void setAjouterColumnWidth(Integer pAjouterColumnWidth) {
        mAjouterColumnWidth = pAjouterColumnWidth;
    }

    /**
     * Returns facetRowAmount property.
     *
     * @return facetRowAmount property.
     */
    public Integer getFacetRowAmount() {
        return mFacetRowAmount;
    }

    /**
     * Sets the value of the facetRowAmount property.
     *
     * @param pFacetRowAmount parameter to set.
     */
    public void setFacetRowAmount(Integer pFacetRowAmount) {
        mFacetRowAmount = pFacetRowAmount;
    }

    /**
     * Returns ajouterRowAmount property.
     *
     * @return ajouterRowAmount property.
     */
    public Integer getAjouterRowAmount() {
        return mAjouterRowAmount;
    }

    /**
     * Sets the value of the ajouterRowAmount property.
     *
     * @param pAjouterRowAmount parameter to set.
     */
    public void setAjouterRowAmount(Integer pAjouterRowAmount) {
        mAjouterRowAmount = pAjouterRowAmount;
    }
    
    /**
     * Returns ajouterSingleColumnWidth property.
     *
     * @return ajouterSingleColumnWidth property.
     */
    public Integer getAjouterSingleColumnWidth() {
		return mAjouterSingleColumnWidth;
	}

    /**
     * Sets the value of the ajouterSingleColumnWidth property.
     *
     * @param pAjouterSingleColumnWidth parameter to set.
     */
	public void setAjouterSingleColumnWidth(Integer pAjouterSingleColumnWidth) {
		mAjouterSingleColumnWidth = pAjouterSingleColumnWidth;
	}

    /* (non-Javadoc)
     * @see atg.nucleus.GenericService#doStartService()
     */
    @Override public void doStartService() throws ServiceException {
        if (getAjouterColumnWidth() == null) {
            if (isLoggingError()) {
                logError("ajouterColumnWidth property is null, please set column's width for ajouter pop-up");
            }

        }
        if (getAjouterRowAmount() == null) {
            if (isLoggingError()) {
                logError("ajouterRowAmount property is null, please set rows amount for ajouter pop-up");
            }

        }
        if (getFacetRowAmount() == null) {
            if (isLoggingError()) {
                logError("facetRowAmount property is null, please amount of facet's values which should be rendered in the 'affiner votre selection' section");
            }

        }
        super.doStartService();
    }

}
