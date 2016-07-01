package com.castorama.search;

import atg.repository.search.refinement.FacetValue;

/**
 * Represent AjouterFacetValue object. AjouterFacetValue contains facetValue
 * value and attribute selectedFacetValuewhich determined whether this facet's
 * value was selected or no. Used for rendering "Ajouter" pop-up.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class AjouterFacetValue {
    /** facetValue property. */
    private FacetValue mFacetValue;

    /**
     * selectedFacetValue property - determines whether facetValue has been
     * already selected.
     */
    private boolean mSelectedFacetValue;

    /**
     * Creates a new AjouterFacetValue object.
     *
     * @param pFacetValue         parameter
     * @param pSelectedFacetValue parameter
     */
    public AjouterFacetValue(FacetValue pFacetValue, boolean pSelectedFacetValue) {
        setFacetValue(pFacetValue);
        setSelectedFacetValue(pSelectedFacetValue);
    }

    /**
     * Returns facetValue property.
     *
     * @return facetValue property.
     */
    public FacetValue getFacetValue() {
        return mFacetValue;
    }

    /**
     * Sets the value of the facetValue property.
     *
     * @param pFacetValue parameter to set.
     */
    public void setFacetValue(FacetValue pFacetValue) {
        mFacetValue = pFacetValue;
    }

    /**
     * Returns selectedFacetValue property.
     *
     * @return selectedFacetValue property.
     */
    public boolean isSelectedFacetValue() {
        return mSelectedFacetValue;
    }

    /**
     * Sets the value of the selectedFacetValue property.
     *
     * @param pSelectedFacetValue parameter to set.
     */
    public void setSelectedFacetValue(boolean pSelectedFacetValue) {
        mSelectedFacetValue = pSelectedFacetValue;
    }
}
