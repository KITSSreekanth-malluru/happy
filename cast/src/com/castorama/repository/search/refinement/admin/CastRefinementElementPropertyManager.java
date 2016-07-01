package com.castorama.repository.search.refinement.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.repository.search.refinement.admin.RefinementElement;
import atg.repository.search.refinement.admin.RefinementElementPropertyManager;
import atg.repository.search.refinement.admin.RefinementPropertyType;
import atg.repository.search.refinement.admin.RefinementRangeType;
import atg.repository.search.refinement.admin.RefinementSortType;

/**
 * Extension for override populateRefinementElement method for populating
 * RefinementElement in the case "non-commerce" facets.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastRefinementElementPropertyManager extends RefinementElementPropertyManager {
    /** mFacetPropertyTypeToValue property */
    private Map mFacetPropertyTypeToValue;

    /** mFacetRangeToValue property */
    private Map mFacetRangeToValue;

    /** mSortTypeToValue property */
    private Map mSortTypeToValue;

    /**
     * Returns facetPropertyTypeToValue property.
     *
     * @return facetPropertyTypeToValue property.
     */
    public Map getFacetPropertyTypeToValue() {
        return mFacetPropertyTypeToValue;
    }

    /**
     * Sets the value of the facetPropertyTypeToValue property.
     *
     * @param pFacetPropertyTypeToValue parameter to set.
     */
    public void setFacetPropertyTypeToValue(Map pFacetPropertyTypeToValue) {
        mFacetPropertyTypeToValue = pFacetPropertyTypeToValue;
    }

    /**
     * Returns facetRangeToValue property.
     *
     * @return facetRangeToValue property.
     */
    public Map getFacetRangeToValue() {
        return mFacetRangeToValue;
    }

    /**
     * Sets the value of the facetRangeToValue property.
     *
     * @param pFacetRangeToValue parameter to set.
     */
    public void setFacetRangeToValue(Map pFacetRangeToValue) {
        mFacetRangeToValue = pFacetRangeToValue;
    }

    /**
     * Returns sortTypeToValue property.
     *
     * @return sortTypeToValue property.
     */
    public Map getSortTypeToValue() {
        return mSortTypeToValue;
    }

    /**
     * Sets the value of the sortTypeToValue property.
     *
     * @param pSortTypeToValue parameter to set.
     */
    public void setSortTypeToValue(Map pSortTypeToValue) {
        mSortTypeToValue = pSortTypeToValue;
    }

    /**
     * Populate refinement element for "non-commerce" facets.
     *
     * @param  pItem          parameter
     * @param  pRefineElement parameter
     *
     * @throws RepositoryException      exception
     * @throws IllegalArgumentException exception
     */
    public void populateRefinementElement(RepositoryItem pItem, RefinementElement pRefineElement)
                                   throws RepositoryException, IllegalArgumentException {
        String propName = null;

        checkItem(pItem);
        if (pRefineElement == null) {
            throw new IllegalArgumentException("CastRefinementElementPropertyManager:populateRefinementElement: Passed refinement element property is null.");
        }

        pRefineElement.setId(pItem.getRepositoryId());

        propName = getTypePropertyName();
        if (propName != null) {
            if ((pItem.getPropertyValue(propName) != null) && (pItem.getPropertyValue(propName) instanceof Integer)) {
                pRefineElement.setType(((Integer) pItem.getPropertyValue(propName)).toString());
            } else {
                pRefineElement.setType((String) pItem.getPropertyValue(propName));
            }
        }

        propName = getPropertyPropertyName();
        if (propName != null) {
            pRefineElement.setProperty((String) pItem.getPropertyValue(propName));

        }

        propName = getPropertyTypePropertyName();
        if (propName != null) {
            Object propertyType = pItem.getPropertyValue(propName);
            if ((propertyType != null) && (propertyType instanceof Integer)) {
                if ((getFacetPropertyTypeToValue() != null) &&
                        (getFacetPropertyTypeToValue().get(((Integer) propertyType).toString()) != null)) {
                    pRefineElement.setPropertyType(RefinementPropertyType.getEnum((String) getFacetPropertyTypeToValue()
                                                                                  .get(((Integer) propertyType)
                                                                                       .toString())));
                }
            } else {
                pRefineElement.setPropertyType(RefinementPropertyType.getEnum((String) pItem.getPropertyValue(propName)));
            }
        }

        propName = getLabelPropertyName();
        if (propName != null) {
            pRefineElement.setLabel((String) pItem.getPropertyValue(propName));
        }

        propName = getRangePropertyName();
        if (propName != null) {
            Object propertyType = pItem.getPropertyValue(propName);
            if ((propertyType != null) && (propertyType instanceof Integer)) {
                if ((getFacetPropertyTypeToValue() != null) &&
                        (getFacetRangeToValue().get(((Integer) propertyType).toString()) != null)) {
                    pRefineElement.setRange(RefinementRangeType.getEnum((String) getFacetRangeToValue().get(((Integer)
                                                                                                                 propertyType)
                                                                                                            .toString())));
                }
            } else {
                pRefineElement.setRange(RefinementRangeType.getEnum((String) pItem.getPropertyValue(propName)));
            }
        }

        propName = getSortPropertyName();
        if (propName != null) {
            Object propertyType = pItem.getPropertyValue(propName);
            if ((propertyType != null) && (propertyType instanceof Integer)) {
                if ((getFacetPropertyTypeToValue() != null) &&
                        (getSortTypeToValue().get(((Integer) propertyType).toString()) != null)) {
                    pRefineElement.setSort(RefinementSortType.getEnum((String) getSortTypeToValue().get(((Integer)
                                                                                                             propertyType)
                                                                                                        .toString())));
                }
            } else {
                pRefineElement.setSort(RefinementSortType.getEnum((String) pItem.getPropertyValue(propName)));
            }
        }

        propName = getLevelsPropertyName();
        if (propName != null) {
            pRefineElement.setLevels((Integer) pItem.getPropertyValue(propName));
        }

        propName = getSelectPropertyName();
        if (propName != null) {
            pRefineElement.setSelect((List) pItem.getPropertyValue(propName));
        }

        propName = getParentRefineElementPropertyName();
        if (propName != null) {
            pRefineElement.setParentRefineElement((RepositoryItem) pItem.getPropertyValue(propName));

        }

        propName = getChildElementsPropertyName();
        if (propName != null) {
            if ((pItem.getPropertyValue(propName) != null) && (pItem.getPropertyValue(propName) instanceof Set)) {
                pRefineElement.setChildElements(new ArrayList((Set) pItem.getPropertyValue(propName)));
            } else {
                pRefineElement.setChildElements((List) pItem.getPropertyValue(propName));
            }
        }

        return;
    }

}
