package com.castorama.seo;

import atg.nucleus.naming.ParameterName;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.service.perfmonitor.PerformanceMonitor;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;

/**
 * Droplet responsible for building human readable URL for category
 *
 * @author EPAM team
 */
public class CategoryLinkDroplet extends DynamoServlet {
    /** CHARSET_UTF_8 constant. */
    private static final String CHARSET_UTF_8 = "UTF-8";

    /** URL_PARAM_JUMP constant. */
    private static final String URL_PARAM_JUMP = "jump";

	/** ROBOT constant. */
    private static final String ROBOT = "robot";

    /** URL_PARAM_CATEGORY_ID constant. */
    private static final String URL_PARAM_CATEGORY_ID = "categoryId";

    /** URL_PARAM_NAV_COUNT constant. */
    private static final String URL_PARAM_NAV_COUNT = "navCount";

    /** URL_PARAM_NAV_ACTION constant. */
    private static final String URL_PARAM_NAV_ACTION = "navAction";

    /** OPARAM_ERROR constant. */
    private static final String OPARAM_ERROR = "error";

    /** OPARAM_OUTPUT constant. */
    private static final String OPARAM_OUTPUT = "output";

    /** PARAMETER_URL constant. */
    private static final String PARAMETER_URL = "url";

    /** ITEM_CATEGORY constant. */
    private static final String ITEM_CATEGORY = "category";

    /** CATEGORY_ID constant. */
    private static final ParameterName CATEGORY_ID = ParameterName.getParameterName(URL_PARAM_CATEGORY_ID);

    /** NAV_ACTION constant. */
    private static final ParameterName NAV_ACTION = ParameterName.getParameterName(URL_PARAM_NAV_ACTION);

    /** NAV_COUNT constant. */
    private static final ParameterName NAV_COUNT = ParameterName.getParameterName(URL_PARAM_NAV_COUNT);

    /** productCatalog property. */
    private Repository mProductCatalog;

    private static final String P_NAV_COUNT = "navCount";

    private static final String P_NAV_ACTION = "navAction";
    
    private static final String P_PRODUCT_LISTING_VIEW = "productListingView";
    
    private static final String P_SEARCH_TYPE = "searchType";
    
    private static final String P_QUESTION = "question";
    
    private static final String P_ADD_FACET = "addFacet";
    
    private static final String P_REMOVE_FACET = "removeFacet";
    
    private static final String P_CURRENT_TAB = "currentTab";
    
    private static final String P_FEATERED_SKU_ID = "featuredSkuId";
    
    private static final String P_TRAIL = "trail";
    
    private static final String P_PAGE_NUM = "pageNum";
    
    private static final String P_SORT_BY_VALUE = "sortByValue";

    /** QS_PARAMETERS constant. */
    private static final Set<ParameterName> QS_PARAMETERS = new HashSet<ParameterName>();

    static {
        QS_PARAMETERS.add(ParameterName.getParameterName(P_NAV_ACTION));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_NAV_COUNT));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_PRODUCT_LISTING_VIEW));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_SEARCH_TYPE));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_QUESTION));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_ADD_FACET));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_REMOVE_FACET));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_CURRENT_TAB));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_FEATERED_SKU_ID));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_TRAIL));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_PAGE_NUM));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_SORT_BY_VALUE));
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
                PerformanceMonitor.startOperation("SEO", "CategoryLinkDroplet.service");
            }

            String categotyID = pRequest.getParameter(CATEGORY_ID);
            RepositoryItem category = null;

            if (categotyID == null || categotyID.length()==0) {
                StringBuffer url = new StringBuffer((String) pRequest.getAttribute("javax.servlet.forward.request_uri"));
                url.append(SEOUtils.createQueryStringWithoutEmptyParams(pRequest, QS_PARAMETERS));
                url.delete(0, 6);
                pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
                pRequest.setParameter(PARAMETER_URL, url.toString());
                return;
            }

            try {
                category = mProductCatalog.getItem(categotyID, ITEM_CATEGORY);
            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    logError(e);
                }
                pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
            }

            if (category != null) {
                StringBuffer url = new StringBuffer();
                url.append(URLDecoder.decode(SEOUtils.getCategoryURL(category),CHARSET_UTF_8));

                if (!pRequest.isBrowserType(ROBOT)) {
                    url.append(SEOUtils.createQueryStringWithoutEmptyParams(pRequest, QS_PARAMETERS));
                }

                pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
                pRequest.setParameter(PARAMETER_URL, url.toString());
            } else {
                pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
            }  // end if-else

        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("SEO", "CategoryLinkDroplet.service");
            }
        }  // end try-finally
    }
}
