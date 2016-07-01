package com.castorama.seo;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.ServletException;

import atg.core.net.URLUtils;
import atg.core.util.StringUtils;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class CanonicalLinkDroplet extends DynamoServlet {

    private static final String PAGE_NUM = "pageNum";

    /** ITEM_PRODUCT constant. */
    private static final String ITEM_PRODUCT = "product";
    /** ITEM_TOPIC constant. */
    private static final String ITEM_TOPIC = "topic";
    /** ITEM_CF_FILTER_TAG constant. */
    private static final String ITEM_CF_FILTER_TAG = "cfFilterTag";
    /** ITEM_REGULAR_CATEGORY constant. */
    private static final String ITEM_REGULAR_CATEGORY = "regularCategory";
    /** ITEM_PIVOT_CATEGORY constant. */
    private static final String ITEM_PIVOT_CATEGORY = "pivotCategory";
    /** ITEM_PROMOTIONAL_INFO constant. */
    private static final String ITEM_PROMOTIONAL_INFO = "promoInformation";
    /** ITEM_DOCUMENT constant */
    private static final String ITEM_DOCUMENT = "castoramaDocument";
    /** TYPE_OF_LINK constant. */
    private static final String TYPE_OF_LINK = "type";

    /** productCatalog property. */
    private Repository mProductCatalog;

    /** OPARAM_ERROR constant. */
    private static final String OPARAM_ERROR = "error";

    /** OPARAM_OUTPUT constant. */
    private static final String OPARAM_OUTPUT = "output";

    /** PARAMETER_URL constant. */
    private static final String PARAMETER_URL = "url";

    /**
     * 
     * ToDo: DOCUMENT ME!
     * 
     * @param pRequest
     *            parameter
     * @param pResponse
     *            parameter
     * 
     * @throws ServletException
     *             exception
     * @throws IOException
     *             exception
     */
    public void service(DynamoHttpServletRequest pRequest,
            DynamoHttpServletResponse pResponse) throws ServletException,
            IOException {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("SEO", "CanonicalLinkDroplet.service");
            }

            String fullLink = null;
            try {
                fullLink = getCanonicalUrl(pRequest, pResponse);
            } catch (RepositoryException e) {
                pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest,pResponse);
            }

            if (fullLink != null) {
                pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
                pRequest.setParameter(PARAMETER_URL, fullLink);
            } else {
                pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
            } // end if-else

        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("SEO",
                        "CanonicalLinkDroplet.service");
            }
        } // end try-finally
    }

    private String getCanonicalUrl(DynamoHttpServletRequest pRequest,
            DynamoHttpServletResponse pResponse) throws ServletException,
            IOException, RepositoryException {
        Object ob = pRequest.getLocalParameter(TYPE_OF_LINK);
        String typeOfLink = null;
        if (ob instanceof String){
            typeOfLink = (String) ob; 
        } else {
            if (isLoggingError()) {
                logError("type is not a String.");
            }
            return null;
        }
        if (StringUtils.isBlank(typeOfLink)) {
            if (isLoggingError()) {
                logError("Type of link is required.");
            }
            return null;
        }

        StringBuffer url = new StringBuffer();
        if (typeOfLink.trim().equalsIgnoreCase(ITEM_PIVOT_CATEGORY)) {
            String forwardQueryString = (String) pRequest.getAttribute("javax.servlet.forward.query_string");
            String forwardUrlString = (String) pRequest.getAttribute("javax.servlet.forward.request_uri");
            url.append(forwardUrlString);
            if (null != forwardQueryString){
//                int numberOfslashes = forwardUrlString.replaceAll("[^/]","").length();
//                if (forwardQueryString.toLowerCase().contains("trail=") && numberOfslashes>=2){
//                    return "";
//                }
                Hashtable queryParams = URLUtils.parseQueryString(forwardQueryString);
                if (queryParams != null && queryParams.size() > 0) {
                    Object pageNumObj = queryParams.get(PAGE_NUM);
                    if (pageNumObj != null && pageNumObj instanceof String[]){
                        String pageNum = ((String[])pageNumObj)[0];
                        if (!StringUtils.isBlank(pageNum) && !pageNum.equals("1")) {
                            url.append('?');
                            url.append("pageNum=");
                            url.append(pageNum);
                        }
                    }
                }
                
            }

        } else if (typeOfLink.trim().equalsIgnoreCase(ITEM_REGULAR_CATEGORY) || 
                   typeOfLink.trim().equalsIgnoreCase(ITEM_PRODUCT) ||
                   typeOfLink.trim().equalsIgnoreCase(ITEM_TOPIC) ||
                   typeOfLink.trim().equalsIgnoreCase(ITEM_CF_FILTER_TAG) ||
                   typeOfLink.trim().equalsIgnoreCase(ITEM_PROMOTIONAL_INFO) ||
                   typeOfLink.trim().equalsIgnoreCase(ITEM_DOCUMENT)){
            url.append((String) pRequest.getAttribute("javax.servlet.forward.request_uri"));
        } else
        return null;
        
        return url.toString();
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
     * @param pProductCatalog
     *            parameter to set.
     */
    public void setProductCatalog(Repository pProductCatalog) {
        mProductCatalog = pProductCatalog;
    }
}
