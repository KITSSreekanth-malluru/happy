package com.castorama.droplet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;

import atg.adapter.gsa.ChangeAwareList;
import atg.adapter.gsa.GSAItem;

import atg.commerce.catalog.CatalogTools;

import atg.nucleus.naming.ParameterName;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.commerce.catalog.CastCatalogTools;

/**
 * FooterInfoDroplet class 
 *
 * @author  EPAM team
 */
public class FooterInfoDroplet extends DynamoServlet {
    /** CATEGORY_ID_PARAM constant */
    public static final String CATEGORY_ID_PARAM = "categoryId";

    /** SEARCH_PARAM constant */
    public static final String SEARCH_PARAM = "question";

    /** PRODUCT_ID constant */
    public static final String PRODUCT_ID = "productId";

    /** PIVOT constant */
    public static final String PIVOT = "pivot";

    /** ACTION constant */
    public static final String ACTION = "action";

    /** PARENT_CATEGORY constant */
    public static final String PARENT_CATEGORY = "parentCategory";

    /** CHILD_CATEGORIES constant */
    public static final String CHILD_CATEGORIES = "fixedChildCategories";

    /** BREADCRUMBS constant */
    public static final String BREADCRUMBS = "beadcrumbs";

    /** TOTAL_COUNT constant */
    public static final String TOTAL_COUNT = "totalCount";

    /** LINES_PER_COLUMN constant */
    public static final String LINES_PER_COLUMN = "linesPerColumn";

    /** CHILDREN constant */
    public static final String CHILDREN = "children";

    /** SIBLINGS constant */
    public static final String SIBLINGS = "siblings";

    /** TOP_CATEGORIES constant */
    public static final String TOP_CATEGORIES = "topNavigationCategories";

    /** OUTPUT constant */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** EMPTY constant */
    public static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /** catalogTools property */
    private CatalogTools mCatalogTools;

