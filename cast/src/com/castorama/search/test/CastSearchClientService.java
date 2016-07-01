package com.castorama.search.test;

import java.util.Collection;

import javax.servlet.Servlet;

import atg.nucleus.ServiceException;

import atg.search.client.SearchClientException;
import atg.search.client.SearchClientService;
import atg.search.client.SearchSession;

import atg.search.routing.command.search.BrowseRequest;
import atg.search.routing.command.search.CategorizeRequest;
import atg.search.routing.command.search.QueryRequest;
import atg.search.routing.command.search.SearchRequest;
import atg.search.routing.command.search.SearchResponse;
import atg.search.routing.command.search.SimilarDocsRequest;
import atg.search.routing.command.search.StructuredQueryRequest;
import atg.search.routing.command.search.ViewDocumentRequest;
import atg.search.routing.command.search.BrowseRequest.Response;

import atg.service.perfmonitor.PerformanceMonitor;

/**
 * Extension for adding performance monitors.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastSearchClientService extends SearchClientService {
    /* (non-Javadoc)
     * @see atg.search.client.SearchClientService#beginSession()
     */
    @Override public SearchSession beginSession() throws SearchClientException {
        SearchSession ss = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastSearchClientService.createDocument");
            }
            ss = super.beginSession();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastSearchClientService.createDocument");
            }
        }
        return ss;
    }

    /* (non-Javadoc)
     * @see atg.search.client.SearchClientService#createAdminServlet()
     */
    @Override protected Servlet createAdminServlet() {
        Servlet s = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastSearchClientService.createDocument");
            }
            s = super.createAdminServlet();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastSearchClientService.createDocument");
            }
        }
        return s;

    }

    /* (non-Javadoc)
     * @see atg.search.client.SearchClientService#doStartService()
     */
    @Override public void doStartService() throws ServiceException {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastSearchClientService.createDocument");
            }
            super.doStartService();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastSearchClientService.createDocument");
            }
        }
    }

    /* (non-Javadoc)
     * @see atg.search.client.SearchClientService#doStopService()
     */
    @Override public void doStopService() throws ServiceException {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastSearchClientService.createDocument");
            }
            super.doStopService();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastSearchClientService.createDocument");
            }
        }
    }

    /* (non-Javadoc)
     * @see atg.search.client.SearchClientService#getSearchEnvironmentTargets()
     */
    @Override public Collection getSearchEnvironmentTargets() throws SearchClientException {
        Collection c = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastSearchClientService.createDocument");
            }
            c = super.getSearchEnvironmentTargets();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastSearchClientService.createDocument");
            }
        }
        return c;
    }

    /* (non-Javadoc)
     * @see atg.search.client.SearchClientService#reset()
     */
    @Override public void reset() {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastSearchClientService.reset");
            }
            super.reset();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastSearchClientService.reset");
            }
        }
    }

    /* (non-Javadoc)
     * @see atg.search.client.SearchClientService#search(atg.search.routing.command.search.BrowseRequest, atg.search.client.SearchSession)
     */
    @Override public Response search(BrowseRequest pRequest, SearchSession pSearchSession)
                              throws SearchClientException {
        Response r = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastSearchClientService.search:BrowseRequest");
            }
            r = super.search(pRequest, pSearchSession);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastSearchClientService.search:BrowseRequest");
            }
        }
        return r;
    }

    /* (non-Javadoc)
     * @see atg.search.client.SearchClientService#search(atg.search.routing.command.search.CategorizeRequest, atg.search.client.SearchSession)
     */
    @Override public atg.search.routing.command.search.CategorizeRequest.Response search(CategorizeRequest pRequest,
                                                                                         SearchSession pSearchSession)
                                                                                  throws SearchClientException {
        atg.search.routing.command.search.CategorizeRequest.Response resp = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastSearchClientService.search:CategorizeRequest");
            }
            resp = super.search(pRequest, pSearchSession);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastSearchClientService.search:CategorizeRequest");
            }
        }
        return resp;
    }

    /* (non-Javadoc)
     * @see atg.search.client.SearchClientService#search(atg.search.routing.command.search.QueryRequest, atg.search.client.SearchSession)
     */
    @Override public atg.search.routing.command.search.QueryRequest.Response search(QueryRequest pRequest,
                                                                                    SearchSession pSearchSession)
                                                                             throws SearchClientException {
        atg.search.routing.command.search.QueryRequest.Response resp = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastSearchClientService.search:QueryRequest");
            }
            resp = super.search(pRequest, pSearchSession);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastSearchClientService.search:QueryRequest");
            }
        }
        return resp;
    }

    /* (non-Javadoc)
     * @see atg.search.client.SearchClientService#search(atg.search.routing.command.search.SearchRequest, atg.search.client.SearchSession)
     */
    @Override public SearchResponse search(SearchRequest pRequest, SearchSession pSearchSession)
                                    throws SearchClientException {
        SearchResponse resp = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastSearchClientService.search:SearchRequest");
            }
            resp = super.search(pRequest, pSearchSession);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastSearchClientService.search:SearchRequest");
            }
        }
        return resp;
    }

    /* (non-Javadoc)
     * @see atg.search.client.SearchClientService#search(atg.search.routing.command.search.SimilarDocsRequest, atg.search.client.SearchSession)
     */
    @Override public atg.search.routing.command.search.SimilarDocsRequest.Response search(SimilarDocsRequest pRequest,
                                                                                          SearchSession pSearchSession)
                                                                                   throws SearchClientException {
        atg.search.routing.command.search.SimilarDocsRequest.Response resp = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastSearchClientService.search:SimilarDocsRequest");
            }
            resp = super.search(pRequest, pSearchSession);

        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastSearchClientService.search:SimilarDocsRequest");
            }
        }
        return resp;
    }

    /* (non-Javadoc)
     * @see atg.search.client.SearchClientService#search(atg.search.routing.command.search.StructuredQueryRequest, atg.search.client.SearchSession)
     */
    @Override public atg.search.routing.command.search.StructuredQueryRequest.Response search(StructuredQueryRequest pRequest,
                                                                                              SearchSession pSearchSession)
                                                                                       throws SearchClientException {
        atg.search.routing.command.search.StructuredQueryRequest.Response resp = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastSearchClientService.search:StructuredQueryRequest");
            }
            resp = super.search(pRequest, pSearchSession);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastSearchClientService.search:StructuredQueryRequest");
            }
        }
        return resp;
    }

    /* (non-Javadoc)
     * @see atg.search.client.SearchClientService#search(atg.search.routing.command.search.ViewDocumentRequest, atg.search.client.SearchSession)
     */
    @Override public atg.search.routing.command.search.ViewDocumentRequest.Response search(ViewDocumentRequest pRequest,
                                                                                           SearchSession pSearchSession)
                                                                                    throws SearchClientException {
        atg.search.routing.command.search.ViewDocumentRequest.Response resp = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastSearchClientService.search:ViewDocumentRequest");
            }
            resp = super.search(pRequest, pSearchSession);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastSearchClientService.search:ViewDocumentRequest");
            }
        }
        return resp;
    }

}
