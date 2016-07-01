package com.castorama.droplet;

import atg.nucleus.naming.ParameterName;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import atg.userprofiling.Profile;

import java.io.IOException;

import javax.servlet.ServletException;

import com.castorama.commerce.profile.Constants;


/**
 * Droplet class to identify if user has Castorama Card.
 *
 * @author Aliaksandr Surma
  */
public class HasCastoramaCard extends DynamoServlet {
    //--------------------------------------
    // Constants

    //open parameters
    protected static final ParameterName TRUE = ParameterName.getParameterName(
            "true");
    protected static final ParameterName FALSE = ParameterName.getParameterName(
            "false");
    protected static final ParameterName ERROR = ParameterName.getParameterName(
            "error");

    //input parameters
    protected static final ParameterName PROFILE = ParameterName.getParameterName(
            "profile");

    //-------------------------------------
    // Properties


    /**
     * Overrides atg.servlet.DynamoServlet service method.
     * Contains logic to identify if user has Castorama Card..
     *
     * @param pRequest DynamoHttpServletRequest instance
     * @param pResponse DynamoHttpServletResponse instance
     *
     * @throws ServletException if any error occurs
     * @throws IOException if any error occurs
     */
    public void service(final DynamoHttpServletRequest pRequest,
        final DynamoHttpServletResponse pResponse)
        throws ServletException, IOException {
        final Object value = pRequest.getObjectParameter(PROFILE.getName());

        if (value == null) {
            if (isLoggingError()) {
                logError("'profile' input parameter is null");
            }

            pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);

            return;
        }

        if (!(value instanceof Profile)) {
            if (isLoggingError()) {
                logError("'profile' input parameter is invalid");
            }

            pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);

            return;
        }

        final Profile user = (Profile) value;

        final Boolean hasCastoramaCard = (Boolean)user.getPropertyValue(Constants.IS_ACTIVE);
        
        if (hasCastoramaCard != null && hasCastoramaCard) {
            pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
        } else {
            pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
        }
    }
}
