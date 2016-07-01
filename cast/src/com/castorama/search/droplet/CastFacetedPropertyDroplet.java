package com.castorama.search.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.repository.search.refinement.FacetManager;

import atg.service.perfmonitor.PerformanceMonitor;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Returns faceted property name for using in jsp for formatted values.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastFacetedPropertyDroplet extends DynamoServlet {
    /*
     * Open parameter names
     */
    /** OUTPUT constant. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** ERROR constant. */
    public static final ParameterName ERROR = ParameterName.getParameterName("error");

    /** FACET_ID constant. */
    public static final String FACET_ID = "facetId";

    /** FACETED_PROPERTY constant. */
    public static final String FACETED_PROPERTY = "facetedProperty";

    /*
     * Properties
     */
    /** facetManager property */
    private FacetManager mFacetManager;

    /**
     * Returns facetManager property
     *
     * @return facetManager property
     */
    public FacetManager getFacetManager() {
        return mFacetManager;
    }

    /**
     * Sets facetManager property
     *
     * @param pFacetManager parameter to set
     */
    public void setFacetManager(FacetManager pFacetManager) {
        mFacetManager = pFacetManager;
    }

    /**
     * Returns faceted property name for using in jsp for formatted values
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
                PerformanceMonitor.startOperation("search", "TEST:CastFacetedPropertyDroplet.service");
            }
            String refinementId = getRefinementId(pRequest);
            FacetManager facetManager = getFacetManager();
            RepositoryItem re = null;

            try {
                re = facetManager.getRefineElementById(refinementId);
            } catch (RepositoryException e) {
                logError(e);
            }

            String propName = null;
            if (re != null) {
                propName =
                    (String) re.getPropertyValue(facetManager.getRefinementElementPropertyManager()
                                                 .getPropertyPropertyName());
            }
            pRequest.setParameter(FACETED_PROPERTY, propName);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetedPropertyDroplet.service");
            }
        }  // end try-finally

        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }

    /**
     * Retrieve refinement value from request
     *
     * @param  pDynamoHttpServletRequest servlet request
     *
     * @return Refinement object
     */
    private String getRefinementId(DynamoHttpServletRequest pDynamoHttpServletRequest) {
        String id = (String) pDynamoHttpServletRequest.getObjectParameter(FACET_ID);

        return id;
    }
}
