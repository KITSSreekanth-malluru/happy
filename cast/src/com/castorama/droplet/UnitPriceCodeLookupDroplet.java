package com.castorama.droplet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Retrieves price per unite for current SKU.
 * 
 * @author Alena_Karpenkava
 * 
 */
public class UnitPriceCodeLookupDroplet extends DynamoServlet {

    public static final String FLAG_AFFICHAGE_UP = "flagAffichageUp";
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");
    private static final ParameterName ERROR = ParameterName.getParameterName("error");
    private static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    public static final String PRICE = "price";
    
    public static final String SKU = "sku";

    
    

    private CastoDropletsPricingTools mDropletsPricingTools;
    

    /**
     * Retrieves price per unite for current SKU.
     * 
     * @param pRequest -
     *            dynamo http request
     * @param pResponse -
     *            dynamo http response
     * @throws ServletException -
     *             exception
     * @throws IOException -
     *             exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        RepositoryItem sku = (atg.repository.RepositoryItemImpl) pRequest.getObjectParameter(SKU);
        Double price = (Double) pRequest.getObjectParameter(PRICE);
        try {
//        Double pricePerUnite = null;
//        String libelle = null;
//        try {
//            if (getProductCatalogRepository() != null && sku != null) {
//                String code = (String) sku.getPropertyValue(CUP);
//                if (!StringUtils.isBlank(code)) {
//                    RepositoryItem codeUnite = getProductCatalogRepository().getItem(code, CODES_UNITE);
//                    if (codeUnite != null) {
//                        String flag = (String) codeUnite.getPropertyValue(FLAG);
//                        libelle = (String) codeUnite.getPropertyValue(LIBELLE);
//                        if (libelle!=null && libelle.equalsIgnoreCase(M2_CONST)){
//                            libelle = "m<sup>2</sup>";
//                        }
//                        Float coefficient = (Float) sku.getPropertyValue(PUPUV);
//                        if (!StringUtils.isBlank(flag) && FLAG_O.equals(flag) && coefficient != null
//                                && coefficient != 0) {
//                            if (price != null) {
//                                pricePerUnite = price / coefficient;
//                            }
//                        }
//                    }
//                }
//            }

            Map<String, Object> params;
            params = getDropletsPricingTools().calculateForUnitPriceCodeLookupDroplet(sku, price);
            if (params.size() > 0) {
//                pRequest.setParameter(PRICE_PER_UNITE, pricePerUnite);
//                pRequest.setParameter(LIBELLE, libelle);
                getDropletsPricingTools().putParamsToRequest(pRequest, params);
                pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
            } else {
                pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
            }

        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError(e.getMessage());
            }
            pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
        }
    }
    
    
    /**
     * @return the dropletsPricingTools
     */
    public CastoDropletsPricingTools getDropletsPricingTools() {
        return mDropletsPricingTools;
    }

    /**
     * @param dropletsPricingTools the dropletsPricingTools to set
     */
    public void setDropletsPricingTools(CastoDropletsPricingTools dropletsPricingTools) {
        this.mDropletsPricingTools = dropletsPricingTools;
    }
}
