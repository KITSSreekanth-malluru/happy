package com.castorama.droplet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import com.castorama.commerce.inventory.CastRepositoryInventoryManager;

import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class FreeShippingDroplet extends DynamoServlet {
    private static final String SKU_PARAM = "sku";
    private static final String STORE_PARAM = "store";
    private static final String SV_AVAILABLE_MAP = "svAvailableMap";
    public static final String FREE_SHIPPING_OPARAM = "freeShipping";

    private CastoDropletsPricingTools dropletsPricingTools;

    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
            throws ServletException, IOException{
        RepositoryItem sku = (RepositoryItem) pRequest.getObjectParameter(SKU_PARAM);
        RepositoryItem store = (RepositoryItem) pRequest.getObjectParameter(STORE_PARAM);
        Map<Integer, Integer> svAvailableMap = (Map<Integer, Integer>) pRequest.getObjectParameter(SV_AVAILABLE_MAP);

        Map<String, Object> params = getDropletsPricingTools().calculateForFreeShippingDroplet(sku, store, svAvailableMap);
        if (params != null){
            pRequest.serviceLocalParameter(FREE_SHIPPING_OPARAM, pRequest, pResponse);
        }
    }

    public CastoDropletsPricingTools getDropletsPricingTools() {
        return dropletsPricingTools;
    }

    public void setDropletsPricingTools(CastoDropletsPricingTools dropletsPricingTools) {
        this.dropletsPricingTools = dropletsPricingTools;
    }


}
