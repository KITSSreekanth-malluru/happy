package com.castorama.search.droplet;

import java.io.IOException;
import java.net.URLDecoder;

import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.search.refinement.CategoryFacetValue;
import atg.commerce.search.refinement.CommerceFacetTrailTools;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.repository.search.refinement.FacetDisjunctionMultiValue;
import atg.repository.search.refinement.FacetException;
import atg.repository.search.refinement.FacetValue;
import atg.repository.search.refinement.RangeFacetDisjunctionMultiValue;
import atg.repository.search.refinement.RefinementFacet;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import atg.userdirectory.droplet.Constants;

/**
 * Droplet configures omniture properties:
 *
 * <ul>
 * <li>s_FullFilterTypes - Comma separated list of all filters currently
 * applied. Use one or more of the following values: "None", "Disponibilité",
 * "Categorie", "Marque", "Prix"
 * <li>s_LastFilterType - Use only one of the following values to select what
 * was the last filter applied by the user: "None", "Disponibilité",
 * "Categorie", "Marque", "Prix"
 * <li>s_LastFilterValue - Use only the last value selected by the user for
 * filtering products
 * </ul>
 * <dt>Input params:
 * <dd>
 * <li><code>trail</code> - search trail
 * <dd>
 * <li><code>ajouterTrail</code> - ajouter trail
 * <dd>
 * <li><code>previousTrail</code> - previous trail
 * <dd>
 * <li><code>lastFilter</code> - last facet
 * ("${facet.refinementProperty:facet.value}")
 * <dd>
 * <li><code>categoryId</code> - current category id
 * <dt>Oparams:
 * <dd>
 * <li><code>output</code> in any case
 * <dt>Output params:
 * <dd>
 * <li><code>lastValue</code> value of "s_LastFilterValue" omniture param. If no
 * filter is used - "None"
 * <dd>
 * <li><code>lastType</code> value of "s_LastFilterType" omniture param. If no
 * filter is used - "None"
 * <dd>
 * <li><code>fullFilterTypes</code> value of "s_FullFilterTypes" omniture param.
 * If no filter is used - "None"
 *
 * @author  EPAM team
 * @version ToDo: INSERT VERSION NUMBER
 */
public class FacetOmniture extends DynamoServlet implements Constants {
    private static final String LAST_REFINEMENT_PROPERTY = "lastRefinementProperty";

	/** LAST_VALUE_PARAM output property */
    private static final String LAST_VALUE_PARAM = "lastValue";

    /** LAST_TYPE_PARAM output property */
    private static final String LAST_TYPE_PARAM = "lastType";

    /** FULL_FILTER_TYPES_PARAM output property */
    private static final String FULL_FILTER_TYPES_PARAM = "fullFilterTypes";

    /** LastFilter separator property */
    private static final String COLON = ":";

    /** NONE property */
    private static final String NONE = "None";

    /** SRCH property */
    private static final String SRCH = "SRCH";

    /** COMMA property */
    private static final String COMMA = ",";

    /** CATEGORY_ID input property */
    private static final ParameterName CATEGORY_ID = ParameterName.getParameterName("categoryId");

    /** TRAIL input property */
    private static final ParameterName TRAIL = ParameterName.getParameterName("trail");

    /** AJOUTER_TRAIL input property */
    private static final ParameterName AJOUTER_TRAIL = ParameterName.getParameterName("ajouterTrail");

    /** LAST_FILTER input property */
    private static final ParameterName LAST_FILTER = ParameterName.getParameterName("lastFilter");

    /** PREV_TRAIL input property */
    private static final ParameterName PREV_TRAIL = ParameterName.getParameterName("previousTrail");

    /**
     * Enum that contains facet
     *
     * @author  EPAM team
     * @version ToDo: INSERT VERSION NUMBER
     */
    enum Facet {
        BRAND_FACET("brand", "Marque"), CATEGORY_FACET("ancestorCategories.$repositoryId", "Categorie"),
        PRIX_FACET("price", "Prix"), DISPONIBILITE_FACET("availability", "Disponibilité"), RECOMENDATION_FACET("rating", "AvisClient"), 
        LIVRAISON_FACET("ccMode", "Livraison"), NONE_FACET(NONE, NONE);
        // RATING_FACET("rating", "rating")

        /** mRefinementProperty property */
        String mRefinementProperty;

        /** mFacetValue property */
        String mFacetValue;

        /** mFacetName property */
        String mFacetName;

        /**
         * Returns facet property by it's refinementProperty. If nothing was
         * found returns NONE facet
         *
         * @param  pRefinementProperty - refinementProperty.
         *
         * @return facet.
         */
        static Facet getFacetByRefinementProperty(String pRefinementProperty) {
            Facet result = null;
            if (!StringUtils.isBlank(pRefinementProperty)) {
                for (Facet facet : Facet.values()) {
                    if (facet.mRefinementProperty.equalsIgnoreCase(pRefinementProperty)) {
                        result = facet;
                        break;
                    }
                }
            }
            if (result == null) {
                result = NONE_FACET;
            }
            return result;
        }

