package com.castorama.droplet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.castorama.commerce.inventory.CastRepositoryInventoryManager;
import com.castorama.commerce.inventory.CastRepositoryInventoryManager.StockLevelStatus;

import atg.adapter.gsa.ChangeAwareList;
import atg.adapter.gsa.ChangeAwareSet;
import atg.adapter.gsa.GSAItem;
import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Returns correct parent category for "mother category carousel" on Bonnes Affaires.
 *
 * @author Epam Team
 */
public class BonnesAffairesParentCategoryLookupDroplet extends DynamoServlet {
    
    /** Input parameters */
    public static final String PRODUCT_PARAM = "product";
    public static final String BONNES_AFFAIRES_PARAM = "ba";
    static final ParameterName ELEMENT_NAME_PARAM = ParameterName.getParameterName("elementName");
    
    /** Output parameters */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");
    public static final ParameterName EMPTY = ParameterName.getParameterName("empty");
    
    /** PARENT_CATEGORIES_PROPERTY constant. */
    private static final String PARENT_CATEGORIES_PROPERTY = "parentCategories";
    
    /** PARENT_CATEGORY_PROPERTY constant. */
    private static final String PARENT_CATEGORY_PROPERTY = "parentCategory";
    
    /** BONNES_AFFAIRES_PROPERTY constant */
    public static final String BONNES_AFFAIRES_PROPERTY = "bonnesAffaires";
    
    /** ELEMENT constant. */
    public static final String ELEMENT = "element";

    /**
     *
     * @param  pRequest  dynamo http request
     * @param  pResponse dynamo http response
     *
     * @throws ServletException
     * @throws IOException
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        GSAItem product = (GSAItem) pRequest.getObjectParameter(PRODUCT_PARAM);
        Boolean bonnesAffairesParam = (Boolean) pRequest.getObjectParameter(BONNES_AFFAIRES_PARAM);
        boolean isBAContext = bonnesAffairesParam != null && bonnesAffairesParam == true;
        GSAItem parentCategory = null;
        
        if (product == null) {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
            return;
        }
        
        if (isBAContext) {
            ChangeAwareSet parentCategories= (ChangeAwareSet) product.getPropertyValue(PARENT_CATEGORIES_PROPERTY);
            if (parentCategories != null) {
                for (Iterator parentCategoryIterator = parentCategories.iterator(); parentCategoryIterator.hasNext();) {
                    GSAItem category = (GSAItem) parentCategoryIterator.next();
                    Boolean bonnesAffairesProperty = (Boolean) category.getPropertyValue(BONNES_AFFAIRES_PROPERTY);
                    if (bonnesAffairesProperty != null && bonnesAffairesProperty == true) {
                        parentCategory = category;
                    }
                }
            }
        }
        
        if (parentCategory == null)
            parentCategory = (GSAItem) product.getPropertyValue(PARENT_CATEGORY_PROPERTY);
        
        String elementName = pRequest.getParameter(ELEMENT_NAME_PARAM);
        if (elementName == null)
            elementName = ELEMENT;
        pRequest.setParameter(elementName, parentCategory);
        
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }
}
