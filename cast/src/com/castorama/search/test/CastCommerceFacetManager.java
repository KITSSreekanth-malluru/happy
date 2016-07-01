package com.castorama.search.test;

import atg.commerce.search.refinement.CommerceFacetManager;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.repository.search.refinement.RefinementFacet;
import atg.repository.search.refinement.admin.RefinementConfiguration;

import atg.service.perfmonitor.PerformanceMonitor;

/**
 * Extension for adding performance monitors.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastCommerceFacetManager extends CommerceFacetManager {
    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.CommerceFacetManager#getCommerceRefineConfigById(java.lang.String)
     */
    @Override public RepositoryItem getCommerceRefineConfigById(String pCommerceRefineConfigId)
                                                         throws RepositoryException {
        RepositoryItem ri = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastCommerceFacetManager.getCommerceRefineConfigById");
            }
            ri = super.getCommerceRefineConfigById(pCommerceRefineConfigId);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCommerceFacetManager.getCommerceRefineConfigById");
            }
        }
        return ri;
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.CommerceFacetManager#getGlobalRefinementConfiguration()
     */
    @Override public RefinementConfiguration getGlobalRefinementConfiguration() throws RepositoryException {
        RefinementConfiguration rc = null;

        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastCommerceFacetManager.getGlobalRefinementConfiguration");
            }
            rc = super.getGlobalRefinementConfiguration();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastCommerceFacetManager.getGlobalRefinementConfiguration");
            }
        }
        return rc;
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.CommerceFacetManager#isCategoryFacet(atg.repository.search.refinement.RefinementFacet)
     */
    @Override public boolean isCategoryFacet(RefinementFacet pFacet) {
        boolean result = false;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastCommerceFacetManager.isCategoryFacet");
            }
            result = super.isCategoryFacet(pFacet);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCommerceFacetManager.isCategoryFacet");
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.CommerceFacetManager#isValueACategoryId(java.lang.String)
     */
    @Override public boolean isValueACategoryId(String pValueToCheck) {
        boolean result = false;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastCommerceFacetManager.isValueACategoryId");
            }
            result = super.isValueACategoryId(pValueToCheck);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCommerceFacetManager.isValueACategoryId");
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see atg.repository.search.refinement.FacetManager#getItem(java.lang.String, java.lang.String)
     */
    @Override protected RepositoryItem getItem(String pItemId, String pItemDescriptorName) throws RepositoryException {
        RepositoryItem ri = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastCommerceFacetManager.getItem");
            }

            ri = super.getItem(pItemId, pItemDescriptorName);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCommerceFacetManager.getItem");
            }
        }
        return ri;
    }

    /* (non-Javadoc)
     * @see atg.repository.search.refinement.FacetManager#getRefineConfigById(java.lang.String)
     */
    @Override public RepositoryItem getRefineConfigById(String pRefineConfigId) throws RepositoryException {
        RepositoryItem ri = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastCommerceFacetManager.getRefineConfigById");
            }
            ri = super.getRefineConfigById(pRefineConfigId);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCommerceFacetManager.getRefineConfigById");
            }
        }
        return ri;
    }

    /* (non-Javadoc)
     * @see atg.repository.search.refinement.FacetManager#getRefineElementById(java.lang.String)
     */
    @Override public RepositoryItem getRefineElementById(String pRefineElementId) throws RepositoryException {
        RepositoryItem ri = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastCommerceFacetManager.getRefineElementById");
            }
            ri = super.getRefineElementById(pRefineElementId);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCommerceFacetManager.getRefineElementById");
            }
        }
        return ri;
    }

    /* (non-Javadoc)
     * @see atg.repository.search.refinement.FacetManager#getSortOptionById(java.lang.String)
     */
    @Override public RepositoryItem getSortOptionById(String pSortOptionId) throws RepositoryException {
        RepositoryItem ri = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastCommerceFacetManager.getSortOptionById");
            }
            ri = super.getSortOptionById(pSortOptionId);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCommerceFacetManager.getSortOptionById");
            }
        }
        return ri;
    }

}
