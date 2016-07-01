package com.castorama.search.test;

import atg.search.client.SearchClientException;

import atg.search.query.config.SearchConfigurationRedirectProcessor;

import atg.search.routing.command.search.SearchResponse;

import atg.service.perfmonitor.PerformanceMonitor;

import atg.servlet.DynamoHttpServletRequest;

/**
 * Extension for adding performance monitors.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastSearchConfigurationRedirectProcessor extends SearchConfigurationRedirectProcessor {
    /*
     * (non-Javadoc)
     *
     * @see atg.search.query.config.SearchConfigurationRedirectProcessor#getRedirect(atg.search.routing.command.search.SearchResponse)
     */
    @Override public String getRedirect(SearchResponse pSearchResponse) {
        String redirect = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:SearchConfigurationRedirectProcessor.getRedirect");
            }
            redirect = super.getRedirect(pSearchResponse);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:SearchConfigurationRedirectProcessor.getRedirect");
            }
        }
        return redirect;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.search.query.config.SearchConfigurationRedirectProcessor#handleAutoRedirect(atg.search.routing.command.search.SearchResponse,
     *      atg.servlet.DynamoHttpServletRequest, java.lang.Object)
     */
    @Override public boolean handleAutoRedirect(SearchResponse pSearchResponse, DynamoHttpServletRequest pDynamoRequest,
                                                Object pCallback) throws SearchClientException {
        boolean ar = false;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:SearchConfigurationRedirectProcessor.handleAutoRedirect");
            }
            ar = super.handleAutoRedirect(pSearchResponse, pDynamoRequest, pCallback);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:SearchConfigurationRedirectProcessor.handleAutoRedirect");
            }
        }
        return ar;
    }

}
