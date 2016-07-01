package com.castorama.search.droplet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;

import atg.commerce.search.refinement.CategoryFacetValue;
import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.repository.RepositoryItem;

import atg.repository.search.refinement.FacetException;
import atg.repository.search.refinement.FacetValueNode;

import atg.service.perfmonitor.PerformanceMonitor;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Filter empty facets values (without search results) from list of facet
 * values(necessary for ranges(rating and etc.))
 *
 * @author Katsiaryna Sharstsiuk
 * @changed by Sergey Kuntsevich
 */
public class CastFacetValuesDroplet extends DynamoServlet {
    /*
     * Open parameter names
     */
    /** OPARAM constant. */
    public static final ParameterName OPARAM = ParameterName.getParameterName("oparam");

    /** ERROR constant. */
    public static final ParameterName ERROR = ParameterName.getParameterName("error");

    /** FACET_VALUES constant. */
    public static final String FACET_VALUES = "facetValues";

    /** FACET_VALUES_RESULT_LIST constant. */
    public static final String FACET_VALUES_RESULT_LIST = "facetValuesResultList";

    /** PIVOT_CATEGORY_ID constant. */
    public static final String PIVOT_CATEGORY_ID = "pivotCategoryId";

    /** IS_SEARCH_FOR_CATEGORY constant. */
    public static final String IS_SEARCH_FOR_CATEGORY = "isSearchForCategory";

    /** PIVOT constant. */
    public static final String PIVOT = "pivot";

    /** FIXED_PARENT_CATEGORIES constant. */
    public static final String FIXED_PARENT_CATEGORIES = "fixedParentCategories";

    /**
     * Returns facet's values list without empty facet values(necessary for
     * ranges(rating and etc.))
     *
     * @param  pRequest  dynamo http request
     * @param  pResponse dynamo http response
     *
     * @throws ServletException
     * @throws IOException
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        boolean isCategoryFacetValues = false;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetValuesDroplet.service");
            }
            Object facetValuesObj = pRequest.getObjectParameter(FACET_VALUES);
            String isSearchForCategory = pRequest.getParameter(IS_SEARCH_FOR_CATEGORY);
            List<FacetValueNode> facetValuesResultList = new ArrayList<FacetValueNode>();
            List<FacetValueNode> categoryFacetValuesResultList = new ArrayList<FacetValueNode>();
            if ((facetValuesObj != null) && (facetValuesObj instanceof FacetValueNode[])) {
                for (FacetValueNode facetValue : (FacetValueNode[]) facetValuesObj) {
                    if ((facetValue.getFacetValue() != null) &&
                            (facetValue.getFacetValue().getMatchingDocsCount() > 0)) {
                        try {
                            if (facetValue.getFacetValue() instanceof CategoryFacetValue) {
                                isCategoryFacetValues = true;
                                CategoryFacetValue categoryFacetValue = (CategoryFacetValue)facetValue.getFacetValue();
                                RepositoryItem catRepItem = categoryFacetValue.getCategoryItem();
                                if (catRepItem == null) continue;
                                if (((Boolean) catRepItem.getPropertyValue(PIVOT)) || 
                                            (!atg.core.util.StringUtils.isBlank(isSearchForCategory) &&
                                            Boolean.valueOf(isSearchForCategory) &&
                                            hasPivotInParent(catRepItem, pRequest.getParameter(PIVOT_CATEGORY_ID)))) {
                                        categoryFacetValuesResultList.add(facetValue);
                                }
                            } else {
                                facetValuesResultList.add(facetValue);
                            }  // end if-else
                        } catch (FacetException e) {
                            if (isLoggingDebug()) {
                                logDebug(e);
                            }
                        }  // end try-catch

                    }  // end if
                }  // end for

            }  // end if
            if (isCategoryFacetValues){
                facetValuesResultList = sortCategoryFacetList(pRequest, categoryFacetValuesResultList);
            }
            pRequest.setParameter(FACET_VALUES_RESULT_LIST, facetValuesResultList);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetValuesDroplet.service");
            }
        }  // end try-finally

        pRequest.serviceLocalParameter(OPARAM, pRequest, pResponse);
    }
    
    private List<FacetValueNode> sortCategoryFacetList(DynamoHttpServletRequest pRequest, List<FacetValueNode> facetValuesResultList){
        Object bj = pRequest.getObjectParameter("pivotCategory");
        if (bj == null){
            return facetValuesResultList;
        }
        RepositoryItem ri = (RepositoryItem) bj;
        List<RepositoryItem> fixedChldCategories = (List<RepositoryItem>) ri.getPropertyValue("fixedChildCategories");
        List<FacetValueNode> tempListCategoryFacetValue = new ArrayList<FacetValueNode>();
        for (RepositoryItem tri:fixedChldCategories){
            String triId = (String) tri.getPropertyValue("id");
            for (FacetValueNode cfv: (List<FacetValueNode>)facetValuesResultList){
                String fvnId = ((CategoryFacetValue)cfv.getFacetValue()).getCategoryId();
                if (triId.equals(fvnId)){
                    tempListCategoryFacetValue.add(cfv);
                }
            }
        }
        return tempListCategoryFacetValue;
    }

    /**
     * Check whether pCatRepItem has pPivotCategoryId pivot category at the
     * parents
     *
     * @param  pCatRepItem      parameter
     * @param  pPivotCategoryId parameter
     *
     * @return true if pPivotCategoryId are contained among parent's categories
     *         of pCatRepItem
     */
    private boolean hasPivotInParent(RepositoryItem pCatRepItem, String pPivotCategoryId) {
        if ((pCatRepItem != null) && !StringUtils.isBlank(pPivotCategoryId)) {
            Set<RepositoryItem> fixedParentCategories =
                (Set<RepositoryItem>) pCatRepItem.getPropertyValue(FIXED_PARENT_CATEGORIES);
            if (fixedParentCategories != null) {
                for (RepositoryItem repItem : fixedParentCategories) {
                    if (pPivotCategoryId.equalsIgnoreCase(repItem.getRepositoryId())) {
                        return true;
                    }
                }

            }

        }
        return false;
    }
}
