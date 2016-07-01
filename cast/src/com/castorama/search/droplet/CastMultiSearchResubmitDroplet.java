package com.castorama.search.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import atg.commerce.search.refinement.CommerceFacetSearchTools;
import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.search.client.SearchClient;
import atg.search.client.SearchClientException;
import atg.search.client.SearchSession;
import atg.search.events.SearchMessageTools;
import atg.search.formhandlers.QueryFormHandler;
import atg.search.formhandlers.SearchContext;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.commerce.profile.SessionBean;

/**
 * Used for multi - search option. Run search for all search types and save
 * results to the session bean.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastMultiSearchResubmitDroplet extends DynamoServlet {
    /*
     * Open parameters
     */
    /** OUTPUT constant. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** ERROR constant. */
    public static final ParameterName ERROR = ParameterName.getParameterName("error");

    /** PAGE_NUM constant. */
    public static final String PAGE_NUM = "pageNum";

    /** CURRENT_TAB constant. */
    public static final String CURRENT_TAB = "currentTab";

    /** SEARCH_RESPONSE constant. */
    public static final String SEARCH_RESPONSE = "searchResponse";

    /** PRODUCT_LISTING_VIEW constant. */
    public static final String PRODUCT_LISTING_VIEW = "productListingView";

    /** SEARCH_TYPE constant. */
    public static final String SEARCH_TYPE = "searchType";

    /** PREVIOUS_SEARCH_TYPE constant. */
    public static final String PREVIOUS_SEARCH_TYPE = "previousSearchType";

    /** SORT_BY_VALUE constant. */
    public static final String SORT_BY_VALUE = "sortByValue";

    /*
     * Properties
     */
    /** facetedSearchSessionBeanPath property */
    private String mFacetedSearchSessionBeanPath;

    /** tabToQFHPathName property */
    private Map<String, String> mTabToQFHPathName;

    /** tabToResponseParameterQFHMap property */
    private Map<String, String> mTabToResponseParameterQFHMap;

    /** tabToFSTPathName property */
    private Map<String, String> mTabToFSTPathName;

    /** tabToResponseParameterFSTMap property */
    private Map<String, String> mTabToResponseParameterFSTMap;

    /** mForkExecution property */
    private boolean mForkExecution = true;

    /** FORK_STRATEGY property */
    private final MultiSearchExecutionStrategy FORK_STRATEGY = new ForkMultiSearchExecutionStrategy();

    /** SEQUENTIAL_STRATEGY property */
    private final MultiSearchExecutionStrategy SEQUENTIAL_STRATEGY = new SequentialMultiSearchExecutionStrategy();

    /**
     * Returns facetedSearchSessionBeanPath property.
     *
     * @return facetedSearchSessionBeanPath property.
     */
    public String getFacetedSearchSessionBeanPath() {
        return mFacetedSearchSessionBeanPath;
    }

    /**
     * Sets the value of the facetedSearchSessionBeanPath property.
     *
     * @param pFacetedSearchSessionBeanPath parameter to set.
     */
    public void setFacetedSearchSessionBeanPath(String pFacetedSearchSessionBeanPath) {
        mFacetedSearchSessionBeanPath = pFacetedSearchSessionBeanPath;
    }

    /**
     * Returns tabToQFHPathName property.
     *
     * @return tabToQFHPathName property.
     */
    public Map<String, String> getTabToQFHPathName() {
        return mTabToQFHPathName;
    }

    /**
     * Sets the value of the tabToQFHPathName property.
     *
     * @param pTabToQFHPathName parameter to set.
     */
    public void setTabToQFHPathName(Map<String, String> pTabToQFHPathName) {
        mTabToQFHPathName = pTabToQFHPathName;
    }

    /**
     * Returns tabToResponseParameterQFHMap property.
     *
     * @return tabToResponseParameterQFHMap property.
     */
    public Map<String, String> getTabToResponseParameterQFHMap() {
        return mTabToResponseParameterQFHMap;
    }

    /**
     * Sets the value of the tabToResponseParameterQFHMap property.
     *
     * @param pTabToResponseParameterQFHMap parameter to set.
     */
    public void setTabToResponseParameterQFHMap(Map<String, String> pTabToResponseParameterQFHMap) {
        mTabToResponseParameterQFHMap = pTabToResponseParameterQFHMap;
    }

    /**
     * Returns tabToFSTPathName property.
     *
     * @return tabToFSTPathName property.
     */
    public Map<String, String> getTabToFSTPathName() {
        return mTabToFSTPathName;
    }

    /**
     * Sets the value of the tabToFSTPathName property.
     *
     * @param pTabToFSTPathName parameter to set.
     */
    public void setTabToFSTPathName(Map<String, String> pTabToFSTPathName) {
        mTabToFSTPathName = pTabToFSTPathName;
    }

    /**
     * Returns tabToResponseParameterFSTMap property.
     *
     * @return tabToResponseParameterFSTMap property.
     */
    public Map<String, String> getTabToResponseParameterFSTMap() {
        return mTabToResponseParameterFSTMap;
    }

    /**
     * Sets the value of the tabToResponseParameterFSTMap property.
     *
     * @param pTabToResponseParameterFSTMap parameter to set.
     */
    public void setTabToResponseParameterFSTMap(Map<String, String> pTabToResponseParameterFSTMap) {
        mTabToResponseParameterFSTMap = pTabToResponseParameterFSTMap;
    }

    /**
     * Returns multi - faceted search results on search results page(Used for
     * "multi-search" search type). Run search for all types(except cases when
     * facet's values are selected on one tab -> if so then only search for this
     * tab re-run, for all other cases search doesn't re-run - other tabs show
     * results from session bean). Save results at the session bean. Run search
     * maximum 3 times - for all type of search. Run search in separate threads.
     *
     * @param  pRequest  dynamo http request
     * @param  pResponse dynamo http response
     *
     * @throws ServletException
     * @throws IOException
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastMultiSearchResubmitDroplet");
            }

            //SessionBean sb = (SessionBean) pRequest.resolveName(getFacetedSearchSessionBeanPath().trim());
            if ((getTabToQFHPathName() != null) && !getTabToQFHPathName().isEmpty()) {
                String currentTab = pRequest.getParameter(CURRENT_TAB);
                String pageNum = pRequest.getParameter(PAGE_NUM);
                if ((pageNum == null) || atg.core.util.StringUtils.isBlank(pageNum)) {
                    pageNum = "1";
                }
                Integer pageNumInt = Integer.valueOf(pageNum);

                List<Runnable> operations = new ArrayList<Runnable>();
                String queryFHComponentPath = null;
                if (!StringUtils.isBlank(currentTab)) {
                    queryFHComponentPath = getTabToQFHPathName().get(currentTab.trim());
                    if (queryFHComponentPath != null) {
                        QueryFormHandler qfh = (QueryFormHandler) pRequest.resolveName(queryFHComponentPath);
                        if (qfh != null) {
                            qfh.setGoToPage(pageNumInt);
                            operations.add(new CastMultiSearchResubmitOperation(qfh, pRequest, pResponse));
                        }
                    }
                } else {
                    java.util.Collection<String> searchViews =
                        (java.util.Collection<String>) getTabToQFHPathName().values();
                    for (String qfhPathName : searchViews) {
                        if (qfhPathName != null) {
                            QueryFormHandler qfh = (QueryFormHandler) pRequest.resolveName(qfhPathName);
                            if (qfh != null) {
                                qfh.setGoToPage(pageNumInt);
                                operations.add(new CastMultiSearchResubmitOperation(qfh, pRequest, pResponse));
                            }
                        }

                    }
                }  // end if-else

                getExecutionStrategy().execute(operations);

                saveRequestValuesToSessionBean(pRequest);
            }  // end if
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastMultiSearchResubmitDroplet");
            }
        }  // end try-finally
    }

    /**
     * Returns executionStrategy property.
     *
     * @return executionStrategy property.
     */
    protected MultiSearchExecutionStrategy getExecutionStrategy() {
        if (isForkExecution()) {
            return FORK_STRATEGY;
        } else {
            return SEQUENTIAL_STRATEGY;
        }
    }

    /**
     * Save request values to session bean.
     *
     * @param pRequest parameter
     */
    private void saveRequestValuesToSessionBean(DynamoHttpServletRequest pRequest) {
        SessionBean sb = (SessionBean) pRequest.resolveName(getFacetedSearchSessionBeanPath().trim());
        if (sb != null) {
            if ((getTabToResponseParameterQFHMap() != null) && !getTabToResponseParameterQFHMap().isEmpty()) {
                for (Map.Entry<String, String> e : getTabToResponseParameterQFHMap().entrySet()) {
                    if (!StringUtils.isBlank(e.getKey()) && !StringUtils.isBlank(e.getValue()) &&
                            !StringUtils.isBlank(getTabToQFHPathName().get(e.getKey()))) {
                        QueryFormHandler qfh =
                            (QueryFormHandler) pRequest.resolveName(getTabToQFHPathName().get(e.getKey()));
                        if ((qfh != null) && (qfh.getSearchResponse() != null))  // qfh is request scope component so put
                                                                                 // it into map

                        // only actual updates for FH values
                        // will be put, check for currentTab value is unneccassary
                        {
                            sb.getValues().put(e.getValue(), qfh);
                            sb.getValues().put(PREVIOUS_SEARCH_TYPE, pRequest.getParameter(SEARCH_TYPE));
                            CommerceFacetSearchTools fst =
                                (CommerceFacetSearchTools) pRequest.resolveName(getTabToFSTPathName().get(e.getKey()));
                            if (fst != null) {
                                sb.getValues().put(getTabToResponseParameterFSTMap().get(e.getKey()), fst);
                            }
                        }
                    }

                }  // end for
                if (!StringUtils.isBlank(pRequest.getParameter(PAGE_NUM))) {
                    sb.getValues().put(PAGE_NUM, pRequest.getParameter(PAGE_NUM));
                }

                sb.getValues().put(PRODUCT_LISTING_VIEW, pRequest.getParameter(PRODUCT_LISTING_VIEW));
                sb.getValues().put(SEARCH_TYPE, pRequest.getParameter(SEARCH_TYPE));
                sb.getValues().put(SORT_BY_VALUE, pRequest.getParameter(SORT_BY_VALUE));
                sb.getValues().put(CURRENT_TAB, pRequest.getParameter(CURRENT_TAB));
            }  // end if
        }  // end if
    }

    /**
     * Returns forkExecution property.
     *
     * @return forkExecution property.
     */
    public boolean isForkExecution() {
        return mForkExecution;
    }

    /**
     * Sets the value of the forkExecution property.
     *
     * @param pForkExecution parameter to set.
     */
    public void setForkExecution(boolean pForkExecution) {
        mForkExecution = pForkExecution;
    }

    /**
     * Interface for multi search startegy
     *
     * @author  Mikalai Khatsko
     */
    interface MultiSearchExecutionStrategy {
        /**
         *
         * @param pOperations
         */
        void execute(List<Runnable> pOperations);
    }

    /**
     * Implementation MultiSearchExecutionStrategy for FORK mode
     *
     * @author  Mikalai Khatsko
     */
    class ForkMultiSearchExecutionStrategy implements MultiSearchExecutionStrategy {
        /**
         * Execute all searches in FORK mode 
         *
         * @param pOperations parameter to set
         */
        public void execute(List<Runnable> pOperations) {
            if (isLoggingDebug()) {
                logDebug("ForkMultiSearchExecutionStrategy.execute("+pOperations+") is started.");
            }
            List<Thread> threads = new ArrayList<Thread>();

            for (Runnable runnable : pOperations) {
                threads.add(new Thread(runnable));
            }

            for (Thread t : threads) {
                t.start();
            }

            for (Thread t : threads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    if (isLoggingError()) {
                        logError(e.getMessage());
                    }
                }
            }
            if (isLoggingDebug()) {
                logDebug("ForkMultiSearchExecutionStrategy.execute("+pOperations+") is ended.");
            }

        }
    }

    /**
     * Implementation MultiSearchExecutionStrategy for SEQUENCE mode
     *
     * @author  Mikalai Khatsko
     */
    class SequentialMultiSearchExecutionStrategy implements MultiSearchExecutionStrategy {
        /**
         * Execute all searches in SEQUENCE mode
         *
         * @param pOperations parameter to set
         */
        public void execute(List<Runnable> pOperations) {
            if (isLoggingDebug()) {
                logDebug("SequentialMultiSearchExecutionStrategy.execute("+pOperations+") is started.");
            }
            for (Runnable runnable : pOperations) {
                runnable.run();
            }
            if (isLoggingDebug()) {
                logDebug("SequentialMultiSearchExecutionStrategy.execute("+pOperations+") is ended.");
            }
        }
    }

    /**
     * Inner class for representing CastMultiSearchResubmitThread object
     *
     * @author Mikalai Khatsko
     */
    class CastMultiSearchResubmitOperation implements Runnable {
        /** Properties */
        private QueryFormHandler mBaseSearchFH;

        /** mRequest property */
        private DynamoHttpServletRequest mRequest;

        /** mResponse property */
        private DynamoHttpServletResponse mResponse;

        /**
         * Constructor
         *
         * @param pBaseSearchFH parameter
         * @param pRequest      parameter
         * @param pResponse     parameter
         */

        public CastMultiSearchResubmitOperation(QueryFormHandler pBaseSearchFH, DynamoHttpServletRequest pRequest,
                                                DynamoHttpServletResponse pResponse) {
            mBaseSearchFH = pBaseSearchFH;
            mRequest = pRequest;
            mResponse = pResponse;
        }

        /**
         * Run search process.
         */
        public void run() {
            if ((mBaseSearchFH != null) && (mRequest != null) && (mResponse != null)) {
                SearchContext currentContext = mBaseSearchFH.getSearchContext();
                mBaseSearchFH.setSearchContext(new SearchContextWrapper(currentContext));
                try {
                    mBaseSearchFH.handleGoToPage(mRequest, mResponse);
                } catch (Throwable t) {
                    return;
                } finally {
                    return;
                }
            }
        }
    }
}

