package com.castorama.mobile.droplet;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;

import atg.adapter.gsa.ChangeAwareList;
import atg.adapter.gsa.ChangeAwareSet;
import atg.adapter.gsa.GSAItem;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Retrieves pivot category for current product
 *
 * @author 
 */
public class PivotCategoryLookupDroplet extends DynamoServlet {

    /** ANCESTOR_CATEGORIES constant. */
    private static final String ANCESTOR_CATEGORIES = "ancestorCategories";

    /** PARENT_CATEGORIES constant. */
    private static final String PARENT_CATEGORIES = "parentCategories";

    /** OUTPUT constant. */
    private static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** ERROR constant. */
    private static final ParameterName ERROR = ParameterName.getParameterName("error");

    /** EMPTY constant. */
    private static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /** PIVOT_CATEGORY constant. */
    private static final String PIVOT_CATEGORY = "pivotCategory";

    /** AUGMENTED_CATEGORY constant. */
    private static final String AUGMENTED_CATEGORY = "augmentedCategory";
    
    /** PRODUCT constant. */
    private static final String PRODUCT = "product";
    
    /** PIVOT constant */
    private static final String PIVOT = "pivot";

    /** PARENT_CATEGORY constant */
    private static final String PARENT_CATEGORY = "parentCategory";

    /**
     * Retrieves pivot category for current product. 
     *
     * @param  pRequest  - dynamo http request
     * @param  pResponse - dynamo http response
     *
     * @throws ServletException - exception
     * @throws IOException      - exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        RepositoryItem product = (RepositoryItem) pRequest.getObjectParameter(PRODUCT);
        RepositoryItem pivotCategory = null;
        RepositoryItem augmentedRealityCategory = (RepositoryItem) pRequest.getObjectParameter("agRootCategory");
        RepositoryItem firstParentAugmentedRealityCategory = null;
        try {
            if (product != null) {
                GSAItem temp = (GSAItem) product.getPropertyValue(PARENT_CATEGORY);
                pivotCategory = returnPivotCategory(temp);
                firstParentAugmentedRealityCategory = returnAGCategory(product, augmentedRealityCategory);
            }  // end if

        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
            pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
        }  // end try-catch
        
        if (pivotCategory != null) {
            pRequest.setParameter(PIVOT_CATEGORY, pivotCategory);
            if (firstParentAugmentedRealityCategory != null){
                pRequest.setParameter(AUGMENTED_CATEGORY, firstParentAugmentedRealityCategory);
            }
            pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        } else {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
        }
    }
    
    /**
     * Checks if category is "Pivot", if not, gets its parent category and checks it.
     * Returns the first parent pivot category of the given item.  
     *
     * @param  GSAItem pItem
     *
     * @return pivot category
     */
    private GSAItem returnPivotCategory(GSAItem pItem) {
        GSAItem tmpItem;
        if (!(Boolean) pItem.getPropertyValue(PIVOT)) {
            GSAItem aItem = (GSAItem) pItem.getPropertyValue(PARENT_CATEGORY);
            if (aItem != null) {
                tmpItem = returnPivotCategory(aItem);
            } else {
                tmpItem = pItem;
            }
        } else {
            tmpItem = pItem;
        }
        return tmpItem;
    }

    /**
     * Checks if one of parent categories is belongs of  Augumented reality category
     * Returns the first parent pivot category of the given item.  
     *
     * @param  RepositoryItem product
     * @param  RepositoryItem augmentedRealityCategory
     *
     * @return augmented reality category
     */
    @SuppressWarnings("rawtypes")
    private GSAItem returnAGCategory(RepositoryItem product, RepositoryItem augmentedRealityCategory) {
        if (augmentedRealityCategory == null) return null;
        String agCategoryId = augmentedRealityCategory.getRepositoryId();
        ChangeAwareSet parentCategories= (ChangeAwareSet) product.getPropertyValue(PARENT_CATEGORIES);
        if (parentCategories == null) return null;
        for (Iterator parentCategoryIterator = parentCategories.iterator(); parentCategoryIterator.hasNext();) {
        GSAItem category = (GSAItem) parentCategoryIterator.next();
        ChangeAwareList ancestorCategories= (ChangeAwareList) category.getPropertyValue(ANCESTOR_CATEGORIES);
        if (ancestorCategories == null) return null;
            for (Iterator ancestorIterator = ancestorCategories.iterator(); ancestorIterator.hasNext();) {
                GSAItem ancestorCategory = (GSAItem) ancestorIterator.next();
                if (ancestorCategory.getRepositoryId().equals(agCategoryId)) {
                    return category;
                }
            }
        }
        return null;
    }

}
