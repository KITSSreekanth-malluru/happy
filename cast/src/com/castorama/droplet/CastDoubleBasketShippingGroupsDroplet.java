package com.castorama.droplet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import com.castorama.commerce.order.CastOrderTools;

import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.ShippingGroup;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Calculates two list of commercItemRelationships from two orders, 
 * which contains empty items if order don't contains product from another order.
 *
 * @author  EPAM team
 * @version 1.0
 */
public class CastDoubleBasketShippingGroupsDroplet extends DynamoServlet {

    /**
     * ERROR_MESSAGE_PARAMETER_NAME constant.
     */
    private static final String ERROR_MESSAGE_PARAMETER_NAME = "errorMessage";

    /**
     * ERROR_LOCAL_PARAMETER_NAME constant.
     */
    private static final String ERROR_LOCAL_PARAMETER_NAME = "error";

    /**
     * LOCAL_CI_RELATIONSHIPS_PARAMETER_NAME constant.
     */
    private static final String LOCAL_CI_RELATIONSHIPS_PARAMETER_NAME = "localCIRelationships";

    /**
     * OUTPUT_LOCAL_PARAMETER_NAME constant.
     */
    private static final String OUTPUT_LOCAL_PARAMETER_NAME = "output";

    /**
     * WEB_CI_RELATIONSHIPS_PARAMETER_NAME constant.
     */
    private static final String WEB_CI_RELATIONSHIPS_PARAMETER_NAME = "webCIRelationships";

    /**
     * WEB_CI_RELATIONSHIPS_PARAMETER_NAME constant.
     */
    private CastOrderTools mOrderTools;

    /**
     * @return the orderTools
     */
    public CastOrderTools getOrderTools() {
        return mOrderTools;
    }

    /**
     * @param pOrderTools the orderTools to set
     */
    public void setOrderTools(CastOrderTools pOrderTools) {
        this.mOrderTools = pOrderTools;
    }

    /**
     * Extends service method of DynamoServlet to returns refreshed commerce items relationships.
     *
     * @param  pRequest  dynamo http request
     * @param  pResponse dynamo http response
     *
     * @throws ServletException 
     * @throws IOException      
     *
     * @see    atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest,
     *         atg.servlet.DynamoHttpServletResponse)
     */
    @Override public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                           throws ServletException, IOException {
        List <ShippingGroup> webList = null;
        List <ShippingGroup> localList = null;
        Object webObj = pRequest.getObjectParameter("web");
        Object localObj = pRequest.getObjectParameter("local");
        if (webObj instanceof List){
            webList = (List<ShippingGroup>) webObj;
        }
        if (localObj instanceof List){
            localList = (List<ShippingGroup>) localObj;
        }

        for(ShippingGroup wGroup:webList ){
            if (localList != null) {
                for (ShippingGroup lGroup:localList){
                    ArrayList<ArrayList<CommerceItemRelationship>> result = getOrderTools().calculateGroups(wGroup, lGroup);
                    pRequest.serviceLocalParameter(OUTPUT_LOCAL_PARAMETER_NAME, pRequest, pResponse);
                    pRequest.setParameter(WEB_CI_RELATIONSHIPS_PARAMETER_NAME, result.get(0));
                    pRequest.setParameter(LOCAL_CI_RELATIONSHIPS_PARAMETER_NAME, result.get(1));
                    return;
                }
            } else {
                pRequest.serviceLocalParameter(OUTPUT_LOCAL_PARAMETER_NAME, pRequest, pResponse);
                pRequest.setParameter(WEB_CI_RELATIONSHIPS_PARAMETER_NAME, wGroup.getCommerceItemRelationships());
                pRequest.setParameter(LOCAL_CI_RELATIONSHIPS_PARAMETER_NAME, wGroup.getCommerceItemRelationships());
                return;
            }
        }
        pRequest.serviceLocalParameter(ERROR_LOCAL_PARAMETER_NAME, pRequest, pResponse);
        pRequest.setParameter(ERROR_MESSAGE_PARAMETER_NAME, "Can't refresh commerce items relationships");
    }

}
