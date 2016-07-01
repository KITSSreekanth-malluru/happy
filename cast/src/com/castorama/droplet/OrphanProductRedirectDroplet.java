/**
 * 
 */
package com.castorama.droplet;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * @author EPAM team
 *
 */
public class OrphanProductRedirectDroplet extends DynamoServlet {

    /** NOT_ORPHAN constant. */
    private static final String NOT_ORPHAN = "notOrphan";
    
    /** PRODUCT constant. */
    private static final String PRODUCT = "product";
    
    /** PARENT_CATS constant. */
    private static final String PARENT_CATS = "parentCategories";
    
    /** PRODUCT_ID constant. */
    private static final String PRODUCT_ID = "productId";

    /** catalogRepository property. */
    private Repository productRepository;

    public Repository getProductRepository() {
        return productRepository;
    }

    public void setProductRepository(Repository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 
     * 
     * @param pRequest parameter
     * @param pResponse parameter
     * 
     * @throws ServletException exception
     * @throws IOException exception
     */
    @SuppressWarnings("unchecked")
    @Override
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

        String prodId = pRequest.getParameter(PRODUCT_ID);
        if ((prodId != null) && (prodId.length() > 0)) {
        
            try {
                RepositoryItem prod = productRepository.getItem(prodId, PRODUCT);
                
                Set<RepositoryItem> parentCats = (Set<RepositoryItem>) prod.getPropertyValue(PARENT_CATS);
                if (parentCats.isEmpty()) {
                    pResponse.setStatus(301);
                    pResponse.setHeader("Location", "/store/global/301.jsp");
                    pResponse.setHeader("Connection", "close");
                } else {
                    pRequest.serviceLocalParameter(NOT_ORPHAN, pRequest, pResponse);
                }
                
            } catch (RepositoryException re) {
                if (isLoggingError()) {
                    logError(re);
                }
            }
        }
    }
}
