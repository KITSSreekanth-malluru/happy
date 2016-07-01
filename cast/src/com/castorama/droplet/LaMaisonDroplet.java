package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.adapter.gsa.ChangeAwareList;
import atg.adapter.gsa.GSAItem;

import atg.commerce.catalog.CatalogTools;

import atg.repository.RepositoryException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 *
 * @author EPAM team
 */
public class LaMaisonDroplet extends DynamoServlet {
    /** CATEGORY_ID_PARAM constant. */
    public static final String CATEGORY_ID_PARAM = "categoryId";

    /** CHILD_CATEGORIES constant. */
    private static final String CHILD_CATEGORIES = "fixedChildCategories";

    /** catalogTools property. */
    private CatalogTools mCatalogTools;

    /**
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        String categoryId = (String) pRequest.getObjectParameter(CATEGORY_ID_PARAM);
        GSAItem category;
        if ((categoryId != null) && !("").equals(categoryId)) {
            try {
                category = (GSAItem) getCatalogTools().findCategory(categoryId);
                ChangeAwareList children = (ChangeAwareList) category.getPropertyValue(CHILD_CATEGORIES);
                pRequest.setParameter("children", children);

            } catch (RepositoryException e) {
                throw new ServletException(e);
            }

        }
        pRequest.serviceParameter("output", pRequest, pResponse);
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
     * @return ToDo: DOCUMENT ME!
     */
    public CatalogTools getCatalogTools() {
        return mCatalogTools;
    }

}
