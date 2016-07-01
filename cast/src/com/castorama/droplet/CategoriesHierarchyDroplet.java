package com.castorama.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;

import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.repository.servlet.CastCatalogNavHistory;

/**
 * @author
 */
public class CategoriesHierarchyDroplet extends DynamoServlet {

    public CastCatalogNavHistory castCatalogNavHistory;

    private static final String MAGASIN_CASTORAMA = "Magasin Castorama";
    private static final String PARENT_CAT = "parentCategory";
    private static final String PARENT_CATS = "parentCategories";
    private static final String DISPLAY_NAME = "displayName";
    private static final String PARENT_CATEGORIES_FOR_CATALOG = "parentCategoriesForCatalog";
    private static final String MASTER_CATALOG = "masterCatalog";

    private static final String PRODUCT = "product";
    private static final String NAV_HISTORY = "navHistory";
    private static final String CATS = "cats";

    private static final String OUTPUT = "output";


    /**
     * @param pRequest  - dynamo http request
     * @param pResponse - dynamo http response
     * @throws ServletException - exception
     * @throws IOException      - exception
     */
    @SuppressWarnings("unchecked")
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        RepositoryItem product = (RepositoryItem) pRequest.getObjectParameter(PRODUCT);
        CastCatalogNavHistory castCatalogNavHistory = (CastCatalogNavHistory) pRequest.getObjectParameter(NAV_HISTORY);
        if (product != null && castCatalogNavHistory != null) {
            List cats = castCatalogNavHistory.getDefaultHistory(product);
            pRequest.setParameter(CATS, cats);
            pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        } else {
            pRequest.setParameter(CATS, null);
            pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        }
    }
}
