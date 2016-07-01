package com.castorama.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.pricing.ItemPriceInfo;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;


public class ProductPriceInfoDroplet extends DynamoServlet {
    private static final String PARAM_SKU_CODE_ARTICLE = "codeArticle";
    private static final String PARAM_SKU_ID = "skuId";
    private static final String PARAM_CURRENCY_CODE = "currencyCode";
    private static final String OPARAM_OUTPUT = "output";
    
    private static final String PROPERTY_ATI_PRICE = "prixVenteTTCEuro";
    private static final String PROPERTY_TF_PRICE = "prixVenteHTEuro";
    private static final String PARAM_ATI_PRICE = "allTaxIncludedPrice";
    private static final String PARAM_TF_PRICE = "taxFreePrice";
    private static final String SKU_TYPE = "casto_sku";
    
    private Repository mProductCatalog;

    /** Calculate price of product unit with ttc and without it*/
    public void service(DynamoHttpServletRequest request, DynamoHttpServletResponse response) 
        throws ServletException, IOException {
        String codeArticle = request.getParameter(PARAM_SKU_CODE_ARTICLE);
        String skuId = request.getParameter(PARAM_SKU_ID);
        if (codeArticle == null && skuId == null) {
            if (isLoggingError()) {
                logError("Some of input parameters '" + PARAM_SKU_CODE_ARTICLE + "' of '" + PARAM_SKU_ID + "' must pass");
            }
            throw new ServletException("Some of input parameters '" + PARAM_SKU_CODE_ARTICLE + "' of '" + PARAM_SKU_ID + "' must pass");
        }
        
        RepositoryItem sku = null;
        try {
            if (codeArticle != null) {
                RepositoryView view = getProductCatalog().getView(SKU_TYPE);
                Object[] rqlParams = new Object[1];
                rqlParams[0] = codeArticle;
                RqlStatement findSkuStatement = RqlStatement.parseRqlStatement("codeArticle = ?0");
                RepositoryItem[] queryResult = findSkuStatement.executeQuery(view, rqlParams);
                sku = queryResult[0];
            } else {
                sku = getProductCatalog().getItem(skuId, SKU_TYPE);
            }
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError(e.getMessage());
            }
            throw new ServletException(e);
        }
        
        double tfPrice = 0.0;
        double atiPrice = 0.0;
        if (sku != null) {
            tfPrice = (Float)sku.getPropertyValue(PROPERTY_TF_PRICE);
            atiPrice = (Float)sku.getPropertyValue(PROPERTY_ATI_PRICE);
        }
        
        if (isLoggingDebug()) {
            logDebug("ProductPriceInfoDroplet: Setting output parameters");
            logDebug("param" + PARAM_TF_PRICE + " " + tfPrice );
            logDebug("param" + PARAM_ATI_PRICE + " " + atiPrice );
        }
        request.setParameter(PARAM_TF_PRICE, tfPrice);
        request.setParameter(PARAM_ATI_PRICE, atiPrice);
        request.serviceParameter(OPARAM_OUTPUT, request, response);
    }

    public Repository getProductCatalog() {
        return mProductCatalog;
    }

    public void setProductCatalog(Repository pProductCatalog) {
        this.mProductCatalog = pProductCatalog;
    }    

}