        /**
         * Sets the value of the value property.
         *
         * @param pValue parameter to set.
         */
        public void setValue(final String pValue) {
            mFacetValue = pValue;
        }

        /**
         * Creates a new Facet object by RefinementProperty  and by it's name.
         *
         * @param pRefinementProperty - refinementProperty
         * @param pName               - facet name
         */
        Facet(String pRefinementProperty, String pName) {
            this.mRefinementProperty = pRefinementProperty;
            this.mFacetName = pName;
        }
        public String toString() {
        	return this.mFacetName + "|" + this.mRefinementProperty + "|" + this.mFacetValue;
        }

    }

    /** commerceFacetTrailTools property */
    private CommerceFacetTrailTools commerceFacetTrailTools;

    /**
     * Reads input params:
     * <dd>
     * <li><code>trail</code> - search trail
     * <dd>
     * <li><code>ajouterTrail</code> - ajouter trail
     * <dd>
     * <li><code>previousTrail</code> - previous trail
     * <dd>
     * <li><code>lastFilter</code> - last facet
     * ("${facet.refinementProperty:facet.value}")
     * <dd>
     * <li><code>categoryId</code> - current category id Configure Output
     * params:
     * <dd>
     * <li><code>lastValue</code> value of "s_LastFilterValue" omniture param.
     * If no filter is used - "None"
     * <dd>
     * <li><code>lastType</code> value of "s_LastFilterType" omniture param. If
     * no filter is used - "None"
     * <dd>
     * <li><code>fullFilterTypes</code> value of "s_FullFilterTypes" omniture
     * param. If no filter is used - "None"<br>
     * Sets them in oparam "output"
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
        String previousTrail = pRequest.getParameter(PREV_TRAIL);
        String categoryId = pRequest.getParameter(CATEGORY_ID);
        String ajouterTrail = pRequest.getParameter(AJOUTER_TRAIL);
        String lastFilter = pRequest.getParameter(LAST_FILTER);
        lastFilter = lastFilter != null ? URLDecoder.decode(lastFilter, "UTF-8") : null;

        if (isLoggingDebug()) {
            logDebug("Start FacetOmniture droplet with next params: trail=[" + trail + "], categoryId=[" + categoryId +
                     "], ajouterTrail=[" + ajouterTrail + "], lastFilter=[" + lastFilter + "]");

        }

        if (!StringUtils.isBlank(ajouterTrail)) {
            if (StringUtils.isBlank(previousTrail)) {
                previousTrail = trail;
            }
            trail = ajouterTrail;
        }

        Facet last = getLastFacet(trail, previousTrail, lastFilter);
        String fullFilterTypes = getFullFilterTypes(trail, categoryId);

        if (fullFilterTypes.equalsIgnoreCase(NONE) && last != Facet.NONE_FACET) {
        	fullFilterTypes = last.mFacetName;
        }

        if (last != null) {
            pRequest.setParameter(LAST_TYPE_PARAM, last.mFacetName);
            pRequest.setParameter(LAST_VALUE_PARAM, last.mFacetValue);
            pRequest.setParameter(LAST_REFINEMENT_PROPERTY, last.mRefinementProperty);
            if (isLoggingDebug()) {
                logDebug("FacetOmniture droplet: lastFilter:" + last.toString());
            }
        }
        pRequest.setParameter(FULL_FILTER_TYPES_PARAM, fullFilterTypes);
        if (isLoggingDebug()) {
            logDebug("FacetOmniture droplet: fullFilterTypes:" + fullFilterTypes);
        }
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);

    }

    /**
     * Gets comma separated list of all filters currently applied. Use one or
     * more of the following values: "None", "Disponibilité", "Categorie",
     * "Marque", "Prix"
     *
     * @param  trail      - current trail
     * @param  categoryId - current category id
     *
     * @return list of all filters currently applied
     */
    private String getFullFilterTypes(final String trail, final String categoryId) {
        StringBuffer fullFilterTypes = new StringBuffer();
        if (!StringUtils.isBlank(trail)) {
            List<FacetValue> trails = getCommerceFacetTrailTools().parseFacetValueString(trail, null);
            if ((trails != null) && !trails.isEmpty()) {
                for (FacetValue facetValue : trails) {
                    if ((facetValue != null) && (facetValue.getFacet() instanceof RefinementFacet)) {
                        try {
                            Facet facet =
                                Facet.getFacetByRefinementProperty(((RefinementFacet) facetValue.getFacet())
                                                                   .getRefinementElement().getProperty());
                            switch (facet) {
                                case CATEGORY_FACET:
                                    if ((facetValue instanceof CategoryFacetValue) &&
                                            categoryId.equals(((CategoryFacetValue) facetValue).getValue())) {
                                        break;
                                    }

                                case DISPONIBILITE_FACET:
                                case PRIX_FACET:
                                case BRAND_FACET:
                                case RECOMENDATION_FACET:
                                case LIVRAISON_FACET:
                                    fullFilterTypes.append(facet.mFacetName + COMMA);
                                    break;

                                default:
                                    break;
                            }

                        } catch (FacetException e) {
                            if (isLoggingDebug()) {
                                logDebug("Incorrect RefinementElement for facet: " + facetValue.toString());
                            }
                        }  // end try-catch
                    }  // end if
                }  // end for
            }  // end if
        }  // end if
        if (fullFilterTypes.length() == 0) {
            fullFilterTypes.append(NONE);
        } else {
            fullFilterTypes.deleteCharAt(fullFilterTypes.length() - 1);
        }
        return String.valueOf(fullFilterTypes);
    }

