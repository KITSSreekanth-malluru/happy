package com.castorama.droplet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.adapter.gsa.ChangeAwareList;
import atg.adapter.gsa.GSAItem;

import atg.commerce.catalog.CatalogTools;

import atg.nucleus.naming.ParameterName;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.commerce.catalog.CastCatalogTools;

/**
 * SeeAlsoDroplet class
 *
 * @author  EPAM team
 */
public class SeeAlsoDroplet extends DynamoServlet {
    /** Oparam: output. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** Oparam: empty. */
    public static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /** ITEM_PARAM constant */
    public static final String ITEM_PARAM = "item";

    /** RESULTS constant */
    public static final String RESULTS = "results";

    /** PAREN_CATEGORY constant */
    public static final String PAREN_CATEGORY = "parentCategory";

    /** CHILD_CATEGORIES constant */
    public static final String CHILD_CATEGORIES = "fixedChildCategories";

    /** PRODUCTS_COUNT constant */
    public static final String PRODUCTS_COUNT = "productsCount";

    /** PIVOT constant */
    public static final String PIVOT = "pivot";

    /** TOP_CATEGORIES constant */
    public static final String TOP_CATEGORIES = "topNavigationCategories";

    /** ANCESTORS constant */
    public static final String ANCESTORS = "ancestorcategories";

    /** catalogTools property */
    CatalogTools mCatalogTools;

    /**
     * This method gets the "item" parameter from request and calculates five
     * sibling categories (with paths) of it's parent pivot category.
     *
     * @param  pRequest  DynamoHttpServletRequest object
     * @param  pResponse DynamoHttpServletResponse object
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        GSAItem item = (GSAItem) pRequest.getObjectParameter(ITEM_PARAM);
        List<Map> calegoriesList = new ArrayList<Map>();
        List finalCategories = new ArrayList();
        List productAmounts = new ArrayList();

        GSAItem parentCategory = returnPivotCategory((GSAItem) item.getPropertyValue(PAREN_CATEGORY));
        String parentCategoryId = (String) parentCategory.getRepositoryId();

        if (isParentTopNavigationCategory(parentCategory)) {
            pRequest.serviceParameter(EMPTY, pRequest, pResponse);
        } else {
            GSAItem grandParentCategory = (GSAItem) parentCategory.getPropertyValue(PAREN_CATEGORY);
            ChangeAwareList uncleCategories;
            if (grandParentCategory != null) {
                uncleCategories = (ChangeAwareList) grandParentCategory.getPropertyValue(CHILD_CATEGORIES);
            } else {
                uncleCategories = (ChangeAwareList) parentCategory.getPropertyValue(CHILD_CATEGORIES);
            }

            List<RepositoryItem> tempUncleCategories = new ArrayList<RepositoryItem>();
            tempUncleCategories.addAll(uncleCategories);
            try {
                for (RepositoryItem categoty : tempUncleCategories) {
                    if (!categoty.getRepositoryId().equals(parentCategoryId)) {
                        LinkedList<RepositoryItem> tmpAncestors = getCatalogTools().getAncestors(categoty);
                        List<RepositoryItem> ancestors = new LinkedList<RepositoryItem>();
                        for (RepositoryItem itemm : tmpAncestors) {
                            if (!((ChangeAwareList) itemm.getPropertyValue(ANCESTORS)).isEmpty()) {
                                ancestors.add(itemm);
                            }
                        }
						int productsAmount = 0;
                        Integer productsCount = (Integer) categoty.getPropertyValue(PRODUCTS_COUNT);
                        if(productsCount != null) {
                        	productsAmount = productsCount.intValue();
                        }
                        if (productsAmount == 0) {
                            ChangeAwareList childCategories =
                                (ChangeAwareList) categoty.getPropertyValue(CHILD_CATEGORIES);
                            for (Iterator iterator = childCategories.iterator(); iterator.hasNext();) {
                                RepositoryItem tmpCategoty = (RepositoryItem) iterator.next();
                                Integer prodCound = (Integer) tmpCategoty.getPropertyValue(PRODUCTS_COUNT);
                                if(prodCound != null) {
                                	productsAmount += prodCound.intValue();
                                }
                            }
                        }
                        ancestors.add(categoty);
                        Map categoriesWithAmount = new HashMap();
                        categoriesWithAmount.put(productsAmount, ancestors);
                        calegoriesList.add(categoriesWithAmount);
                        productAmounts.add(productsAmount);
                    }  // end if
                }  // end for

            } catch (Exception e) {
                if (isLoggingError()) {
                    logError(e);
                }

            }  // end try-catch

            Collections.sort(productAmounts, Collections.reverseOrder());
            int size = (productAmounts.size() > 5) ? 5 : productAmounts.size();
            if (calegoriesList.size() < 5) {
                size = calegoriesList.size();
            }

            for (int i = 0; i < size; i++) {
                int key = ((Integer) productAmounts.get(i)).intValue();
                for (Map tmpCategoty : calegoriesList) {
                    Object object = tmpCategoty.get(key);
                    if (object != null) {
                        finalCategories.add(object);
                        tmpCategoty.remove(key);
                        break;
                    }
                }
            }

            pRequest.setParameter(RESULTS, finalCategories);
            pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
        }  // end if-else
    }

    /**
     * Returns first parent pivot category for the product
     *
     * @param  item 
     *
     * @return GSAItem category
     */
    private GSAItem returnPivotCategory(GSAItem item) {
        GSAItem tmpItem;
        if (!(Boolean) item.getPropertyValue(PIVOT)) {
            GSAItem aItem = (GSAItem) item.getPropertyValue(PAREN_CATEGORY);
            if (aItem != null) {
                tmpItem = returnPivotCategory(aItem);
            } else {
                tmpItem = item;
            }
        } else {
            tmpItem = item;
        }
        return tmpItem;
    }

    /**
     * Checks is category one of top navigational
     */
    private boolean isParentTopNavigationCategory(GSAItem parentCategory) {
        String thisId = (String) parentCategory.getRepositoryId();
        if (parentCategory.getPropertyValue(PAREN_CATEGORY) == null) {
            return true;  // this is "Master Catalog" category
        }
        CastCatalogTools catalogTools = (CastCatalogTools) getCatalogTools();
        RepositoryItem item = catalogTools.getCastoramaCatalog();
        ChangeAwareList topCategories = (ChangeAwareList) item.getPropertyValue(TOP_CATEGORIES);
        for (Iterator<RepositoryItem> iter = topCategories.iterator(); iter.hasNext();) {
            if (iter.next().getRepositoryId().equals(thisId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns Catalog Tools to access Product Catalog
     *
     * @return the CatalogTools
     */
    public CatalogTools getCatalogTools() {
        return mCatalogTools;
    }

    /**
     * Sets Catalog Tools to access Product Catalog
     *
     * @param pCatalogTools
     */
    public void setCatalogTools(CatalogTools pCatalogTools) {
        mCatalogTools = pCatalogTools;
    }

}
