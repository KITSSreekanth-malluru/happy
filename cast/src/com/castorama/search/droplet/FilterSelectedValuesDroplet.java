package com.castorama.search.droplet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.search.refinement.CommerceFacetTrailTools;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.repository.search.refinement.FacetValue;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Returns all selected facet's values necessary for mark these values as
 * selected at the "Ajouter" pop-up.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class FilterSelectedValuesDroplet extends DynamoServlet {
    /*
     * Open parameters
     */
    /** OUTPUT constant. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** TRAIL constant. */
    public static final String TRAIL = "trail";

    /** FACET_ID constant. */
    public static final String FACET_ID = "facetId";

    /** SELECTED_VALUES_FOR_FACET constant. */
    public static final String SELECTED_VALUES_FOR_FACET = "selectedValuesForFacet";

    /*
     * Properties
     */
    /** commerceFacetTrailTools property */
    private CommerceFacetTrailTools mCommerceFacetTrailTools;

    /**
     * Returns commerceFacetTrailTools property
     *
     * @return the commerceFacetTrailTools property
     */
    public CommerceFacetTrailTools getCommerceFacetTrailTools() {
        return mCommerceFacetTrailTools;
    }

    /**
     * Sets commerceFacetTrailTools property
     *
     * @param pCommerceFacetTrailTools the commerceFacetTrailTools to set
     */
    public void setCommerceFacetTrailTools(CommerceFacetTrailTools pCommerceFacetTrailTools) {
        mCommerceFacetTrailTools = pCommerceFacetTrailTools;
    }

    /**
     * Retrieves all selected facet's values necessary for mark these values as
     * selected at the pop-up - "Ajouter". Get trail and facetId as input
     * parameters. Then filter trail by facetId. And returns filtered list of
     * selected facet value items.
     *
     * @param  pRequest  dynamo http request
     * @param  pResponse dynamo http response
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        String trail = pRequest.getParameter(TRAIL);
        String facetId = pRequest.getParameter(FACET_ID);
        List<FacetValue> trailStringPairs = getCommerceFacetTrailTools().parseTrailStringIntoPairs(trail);

        if ((trailStringPairs != null) && !trailStringPairs.isEmpty() && !StringUtils.isBlank(facetId)) {
            List<FacetValue> selectedValuesForCurrentFacet = new ArrayList<FacetValue>();

            // remove SRCH : PIVOT : or ROOT category facet
            trailStringPairs.remove(0);
            for (FacetValue fv : trailStringPairs) {
                if ((fv != null) && facetId.equalsIgnoreCase(fv.getFacet().getId())) {
                    selectedValuesForCurrentFacet.add(fv);
                }
            }
            pRequest.setParameter(SELECTED_VALUES_FOR_FACET, selectedValuesForCurrentFacet);
        }

        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }

}
