package com.castorama.search.droplet;

import java.io.IOException;

import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.repository.search.routing.beans.SearchEngine;
import atg.repository.search.routing.beans.SearchEngineServerState;
import atg.repository.search.routing.beans.SearchEnvironment;

import atg.search.routing.RoutingSystemService;
import atg.search.routing.SearchEngineServiceImpl;

import atg.service.perfmonitor.PerformanceMonitor;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Helper class for checking availability of search environment. If search
 * environment initialized then "initialized" open parameter is rendered,
 * otherwise "unavailable" parameter is rendered.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class SearchEnvironmentStatus extends DynamoServlet {
    /*
     * Open parameters
     */
    /** UNAVAILABLE constant. */
    public static final ParameterName UNAVAILABLE = ParameterName.getParameterName("unavailable");

    /** INITIALIZED constant. */
    public static final ParameterName INITIALIZED = ParameterName.getParameterName("initialized");

    /** SEARCH_ENVIRONMENT_TYPES constant. */
    public static String SEARCH_ENVIRONMENT_TYPES = "searchEnvironmentTypes";

    /*
     * Properties
     */
    /** searchEngineService property */
    private SearchEngineServiceImpl mSearchEngineService;

    /** searchTypeToEnvironmentName property */
    private Map<String, String> mSearchTypeToEnvironmentName;

    /**
     * Returns searchEngineService property.
     *
     * @return searchEngineService property.
     */
    public SearchEngineServiceImpl getSearchEngineService() {
        return mSearchEngineService;
    }

    /**
     * Sets the value of the searchEngineService property.
     *
     * @param pSearchEngineService parameter to set.
     */
    public void setSearchEngineService(SearchEngineServiceImpl pSearchEngineService) {
        mSearchEngineService = pSearchEngineService;
    }

    /**
     * Returns searchTypeToEnvironmentName property.
     *
     * @return searchTypeToEnvironmentName property.
     */
    public Map<String, String> getSearchTypeToEnvironmentName() {
        return mSearchTypeToEnvironmentName;
    }

    /**
     * Sets the value of the searchTypeToEnvironmentName property.
     *
     * @param pSearchTypeToEnvironmentName parameter to set.
     */
    public void setSearchTypeToEnvironmentName(Map<String, String> pSearchTypeToEnvironmentName) {
        mSearchTypeToEnvironmentName = pSearchTypeToEnvironmentName;
    }

    /**
     * Check whether search environment was initialized or not. This is useful
     * for use in jsps -> allows not to do calls to stopped search environment.
     * If search environment initialized then "initialized" open parameter is
     * rendered, otherwise "unavailable" parameter is rendered.
     *
     * @param  pRequest  dynamo http request
     * @param  pResponse dynamo http response
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:SearchEnvironmentStatus.service");
            }
            String searchEnvironmentTypesStr = pRequest.getParameter(SEARCH_ENVIRONMENT_TYPES);
            boolean searchEngineInitialized = false;

            if ((getSearchTypeToEnvironmentName() != null) && !getSearchTypeToEnvironmentName().isEmpty() &&
                    (getSearchEngineService() != null) && !StringUtils.isBlank(searchEnvironmentTypesStr)) {
                String[] searchEnvironmentTypes = searchEnvironmentTypesStr.split(":");
                if (searchEnvironmentTypes.length > 0) {
                    RoutingSystemService rss = getSearchEngineService().getRoutingSystemService();
                    if (rss != null) {
                        for (String seType : searchEnvironmentTypes) {
                            if (!StringUtils.isBlank(seType) &&
                                    !StringUtils.isBlank(getSearchTypeToEnvironmentName().get(seType))) {
                                SearchEnvironment sEnv =
                                    rss.getSearchEnvironmentByName(getSearchTypeToEnvironmentName().get(seType));
                                if (sEnv != null) {
                                    Collection<SearchEngine> searchEngines = rss.getSearchEnginesForEnvironment(sEnv);
                                    if ((searchEngines != null) && !searchEngines.isEmpty()) {
                                        for (SearchEngine se : searchEngines) {
                                            if (se != null && SearchEngineServerState.getInitialized().equals(se.getServerState())) {
                                                searchEngineInitialized = true;
                                                break;
                                            }
                                        }
                                        if (searchEngineInitialized) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }  // end for
                    }  // end if
                }  // end if
            }  // end if
            if (searchEngineInitialized) {
                pRequest.serviceLocalParameter(INITIALIZED, pRequest, pResponse);
            } else {
                pRequest.serviceLocalParameter(UNAVAILABLE, pRequest, pResponse);
            }
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:SearchEnvironmentStatus.service");
            }
        }  // end try-finally

    }

}
