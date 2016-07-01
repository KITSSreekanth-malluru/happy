package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.catalog.CatalogTools;

import atg.nucleus.naming.ParameterName;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * FooterColorDroplet class - gets the "style" property from the given category and shows it.
 *
 * @author  EPAM team
 */
public class FooterColorDroplet extends DynamoServlet {
    /** OUTPUT constant */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** EMPTY constant */
    public static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /** CATEGORY_ID constant */
    public static final String CATEGORY_ID = "categoryId";

    /** CATEGORY constant */
    public static final String CATEGORY = "category";

    /** STYLE constant */
    public static final String STYLE = "style";

    /** catalogTools property */
    private CatalogTools mCatalogTools;

    /**
     * Gets the "style" property from the given category and shows it.
     *
     * @param  pRequest  DynamoHttpServletRequest object
     * @param  pResponse DynamoHttpServletResponse object
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        String catId = pRequest.getParameter(CATEGORY_ID);
        if (catId != null) {
            try {
                RepositoryItem category = getCatalogTools().findCategory(catId, CATEGORY);
                if (category != null) {
                    pRequest.setParameter(STYLE, category.getPropertyValue(STYLE));
                    pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
                } else {
                    pRequest.serviceParameter(EMPTY, pRequest, pResponse);
                }
            } catch (RepositoryException e) {
                pRequest.serviceParameter(EMPTY, pRequest, pResponse);
            }
        } else {
            pRequest.serviceParameter(EMPTY, pRequest, pResponse);
        }
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