/**
 * Wrapper for SearchContext
 *
 * @author  Mikalai Khatsko
 */
class SearchContextWrapper extends SearchContext {
    /** mWrappedSearchContext property */
    private SearchContext mWrappedSearchContext;

    /**
     * Creates a new SearchContextWrapper object.
     *
     * @param pSearchContext 
     */
    public SearchContextWrapper(SearchContext pSearchContext) {
        mWrappedSearchContext = pSearchContext;
    }

    /**
     * Clear search sessions
     */
    @Override public void clearSearchSessions() {
        synchronized (mWrappedSearchContext) {
            mWrappedSearchContext.clearSearchSessions();
        }
    }

    /**
     * Returns failoverConnections property.
     *
     * @return failoverConnections property.
     */
    @Override public String[] getFailoverConnections() {
        return mWrappedSearchContext.getFailoverConnections();
    }

    /**
     * Returns maxAttemptsBeforeFailover property.
     *
     * @return maxAttemptsBeforeFailover property.
     */
    @Override public int getMaxAttemptsBeforeFailover() {
        return mWrappedSearchContext.getMaxAttemptsBeforeFailover();
    }

    /**
     * Returns primaryConnection property.
     *
     * @return primaryConnection property.
     */
    @Override public String getPrimaryConnection() {
        return mWrappedSearchContext.getPrimaryConnection();
    }

