package com.castorama.search.droplet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;

import atg.adapter.gsa.GSAItem;

import atg.nucleus.naming.ParameterName;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.repository.search.refinement.FacetSearchTools;

import atg.service.perfmonitor.PerformanceMonitor;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Returns correct facets order according to priority facets in the facet set or
 * at the refine config.
 *
 * @author Katsiaryna Sharstsiuk
 */

public class CastFacetPriorityDroplet extends DynamoServlet {
    /*
     * Open parameter names
     */
    /** OUTPUT constant. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** ERROR constant. */
    public static final ParameterName ERROR = ParameterName.getParameterName("error");

    /** REFINE_CONFIG constant. */
    public static final String REFINE_CONFIG = "refineConfig";

    /** ORDERED_FACETS_IDS constant. */
    public static final String ORDERED_FACETS_IDS = "orderedFacetsIds";

    /** REPOSITORY_PARAM constant. */
    public static final String REPOSITORY_PARAM = "repository";

    /** SEARCH_RESPONSE constant. */
    public static final String SEARCH_RESPONSE = "searchResponse";

    /** PRIORITY constant. */
    public static final String PRIORITY = "priority";

    /** IS_DOCUMENT constant. */
    public static final String IS_DOCUMENT = "isDocument";

    /** FACET_SET constant. */
    public static final String FACET_SET = "facetSet";

    /** CHILD_FACETS constant. */
    public static final String CHILD_FACETS = "childFacets";

    /** REFINE_ELEMENTS constant. */
    public static final String REFINE_ELEMENTS = "refineElements";

    /** GENERATED_REFINE_ELEMENTS constant. */
    public static final String GENERATED_REFINE_ELEMENTS = "generatedRefineElements";

    /*
     * Properties
     */
    /** facetSearchTools property */
    private FacetSearchTools mFacetSearchTools;

    /** defaultPriority property */
    private int mDefaultPriority = 0;

    /**
     * Returns facetSearchTools property.
     *
     * @return facetSearchTools property.
     */
    public FacetSearchTools getFacetSearchTools() {
        return mFacetSearchTools;
    }

    /**
     * Sets the value of the facetSearchTools property.
     *
     * @param pFacetSearchTools parameter to set.
     */
    public void setFacetSearchTools(FacetSearchTools pFacetSearchTools) {
        mFacetSearchTools = pFacetSearchTools;
    }

    /**
     * Returns defaultPriority property.
     *
     * @return defaultPriority property.
     */
    public int getDefaultPriority() {
        return mDefaultPriority;
    }

    /**
     * Sets the value of the defaultPriority property.
     *
     * @param pDefaultPriority parameter to set.
     */
    public void setDefaultPriority(int pDefaultPriority) {
        mDefaultPriority = pDefaultPriority;
    }

    /**
     * Returns correct facets order according to priority facets in the facet
     * set or at the refine config.
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
                PerformanceMonitor.startOperation("search", "TEST:CastFacetPriorityDroplet.service");
            }
            Object refineConfigObj = pRequest.getObjectParameter(REFINE_CONFIG);
            Object repository = pRequest.getObjectParameter(REPOSITORY_PARAM);

            if ((refineConfigObj instanceof GSAItem) &&
                    (((GSAItem) refineConfigObj).getPropertyValue(REFINE_ELEMENTS) != null)) {
                GSAItem refineConfig = (GSAItem) refineConfigObj;
                setFacetOrder(refineConfig, pRequest);
            } else if (refineConfigObj instanceof String) {
                if ((pRequest.getParameter(IS_DOCUMENT) != null) &&
                        Boolean.valueOf(pRequest.getParameter(IS_DOCUMENT))) {
                    if ((repository != null) && (repository instanceof Repository)) {
                        try {
                            RepositoryItem facetSet =
                                ((Repository) repository).getItem((String) refineConfigObj, FACET_SET);
                            if (facetSet != null) {
                                Set<GSAItem> refEls = (Set<GSAItem>) facetSet.getPropertyValue(CHILD_FACETS);
                                List orderedFacetsIds = new ArrayList();
                                for (GSAItem refEl : refEls) {
                                    orderedFacetsIds.add(refEl.getRepositoryId());
                                }
                                pRequest.setParameter(ORDERED_FACETS_IDS, orderedFacetsIds);

                            }
                        } catch (RepositoryException e) {
                            if (isLoggingError()) {
                                logError(e.getMessage());
                            }
                        }
                    }
                } else {
                    if ((repository != null) && (repository instanceof Repository)) {
                        GSAItem refineConfig;
                        try {
                            refineConfig =
                                (GSAItem) ((Repository) repository).getItem((String) refineConfigObj, REFINE_CONFIG);
                            setFacetOrder(refineConfig, pRequest);
                        } catch (RepositoryException e) {
                            if (isLoggingError()) {
                                logError(e.getMessage());
                            }
                        }

                    }
                }  // end if-else
            }  // end if-else
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetPriorityDroplet.service");
            }
        }  // end try-finally
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }

    /**
     * Sets output parameter orderedFacetsIds - ordered facet ids array(ordered
     * according to priority)
     *
     * @param pRefineConfig parameter
     * @param pRequest      parameter
     */
    private void setFacetOrder(GSAItem pRefineConfig, DynamoHttpServletRequest pRequest) {
        List<GSAItem> refEls = (List<GSAItem>) pRefineConfig.getPropertyValue(GENERATED_REFINE_ELEMENTS);

        if (refEls != null) {
            Set priorities = new HashSet();
            for (GSAItem refEl : refEls) {
                if (refEl.getPropertyValue(PRIORITY) != null) {
                    priorities.add((Integer) refEl.getPropertyValue(PRIORITY));
                } else {
                    priorities.add(getDefaultPriority());
                }
            }
            Object[] prioritiesArray = (Object[]) priorities.toArray();
            Arrays.sort(prioritiesArray);
            List orderedFacetsIds = new ArrayList();
            for (int i = 0; i < prioritiesArray.length; i++) {
                for (GSAItem refEl : refEls) {
                    if ((refEl.getPropertyValue(PRIORITY) != null) &&
                            prioritiesArray[i].equals(refEl.getPropertyValue(PRIORITY))) {
                        orderedFacetsIds.add(refEl.getRepositoryId());
                    } else if ((refEl.getPropertyValue(PRIORITY) == null) &&
                                   prioritiesArray[i].equals(Integer.valueOf(getDefaultPriority()))) {
                        orderedFacetsIds.add(refEl.getRepositoryId());
                    }
                }
            }
            pRequest.setParameter(ORDERED_FACETS_IDS, orderedFacetsIds);

        }  // end if
    }

}
