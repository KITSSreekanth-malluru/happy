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
 * Build trail according to the selected values in ajouter(pop-up).
 *
 * @author Katsiaryna Sharstsiuk
 */
public class AjouterTrailBuilder extends DynamoServlet {
    /*
     * Open parameter names
     */
    /** OUTPUT constant. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** ERROR constant. */
    public static final ParameterName ERROR = ParameterName.getParameterName("error");

    /*
     * Constants
     */
    /** PREVIOS_TRAIL constant. */
    public static final String PREVIOS_TRAIL = "previousTrail";

    /** AJOUTER_TRAIL constant. */
    public static final String AJOUTER_TRAIL = "ajouterTrail";

    /** AJOUTER_FACET_ID constant. */
    public static final String AJOUTER_FACET_ID = "ajouterFacetId";

    /*
     * Properties
     */
    /** commerceFacetTrailTools property */
    private CommerceFacetTrailTools mCommerceFacetTrailTools;

    /** trailSeparator property */
    private String mTrailSeparator = ":";

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
     * Build trail according to the selected values in ajouter(pop-up).
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
                PerformanceMonitor.startOperation("search", "TEST:AjouterTrailBuilder.service");
            }
            String ajouterFacetId = pRequest.getParameter(AJOUTER_FACET_ID);
            if (!StringUtils.isBlank(ajouterFacetId)) {
                String[] ajouterSelectedValues = pRequest.getParameterValues("ajouter_" + ajouterFacetId);
                String previosTrail = pRequest.getParameter(PREVIOS_TRAIL);
                if ((getCommerceFacetTrailTools() != null) && !StringUtils.isBlank(previosTrail) &&
                        previosTrail.contains(ajouterFacetId) && previosTrail.contains(ajouterFacetId)) {
                    // first facet value should stay the SAME!!! : this could be SRCH facet, pivot category facet or root
                    // category facet.

                    List<FacetValue> trails = getCommerceFacetTrailTools().parseFacetValueString(previosTrail, null);

                    Map<String, ArrayList<String>> facetIdToFacetValues = new HashMap<String, ArrayList<String>>();

                    String firstSafeFacet;

                    /*
                     * pivot category || SRCH facet || root catgory facet - should stay the same; if this facet will be
                     * multiple (facetId:facetValue1|facetValue2|...) So do check, the reason of this is .getTrailString()
                     * method returns not correct value
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
                        String lastFacetValue = "";

                        // create new trail value for current ajouterFacetId
                        if ((ajouterSelectedValues != null) && (ajouterSelectedValues.length > 0)) {
                            StringBuffer ajouterFacetTrailValue = new StringBuffer(ajouterFacetId);
                            ajouterFacetTrailValue.append(":");
                            for (String str : ajouterSelectedValues) {
                                if (!StringUtils.isBlank(str)) {
                                    str = str.substring(str.indexOf(":") + 1);
                                    if (str.contains("|LAST")) {
                                        lastFacetValue = str;
                                    } else {
                                        ajouterFacetTrailValue.append(str).append("|");
                                    }
                                }
                            }

                            if (ajouterFacetTrailValue.lastIndexOf("|") == (ajouterFacetTrailValue.length() - 1)) {
                                ajouterFacetTrailValue.replace(ajouterFacetTrailValue.lastIndexOf("|"),
                                                               ajouterFacetTrailValue.length(), "");
                            }
                            if (!StringUtils.isBlank(lastFacetValue)) {
                            	if (ajouterFacetTrailValue.lastIndexOf(":") != ajouterFacetTrailValue.length() - 1) {
                            		ajouterFacetTrailValue.append("|");
                            	}
                                ajouterFacetTrailValue.append(lastFacetValue);
                            }
                            ((ArrayList) facetIdToFacetValues.get(ajouterFacetId)).clear();
                            ((ArrayList) facetIdToFacetValues.get(ajouterFacetId)).add(ajouterFacetTrailValue
                                                                                       .toString());
                        } else {
                            facetIdToFacetValues.remove(ajouterFacetId);
                        }  // end if-else

                        Map<String, String> resultedMapFacetIdToTrail = new HashMap<String, String>();

                        // trails for queries
                        // prepend first facet value (srch || pivot || root category)
                        StringBuffer facetsSB = new StringBuffer(firstSafeFacet);

                        for (Map.Entry<String, ArrayList<String>> facets : facetIdToFacetValues.entrySet()) {
                            for (String str : facets.getValue()) {
                                facetsSB.append(":").append(str);
                            }

                        }
                       
                        pRequest.setParameter(AJOUTER_TRAIL, facetsSB.toString());

                    }  // end if

                }  // end if

            }  // end if
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:AjouterTrailBuilder.service");
            }
        }  // end try-finally
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }
}