    /**
     * Returns searchClient property.
     *
     * @return searchClient property.
     *
     * @throws SearchClientException - exception
     */
    @Override public SearchClient getSearchClient() throws SearchClientException {
        return mWrappedSearchContext.getSearchClient();
    }

    /**
     * Returns searchMessageService property.
     *
     * @return searchMessageService property.
     */
    @Override public SearchMessageTools getSearchMessageService() {
        return mWrappedSearchContext.getSearchMessageService();
    }

    /**
     * Returns searchSession property.
     *
     * @param  pRequestChainToken parameter to set.
     *
     * @return searchSession property.
     */
    @Override public SearchSession getSearchSession(String pRequestChainToken) {
        synchronized (mWrappedSearchContext) {
            return mWrappedSearchContext.getSearchSession(pRequestChainToken);
        }
    }

    /**
     * Returns firingSearchEvents property.
     *
     * @return firingSearchEvents property.
     */
    @Override public boolean isFiringSearchEvents() {
        return mWrappedSearchContext.isFiringSearchEvents();
    }

    /**
     * Realization of synchronization for mapSearchSession
     *
     * @param  pRequestChainToken
     * @param  pSearchSession
     * @param  pPreviousRequestChainToken 
     *
     * @throws SearchClientException 
     */
    @Override public void mapSearchSession(String pRequestChainToken, SearchSession pSearchSession, String pPreviousRequestChainToken
                                           ) throws SearchClientException {
        synchronized (mWrappedSearchContext) {
            mWrappedSearchContext.mapSearchSession(pRequestChainToken, pSearchSession, pPreviousRequestChainToken);
        }
    }

