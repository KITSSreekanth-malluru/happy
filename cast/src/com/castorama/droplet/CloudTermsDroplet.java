package com.castorama.droplet;

import java.io.IOException;

import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import atg.repository.rql.RqlStatement;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * ToDo: DOCUMENT ME!
 *
 * @author EPAM team
 */
public class CloudTermsDroplet extends DynamoServlet {
    /** OUTPUT constant. */
    public static final String OUTPUT = "output";

    /** EMPTY constant. */
    public static final String EMPTY = "empty";

    /** REFERENCE_ID constant. */
    public static final String REFERENCE_ID = "referenceId";

    /** DOCUMENT_ID constant. */
    private static final String DOCUMENT_ID = "documentId";

    /** PRODUCT_ID constant. */
    private static final String PRODUCT_ID = "productId";

    /** catalogRepository property. */
    private Repository mCatalogRepository;

    /** termsRepository property. */
    private Repository mTermsRepository;

    /**
     *
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    @Override public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                           throws ServletException, IOException {
        String referenceId = null;
        String paramId = pRequest.getParameter(DOCUMENT_ID);
        if (!StringUtils.isEmpty(paramId)) {
            referenceId = getReferenceIdByDocumentId(paramId);
        } else {
            paramId = pRequest.getParameter(PRODUCT_ID);
            referenceId = getReferenceIdByProductId(paramId);
        }

        if (referenceId != null) {
            pRequest.setParameter(REFERENCE_ID, referenceId);
            pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        } else {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
        }
    }

    /**
     * Returns referenceIdByDocumentId property.
     *
     * @param  documentId parameter to set.
     *
     * @return referenceIdByDocumentId property.
     */
    private String getReferenceIdByDocumentId(String documentId) {
        String result = null;
        if (!StringUtils.isEmpty(documentId)) {
            try {
                if (checkReferenceId(documentId)) {
                    return documentId;
                }
                RepositoryItem item = getCatalogRepository().getItem(documentId, "castoramaDocument");
                if (item != null) {
                    Set categories = (Set) item.getPropertyValue("categories");
                    if (categories != null) {
                        RepositoryItem category;
                        for (Iterator iter = categories.iterator(); iter.hasNext();) {
                            category = (RepositoryItem) iter.next();
                            String catId = category.getRepositoryId();
                            if (checkReferenceId(catId)) {
                                result = catId;
                                break;
                            }
                        }
                    }
                }

            } catch (Exception e) {
                if (isLoggingDebug()) {
                    logError(e);
                }
            }  // end try-catch
        }  // end if
        return result;
    }

    /**
     * Returns referenceIdByProductId property.
     *
     * @param  productId parameter to set.
     *
     * @return referenceIdByProductId property.
     */
    private String getReferenceIdByProductId(String productId) {
        String result = null;
        if (!StringUtils.isEmpty(productId)) {
            try {
                RepositoryItem item = getCatalogRepository().getItem(productId, "product");
                if (item != null) {
                    Set categories = (Set) item.getPropertyValue("castoAncestorCategories");
                    if (categories != null) {
                        RepositoryItem category;
                        for (Iterator iter = categories.iterator(); iter.hasNext();) {
                            category = (RepositoryItem) iter.next();
                            String catId = category.getRepositoryId();
                            if (checkReferenceId(catId)) {
                                result = catId;
                                break;
                            }
                        }
                    }
                }

            } catch (Exception e) {
                if (isLoggingDebug()) {
                    logError(e);
                }
            }  // end try-catch
        }  // end if
        return result;
    }

    /**
     *
     *
     * @param  referenceId parameter
     *
     * @return
     *
     * @throws RepositoryException exception
     */
    private boolean checkReferenceId(String referenceId) throws RepositoryException {
        boolean result = false;
        RepositoryView view = getTermsRepository().getView("cloud");
        RqlStatement statement = RqlStatement.parseRqlStatement("ReferenceId" + " = ?0");
        RepositoryItem[] items = statement.executeQuery(view, new Object[] {referenceId});
        if ((items != null) && (items.length > 0)) {
            result = true;
        }
        return result;
    }

    /**
     * Returns catalogRepository property.
     *
     * @return catalogRepository property.
     */
    public Repository getCatalogRepository() {
        return mCatalogRepository;
    }

    /**
     * Sets the value of the catalogRepository property.
     *
     * @param pCatalogRepository parameter to set.
     */
    public void setCatalogRepository(Repository pCatalogRepository) {
        mCatalogRepository = pCatalogRepository;
    }

    /**
     * Returns termsRepository property.
     *
     * @return termsRepository property.
     */
    public Repository getTermsRepository() {
        return mTermsRepository;
    }

    /**
     * Sets the value of the termsRepository property.
     *
     * @param pTermsRepository parameter to set.
     */
    public void setTermsRepository(Repository pTermsRepository) {
        mTermsRepository = pTermsRepository;
    }

}
