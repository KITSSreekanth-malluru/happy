package com.castorama.commerce.search.refinement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import atg.commerce.search.refinement.CommerceFacetTrailTools;
import atg.commerce.search.refinement.admin.CommerceRefinementConfiguration;
import atg.commerce.search.refinement.custom.CustomCatalogFacetTrailTools;

import atg.core.util.StringUtils;

import atg.repository.RepositoryItem;

import atg.repository.search.refinement.DisjunctionMultiValue;
import atg.repository.search.refinement.Facet;
import atg.repository.search.refinement.FacetDisjunctionMultiValue;
import atg.repository.search.refinement.FacetException;
import atg.repository.search.refinement.FacetTrail;
import atg.repository.search.refinement.FacetValue;
import atg.repository.search.refinement.RangeFacetDisjunctionMultiValue;
import atg.repository.search.refinement.RangeFacetValue;
import atg.repository.search.refinement.admin.RefinementConfiguration;

import atg.service.perfmonitor.PerformanceMonitor;

/**
 * Override removeFacetValue method for maintaining correct removing multi vlued
 * facets. Add performance monitors to platform methods.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastoramaFacetTrailTools extends CustomCatalogFacetTrailTools {
    /*
     * (non-Javadoc)
     *
     * @see atg.commerce.search.refinement.CommerceFacetTrailTools#removeFacetValue(java.util.ArrayList,
     *      atg.repository.search.refinement.FacetValue)
     */
    @Override public void removeFacetValue(ArrayList pFacetValueList, FacetValue pValue) {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastoramaFacetTrailTools.removeFacetValue");
            }

            if ((pFacetValueList != null) && (pValue != null)) {
                for (FacetValue fv : (List<FacetValue>) pFacetValueList) {
                    if ((fv != null) && (fv instanceof DisjunctionMultiValue) &&
                            fv.getFacet().getId().equalsIgnoreCase(pValue.getFacet().getId())) {
                        String fvStr = ((DisjunctionMultiValue) fv).toString();
                        boolean isFVContainsLast = false;
                        String lastIndicator = "";

                        if ((fv instanceof RangeFacetDisjunctionMultiValue)) {
                            lastIndicator =
                                (!StringUtils.isBlank(((RangeFacetDisjunctionMultiValue) fv).getIndicatorSeparator()))
                                ? ((RangeFacetDisjunctionMultiValue) fv).getIndicatorSeparator() : "";
                            lastIndicator =
                                (!StringUtils.isBlank(((RangeFacetDisjunctionMultiValue) fv).getLastIndicator()))
                                ? lastIndicator.concat(((RangeFacetDisjunctionMultiValue) fv).getLastIndicator())
                                : lastIndicator;

                            if (!StringUtils.isBlank(lastIndicator) && fvStr.contains(lastIndicator)) {
                                isFVContainsLast = true;
                            }
                        }

                        List<Object> multiFacetValues =
                            new ArrayList(Arrays.asList(((DisjunctionMultiValue) fv).getValues()));
                        Object lastRangeValue = null;

                        if (isFVContainsLast && (multiFacetValues.size() > 0)) {
                            lastRangeValue = multiFacetValues.get(multiFacetValues.size() - 1);
                        }

                        Object currentValue = pValue instanceof FacetDisjunctionMultiValue ? ((FacetDisjunctionMultiValue)pValue).getSingleValue() : 
                            pValue instanceof RangeFacetDisjunctionMultiValue ? ((RangeFacetDisjunctionMultiValue)pValue).getSingleValue() : 
                                pValue.getValue();

                        if (multiFacetValues.remove(currentValue)) {
                            if (multiFacetValues.size() == 1) {
                                int i = pFacetValueList.indexOf(fv);

                                pFacetValueList.remove(i);

                                String newFacetValue = (String) multiFacetValues.get(0);

                                if (isFVContainsLast && newFacetValue.equals(lastRangeValue)) {
                                    newFacetValue = newFacetValue.concat(lastIndicator);
                                }

                                pFacetValueList.add(i,
                                                    getFacetValue(getFacet(pValue.getFacet().getId()), newFacetValue));

                                break;
                            } else {
                                ((DisjunctionMultiValue) fv).setValues(multiFacetValues.toArray());

                                if (isFVContainsLast && currentValue.equals(lastRangeValue) &&
                                        (fv instanceof RangeFacetDisjunctionMultiValue)) {
                                    ((RangeFacetDisjunctionMultiValue) fv).setLastRangeValue(false);
                                }
                            }  // end if-else
                        }  // end if
                    }  // end if
                }  // end for
            }  // end if
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastoramaFacetTrailTools.removeFacetValue");
            }
        }  // end try-finally

        super.removeFacetValue(pFacetValueList, pValue);
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.CommerceFacetTrailTools#getCommerceRefinementConfiguration(atg.repository.RepositoryItem)
     */
    @Override public CommerceRefinementConfiguration getCommerceRefinementConfiguration(RepositoryItem pItem)
                                                                                 throws FacetException {
        CommerceRefinementConfiguration crc = null;

        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastoramaFacetTrailTools.getCommerceRefinementConfiguration");
            }

            crc = super.getCommerceRefinementConfiguration(pItem);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastoramaFacetTrailTools.getCommerceRefinementConfiguration");
            }
        }

        return crc;
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.CommerceFacetTrailTools#getFacetValue(atg.repository.search.refinement.Facet, java.lang.String)
     */
    @Override public FacetValue getFacetValue(Facet pFacet, String pFacetValueString) {
        FacetValue fv = null;

        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastoramaFacetTrailTools.getFacetValue");
            }

            fv = super.getFacetValue(pFacet, pFacetValueString);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastoramaFacetTrailTools.getFacetValue");
            }
        }

        return fv;
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.CommerceFacetTrailTools#getRefinementConfigurationItemFromCategory(atg.repository.RepositoryItem)
     */
    @Override public RepositoryItem getRefinementConfigurationItemFromCategory(RepositoryItem pCategory) {
        RepositoryItem ri = null;

        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastoramaFacetTrailTools.getRefinementConfigurationItemFromCategory");
            }

            ri = super.getRefinementConfigurationItemFromCategory(pCategory);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastoramaFacetTrailTools.getRefinementConfigurationItemFromCategory");
            }
        }

        return ri;
    }

    /* (non-Javadoc)
     * @see atg.repository.search.refinement.FacetTrailTools#createFacetTrail(atg.repository.search.refinement.FacetTrailTools.FacetTrailElements, atg.repository.search.refinement.admin.RefinementConfiguration)
     */
    @Override public FacetTrail createFacetTrail(FacetTrailElements pFacetTrailElements,
                                                 RefinementConfiguration pRefinementConfiguration)
                                          throws FacetException {
        FacetTrail ft = null;

        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastoramaFacetTrailTools.createFacetTrail");
            }

            ft = super.createFacetTrail(pFacetTrailElements, pRefinementConfiguration);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastoramaFacetTrailTools.createFacetTrail");
            }
        }

        return ft;
    }

    /* (non-Javadoc)
     * @see atg.repository.search.refinement.FacetTrailTools#getFacet(java.lang.String)
     */
    @Override public Facet getFacet(String pFacetString) {
        Facet f = null;

        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastoramaFacetTrailTools.getFacet");
            }

            f = super.getFacet(pFacetString);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastoramaFacetTrailTools.getFacet");
            }
        }

        return f;
    }

    /* (non-Javadoc)
     * @see atg.repository.search.refinement.FacetTrailTools#getFacetTrailElements(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override public FacetTrailElements getFacetTrailElements(String pTrail, String pSortBy, String pAddFacet,
                                                              String pRemoveFacet, String pRemoveAllFacets,
                                                              String pRemoveFacetType) throws FacetException {
        FacetTrailElements fte = null;

        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastoramaFacetTrailTools.getFacetTrailElements");
            }

            fte =
                super.getFacetTrailElements(pTrail, pSortBy, pAddFacet, pRemoveFacet, pRemoveAllFacets,
                                            pRemoveFacetType);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastoramaFacetTrailTools.getFacetTrailElements");
            }
        }

        return fte;
    }

    /* (non-Javadoc)
     * @see atg.repository.search.refinement.FacetTrailTools#getRefinementConfiguration(atg.repository.RepositoryItem)
     */
    @Override protected RefinementConfiguration getRefinementConfiguration(RepositoryItem pItem) throws FacetException {
        RefinementConfiguration rc = null;

        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastoramaFacetTrailTools.getRefinementConfiguration");
            }

            rc = super.getRefinementConfiguration(pItem);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastoramaFacetTrailTools.getRefinementConfiguration");
            }
        }

        return rc;
    }

    /* (non-Javadoc)
     * @see atg.repository.search.refinement.FacetTrailTools#parseFacetValueString(java.lang.String, java.lang.String)
     */
    @Override public ArrayList parseFacetValueString(String pFacetValueString, String pRemoveFacetType) {
        ArrayList al = null;

        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastoramaFacetTrailTools.parseFacetValueString");
            }

            al = super.parseFacetValueString(pFacetValueString, pRemoveFacetType);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastoramaFacetTrailTools.parseFacetValueString");
            }
        }

        return al;
    }

    /* (non-Javadoc)
     * @see atg.repository.search.refinement.FacetTrailTools#parseTrailStringIntoPairs(java.lang.String)
     */
    @Override public ArrayList<FacetValue> parseTrailStringIntoPairs(String pTrailString) {
        if(isLoggingDebug())
        {
            logDebug("parseTrailStringIntoPairs() called.");
            logDebug((new StringBuilder()).append("pTrailString: ").append(pTrailString).toString());
        }
        ArrayList<FacetValue> pairList = new ArrayList<FacetValue>();

        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastoramaFacetTrailTools.parseTrailStringIntoPairs");
            }

            if (pTrailString != null) {
                ArrayList<FacetValue> facetValueList = new ArrayList<FacetValue>();
                processAddFacet(facetValueList, pTrailString);

                for (FacetValue facetValue : facetValueList) {
                if(isLoggingDebug())
                    logDebug((new StringBuilder()).append("Processing facetValue: ").append(facetValue).toString());
                    if (facetValue instanceof DisjunctionMultiValue) {
                        DisjunctionMultiValue dFacetValue = (DisjunctionMultiValue) facetValue;
                    if(isLoggingDebug())
                        logDebug((new StringBuilder()).append("Got ").append(dFacetValue.getValues().length).append(" OR'd values").toString());
                        Object lastFV = null;
                        Object[] fvValues = dFacetValue.getValues();

                        if ((dFacetValue instanceof RangeFacetDisjunctionMultiValue) && (fvValues != null) &&
                                (fvValues.length > 0) &&
                                ((RangeFacetDisjunctionMultiValue) dFacetValue).isLastRangeValue()) {
                            //"last" facet's value should be the last in list
                            lastFV = fvValues[fvValues.length - 1];
                        }

                        for (Object oValue : dFacetValue.getValues()) {
                            String value = oValue.toString();
                        if(isLoggingDebug())
                            logDebug((new StringBuilder()).append("Processing OR'd value: ").append(value).toString());
                        try
                        {
                            FacetValue newFacetValue = (FacetValue) facetValue.clone();
                            if (newFacetValue instanceof FacetDisjunctionMultiValue) {

                                    if ((lastFV != null) && !value.equals(lastFV.toString())) {
                                        ((RangeFacetValue) newFacetValue).setLastRangeValue(false);
                                    }
                                } else {
                                    newFacetValue = (FacetValue) facetValue.clone();
                                }

                                newFacetValue.setValue(value);

                                pairList.add(newFacetValue);

                            if(isLoggingDebug())
                                logDebug((new StringBuilder()).append("Added new facet value: ").append(newFacetValue).toString());

                            } catch (CloneNotSupportedException cne) {
                                if (isLoggingError()) {
                                    logError("FacetValue or RangeFacetValue couldn't be cloned.", cne);
                                }
                            }  // end try-catch
                        }  // end for
                    } else {
                        pairList.add(facetValue);
                    }  // end if-else
                }  // end for
            }  // end if

        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastoramaFacetTrailTools.parseTrailStringIntoPairs");
            }
        }  // end try-finally

        return pairList;
    }

    /* (non-Javadoc)
     * @see atg.repository.search.refinement.FacetTrailTools#processAddFacet(java.util.ArrayList, java.lang.String)
     */
    @Override public void processAddFacet(ArrayList pFacetValueList, String pAddFacet) {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastoramaFacetTrailTools.processAddFacet");
            }

            super.processAddFacet(pFacetValueList, pAddFacet);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastoramaFacetTrailTools.processAddFacet");
            }
        }
    }

    /* (non-Javadoc)
     * @see atg.repository.search.refinement.FacetTrailTools#processAddMultipleFacets(java.util.ArrayList, java.util.List)
     */
    @Override public void processAddMultipleFacets(ArrayList<FacetValue> pFacetValueList, List<String> pAddFacetList) {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastoramaFacetTrailTools.processAddMultipleFacets");
            }

            super.processAddMultipleFacets(pFacetValueList, pAddFacetList);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastoramaFacetTrailTools.processAddMultipleFacets");
            }
        }
    }

    /* (non-Javadoc)
     * @see atg.repository.search.refinement.FacetTrailTools#processRemoveFacet(java.util.ArrayList, java.lang.String)
     */
    @Override public void processRemoveFacet(ArrayList pFacetValueList, String pRemoveFacet) {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastoramaFacetTrailTools.processRemoveFacet");
            }

            super.processRemoveFacet(pFacetValueList, pRemoveFacet);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastoramaFacetTrailTools.processRemoveFacet");
            }
        }
    }
}
