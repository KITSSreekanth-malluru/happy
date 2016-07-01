package com.castorama.droplet;

import java.io.IOException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.servlet.ServletException;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.repository.servlet.NavHistory;
import atg.repository.servlet.NavHistoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 *
 * @author EPAM team
 */
public class StyleLookupDroplet extends DynamoServlet {
    /** productCatalog constant. */
    private Repository mProductCatalog;

    /**
     * Returns navHistory property.
     *
     * @param  pRequest parameter to set.
     *
     * @return navHistory property.
     */
    public NavHistory getNavHistory(DynamoHttpServletRequest pRequest) {
        return (NavHistory) pRequest.resolveName("/atg/commerce/catalog/CatalogNavHistory");
    }

    /**
     * Returns productCatalog property.
     *
     * @return productCatalog property.
     */
    public Repository getProductCatalog() {
        return mProductCatalog;
    }

    /**
     * Sets the value of the productCatalog property.
     *
     * @param pProductCatalog parameter to set.
     */
    public void setProductCatalog(Repository pProductCatalog) {
        mProductCatalog = pProductCatalog;
    }

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
        LinkedList history = getNavHistory(pRequest).getNavHistory();
        for (Iterator it = history.iterator(); it.hasNext();) {
            Object nameObj = it.next();
            if (nameObj instanceof NavHistoryItem) {
                continue;
            }
            RepositoryItem item = (RepositoryItem) nameObj;
            try {
                if (item.getItemDescriptor().getItemDescriptorName().endsWith("category")) {
                    pRequest.setParameter("style", item.getPropertyValue("style"));
                    pRequest.serviceLocalParameter("output", pRequest, pResponse);
                    return;
                }
                if (item.getItemDescriptor().getItemDescriptorName().endsWith("Document")) {
                    Set<RepositoryItem> categories = (Set<RepositoryItem>) item.getPropertyValue("categories");
                    for (Iterator<RepositoryItem> itCat = categories.iterator(); itCat.hasNext();) {
                        RepositoryItem category = itCat.next();
                        pRequest.setParameter("style", category.getPropertyValue("style"));
                        pRequest.serviceLocalParameter("output", pRequest, pResponse);
                        return;
                    }
                }
            } catch (RepositoryException e) {
                pRequest.serviceLocalParameter("error", pRequest, pResponse);
            }
        }
        String catId = pRequest.getParameter("categoryId");
        if (catId != null) {
            try {
                RepositoryItem category = getProductCatalog().getItem(catId, "category");
                if (category != null) {
                    pRequest.setParameter("style", category.getPropertyValue("style"));
                    pRequest.serviceLocalParameter("output", pRequest, pResponse);
                } else {
                    pRequest.serviceLocalParameter("empty", pRequest, pResponse);
                }

            } catch (RepositoryException e) {
                pRequest.serviceLocalParameter("empty", pRequest, pResponse);
            }
        } else {
            pRequest.serviceLocalParameter("empty", pRequest, pResponse);
        }
    }

}
