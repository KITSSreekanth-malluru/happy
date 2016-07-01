package com.castorama.mobile.droplet;

import static com.castorama.mobile.tools.CastoramaConstants.MAGASIN;

import java.io.IOException;
import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * find product location.
 * 
 * @author MK
 */
public class EmplacementLookupDroplet extends DynamoServlet {

    /** OUTPUT constant. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** EMPTY constant. */
    public static final ParameterName EMPTY = ParameterName.getParameterName("empty");
    
    /** STORE_ID constant. */
    private static final String EMPLACEMENTS = "emplacements";

    /** mStoreTools property. */
    private Repository mRackRepository;

    /**
     * Returns rackRepository property.
     * 
     * @return rackRepository property.
     */
    public Repository getRackRepository() {
        return mRackRepository;
    }

    /**
     * Sets the value of the rackRepository property.
     * 
     * @param pRackRepository parameter to set.
     */
    public void setRackRepository(Repository pRackRepository) {
        mRackRepository = pRackRepository;
    }

    /**
     * Find a location of product.
     * 
     * @param pRequest parameter
     * @param pResponse parameter
     * 
     * @throws ServletException exception
     * @throws IOException exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

         String stockStoreId = (String) pRequest.getParameter("stockStoreId");
         String productId = (String) pRequest.getParameter("productId");
         if (StringUtils.isBlank(stockStoreId) || StringUtils.isBlank(productId) ){
             return;
         }
        
         Repository rep = getRackRepository();
         try {
             RepositoryView repView = rep.getView("emplacement");
             RqlStatement emplacementRQL = RqlStatement.parseRqlStatement("storeId=?0 and productId=?1 RANGE 0+10");
             RepositoryItem[] emplacements = emplacementRQL.executeQuery(repView, new Object[] {stockStoreId, productId});
             if (emplacements != null && emplacements.length>0){
                 pRequest.setParameter(EMPLACEMENTS, emplacements);
                 pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
             }else {
                 pRequest.setParameter(EMPLACEMENTS, emplacements);
                 pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
             }
         } catch (RepositoryException e) {
             if (isLoggingError()) {
                 logError("Error occured in com.castorama.mobile.droplet.EmplacementLookupDroplet.service(): ",e);
             }
         }
    }
}