    /**
     * Gets element of enum Facet:
     * <li>its value: value of the last facet applied by the user
     * <li>its name: name of the last facet applied by the user
     *
     * @param  trail         - current trail
     * @param  previousTrail - previous trail
     * @param  lastFilter    - last applied facet
     *
     * @return element of enum Facet with properties of the last filter applied
     *         by the user. If no filter is used returns NONE facet
     */
    private Facet getLastFacet(String trail, String previousTrail, String lastFilter) {
        Facet last;
        int indexOfColon = (lastFilter == null) ? -1 : lastFilter.indexOf(COLON);
        if ((lastFilter != null) && (lastFilter.trim().length() > 0) && (indexOfColon > 0)) {
            last = Facet.getFacetByRefinementProperty(lastFilter.substring(0, indexOfColon));
            if (last != Facet.NONE_FACET) {
                last.setValue(lastFilter.substring(indexOfColon + 1));
            }
        } else {
            last = getLastFilterTypeFromTrail(previousTrail, trail);
        }
        if (last == null) {
            last = Facet.NONE_FACET;
        }
        return last;
    }

    /**
     * Gets element of enum Facet (by trail - if lastFilter param was missed (it
     * happens from ajouter)):
     * <li>its value: value of the last facet applied by the user
     * <li>its name: name of the last facet applied by the user
     *
     * @param  previousTrail -  previous trail.
     * @param  trail         - current trail.
     *
     * @return element of enum Facet with properties of the last filter applied
     *         by the user on ajouter popup.
     */
    private Facet getLastFilterTypeFromTrail(final String previousTrail, final String trail) {
        Facet result = null;
        if ((previousTrail != null) && (trail != null) && !previousTrail.equalsIgnoreCase(trail)) {
            List<FacetValue> prevTrails = getCommerceFacetTrailTools().parseTrailStringIntoPairs(previousTrail);
            List<FacetValue> trails = getCommerceFacetTrailTools().parseTrailStringIntoPairs(trail);
            for (FacetValue newFacetValue : trails) {
                if ((newFacetValue != null) && !isListContainsFacetValue(prevTrails, newFacetValue) &&
                        (newFacetValue.getFacet() instanceof RefinementFacet)) {
                    try {
                        result =
                            Facet.getFacetByRefinementProperty(((RefinementFacet) newFacetValue.getFacet())
                                                               .getRefinementElement().getProperty());

                        if (result != Facet.NONE_FACET) {
                            Object value = newFacetValue instanceof FacetDisjunctionMultiValue ? ((FacetDisjunctionMultiValue)newFacetValue).getSingleValue() : 
                                newFacetValue instanceof RangeFacetDisjunctionMultiValue ? ((RangeFacetDisjunctionMultiValue)newFacetValue).getSingleValue() : 
                                    newFacetValue.getValue();
                            result.setValue(String.valueOf(value));
                            break;
                        }
                    } catch (FacetException e) {
                        if (isLoggingDebug()) {
                            logDebug("Incorrect RefinementElement for facet: " + newFacetValue.toString());
                        }
                    }
                }
            }
        }  // end if
        return result;
    }
    
    private boolean isListContainsFacetValue(List<FacetValue> list, FacetValue fv) {
        Object fv_value = fv instanceof FacetDisjunctionMultiValue ? ((FacetDisjunctionMultiValue)fv).getSingleValue() : 
            fv instanceof RangeFacetDisjunctionMultiValue ? ((RangeFacetDisjunctionMultiValue)fv).getSingleValue() : 
                fv.getValue();
        for (FacetValue e : list) {
            Object e_value = e instanceof FacetDisjunctionMultiValue ? ((FacetDisjunctionMultiValue)e).getSingleValue() : 
                e instanceof RangeFacetDisjunctionMultiValue ? ((RangeFacetDisjunctionMultiValue)e).getSingleValue() : 
                    e.getValue();
            if ((e.getFacet().equals(fv.getFacet())) && (e_value.equals(fv_value)))
                return true;
        }
        return false;
    }
    
    /**
     * Gets commerceFacetTrailTools
     *
     * @return the commerceFacetTrailTools
     */
    public CommerceFacetTrailTools getCommerceFacetTrailTools() {
        return commerceFacetTrailTools;
    }

    /**
     * Sets commerceFacetTrailTools
     *
     * @param commerceFacetTrailTools the commerceFacetTrailTools to set
     */
    public void setCommerceFacetTrailTools(CommerceFacetTrailTools commerceFacetTrailTools) {
        this.commerceFacetTrailTools = commerceFacetTrailTools;
    }
}
