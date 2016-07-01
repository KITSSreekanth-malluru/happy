package com.castorama.seo;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class DocumentLinkDroplet extends DynamoServlet {
    
    /** TYPE_ROBOT property */
    private static final String TYPE_ROBOT = "robot";

    /** ITEM_DOCUMENT property */
    private static final String ITEM_DOCUMENT = "castoramaDocument";

    /** PARAMETER_URL constant. */
    private static final String PARAMETER_URL = "url";

    /** OPARAM_ERROR constant. */
    private static final String OPARAM_ERROR = "error";

    /** OPARAM_OUTPUT constant. */
    private static final String OPARAM_OUTPUT = "output";

    /** PRODUCT_ID constant. */
    private static final ParameterName DOCUMENT_ID = ParameterName.getParameterName("documentId");
    
    private static final String P_NAV_ACTION = "navAction";
    
    private static final String P_NAV_COUNT = "navCount";

    private static final Set<ParameterName> QS_PARAMETERS = new HashSet<ParameterName>();

    static {
        QS_PARAMETERS.add(ParameterName.getParameterName(P_NAV_ACTION));
        QS_PARAMETERS.add(ParameterName.getParameterName(P_NAV_COUNT));
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
                PerformanceMonitor.startOperation("SEO", "DocumentLinkDroplet.service");
            }

            String documentID = pRequest.getParameter(DOCUMENT_ID);
            RepositoryItem document = null;

            if (documentID == null) {
                pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
                return;
            }

            try {
                document = mProductCatalog.getItem(documentID, ITEM_DOCUMENT);
            } catch (RepositoryException e) {
                logError(e);
                pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
            }

            if (document != null) {
                StringBuffer url = new StringBuffer();
                url.append(SEOUtils.getDocumentURL(document));
                
                if (!pRequest.isBrowserType(TYPE_ROBOT)) {

                    if (SEOUtils.getNotEmptyDocExtURL(document) == null){
                        url.append(SEOUtils.createQueryStringWithoutEmptyParams(pRequest, QS_PARAMETERS));
                    }
                }  // end if
                
                pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
                pRequest.setParameter(PARAMETER_URL, url.toString());
            } else {
                pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
            }  // end if-else
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("SEO", "DocumentLinkDroplet.service");
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