    /**
     * Calculates parent category and its siblings and iterates over them.
     *
     * @param  pRequest  DynamoHttpServletRequest object
     * @param  pResponse DynamoHttpServletResponse object
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        Object beadcrumbsParam = pRequest.getObjectParameter(ACTION);
        boolean beadcrumbs = ((beadcrumbsParam != null) && BREADCRUMBS.equals((String) beadcrumbsParam));

        String productId = (String) pRequest.getObjectParameter(PRODUCT_ID);
        boolean hasProductId = ((productId != null) && !("").equals(productId));

        String searchParam = (String) pRequest.getObjectParameter(SEARCH_PARAM);
        if ((searchParam != null) && !("").equals(searchParam)) {
            int totalCount = countItems(getTopCategories()) + 1;
            int linesPerColumn = Math.round(totalCount / 4);
            pRequest.setParameter(TOTAL_COUNT, totalCount);
            pRequest.setParameter(LINES_PER_COLUMN, linesPerColumn);
            pRequest.setParameter(TOP_CATEGORIES, getTopCategories());
        }

        String categoryId = (String) pRequest.getObjectParameter(CATEGORY_ID_PARAM);
        GSAItem category;
        if (hasProductId || ((categoryId != null) && !("").equals(categoryId))) {
            try {
                category = (GSAItem) getCatalogTools().findCategory(categoryId);
                if (hasProductId) {
                    final RepositoryItem productObject = getCatalogTools().findProduct(productId);
                    if (productObject == null) {
                        if (isLoggingWarning()) {
                            logWarning("Null was returned instead of product with id=" + productId);
                        }
                    } else {
                        category = returnPivotCategory((GSAItem) productObject.getPropertyValue(PARENT_CATEGORY));
                    }
                }

                if (beadcrumbs) {
                    LinkedList<RepositoryItem> tmpAncestors = getCatalogTools().getAncestors(category);
                    ArrayList<RepositoryItem> beadcrumbItems = new ArrayList<RepositoryItem>();
                    for (RepositoryItem item : tmpAncestors) {
                        if (!isTopNavigationCategory(item)) {
                            beadcrumbItems.add(item);
                        }
                    }
                    beadcrumbItems.add(category);  // here "category" item will be the last in breadcrumbs
                    pRequest.setParameter(BREADCRUMBS, beadcrumbItems);
                } else {
                    int totalCount = 0;
                    if ((Boolean) category.getPropertyValue(PIVOT)) {
                        // For list pages
                        GSAItem parentCategory = (GSAItem) category.getPropertyValue(PARENT_CATEGORY);
                        List<RepositoryItem> repositorySiblings = (List<RepositoryItem>) parentCategory.getPropertyValue(CHILD_CATEGORIES);
                        List<RepositoryItem> siblings = new ArrayList<RepositoryItem>();
                        for(RepositoryItem sibling : repositorySiblings){
                            if ((Boolean) (sibling).getPropertyValue(PIVOT)) {
                                siblings.add(sibling);
                                totalCount++; // category itself
                                totalCount += ((List) sibling.getPropertyValue(CHILD_CATEGORIES)).size(); // child categories
                            }
                        }
                        int linesPerColumn = Math.round(totalCount / 4);
                        pRequest.setParameter(TOTAL_COUNT, totalCount);
                        pRequest.setParameter(LINES_PER_COLUMN, linesPerColumn);
                        pRequest.setParameter(SIBLINGS, siblings);
                    } else {
                        // For category pages
                        ChangeAwareList children = (ChangeAwareList) category.getPropertyValue(CHILD_CATEGORIES);
                        totalCount += countItems(children);
                        int linesPerColumn = Math.round(totalCount / 4);
                        pRequest.setParameter(TOTAL_COUNT, totalCount);
                        pRequest.setParameter(LINES_PER_COLUMN, linesPerColumn);
                        pRequest.setParameter(CHILDREN, children);
                    }  // end if-else
                }  // end if-else

            } catch (RepositoryException e) {
                pRequest.serviceParameter(EMPTY, pRequest, pResponse);
            }  // end try-catch

        } else {
            int totalCount = 1;  // 1 means "Retour haut de page"
            totalCount += countItems(getTopCategories());
            int linesPerColumn = Math.round(totalCount / 4);
            pRequest.setParameter(TOTAL_COUNT, totalCount);
            pRequest.setParameter(LINES_PER_COLUMN, linesPerColumn);
            pRequest.setParameter(TOP_CATEGORIES, getTopCategories());
        }  // end if-else

        pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
    }

    /**
     * Counts items in the given List of categories
     *
     * @param  pCategories List of categories
     *
     * @return items' amount
     */
    private int countItems(ChangeAwareList pCategories) {
        int count = pCategories.size();
        for (Iterator<RepositoryItem> iterator = pCategories.iterator(); iterator.hasNext();) {
            count += ((ChangeAwareList) iterator.next().getPropertyValue(CHILD_CATEGORIES)).size();
        }
        return count;
    }

    /**
     * Returns topCategories property.
     *
     * @return topCategories property.
     */
    private ChangeAwareList getTopCategories() {
        CastCatalogTools catalogTools = (CastCatalogTools) getCatalogTools();
        RepositoryItem item = catalogTools.getCastoramaCatalog();
        return (ChangeAwareList) item.getPropertyValue(TOP_CATEGORIES);
    }

    /**
     * Checks if given category one from "topNavigationCategories".
     *
     * @param  pItem parameter to set.
     *
     * @return topNavigationCategory property.
     */
    private boolean isTopNavigationCategory(RepositoryItem pItem) {
        if (pItem.getPropertyValue(PARENT_CATEGORY) == null) {
            return true;  // this is "Master Catalog" category
        }
        final String thisId = pItem.getRepositoryId();
        for (Iterator<RepositoryItem> iter = getTopCategories().iterator(); iter.hasNext();) {
            if (iter.next().getRepositoryId().equals(thisId)) {
                return true;
            }
        }
        return false;
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
     * Sets the value of the catalogTools property.
     *
     * @param pCatalogTools parameter to set.
     */
    public void setCatalogTools(CatalogTools pCatalogTools) {
        mCatalogTools = pCatalogTools;
    }

    /**
     * Returns the property catalogTools which is a reference to a DCS component
     * that takes care of many common catalog related tasks.
     *
     * @return catalogTools property.
     */
    public CatalogTools getCatalogTools() {
        return mCatalogTools;
    }

}
