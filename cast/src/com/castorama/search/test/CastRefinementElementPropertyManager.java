package com.castorama.search.test;

import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.repository.search.refinement.admin.RefinementElement;
import atg.repository.search.refinement.admin.RefinementElementPropertyManager;

import atg.service.perfmonitor.PerformanceMonitor;

/**
 * Extension for adding performance monitors.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastRefinementElementPropertyManager extends RefinementElementPropertyManager {
    /* (non-Javadoc)
     * @see atg.repository.search.refinement.admin.RefinementElementPropertyManager#checkItem(atg.repository.RepositoryItem)
     */
    @Override protected void checkItem(RepositoryItem pItem) throws RepositoryException, IllegalArgumentException {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastRefinementElementPropertyManager.checkItem");
            }
            super.checkItem(pItem);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastRefinementElementPropertyManager.checkItem");
            }
        }
    }

    /* (non-Javadoc)
     * @see atg.repository.search.refinement.admin.RefinementElementPropertyManager#createNewRefinementElement()
     */
    @Override public RefinementElement createNewRefinementElement() {
        RefinementElement rl = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastRefinementElementPropertyManager.createNewRefinementElement");
            }
            rl = super.createNewRefinementElement();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastRefinementElementPropertyManager.createNewRefinementElement");
            }
        }
        return rl;
    }

    /* (non-Javadoc)
     * @see atg.repository.search.refinement.admin.RefinementElementPropertyManager#populateRefinementElement(atg.repository.RepositoryItem, atg.repository.search.refinement.admin.RefinementElement)
     */
    @Override public void populateRefinementElement(RepositoryItem pItem, RefinementElement pRefineElement)
                                             throws RepositoryException, IllegalArgumentException {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastRefinementElementPropertyManager.populateRefinementElement");
            }
            super.populateRefinementElement(pItem, pRefineElement);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastRefinementElementPropertyManager.populateRefinementElement");
            }
        }
    }

    /* (non-Javadoc)
     * @see atg.repository.search.refinement.admin.RefinementElementPropertyManager#populateRepositoryItem(atg.repository.search.refinement.admin.RefinementElement, atg.repository.MutableRepositoryItem)
     */
    @Override public void populateRepositoryItem(RefinementElement pRefineElement, MutableRepositoryItem pItem)
                                          throws RepositoryException, IllegalArgumentException {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastRefinementElementPropertyManager.populateRepositoryItem");
            }
            super.populateRepositoryItem(pRefineElement, pItem);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastRefinementElementPropertyManager.populateRepositoryItem");
            }
        }
    }

}
