package com.castorama.seo;

import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.service.perfmonitor.PerformanceMonitor;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

/**
 * Droplet responsible for building human readable URL for product
 *
 * @author EPAM team
 */
public class ProductLinkDroplet extends DynamoServlet {

    private static final String P_IS_FEATURED_PRODUCT = "isFeaturedProduct";

    private static final String P_SKU_ID = "skuId";

    private static final String P_IS_SEARCH_RESULT = "isSearchResult";

    private static final String P_SORT_BY_VALUE = "sortByValue";

    private static final String P_NAV_COUNT = "navCount";

    private static final String P_NAV_ACTION = "navAction";

    private static final String P_CATEGORY_ID = "categoryId";

    private static final String P_FROM_SV_CONTENT = "fromSVContext";

    private static final String P_BA_CATEGORY = "ba";
    
    private static final String P_HIDE_BREADCRUMBS = "hideBreadcrumbs";

    private static final String PRODUCT_ID = "productId";

    /** TYPE_ROBOT property */
    private static final String TYPE_ROBOT = "robot";

    /** ITEM_PRODUCT property */
    private static final String ITEM_PRODUCT = "product";

    /** PARAMETER_URL constant. */
    private static final String P_URL = "url";

    /** OPARAM_ERROR constant. */
    private static final String OPARAM_ERROR = "error";

    /** OPARAM_OUTPUT constant. */
    private static final String OPARAM_OUTPUT = "output";

    /** PRODUCT_ID constant. */
    private static final ParameterName P_PRODUCT_ID = ParameterName.getParameterName(PRODUCT_ID);

    /** QS_PARAMETERS constant. */
    private static final Set<ParameterName> QS_PARAMETERS = new HashSet<ParameterName>();
    
    static {
        QS_PARAMETERS.add(ParameterName.getParameterName(P_CATEGORY_ID));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_NAV_ACTION));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_NAV_COUNT));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_SORT_BY_VALUE));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_IS_SEARCH_RESULT));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_SKU_ID));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_IS_FEATURED_PRODUCT));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_FROM_SV_CONTENT));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_BA_CATEGORY));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_HIDE_BREADCRUMBS));
    }

    /** productCatalog property. */
    private Repository mProductCatalog;

    /**
     * ToDo: DOCUMENT ME!
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
                PerformanceMonitor.startOperation("SEO", "ProductLinkDroplet.service");
            }

            String productID = pRequest.getParameter(P_PRODUCT_ID);
            RepositoryItem product = null;

            if (productID == null) {
                pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
                return;
            }

            try {
                product = mProductCatalog.getItem(productID, ITEM_PRODUCT);
            } catch (RepositoryException e) {
                logError(e);
                pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
            }

            if (product != null) {
                StringBuffer url = new StringBuffer();
                url.append(SEOUtils.getProductURL(product));

                String queryString = null;
                if (!pRequest.isBrowserType(TYPE_ROBOT)) {
                    
                    url.append(SEOUtils.createQueryStringWithoutEmptyParams(pRequest, QS_PARAMETERS));
                }
                
                pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
                pRequest.setParameter(P_URL, url.toString());
            } else {
                pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
            }  // end if-else
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("SEO", "ProductLinkDroplet.service");
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
