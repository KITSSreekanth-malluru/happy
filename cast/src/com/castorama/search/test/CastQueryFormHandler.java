package com.castorama.search.test;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.search.client.SearchClientException;
import atg.search.client.SearchSession;

import atg.search.formhandlers.QueryFormHandler;

import atg.search.routing.command.search.SearchRequest;
import atg.search.routing.command.search.SearchResponse;

import atg.service.perfmonitor.PerformanceMonitor;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * Extension for adding performance monitors.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastQueryFormHandler extends QueryFormHandler {
    /*
     * (non-Javadoc)
     *
     * @see atg.search.formhandlers.QueryFormHandler#beforeSearch(atg.search.routing.command.search.SearchRequest,
     *      atg.search.client.SearchSession)
     */
    @Override protected SearchRequest beforeSearch(SearchRequest pRequest, SearchSession pSession) {
        SearchRequest inRequest = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastQueryFormHandler.beforeSearch");
            }
            inRequest = super.beforeSearch(pRequest, pSession);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastQueryFormHandler.beforeSearch");
            }
        }
        return inRequest;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.search.formhandlers.PagedRequestFormHandler#handleGoToPage(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse)
     */
    @Override public boolean handleGoToPage(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                     throws ServletException, IOException {
        boolean result = false;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastQueryFormHandler.handleGoToPage");
            }
            result = super.handleGoToPage(pRequest, pResponse);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastQueryFormHandler.handleGoToPage");
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.search.formhandlers.BaseSearchFormHandler#afterSearch(atg.search.routing.command.search.SearchRequest,
     *      atg.search.routing.command.search.SearchResponse, atg.search.client.SearchSession)
     */
    @Override protected SearchResponse afterSearch(SearchRequest pRequest, SearchResponse pResults,
                                                   SearchSession pSession) {
        SearchResponse sr = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastQueryFormHandler.afterSearch");
            }
            sr = super.afterSearch(pRequest, pResults, pSession);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastQueryFormHandler.afterSearch");
            }
        }
        return sr;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.search.formhandlers.BaseSearchFormHandler#createSearchSession(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse)
     */
    @Override public SearchSession createSearchSession(DynamoHttpServletRequest pRequest,
                                                       DynamoHttpServletResponse pResponse) {
        SearchSession ss = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastQueryFormHandler.createSearchSession");
            }
            ss = super.createSearchSession(pRequest, pResponse);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastQueryFormHandler.createSearchSession");
            }
        }
        return ss;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.search.formhandlers.BaseSearchFormHandler#doRedirect(atg.servlet.DynamoHttpServletRequest,
     *      java.lang.String)
     */
    @Override protected boolean doRedirect(DynamoHttpServletRequest pRequest, String pUrl) throws IOException {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastQueryFormHandler.doRedirect");
            }
            return super.doRedirect(pRequest, pUrl);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastQueryFormHandler.doRedirect");
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.search.formhandlers.BaseSearchFormHandler#handleClearForm(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse)
     */
    @Override public boolean handleClearForm(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                      throws ServletException, IOException {
        boolean hcf = false;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastQueryFormHandler.handleClearForm");
            }
            hcf = super.handleClearForm(pRequest, pResponse);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastQueryFormHandler.handleClearForm");
            }
        }
        return hcf;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.search.formhandlers.BaseSearchFormHandler#handleConnect(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse)
     */
    @Override public boolean handleConnect(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                    throws IOException, ServletException {
        boolean hc = false;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastQueryFormHandler.handleConnect");
            }
            hc = super.handleConnect(pRequest, pResponse);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastQueryFormHandler.handleConnect");
            }
        }
        return hc;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.search.formhandlers.BaseSearchFormHandler#handleErrorCondition(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse, java.lang.Exception)
     */
    @Override protected boolean handleErrorCondition(DynamoHttpServletRequest pRequest,
                                                     DynamoHttpServletResponse pResponse, Exception pException)
                                              throws IOException, ServletException {
        boolean hec = false;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastQueryFormHandler.handleErrorCondition");
            }
            hec = super.handleErrorCondition(pRequest, pResponse, pException);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastQueryFormHandler.handleErrorCondition");
            }
        }
        return hec;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.search.formhandlers.BaseSearchFormHandler#handleLoad(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse)
     */
    @Override public boolean handleLoad(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                 throws IOException, ServletException {
        boolean hl = false;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastQueryFormHandler.handleLoad");
            }
            hl = super.handleLoad(pRequest, pResponse);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastQueryFormHandler.handleLoad");
            }
        }
        return hl;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.search.formhandlers.BaseSearchFormHandler#handleSearch(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse)
     */
    @Override public boolean handleSearch(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                   throws IOException, ServletException {
        boolean hs = false;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastQueryFormHandler.handleSearch");
            }
            hs = super.handleSearch(pRequest, pResponse);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastQueryFormHandler.handleSearch");
            }
        }
        return hs;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.search.formhandlers.BaseSearchFormHandler#isForceDeployedOnlyAssets()
     */
    @Override public boolean isForceDeployedOnlyAssets() {
        boolean fdoa = false;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastQueryFormHandler.isForceDeployedOnlyAssets");
            }
            fdoa = super.isForceDeployedOnlyAssets();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastQueryFormHandler.isForceDeployedOnlyAssets");
            }
        }
        return fdoa;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.search.formhandlers.BaseSearchFormHandler#isPageRequest()
     */
    @Override public boolean isPageRequest() {
        boolean pr = false;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastQueryFormHandler.isPageRequest");
            }
            pr = super.isPageRequest();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastQueryFormHandler.isPageRequest");
            }
        }
        return pr;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.search.formhandlers.BaseSearchFormHandler#isRedirectEnabled()
     */
    @Override public boolean isRedirectEnabled() {
        boolean re = false;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastQueryFormHandler.isRedirectEnabled");
            }
            re = super.isRedirectEnabled();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastQueryFormHandler.isRedirectEnabled");
            }
        }
        return re;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.search.formhandlers.BaseSearchFormHandler#isShowDetailedResults()
     */
    @Override public boolean isShowDetailedResults() {
        boolean sdr = false;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastQueryFormHandler.isShowDetailedResults");
            }
            sdr = super.isShowDetailedResults();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastQueryFormHandler.isShowDetailedResults");
            }
        }
        return sdr;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.search.formhandlers.BaseSearchFormHandler#isShowXml()
     */
    @Override public boolean isShowXml() {
        boolean sx = false;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastQueryFormHandler.isShowXml");
            }
            sx = super.isShowXml();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastQueryFormHandler.isShowXml");
            }
        }
        return sx;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.search.formhandlers.BaseSearchFormHandler#search(atg.search.routing.command.search.SearchRequest,
     *      atg.search.client.SearchSession)
     */
    @Override protected SearchResponse search(SearchRequest pSearchRequest, SearchSession pSession)
                                       throws SearchClientException {
        SearchResponse sr = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastQueryFormHandler.search");
            }
            sr = super.search(pSearchRequest, pSession);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastQueryFormHandler.search");
            }
        }
        return sr;
    }

}
