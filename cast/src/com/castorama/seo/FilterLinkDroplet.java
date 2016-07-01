package com.castorama.seo;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class FilterLinkDroplet extends DynamoServlet {
    
    /** TYPE_ROBOT property */
    private static final String TYPE_ROBOT = "robot";

    /** ITEM_TOPIC property */
    private static final String ITEM_FILTER = "castoDocFilterTag";

    /** PARAMETER_URL constant. */
    private static final String PARAMETER_URL = "url";

    /** OPARAM_ERROR constant. */
    private static final String OPARAM_ERROR = "error";

    /** OPARAM_OUTPUT constant. */
    private static final String OPARAM_OUTPUT = "output";

    /** PRODUCT_ID constant. */
    private static final ParameterName FILTER_ID = ParameterName.getParameterName("filterId");


    /** productCatalog property. */
    private Repository mProductCatalog;

    /**
     * 
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("SEO", "FilterLinkDroplet.service");
            }

            String filterID = pRequest.getParameter(FILTER_ID);
            RepositoryItem filter = null;

            if (filterID == null) {
                pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
                return;
            }

            try {
                filter = mProductCatalog.getItem(filterID, ITEM_FILTER);
            } catch (RepositoryException e) {
                logError(e);
                pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
            }

            if (filter != null) {
                StringBuffer url = new StringBuffer();
                url.append(SEOUtils.getFilterURL(filter));
                
//                if (!pRequest.isBrowserType(TYPE_ROBOT)) {
//                    url.append(SEOUtils.createQueryStringWithoutEmptyParams(pRequest, QS_PARAMETERS));
//                }  // end if
                
                pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
                pRequest.setParameter(PARAMETER_URL, url.toString());
            } else {
                pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
            }  // end if-else
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("SEO", "FilterLinkDroplet.service");
            }
        }  // end try-finally
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
}
