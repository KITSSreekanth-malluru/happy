package com.castorama.droplet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Calculate percent and discount of the product.
 * 
 * @author Alena_Karpenkava
 * 
 */
public class CastPriceDroplet extends DynamoServlet {

    /**
     * 
     */
    private static final String ENABLED_CAST_CART = "enabledCastCart";
    private static final String STORE_IS_LOCAL = "storeIsLocal";
    /**
     * 
     */
    private static final String SHOW_CAST_CARD_PRICE = "showCastCardPrice";
    static final String LIST_PRICE = "listPrice";
    static final String SALE_PRICE = "salePrice";
    static final String CARD_PRICE = "cardPrice";
    
    /** Output parameters */
    static final ParameterName OUTPUT = ParameterName.getParameterName("output");
    
    private CastoDropletsPricingTools mDropletsPricingTools;

    /**
     * Calculate percent and discount of the product.
     * 
     * @param pRequest -
     *            dynamo http request
     * @param pResponse -
     *            dynamo http response
     * @throws IOException -
     *             exception
     * @throws ServletException -
     *             exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
      BigDecimal listPrice = null;
      BigDecimal salePrice = null;
      BigDecimal cardPrice = null;
      try {
          listPrice = new BigDecimal("" + pRequest.getObjectParameter(LIST_PRICE));
      } catch (NumberFormatException ex) {
          if (isLoggingError())
              logError("NumberFormatException: Please check list_price param: " + pRequest.getObjectParameter(LIST_PRICE));
      }
      try {
          salePrice = new BigDecimal("" + pRequest.getObjectParameter(SALE_PRICE));
      } catch (NumberFormatException ex) {
          if (isLoggingError())
              logError("NumberFormatException: Please check sale_price param: " + pRequest.getObjectParameter(SALE_PRICE));
      }
      Object showCardPriceObj = pRequest.getObjectParameter(SHOW_CAST_CARD_PRICE);
      Object enabledCastCartObj = pRequest.getObjectParameter(ENABLED_CAST_CART);
      Object storeIsLocalObj = pRequest.getObjectParameter(STORE_IS_LOCAL);
      Object reqPrice = pRequest.getObjectParameter(CARD_PRICE);
      cardPrice = (reqPrice != null) ? new BigDecimal(reqPrice.toString()) : null;
      Boolean showCardPrice = (showCardPriceObj != null) ? (Boolean) showCardPriceObj : false;
      Boolean enabledCastCart = (enabledCastCartObj != null) ? (Boolean) enabledCastCartObj : false;
      Boolean storeIsLocal = (storeIsLocalObj != null) ? (Boolean) storeIsLocalObj : false;
      
      Map<String, Object> outputParams = getDropletsPricingTools()
              .calculateForCastPriceDroplet(listPrice, salePrice,cardPrice, showCardPrice,enabledCastCart,storeIsLocal);
      getDropletsPricingTools().putParamsToRequest(pRequest, outputParams);
      pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
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
