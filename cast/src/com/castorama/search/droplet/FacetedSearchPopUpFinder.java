package com.castorama.search.droplet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.search.refinement.CommerceFacetTrailTools;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.repository.search.refinement.DisjunctionMultiValue;
import atg.repository.search.refinement.FacetValue;

import atg.service.perfmonitor.PerformanceMonitor;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Forms correct trails for ajouters(pop-ups).
 *
 * @author Katsiaryna Sharstsiuk
 */
public class FacetedSearchPopUpFinder extends DynamoServlet {
    /*
     * Open parameters
     */
    /** OUTPUT constant. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** ERROR constant. */
    public static final ParameterName ERROR = ParameterName.getParameterName("error");

    /** TRAIL constant. */
    public static final String TRAIL = "trail";

    /** RESULTED_MAP_FACET_ID_TO_TRAIL constant. */
    public static final String RESULTED_MAP_FACET_ID_TO_TRAIL = "resultedMapFacetIdToTrail";

    /*
     * Properties
     */
    /** trailSeparator property */
    private String mTrailSeparator = ":";

    /** commerceFacetTrailTools property */
    private CommerceFacetTrailTools mCommerceFacetTrailTools;

    /**
     * Returns trailSeparator property.
     *
     * @return trailSeparator property.
     */
    public String getTrailSeparator() {
        return mTrailSeparator;
    }

    /**
     * Sets the value of the trailSeparator property.
     *
     * @param pTrailSeparator parameter to set.
     */
    public void setTrailSeparator(String pTrailSeparator) {
        mTrailSeparator = pTrailSeparator;
    }

    /**
     * Returns commerceFacetTrailTools property.
     *
     * @return commerceFacetTrailTools property.
     */
    public CommerceFacetTrailTools getCommerceFacetTrailTools() {
        return mCommerceFacetTrailTools;
    }

    /**
     * Sets the value of the commerceFacetTrailTools property.
     *
     * @param pCommerceFacetTrailTools parameter to set.
     */
    public void setCommerceFacetTrailTools(CommerceFacetTrailTools pCommerceFacetTrailTools) {
        mCommerceFacetTrailTools = pCommerceFacetTrailTools;
    }

    /**
     * Forms correct trails for ajouters(pop-ups). Save ajouter's trail as map
     * :ajouter id to trail. If trail is empty or contains only one value(case
     * for product listing (we don't show pivot category facet at the UI)) so no
     * one facet value was selected, so re-running search for pop-up isn't
     * necessary. the same situation for question parameter(search re-run isn't
     * necessary).
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
                PerformanceMonitor.startOperation("search", "TEST:FacetedSearchPopUpFinder.service");
            }
            String initialTrail = pRequest.getParameter(TRAIL);

            if ((getCommerceFacetTrailTools() != null) && !StringUtils.isBlank(initialTrail) &&
                    (initialTrail.indexOf(":") != initialTrail.lastIndexOf(":"))) {
                List<FacetValue> trails = getCommerceFacetTrailTools().parseFacetValueString(initialTrail, null);

                Map<String, ArrayList<String>> facetIdToFacetValues = new HashMap<String, ArrayList<String>>();

                String firstSafeFacet;

                /*
                 * pivot category || SRCH facet || root catgory facet - should stay the same; if this facet will be multiple
                 * (facetId:facetValue1|facetValue2|...) So do check, the reason of this is .getTrailString() method returns
                 * not correct value
                 */
                if (trails.get(0) instanceof DisjunctionMultiValue) {
                    firstSafeFacet = trails.get(0).toString();
                    trails.remove(0);
                } else {
                    firstSafeFacet = trails.get(0).getTrailString();
                    trails.remove(0);
                }

                if (trails != null) {
                    for (FacetValue fv : trails) {
                        facetIdToFacetValues.put(fv.getFacet().getId(), new ArrayList());
                    }

                    for (FacetValue fv : trails) {
                        if (facetIdToFacetValues.get(fv.getFacet().getId()) != null) {
                            if (fv instanceof DisjunctionMultiValue) {
                                ((ArrayList) facetIdToFacetValues.get(fv.getFacet().getId())).add(fv.toString());
                            } else {
                                ((ArrayList) facetIdToFacetValues.get(fv.getFacet().getId())).add(fv.getTrailString());
                            }
                        }
                    }
                    Map<String, String> resultedMapFacetIdToTrail = new HashMap<String, String>();

                    // trails for queries
                    for (Map.Entry<String, ArrayList<String>> excludedFacet : facetIdToFacetValues.entrySet()) {
                        StringBuffer inludedFacetsSB = new StringBuffer();

                        for (Map.Entry<String, ArrayList<String>> inludedFacets : facetIdToFacetValues.entrySet()) {
                            if (!excludedFacet.getKey().equalsIgnoreCase(inludedFacets.getKey())) {
                                for (String str : inludedFacets.getValue()) {
                                    inludedFacetsSB.append(str).append(":");
                                }
                            }
                        }

                        // prepend first facet value (srch || pivot || root category)
                        inludedFacetsSB.insert(0, ":").insert(0, firstSafeFacet);

                        // delete extra last ":"
                        if (inludedFacetsSB.lastIndexOf(":") == (inludedFacetsSB.length() - 1)) {
                            inludedFacetsSB.replace(inludedFacetsSB.lastIndexOf(":"), inludedFacetsSB.length(), "");
                        }

                        resultedMapFacetIdToTrail.put(excludedFacet.getKey(), inludedFacetsSB.toString());

                    }  // end for

                    pRequest.setParameter(RESULTED_MAP_FACET_ID_TO_TRAIL, resultedMapFacetIdToTrail);
                }  // end if

            }  // end if
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:FacetedSearchPopUpFinder.service");
            }
        }  // end try-finally
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }
}
