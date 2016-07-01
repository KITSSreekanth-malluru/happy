package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import com.castorama.commerce.order.CastOrderManager;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * ToDo: DOCUMENT ME!
 *
 * @author  EPAM team
 * @version ToDo: INSERT VERSION NUMBER
 */
public class CastCheckOrdersCountDroplet extends DynamoServlet {
	/** Input parameters */
	static final ParameterName USER_ID = ParameterName.getParameterName("userId");

	/** Output parameters */
	static final ParameterName OUTPUT = ParameterName.getParameterName("output");
	static final ParameterName EMPTY = ParameterName.getParameterName("empty");

	/** Order manager */
    private CastOrderManager mOrderManager;

    /**
     * Returns orderManager property.
     *
     * @return orderManager property.
     */
    public CastOrderManager getOrderManager() {
        return mOrderManager;
    }

    /**
     * Sets the value of the orderManager property.
     *
     * @param pOrderManager parameter to set.
     */
    public void setOrderManager(CastOrderManager pOrderManager) {
        mOrderManager = pOrderManager;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  req ToDo: DOCUMENT ME!
     * @param  res ToDo: DOCUMENT ME!
     *
     * @throws ServletException ToDo: DOCUMENT ME!
     * @throws IOException      ToDo: DOCUMENT ME!
     */
    @Override public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponce) throws ServletException,
                                                                                                      IOException {
		String userId = pRequest.getParameter(USER_ID);
        if (userId != null && getOrderManager().hasOrders(userId)) {
        	pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponce);
        } else {
        	pRequest.serviceLocalParameter(EMPTY, pRequest, pResponce);
        }
    }

}
