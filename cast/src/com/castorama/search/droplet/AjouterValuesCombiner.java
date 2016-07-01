package com.castorama.search.droplet;

import java.io.IOException;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;

import com.castorama.search.AjouterFacetValue;

import atg.commerce.search.refinement.CommerceFacetTrailTools;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.repository.search.refinement.DisjunctionMultiValue;
import atg.repository.search.refinement.FacetDisjunctionMultiValue;
import atg.repository.search.refinement.FacetValue;
import atg.repository.search.refinement.FacetValueNode;
import atg.repository.search.refinement.RangeFacetDisjunctionMultiValue;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Build list of AjouterFacetValue objects. Retrieves trail, parses it, creates
 * AjouterFacetValue("facet value object", true) objects(true - value was
 * selected), and puts created values to resulted list. Retrieves list of
 * facetValueNode objects for current pop-up, iterates through this list and
 * creates AjouterFacetValue("facet value object", false) objects on basis
 * non-selected values from facetValueNode list (false - non-selected values).
 * Returns resulted list of AjouterFacetValue objects.
 *
 * <p>Description of AjouterValuesCombiner parameters:</p>
 *
 * <dl>
 * <dt>output</dt>
 * <dd>output parameter</dd>
 *
 * <dt>empty</dt>
 * <dd>empty output parameter</dd>
 *
 * <dt>trail</dt>
 * <dd>input parameter for passing current search's trail</dd>
 *
 * <dt>ajouterFacetFalueNodeList</dt>
 * <dd>input parameter for passing values which should be rendered at the
 * current pop-up</dd>
 *
 * <dt>facetId</dt>
 * <dd>input parameter - used for filter values from trail. only values for
 * current facet should be added at the resulted list.</dd>
 *
 * <dt>ajouterFacetValueList</dt>
 * <dd>returns resulted list of facet values</dd>
 *
 * @author Katsiaryna Sharstsiuk
 */
public class AjouterValuesCombiner extends DynamoServlet {
    /** OUTPUT constant. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** EMPTY constant. */
    public static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /** TRAIL constant. */
    public static final ParameterName TRAIL = ParameterName.getParameterName("trail");

    /** AJOUTER_FACET_VALUE_NODE_LIST constant. */
    public static final ParameterName AJOUTER_FACET_VALUE_NODE_LIST =
        ParameterName.getParameterName("ajouterFacetFalueNodeList");

    /** FACET_ID constant. */
    public static final ParameterName FACET_ID = ParameterName.getParameterName("facetId");

    /** AJOUTER_FACET_VALUE_LIST constant. */
    public static final String AJOUTER_FACET_VALUE_LIST = "ajouterFacetValueList";

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
     * Returns combined list of values for current pop-up.
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
        Object ajouterFacetValueNodeListObj = pRequest.getObjectParameter(AJOUTER_FACET_VALUE_NODE_LIST);
        List<FacetValue> trailStringPairs = getCommerceFacetTrailTools().parseTrailStringIntoPairs(trail);

        if ((trailStringPairs != null) && !trailStringPairs.isEmpty() && !StringUtils.isBlank(facetId)) {
            List<AjouterFacetValue> selectedValuesForCurrentFacet = new LinkedList<AjouterFacetValue>();
            AjouterFacetValue ajFacetValue = null;

            // remove SRCH : PIVOT : or ROOT category facet
            trailStringPairs.remove(0);
           /* for (FacetValue fv : trailStringPairs) {
                if ((fv != null) && facetId.equalsIgnoreCase(fv.getFacet().getId())) {
                    ajFacetValue = new AjouterFacetValue(fv, true);
                    selectedValuesForCurrentFacet.add(ajFacetValue);
                }
            }*/
            if ((ajouterFacetValueNodeListObj != null) && (ajouterFacetValueNodeListObj instanceof List) &&
                    !((List) ajouterFacetValueNodeListObj).isEmpty()) {
                selectedValuesForCurrentFacet.addAll(filterSelectedFacetValues(trailStringPairs,
                                                                               (List<FacetValueNode>)
                                                                               ajouterFacetValueNodeListObj));
            }
            if (selectedValuesForCurrentFacet != null) {
                pRequest.setParameter(AJOUTER_FACET_VALUE_LIST, selectedValuesForCurrentFacet);
                pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
            } else {
                pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
            }

        } else {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);  // end if
        }  // end if-else

    }

    /**
     * Creates list of AjouterFacetValue objects from non-selected items of
     * passed pAjouterFacetValueList list.
     *
     * @param  pSelectedFacetValues       parameter
     * @param  pAjouterFacetValueNodeList parameter
     *
     * @return list of AjouterFacetValue objects
     */
    private List<AjouterFacetValue> filterSelectedFacetValues(List<FacetValue> pSelectedFacetValues,
                                                              List<FacetValueNode> pAjouterFacetValueNodeList) {
        List<AjouterFacetValue> filteredFacetValues = new LinkedList<AjouterFacetValue>();
        AjouterFacetValue ajFacetValue = null;
        for (FacetValueNode fvn : pAjouterFacetValueNodeList) {
        	boolean contains = false;
        	FacetValue facetValue = fvn.getFacetValue();
            for (FacetValue selectedFacetValues : pSelectedFacetValues) {
				if (selectedFacetValues instanceof DisjunctionMultiValue) {
					DisjunctionMultiValue dfv = (DisjunctionMultiValue) selectedFacetValues;
					Object[] dfvls = dfv.getValues();
					for (Object object : dfvls) {
                        Object value = facetValue instanceof FacetDisjunctionMultiValue ? ((FacetDisjunctionMultiValue)facetValue).getSingleValue() : 
                            facetValue instanceof RangeFacetDisjunctionMultiValue ? ((RangeFacetDisjunctionMultiValue)facetValue).getSingleValue() : 
                                facetValue.getValue();
                        if (object.equals(value)) {
							contains = true;
							break;
						}
					}
				} else {
					if (selectedFacetValues.equals(facetValue)) {
						contains = true;
						break;
            }
				}
			}
        	ajFacetValue = new AjouterFacetValue(facetValue, contains);
    		filteredFacetValues.add(ajFacetValue);
        	/*if (!contains) {
        		ajFacetValue = new AjouterFacetValue(fvn.getFacetValue(), false);
        		filteredFacetValues.add(ajFacetValue);
        	}*/
            /*if (!pSelectedFacetValues.contains(fvn.getFacetValue())) {
                ajFacetValue = new AjouterFacetValue(fvn.getFacetValue(), false);
                filteredFacetValues.add(ajFacetValue);
            }*/
        }

        return filteredFacetValues;
    }

}