    /**
     * Runs reconnect
     *
     * @throws SearchClientException
     */
    @Override public void reconnect() throws SearchClientException {
        mWrappedSearchContext.reconnect();
    }

    /**
     * Sets the value of the failoverConnections property.
     *
     * @param pFailoverConnections parameter to set.
     */
    @Override public void setFailoverConnections(String[] pFailoverConnections) {
        mWrappedSearchContext.setFailoverConnections(pFailoverConnections);
    }

    /**
     * Sets the value of the firingSearchEvents property.
     *
     * @param pFiringSearchEvents parameter to set.
     */
    @Override public void setFiringSearchEvents(boolean pFiringSearchEvents) {
        mWrappedSearchContext.setFiringSearchEvents(pFiringSearchEvents);
    }

    /**
     * Sets the value of the maxAttemptsBeforeFailover property.
     *
     * @param pMaxAttemptsBeforeFailover parameter to set.
     */
    @Override public void setMaxAttemptsBeforeFailover(int pMaxAttemptsBeforeFailover) {
        mWrappedSearchContext.setMaxAttemptsBeforeFailover(pMaxAttemptsBeforeFailover);
    }

    /**
     * Sets the value of the primaryConnection property.
     *
     * @param pPrimaryConnection parameter to set.
     */
    @Override public void setPrimaryConnection(String pPrimaryConnection) {
        mWrappedSearchContext.setPrimaryConnection(pPrimaryConnection);
    }

    /**
     * Sets the value of the searchMessageService property.
     *
     * @param pSearchMessageService parameter to set.
     */
    @Override public void setSearchMessageService(SearchMessageTools pSearchMessageService) {
        mWrappedSearchContext.setSearchMessageService(pSearchMessageService);
    }

    /**
     * Sets the value of the searchSession property.
     *
     * @param pSearchSession parameter to set.
     */
    @Override public void setSearchSession(SearchSession pSearchSession) {
        synchronized (mWrappedSearchContext) {
            mWrappedSearchContext.setSearchSession(pSearchSession);
        }
    }

}
