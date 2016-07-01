package com.castorama.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;

import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * 
 * @author
 */
public class MasterCatalogFilterDroplet extends DynamoServlet {
    
    private static final String PRODUCTS = "products";
    
    private static final String CATALOGS = "catalogs";
    
    private static final String MASTER_CATALOG = "masterCatalog";
    
    private static final String LIST = "list";

    private static final String OUTPUT = "output";

    
    /**
     * 
     * @param  pRequest  - dynamo http request
     * @param  pResponse - dynamo http response
     *
     * @throws ServletException - exception
     * @throws IOException      - exception
     */
    @SuppressWarnings("unchecked")
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
            throws ServletException, IOException {
        List<RepositoryItem> products = (List<RepositoryItem>) pRequest.getObjectParameter(PRODUCTS);
        if (products != null) {
            List<RepositoryItem> filteredProducts = new ArrayList<RepositoryItem>();
            
            for (RepositoryItem product : products) {
                Set<RepositoryItem> catalogs = (Set<RepositoryItem>) product.getPropertyValue(CATALOGS);
                if ((catalogs != null) && (catalogs.size() > 0)) {
                    for (RepositoryItem catalog : catalogs) {
                        if (catalog.getRepositoryId().equals(MASTER_CATALOG)) {
                            filteredProducts.add(product);
                            break;
                        }
                    }
                }
            }
            
            if (filteredProducts.size() > 0) {
                pRequest.setParameter(LIST, filteredProducts);
                pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
            }
        }
    }
}
