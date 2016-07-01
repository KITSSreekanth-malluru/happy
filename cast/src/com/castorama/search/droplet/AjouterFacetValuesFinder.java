package com.castorama.search.droplet;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.repository.search.refinement.FacetHolder;
import atg.repository.search.refinement.FacetSearchTools;
import atg.repository.search.refinement.FacetValueNode;

import atg.search.formhandlers.QueryFormHandler;

import atg.service.perfmonitor.PerformanceMonitor;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Run search for "pop-up".
 *
 * @author Katsiaryna Sharstsiuk
 */
public class AjouterFacetValuesFinder extends DynamoServlet {
    /** OUTPUT constant. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** ERROR constant. */
    public static final ParameterName ERROR = ParameterName.getParameterName("error");

    /** TRAIL constant. */
    public static final String TRAIL = "trail";

    /** CURRENT_AJOUTER_FACET_ID constant. */
    public static final String CURRENT_AJOUTER_FACET_ID = "currentAjouterFacetId";

    /** CURRENT_AJOUTER_FACET_TRAIL constant. */
    public static final String CURRENT_AJOUTER_FACET_TRAIL = "currentAjouterFacetTrail";

    /** AJOUTER_RESULTED_FACET_VALUE_NODES constant. */
    public static final String AJOUTER_RESULTED_FACET_VALUE_NODES = "ajouterResultedFacetValueNodes";

    /** ajouterFHName property */
    private String mAjouterFHName = "/com/castorama/ajouter/AjouterSearchFormHandler";

    /** ajouterFSTName property */
    private String mAjouterFSTName = "/atg/search/repository/FacetSearchTools";

    /**
     * Returns ajouterFHName property.
     *
     * @return ajouterFHName property.
     */
    public String getAjouterFHName() {
        return mAjouterFHName;
    }

    /**
     * Sets the value of the ajouterFHName property.
     *
     * @param pAjouterFHName parameter to set.
     */
    public void setAjouterFHName(String pAjouterFHName) {
        mAjouterFHName = pAjouterFHName;
    }

    /**
     * Returns ajouterFSTName property.
     *
     * @return ajouterFSTName property.
     */
    public String getAjouterFSTName() {
        return mAjouterFSTName;
    }

    /**
     * Sets the value of the ajouterFSTName property.
     *
     * @param pAjouterFSTName parameter to set.
     */
    public void setAjouterFSTName(String pAjouterFSTName) {
        mAjouterFSTName = pAjouterFSTName;
    }

    /**
     * If trail is empty or contains only one value(case for product listing (we
     * don't show pivot category facet at the UI)) so no one facet value was
     * selected, so re-running search for pop-up isn't necessary. the same
     * situation for question parameter(search re-run isn't necessary).
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
                PerformanceMonitor.startOperation("search", "TEST:AjouterFacetValuesFinder.service");
            }
            String currentAjouterFacetId = pRequest.getParameter(CURRENT_AJOUTER_FACET_ID);
            String currentAjouterFacetTrail = pRequest.getParameter(CURRENT_AJOUTER_FACET_TRAIL);
            if (!StringUtils.isBlank(currentAjouterFacetId) && !StringUtils.isBlank(currentAjouterFacetTrail)) {
                Map<String, FacetValueNode[]> additionalFacetValuesToFacetMap = new HashMap<String, FacetValueNode[]>();

                // all search request will be run one by one
                FacetSearchTools ajouterFST = (FacetSearchTools) pRequest.resolveName(getAjouterFSTName());
                QueryFormHandler ajouterFH = (QueryFormHandler) pRequest.resolveName(getAjouterFHName());

                ajouterFST.setFacetTrail(currentAjouterFacetTrail);
                if (!StringUtils.isBlank(pRequest.getParameter("startCategory")) &&
                        !StringUtils.isBlank(pRequest.getParameter("catgoryFacetId")) &&
                        currentAjouterFacetTrail.startsWith(pRequest.getParameter("catgoryFacetId"))) {
                    ((atg.search.routing.command.search.QueryRequest) ajouterFH.getSearchRequest()).setStartCategory(pRequest
                                                                                                                     .getParameter("startCategory"));
                }

                ajouterFH.handleSearch(pRequest, pResponse);

                for (FacetHolder fh : ajouterFST.getFacets()) {
                    if (currentAjouterFacetId.equalsIgnoreCase(fh.getFacet().getId())) {
                        pRequest.setParameter(AJOUTER_RESULTED_FACET_VALUE_NODES, fh.getFacetValueNodes());
                        break;
                    }
                }

            }  // end if
        } finally {
            if (PerformanceMonitor.isEnabled()) {  // end if
                PerformanceMonitor.endOperation("search", "TEST:AjouterFacetValuesFinder.service");
            }
        }  // end try-finally
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }
}
