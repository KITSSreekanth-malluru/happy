package com.castorama.search.test;

import atg.commerce.search.refinement.admin.CommerceRefinementConfiguration;
import atg.commerce.search.refinement.admin.CustomRefinementConfiguration;
import atg.commerce.search.refinement.admin.CustomRefinementConfigurationPropertyManager;

import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.repository.search.refinement.admin.RefinementConfiguration;

import atg.service.perfmonitor.PerformanceMonitor;

/**
 * Extension for adding performance monitors.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastCustomRefinementConfigurationPropertyManager extends CustomRefinementConfigurationPropertyManager {
    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.admin.CustomRefinementConfigurationPropertyManager#createNewCustomRefinementConfiguration()
     */
    @Override public CustomRefinementConfiguration createNewCustomRefinementConfiguration() {
        CustomRefinementConfiguration crc = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastCustomRefinementConfigurationPropertyManager.createNewCustomRefinementConfiguration");
            }
            crc = super.createNewCustomRefinementConfiguration();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastCustomRefinementConfigurationPropertyManager.createNewCustomRefinementConfiguration");
            }
        }
        return crc;
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.admin.CustomRefinementConfigurationPropertyManager#populateCustomRefinementConfiguration(atg.repository.RepositoryItem, atg.commerce.search.refinement.admin.CustomRefinementConfiguration)
     */
    @Override public void populateCustomRefinementConfiguration(RepositoryItem pItem,
                                                                CustomRefinementConfiguration pRefinementConfiguration)
                                                         throws RepositoryException, IllegalArgumentException {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastCustomRefinementConfigurationPropertyManager.populateCustomRefinementConfiguration");
            }
            super.populateCustomRefinementConfiguration(pItem, pRefinementConfiguration);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastCustomRefinementConfigurationPropertyManager.populateCustomRefinementConfiguration");
            }
        }
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.admin.CustomRefinementConfigurationPropertyManager#populateRepositoryItem(atg.commerce.search.refinement.admin.CustomRefinementConfiguration, atg.repository.MutableRepositoryItem)
     */
    @Override public void populateRepositoryItem(CustomRefinementConfiguration pRefinementConfiguration,
                                                 MutableRepositoryItem pItem) throws RepositoryException,
                                                                                     IllegalArgumentException {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastCustomRefinementConfigurationPropertyManager.populateRepositoryItem");
            }
            super.populateRepositoryItem(pRefinementConfiguration, pItem);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastCustomRefinementConfigurationPropertyManager.populateRepositoryItem");
            }
        }
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.admin.CommerceRefinementConfigurationPropertyManager#createNewCommerceRefinementConfiguration()
     */
    @Override public CommerceRefinementConfiguration createNewCommerceRefinementConfiguration() {
        CommerceRefinementConfiguration crc = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastCustomRefinementConfigurationPropertyManager.createNewCommerceRefinementConfiguration");
            }
            crc = super.createNewCommerceRefinementConfiguration();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastCustomRefinementConfigurationPropertyManager.createNewCommerceRefinementConfiguration");
            }
        }
        return crc;
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.admin.CommerceRefinementConfigurationPropertyManager#populateCommerceRefinementConfiguration(atg.repository.RepositoryItem, atg.commerce.search.refinement.admin.CommerceRefinementConfiguration)
     */
    @Override public void populateCommerceRefinementConfiguration(RepositoryItem pItem,
                                                                  CommerceRefinementConfiguration pRefinementConfiguration)
                                                           throws RepositoryException, IllegalArgumentException {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastCustomRefinementConfigurationPropertyManager.populateCommerceRefinementConfiguration");
            }
            super.populateCommerceRefinementConfiguration(pItem, pRefinementConfiguration);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastCustomRefinementConfigurationPropertyManager.populateCommerceRefinementConfiguration");
            }
        }
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.admin.CommerceRefinementConfigurationPropertyManager#populateRepositoryItem(atg.commerce.search.refinement.admin.CommerceRefinementConfiguration, atg.repository.MutableRepositoryItem)
     */
    @Override public void populateRepositoryItem(CommerceRefinementConfiguration pRefinementConfiguration,
                                                 MutableRepositoryItem pItem) throws RepositoryException,
                                                                                     IllegalArgumentException {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastCustomRefinementConfigurationPropertyManager.populateRepositoryItem");
            }
            super.populateRepositoryItem(pRefinementConfiguration, pItem);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastCustomRefinementConfigurationPropertyManager.populateRepositoryItem");
            }
        }
    }

    /* (non-Javadoc)
     * @see atg.repository.search.refinement.admin.RefinementConfigurationPropertyManager#checkItem(atg.repository.RepositoryItem)
     */
    @Override protected void checkItem(RepositoryItem pItem) throws RepositoryException, IllegalArgumentException {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastCustomRefinementConfigurationPropertyManager.checkItem");
            }
            super.checkItem(pItem);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastCustomRefinementConfigurationPropertyManager.checkItem");
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
                                                  "TEST:CastCustomRefinementConfigurationPropertyManager.createNewRefinementConfiguration");
            }
            rc = super.createNewRefinementConfiguration();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastCustomRefinementConfigurationPropertyManager.createNewRefinementConfiguration");
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
                                                  "TEST:CastCustomRefinementConfigurationPropertyManager.populateRefinementConfiguration");
            }
            super.populateRefinementConfiguration(pItem, pRefineConfig);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastCustomRefinementConfigurationPropertyManager.populateRefinementConfiguration");
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
                                                  "TEST:CastCustomRefinementConfigurationPropertyManager.populateRepositoryItem");
            }
            super.populateRepositoryItem(pRefineConfig, pItem);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastCustomRefinementConfigurationPropertyManager.populateRepositoryItem");
            }
        }
    }

}
