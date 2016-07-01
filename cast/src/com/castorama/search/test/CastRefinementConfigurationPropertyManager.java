package com.castorama.search.test;

import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.repository.search.refinement.admin.RefinementConfiguration;
import atg.repository.search.refinement.admin.RefinementConfigurationPropertyManager;

import atg.service.perfmonitor.PerformanceMonitor;

/**
 * Extension for adding performance monitors.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastRefinementConfigurationPropertyManager extends RefinementConfigurationPropertyManager {
    /* (non-Javadoc)
     * @see atg.repository.search.refinement.admin.RefinementConfigurationPropertyManager#checkItem(atg.repository.RepositoryItem)
     */
    @Override protected void checkItem(RepositoryItem pItem) throws RepositoryException, IllegalArgumentException {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastRefinementConfigurationPropertyManager.checkItem");
            }
            super.checkItem(pItem);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastRefinementConfigurationPropertyManager.checkItem");
            }
        }
    }

    /* (non-Javadoc)
     * @see atg.repository.search.refinement.admin.RefinementConfigurationPropertyManager#createNewRefinementConfiguration()
     */
    @Override public RefinementConfiguration createNewRefinementConfiguration() {
        RefinementConfiguration rc = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastRefinementConfigurationPropertyManager.createNewRefinementConfiguration");
            }
            rc = super.createNewRefinementConfiguration();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastRefinementConfigurationPropertyManager.createNewRefinementConfiguration");
            }
        }
        return rc;
    }

    /* (non-Javadoc)
     * @see atg.repository.search.refinement.admin.RefinementConfigurationPropertyManager#populateRefinementConfiguration(atg.repository.RepositoryItem, atg.repository.search.refinement.admin.RefinementConfiguration)
     */
    @Override public void populateRefinementConfiguration(RepositoryItem pItem, RefinementConfiguration pRefineConfig)
                                                   throws RepositoryException, IllegalArgumentException {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastRefinementConfigurationPropertyManager.populateRefinementConfiguration");
            }
            super.populateRefinementConfiguration(pItem, pRefineConfig);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastRefinementConfigurationPropertyManager.populateRefinementConfiguration");
            }
        }
    }

    /* (non-Javadoc)
     * @see atg.repository.search.refinement.admin.RefinementConfigurationPropertyManager#populateRepositoryItem(atg.repository.search.refinement.admin.RefinementConfiguration, atg.repository.MutableRepositoryItem)
     */
    @Override public void populateRepositoryItem(RefinementConfiguration pRefineConfig, MutableRepositoryItem pItem)
                                          throws RepositoryException, IllegalArgumentException {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastRefinementConfigurationPropertyManager.populateRepositoryItem");
            }
            super.populateRepositoryItem(pRefineConfig, pItem);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastRefinementConfigurationPropertyManager.populateRepositoryItem");
            }
        }
    }
}
